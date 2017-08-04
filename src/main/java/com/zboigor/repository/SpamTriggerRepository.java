package com.zboigor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.zboigor.model.SpamTrigger;

import java.util.List;
import java.util.Optional;

/**
 * @author Igor Zboichik
 * @since 2017-08-03
 */
@Repository
public interface SpamTriggerRepository extends JpaRepository<SpamTrigger, Long> {

    List<SpamTrigger> findByTriggerTextAndIsGlobal(String triggerText, Boolean isGlobal);
    List<SpamTrigger> findByChatIdAndTriggerText(Long chatId, String triggerText);
}
