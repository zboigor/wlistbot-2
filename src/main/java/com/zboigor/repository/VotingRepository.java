package com.zboigor.repository;

import com.zboigor.model.Voting;
import com.zboigor.model.VotingPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Igor Zboichik
 * @since 2017-08-30
 */
@Repository
public interface VotingRepository extends JpaRepository<Voting, VotingPK> {

    Optional<Voting> findById_ChatIdAndId_MessageId(Long chatId, Integer messageId);
}
