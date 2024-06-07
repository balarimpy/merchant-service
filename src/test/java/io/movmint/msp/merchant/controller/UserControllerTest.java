package io.movmint.msp.merchant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.movmint.msp.merchant.MerchantApplicationTests;
import io.movmint.msp.merchant.TestUtil;
import io.movmint.msp.merchant.api.v1.UserController;
import io.movmint.msp.merchant.api.v1.model.request.UserRequest;
import io.movmint.msp.merchant.data.entity.User;
import io.movmint.msp.merchant.data.enums.UserRole;
import io.movmint.msp.merchant.data.enums.UserStatus;
import io.movmint.msp.merchant.service.UserService;
import io.movmint.msp.merchant.utils.PasswordUtil;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest extends MerchantApplicationTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserController userController;
    @Autowired
    private UserService userService;
    static HttpHeaders httpHeaders = new HttpHeaders();

    @Test
    public void contextLoads() throws Exception {
        assertThat(userController).isNotNull();
    }

    @BeforeEach
    public void setUp() throws Exception {
        openMocks(this);
    }

    @Test
    @DisplayName("POST /user : Success if Given user object to save in mongo db")
    public void createUser_Success() throws Exception {
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

        ResultActions response = mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .headers(httpHeaders));
        response.andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /user : Fail when user email already exist")
    public void createBusiness_duplicate_email_fail() throws Exception {
        UserRequest request = TestUtil.createUserRequestForTesting();
        when(userRepository.existsByEmail(request.email())).thenReturn(true);
        when(userRepository.existsByPhone(request.phone())).thenReturn(false);
        ResultActions response =
                mockMvc.perform(
                        post("/user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .headers(httpHeaders));
        response.andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        response.andExpect(jsonPath("$.code").value("MVC-0007"));
    }

    @Test
    @DisplayName("POST /user : Fail when user name already exist")
    public void createBusiness_duplicate_phone_fail() throws Exception {
        UserRequest request = TestUtil.createUserRequestForTesting();
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.existsByPhone(request.phone())).thenReturn(true);
        ResultActions response =
                mockMvc.perform(
                        post("/user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .headers(httpHeaders));
        response.andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        response.andExpect(jsonPath("$.code").value("MVC-0007"));
    }

    @Test
    @DisplayName("POST /user : Fail when request required field is empty")
    public void createUser_empty_field_fail() throws Exception {
        UserRequest request = new UserRequest(
                "",
                "",
                "",
                "",
                null); //all fields are empty
        ResultActions response =
                mockMvc.perform(
                        post("/user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .headers(httpHeaders));
        response.andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        response.andExpect(jsonPath("$.code").value("MVC-0003"));
        response.andExpect(jsonPath("$.details.additionalInfo").isMap());
        response.andExpect(jsonPath("$.details.additionalInfo.*", Matchers.hasSize(5)));
    }

    @Test
    @DisplayName("POST /user : Fail when request invalid email format and password length")
    public void createUser_invalid_field_fail() throws Exception {
        UserRequest request = new UserRequest(
                "movmint user",
                "123456789",
                "testusermovmint.com",// wring email id
                "123456",   // less than 8 characters
                UserRole.MANAGER
        );
        ResultActions response =
                mockMvc.perform(
                        post("/user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .headers(httpHeaders));
        response.andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        response.andExpect(jsonPath("$.code").value("MVC-0003"));
        response.andExpect(jsonPath("$.details.additionalInfo").isMap());
        response.andExpect(jsonPath("$.details.additionalInfo.*", Matchers.hasSize(2)));
    }
}
