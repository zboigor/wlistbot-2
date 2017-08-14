package com.zboigor.repository;

import com.zboigor.model.ActivityAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.util.Collection;

/**
 * @author sss3 (Vladimir Aleexeev)
 */
public interface ActivityAuditRepository extends JpaRepository<ActivityAudit, Long> {

    Collection<ActivityAudit> findByChatIdAndUserIdAndDateAfter(Long chatId, Integer userId, Date date);

    void deleteByDateBefore(Date date);
}
