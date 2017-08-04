package com.zboigor.service;

import org.springframework.stereotype.Service;
import com.zboigor.model.BanVote;
import com.zboigor.repository.BanVoteRepository;
import com.zboigor.util.Pair;

import java.util.List;
import java.util.Optional;

/**
 * @author Igor Zboichik
 * @since 2017-08-03
 */
@Service
public class BanVoteService {

    private final BanVoteRepository banVoteRepository;

    public BanVoteService(BanVoteRepository banVoteRepository) {this.banVoteRepository = banVoteRepository;}

    public void vote(Long chatId, Integer voteMessageId, Integer userId, Boolean ban) {
        Optional<BanVote> voteOptional = banVoteRepository.findOneByChatIdAndVoteMessageIdAndUserId(chatId, voteMessageId, userId);
        if (voteOptional.isPresent()) {
            banVoteRepository.save(voteOptional.get().setBan(ban));
        } else {
            banVoteRepository.save(new BanVote()
                .setChatId(chatId)
                .setVoteMessageId(voteMessageId)
                .setUserId(userId)
                .setBan(ban)
            );
        }
    }

    public Pair<Integer, Integer> getActualVotes(Long chatId, Integer voteMessageId) {
        List<BanVote> allVotes = banVoteRepository.findAllByChatIdAndVoteMessageId(chatId, voteMessageId);
        int ban = 0;
        int notBan = 0;

        for (BanVote vote : allVotes) {
            if (vote.getBan()) {
                ban++;
            } else {
                notBan++;
            }
        }
        return new Pair<>(ban, notBan);
    }
}
