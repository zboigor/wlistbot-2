package com.zboigor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Igor Zboichik
 * @since 2017-08-30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VotingPK implements Serializable {

    private Long chatId;
    private Integer messageId;
}
