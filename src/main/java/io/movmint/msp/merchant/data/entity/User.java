package io.movmint.msp.merchant.data.entity;

import io.movmint.msp.merchant.data.enums.UserRole;
import io.movmint.msp.merchant.data.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@SuperBuilder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document
public class User extends BaseEntity {
    @Field(name = "name")
    private String name;
    @Field(name = "phone")
    private String phone;
    @Field(name = "email")
    private String email;
    @Field(name = "password")
    private String password;
    @Field(name = "status")
    private UserStatus status;
    @Field(name = "role")
    private UserRole role;
    @Field(name = "isEmailConfirmed")
    private boolean isEmailConfirmed;
    @Field(name = "token")
    private String token;
}
