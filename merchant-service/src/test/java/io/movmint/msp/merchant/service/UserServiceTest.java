package io.movmint.msp.merchant.service;

import io.movmint.msp.merchant.MerchantApplicationTests;
import io.movmint.msp.merchant.TestUtil;
import io.movmint.msp.merchant.api.v1.model.request.UserRequest;
import io.movmint.msp.merchant.data.entity.User;
import io.movmint.msp.merchant.data.enums.UserStatus;
import io.movmint.msp.merchant.exception.DuplicateEntryException;
import io.movmint.msp.merchant.exception.MerchantServiceException;
import io.movmint.msp.merchant.utils.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest extends MerchantApplicationTests {
    @Autowired
    private UserService userService;

    @BeforeEach
    public void setUp() throws Exception {
        openMocks(this);
    }

    @Test
    @DisplayName(
            "Create User : Success if Given user object to save in mongo db")
    public void createUser_Success() throws MerchantServiceException {
        UserRequest request = TestUtil.createUserRequestForTesting();
        String password = PasswordUtil.hashPassword(request.password());
        User user = User.builder().
                id("663b4b192260bb2b7842b239").
                name(request.name()).
                phone(request.phone()).
                status(UserStatus.DEACTIVE).
                role(request.role()).
                email(request.email()).
                password(password).
                isEmailConfirmed(false).
                build();


        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.existsByPhone(request.phone())).thenReturn(false);
        User response = userService.create(request);

        assertThat(response.getName()).isEqualTo(request.name());
        assertThat(response.getStatus()).isEqualTo(UserStatus.DEACTIVE);
    }

    @Test
    @DisplayName("Create User : Fail when existing user email insert")
    public void createUser_duplicate_email_fail() throws MerchantServiceException {
        UserRequest request = TestUtil.createUserRequestForTesting();
        when(userRepository.existsByEmail(request.email())).thenReturn(true);
        when(userRepository.existsByPhone(request.phone())).thenReturn(false);
        assertThatThrownBy(() -> userService.create(request)).isInstanceOf(DuplicateEntryException.class);
    }

    @Test
    @DisplayName("Create User : Fail when existing user phone insert")
    public void createUser_duplicate_phone_fail() throws MerchantServiceException {
        UserRequest request = TestUtil.createUserRequestForTesting();
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.existsByPhone(request.phone())).thenReturn(true);
        assertThatThrownBy(() -> userService.create(request)).isInstanceOf(DuplicateEntryException.class);
    }

}
