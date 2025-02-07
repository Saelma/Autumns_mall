package com.example.AutumnMall.Member.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.example.AutumnMall.utils.audit.Auditable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name="role")
@NoArgsConstructor
@Setter
@Getter
public class Role extends Auditable {
    @Id
    @Column(name="role_id")
    private Long roleId;

    @Column(length = 20)
    private String name;

    @Override
    public String toString() {
        return "Role{" +
                "roleId=" + roleId +
                ", name='" + name + '\'' +
                '}';
    }
}