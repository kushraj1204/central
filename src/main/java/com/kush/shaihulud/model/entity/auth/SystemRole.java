package com.kush.shaihulud.model.entity.auth;

import com.kush.shaihulud.model.request.UpdateRoleRequest;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "nchlcntrl_system_roles")
public class SystemRole extends com.kush.shaihulud.model.entity.metainfo.AbstractCorePersistableEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "code", nullable = false,unique = true)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;


    @Column(name = "action_scope")
    private String actionScope;


    @Type(JsonBinaryType.class)
    @Column(name = "action_code", columnDefinition = "jsonb")
    private Set<String> actionCode;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "del_flg")
    private boolean delFlg;

    @Column(name = "entity_cre_flg")
    private boolean entityCreFlg;

    @Column(name = "entity_cre_user_id", length = 20)
    private String entityCreUserId;

    @Column(name = "entity_cre_time")
    private LocalDateTime entityCreTime;

    @Type(JsonBinaryType.class)
    @Column(name = "update_obj", columnDefinition = "jsonb")
    private UpdateRoleRequest updateObj;

}
