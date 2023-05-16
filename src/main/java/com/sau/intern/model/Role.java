package com.sau.intern.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "role")
@Getter
@Setter
@Where(clause = "is_deleted is not true")
@SQLDelete(sql = "update role set is_deleted = true where id = ?")
@NoArgsConstructor
public class Role extends BaseEntity{

    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "role", orphanRemoval = true)
    @JsonManagedReference
    private Set<RolePermission> permissionList = new HashSet<>();

}
