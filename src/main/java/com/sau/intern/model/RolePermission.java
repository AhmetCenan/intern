package com.sau.intern.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "role_permission")
@Getter
@Setter
@Where(clause = "is_deleted is not true")
@SQLDelete(sql = "update role_permission set is_deleted = true where id = ?")
@NoArgsConstructor
public class RolePermission extends BaseEntity{

    private String name;

    @ManyToOne
    @JoinColumn(name = "role_id")
    @JsonBackReference
    private Role role;

}
