package io.movmint.msp.merchant.data.entity;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public class BaseEntity implements Serializable {
    @Id
    @Builder.Default
    private String id = UUID.randomUUID().toString();
    @CreatedDate
    @Field(name = "createdAt")
    private Instant createdAt;

    @LastModifiedDate
    @Builder.Default
    @Field(name = "updatedAt")
    private Instant updatedAt = Instant.now();

    @PrePersist
    public void setCreatedAt() {
        createdAt = Instant.now();
    }
    @PreUpdate
    public void setUpdatedAt() {
        updatedAt = Instant.now();
    }


}