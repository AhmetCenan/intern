package com.sau.intern.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Setter
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = {"email"}))
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "is_deleted is not true")
@SQLDelete(sql = "update users set is_deleted = true where id = ?")
public class User extends BaseEntity{

    private String name;

    private String surName;

    private String email;

    private String password;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
}