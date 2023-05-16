package com.sau.intern.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    private boolean isDeleted;

    @CreatedDate
    @JsonIgnore
    private Instant createdDate;

    @CreatedBy
    @JsonIgnore
    private Long creator;

    @LastModifiedDate
    @JsonIgnore
    private Instant updatedDate;

    @LastModifiedBy
    @JsonIgnore
    private Long updater;
}
