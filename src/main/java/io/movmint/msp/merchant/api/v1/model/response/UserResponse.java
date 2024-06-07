package io.movmint.msp.merchant.api.v1.model.response;

import io.movmint.msp.merchant.data.entity.User;
import io.movmint.msp.merchant.data.enums.UserRole;
import io.movmint.msp.merchant.data.enums.UserStatus;
import lombok.Getter;

@Getter
public class UserResponse {
    private String id;
    private String name;
    private String phone;
    private String email;
    private UserStatus status;
    private UserRole role;
    private boolean isEmailConfirmed;

    public static UserResponse create(final User user) {
        UserResponse response = new UserResponse();
        if (user != null) {
            response.id = user.getId();
            response.name = user.getName();
            response.status = user.getStatus();
            response.phone = user.getPhone();
            response.email = user.getEmail();
            response.role = user.getRole();
            response.isEmailConfirmed = user.isEmailConfirmed();
        }
        return response;
    }
}
