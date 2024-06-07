package io.movmint.msp.merchant.api.v1;

import io.movmint.msp.merchant.api.ApiHeader;
import io.movmint.msp.merchant.api.v1.model.request.UserRequest;
import io.movmint.msp.merchant.api.v1.model.response.UserResponse;
import io.movmint.msp.merchant.exception.MerchantServiceException;
import io.movmint.msp.merchant.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * User Controller.
 */
@Log4j2
@RestController
@RestControllerAdvice
@RequestMapping(
        value = "/user",
        method = {RequestMethod.POST, RequestMethod.GET, RequestMethod.DELETE}
)
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * POST /user
     * Create User details and assign them roles.
     * @since : 2024-05-08
     *
     * @param userRequest {@link UserRequest}
     * @return ResponseEntity
     * - With status code 200 (OK) and userResponse {@link UserResponse} details if the operation is successful.
     * - With status code 400 (Bad Request) or 500 (Internal Server Error) with body ErrorResponse object if there's an error.
     */
    @PostMapping()
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequest userRequest) throws MerchantServiceException {
        return ResponseEntity.ok(UserResponse.create(userService.create(userRequest)));
    }

    /**
     * GET /user/virtual-wallet
     * Get assigned virtual wallets from virtual wallet service.
     * @since : 2024-05-10
     *
     * @param userId
     * @return ResponseEntity
     * - With status code 200 (OK) and list of VirtualWalletDto.
     * - With status code 400 (Bad Request) or 500 (Internal Server Error) with body ErrorResponse object if there's an error.
     */
    @GetMapping("/virtual-wallet")
    public  ResponseEntity<?> getAssignedVirtualWallet(@NotNull @RequestHeader(ApiHeader.USER_ID) final String userId) throws MerchantServiceException {
        return ResponseEntity.ok(userService.getAssignedVirtualWallets(userId));
    }

    /**
     * GET /user/all/virtual-wallet
     * Get all users and their VW information.
     * @since : 2024-05-10
     *
     * @param userId
     * @return ResponseEntity
     * - With status code 200 (OK) and list of VirtualWalletDto.
     * - With status code 400 (Bad Request) or 500 (Internal Server Error) with body ErrorResponse object if there's an error.
     */
    @GetMapping("/all/virtual-wallet")
    public  ResponseEntity<?> getAllVirtualWallet(@NotNull @RequestHeader(ApiHeader.USER_ID) final String userId) throws MerchantServiceException {
        return ResponseEntity.ok(userService.getAllVirtualWallet(userId));
    }
}
