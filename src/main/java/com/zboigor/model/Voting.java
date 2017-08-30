package com.zboigor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author Igor Zboichik
 * @since 2017-08-30
 */
@Data
@Table(name = "voting")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Voting implements Serializable {

    @EmbeddedId
    private VotingPK id;

    private Integer initiatorId;
    private String initiatorName;
}
