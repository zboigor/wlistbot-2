package com.zboigor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.zboigor.model.BanVote;

import java.util.List;
import java.util.Optional;

@Repository
public interface BanVoteRepository extends JpaRepository<BanVote, Long> {

    Optional<BanVote> findOneByChatIdAndVoteMessageIdAndUserId(Long chatId, Integer voteMessageId, Integer userId);
    List<BanVote> findAllByChatIdAndVoteMessageId(Long chatId, Integer voteMessageId);
}
