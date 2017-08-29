package com.zboigor.model;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.sql.Date;

/**
 * @author sss3 (Vladimir Aleexeev)
 */
@Data
@Entity
@Table(name = "activity_audit")
@Accessors(chain = true)
public class ActivityAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ban_votes_id_seq")
    @SequenceGenerator(name = "ban_votes_id_seq", sequenceName = "ban_votes_id_seq", allocationSize = 1)
    private Long id;

    private Long chatId;
    private Integer userId;
    private Long count;
    private Date date;

}
