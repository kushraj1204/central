package com.kush.shaihulud.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name="audit_table")
@SequenceGenerator(name = "audit_table_audit_id_seq", sequenceName = "audit_table_audit_id_seq",
        allocationSize = 1)
public class AuditDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "audit_table_audit_id_seq")
    @Column(name = "audit_id")

    private Integer auditId;

    @Column(name = "rec_date")
    @Temporal(TemporalType.DATE)
    private Date recDate;

    @Column(name = "event_flg", length = 1)
    private String eventFlg;

    @Column(name = "code", length = 20)
    private String code;

    @Column(name = "cust_id", length = 9)
    private String custId;

    @Column(name = "mobile_no", length = 10)
    private String mobileNo;

    @Column(name = "table_key", length = 50)
    private String tableKey;

    @Column(name = "modified_field", length = 200)
    private String modifiedField;

    @Column(name = "audit_bank_id", length = 20)
    private String auditBankId;

    @Column(name = "audit_branch_id", length = 20)
    private String auditBranchId;

    @Column(name = "rcre_user_id", length = 20)
    private String rcreUserId;

    @Column(name = "rcre_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date rcreTime;

    @Column(name = "auth_id", length = 20)
    private String authId;

    @Column(name = "auth_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date authTime;

    @Column(name = "entity_cre_flg", length = 1)
    private String entityCreFlg;


}
