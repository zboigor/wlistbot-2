package com.zboigor.model;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author Igor Zboichik
 * @since 2017-08-03
 */
@Data
@Entity
@Table(name = "ban_votes")
@Accessors(chain = true)
public class BanVote {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ban_votes_id_seq")
    @SequenceGenerator(name = "ban_votes_id_seq", sequenceName = "ban_votes_id_seq", allocationSize = 1)
    private Long id;

    private Long chatId;
    private Integer voteMessageId;
    private Integer userId;
    private Boolean ban;
}
