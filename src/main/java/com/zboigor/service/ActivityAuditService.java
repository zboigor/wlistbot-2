package com.zboigor.service;

import com.zboigor.config.AppProperties;
import com.zboigor.model.ActivityAudit;
import com.zboigor.repository.ActivityAuditRepository;
import com.zboigor.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author sss3 (Vladimir Aleexeev)
 */
@Service
public class ActivityAuditService {

    private static final int CACHE_SIZE = 1000;

    private final Collection<Pair<Long, Integer>> cache = new CopyOnWriteArrayList<>();
    private final AtomicInteger currentCacheSize = new AtomicInteger();

    private final ActivityAuditRepository activityAuditRepository;
    private final int activityCheckDay;

    public ActivityAuditService(ActivityAuditRepository activityAuditRepository, AppProperties appProperties) {
        this.activityAuditRepository = activityAuditRepository;
        this.activityCheckDay = appProperties.getActivityTime();
    }

    public void audit(Long chatId, Integer userId) {
        cache.add(new Pair<>(chatId, userId));
        if (currentCacheSize.incrementAndGet() > CACHE_SIZE) {
            flush();
        }
    }

    public Long getLatestActivityCount(Long chatId, Integer userId) {
        final Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DATE, -activityCheckDay);
        final Date date = new Date(instance.getTime().getTime());
        final Collection<ActivityAudit> lastActivity = activityAuditRepository.findByChatIdAndUserIdAndDateAfter(chatId, userId, date);
        final Long reduce = lastActivity.stream().map(ActivityAudit::getCount).reduce(0L, Long::sum);
        final Long fromCache = groupingCache().getOrDefault(new Pair<>(chatId, userId), 0L);
        return fromCache + reduce;
    }


    private void flush() {
        synchronized (cache) {
            final long time = new java.util.Date().getTime();
            final Date currentDate = new Date(time);
            final List<ActivityAudit> collect = groupingCache().entrySet()
                    .stream().map(e -> new ActivityAudit().setChatId(e.getKey().getFirst()).setUserId(e.getKey().getSecond())
                            .setCount(e.getValue()).setDate(currentDate))
                    .collect(Collectors.toList());
            activityAuditRepository.save(collect);
            activityAuditRepository.flush();
            currentCacheSize.set(0);
            cache.clear();
        }
    }

    private Map<Pair<Long, Integer>, Long> groupingCache() {
        return cache.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    @Component
    public static class EvictJob {

        private final ActivityAuditRepository activityAuditRepository;
        private final int activityCheckDay;

        public EvictJob(ActivityAuditRepository activityAuditRepository, AppProperties appProperties) {
            this.activityAuditRepository = activityAuditRepository;
            activityCheckDay = appProperties.getActivityTime();
        }

        @Scheduled(cron = "0 4 * * *") //every day in 4 am
        public void evict() {
            final Calendar instance = Calendar.getInstance();
            instance.add(Calendar.DATE, - activityCheckDay - 1);
            final Date date = new Date(instance.getTime().getTime());
            activityAuditRepository.deleteByDateBefore(date);
        }

    }

}
