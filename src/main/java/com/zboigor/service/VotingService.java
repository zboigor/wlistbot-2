package com.zboigor.service;

import com.zboigor.model.Voting;
import com.zboigor.model.VotingPK;
import com.zboigor.repository.VotingRepository;
import org.springframework.stereotype.Service;

/**
 * @author Igor Zboichik
 * @since 2017-08-30
 */
@Service
public class VotingService {

    private final VotingRepository votingRepository;

    public VotingService(VotingRepository votingRepository) {
        this.votingRepository = votingRepository;
    }

    public Voting save(Long chatId, Integer messageId, Integer initiatorId, String initiatorName) {
        return votingRepository.save(new Voting(new VotingPK(chatId, messageId), initiatorId, initiatorName));
    }

    public Voting find(Long chatId, Integer messageId) {
        return votingRepository.findById_ChatIdAndId_MessageId(chatId, messageId).orElse(null);
    }
}
