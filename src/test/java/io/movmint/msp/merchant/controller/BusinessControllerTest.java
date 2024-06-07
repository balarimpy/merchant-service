package io.movmint.msp.merchant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.movmint.msp.merchant.MerchantApplicationTests;
import io.movmint.msp.merchant.TestUtil;
import io.movmint.msp.merchant.api.v1.BusinessController;
import io.movmint.msp.merchant.api.v1.model.request.AccountReq;
import io.movmint.msp.merchant.api.v1.model.request.BusinessRequest;
import io.movmint.msp.merchant.data.entity.Account;
import io.movmint.msp.merchant.data.entity.AccountMetadata;
import io.movmint.msp.merchant.data.entity.Business;
import io.movmint.msp.merchant.data.enums.BusinessStatus;
import io.movmint.msp.merchant.data.enums.BusinessType;
import io.movmint.msp.merchant.data.enums.SDWalletStatus;
import io.movmint.msp.merchant.service.BusinessService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BusinessControllerTest extends MerchantApplicationTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BusinessController businessController;
    @Autowired
    private BusinessService businessService;
    static HttpHeaders httpHeaders = new HttpHeaders();
    public static UUID WSP_ID = UUID.randomUUID();

    @Test
    public void contextLoads() throws Exception {
        assertThat(businessController).isNotNull();
    }

    @BeforeEach
    public void setUp() throws Exception {
        openMocks(this);
    }

    @Test
    @DisplayName("POST /business : Success if Given business object to save in mongo db")
    public void createBusiness_Success() throws Exception {
        BusinessRequest request = TestUtil.createHqWalletForTesting(WSP_ID, UUID.randomUUID());

        Business business =
                Business.builder()
                        .name(request.name())
                        .wspId(WSP_ID)
                        .status(request.status())
                        .type(request.type())
                        .account(
                                Account.builder()
                                        .agentId(request.account().agentId())
                                        .accountName(request.account().accountName())
                                        .accountNo(request.account().accountNo())
                                        .customName(request.account().customName())
                                        .walletLimit(request.account().walletLimit())
                                        .walletLimitId(request.account().walletLimitId())
                                        .status(SDWalletStatus.PENDING_VERIFICATION)
                                        .metadata(new AccountMetadata(
                                                request.account().metadata().district(),
                                                request.account().metadata().industry()
                                        ))
                                        .build())
                        .build();

        when(businessRepository.save(any(Business.class))).thenReturn(business);

        ResultActions response = mockMvc.perform(post("/business")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .headers(httpHeaders));
        response.andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /business : Fail when existing business name post")
    public void createBusiness_duplicate_fail() throws Exception {
        BusinessRequest request = TestUtil.createHqWalletForTesting(WSP_ID, UUID.randomUUID());
        when(businessRepository.existsByName("Test Business")).thenReturn(true);
        ResultActions response =
                mockMvc.perform(
                        post("/business")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .headers(httpHeaders));
        response.andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        response.andExpect(jsonPath("$.code").value("MVC-0007"));
    }

    @Test
    @DisplayName("POST /business : Fail when business name is empty")
    public void createBusiness_empty_field_fail() throws Exception {
        BusinessRequest request = new BusinessRequest(
                "",
                BusinessType.SIMPLE,
                BusinessStatus.OFFBOARDED,
                WSP_ID,
                new AccountReq(
                        "example-accountNo-456",
                        "example-accountName",
                        "example-customName",
                        UUID.randomUUID(), null, null, null));
        ResultActions response =
                mockMvc.perform(
                        post("/business")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .headers(httpHeaders));
        response.andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        response.andExpect(jsonPath("$.code").value("MVC-0003"));
    }
}
