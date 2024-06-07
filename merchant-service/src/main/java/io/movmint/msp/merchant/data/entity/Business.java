package io.movmint.msp.merchant.data.entity;

import io.movmint.msp.merchant.data.enums.BusinessStatus;
import io.movmint.msp.merchant.data.enums.BusinessType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Business extends BaseEntity {
    private String name;
    private BusinessType type;
    private BusinessStatus status;
    private UUID wspId;
    private Account account;
}
