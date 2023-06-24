package com.nchl.central.model.entity.auth;


import com.nchl.central.model.enums.SystemActionScope;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "nchlcntrl_system_actions")
public class SystemAction implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "label", nullable = false)
    private String label;

    @Column(name = "short_label", nullable = false)
    private String shortLabel;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_scope", nullable = false)
    private SystemActionScope actionScope;


}
