package com.kush.shaihulud.model.entity.metainfo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractCorePersistableEntity implements Serializable {

    @CreatedDate
    @Column(name = "rcre_time", updatable=false)
    LocalDateTime created;

    @LastModifiedDate
    @Column(name = "lchg_time",insertable=false)
    LocalDateTime lastModified;

    @CreatedBy
    @AttributeOverride(name = "username", column = @Column(name = "rcre_user_id", updatable=false))
    @Embedded
    com.kush.shaihulud.model.entity.metainfo.SystemUsername createdBy;

    @LastModifiedBy
    @AttributeOverride(name = "username", column = @Column(name = "lchg_user_id",insertable=false))
    @Embedded
    com.kush.shaihulud.model.entity.metainfo.SystemUsername lastModifiedBy;


}
