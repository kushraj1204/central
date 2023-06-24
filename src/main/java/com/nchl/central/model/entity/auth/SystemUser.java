package com.nchl.central.model.entity.auth;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@Table(name = "user_table")
@SequenceGenerator(name = "user_table_id_seq", sequenceName = "user_table_id_seq",
        allocationSize = 1)
public class SystemUser extends com.nchl.central.model.entity.metainfo.AbstractCorePersistableEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "user_table_id_seq")
    private Long id;

    @Column(name = "emp_id", nullable = false)
    private String empId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email_id", nullable = false)
    private String emailId;

    @Column(name = "gender")
    private String gender;

    @Column(name = "role_code")
    private int roleCode;

    @Column(name = "enabled", nullable = false)
    private int enabled;

    @Column(name = "account_non_expired")
    private int accountNonExpired;

    @Column(name = "credential_non_expired")
    private int credentialNonExpired;

    @Column(name = "account_non_locked")
    private int accountNonLocked;

    @Column(name = "del_flg")
    private String delFlg;

    @Column(name = "no_of_login_attempts")
    private Integer noOfLoginAttempts;

    @Column(name = "pwd_lchg_date")
    private LocalDateTime pwdLchgDate;
    @Column(name = "entity_cre_flg", length = 1)
    private String entityCreFlg;

    @Column(name = "entity_cre_user_id", length = 20)
    private String entityCreUserId;

    @Column(name = "entity_cre_time")
    private LocalDateTime entityCreTime;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = {@JoinColumn(name = "user_id")}, inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private Set<SystemRole> systemRoles;

}
