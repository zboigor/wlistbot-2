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
@Table(name = "spam_trigger")
@Accessors(chain = true)
public class SpamTrigger {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "spam_trigger_id_seq")
    @SequenceGenerator(name = "spam_trigger_id_seq", sequenceName = "spam_trigger_id_seq", allocationSize = 1)
    private Long id;

    private Long chatId;
    private String triggerText;
    private Boolean isGlobal;
}