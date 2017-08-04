package com.zboigor.service;

import org.springframework.stereotype.Service;
import com.zboigor.model.SpamTrigger;
import com.zboigor.repository.SpamTriggerRepository;

import java.util.List;

/**
 * @author Igor Zboichik
 * @since 2017-08-03
 */
@Service
public class SpamTriggerService {

    private final SpamTriggerRepository spamTriggerRepository;

    public SpamTriggerService(SpamTriggerRepository spamTriggerRepository) {this.spamTriggerRepository = spamTriggerRepository;}

    public List<SpamTrigger> loadAll() {
        return spamTriggerRepository.findAll();
    }

    public SpamTrigger add(Long chatId, String trigger) {
        return spamTriggerRepository.save(new SpamTrigger().setChatId(chatId).setTriggerText(trigger).setIsGlobal(false));
    }

    public SpamTrigger addGlobal(String trigger) {
        return spamTriggerRepository.save(new SpamTrigger().setTriggerText(trigger).setIsGlobal(true));
    }

    public void removeGlobal(String trigger) {
        List<SpamTrigger> spamTriggers = spamTriggerRepository.findByTriggerTextAndIsGlobal(trigger, true);
        spamTriggerRepository.delete(spamTriggers);
    }

    public void remove(Long chatId, String trigger) {
        List<SpamTrigger> spamTriggers = spamTriggerRepository.findByChatIdAndTriggerText(chatId, trigger);
        spamTriggerRepository.delete(spamTriggers);
    }
}
