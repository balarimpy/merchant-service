package io.movmint.msp.merchant.service;

import io.movmint.msp.merchant.api.v1.model.request.UserRequest;
import io.movmint.msp.merchant.data.entity.User;
import io.movmint.msp.merchant.data.enums.UserRole;
import io.movmint.msp.merchant.data.enums.UserStatus;
import io.movmint.msp.merchant.data.repository.UserRepository;
import io.movmint.msp.merchant.exception.DuplicateEntryException;
import io.movmint.msp.merchant.exception.ExternalServiceCallException;
import io.movmint.msp.merchant.exception.MerchantServiceException;
import io.movmint.msp.merchant.exception.NotFoundException;
import io.movmint.msp.merchant.exception.UnauthorizedException;
import io.movmint.msp.merchant.utils.PasswordUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Log4j2
@Service
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    @Value("${virtual-wallet-service.url.get.assigned-wallets}")
    private String getAssignedWalletsUrl;
    @Value("${virtual-wallet-service.url.get.all-virtual-wallets}")
    private String getAllWalletsUrl;;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }

    /**
     * Create User details and assign them roles.
     * @since : 2024-05-08
     *
     * @param userRequest {@link UserRequest}
     * @return User {@link User} with the created User details if success,
     * @throws MerchantServiceException based on the validation.
     */
    @Transactional
    public User create(UserRequest userRequest) throws MerchantServiceException {
        User user = createUserFactory(userRequest);
        //To check if a user email already exists
        if(userRepository.existsByEmail(userRequest.email())){
            log.error("email [%s] already exists ", userRequest.email());
            throw new DuplicateEntryException("email");
        }
        //To check if a user phone already exists
        if(userRepository.existsByPhone(userRequest.phone())){
            log.error("phone [%s] already exists ", userRequest.phone());
            throw new DuplicateEntryException("phone");
        }
        user.setStatus(UserStatus.DEACTIVE);
        user.setEmailConfirmed(false);
        //hashing the password and set to entity
        user.setPassword(PasswordUtil.hashPassword(userRequest.password()));
        user.setToken(PasswordUtil.generateToken());
        log.info("saving user " + userRequest.name());
        User sevedUser = userRepository.save(user);

        return sevedUser;
    }

    /**
     * Convert user request record to User entity.
     * @since : 2024-05-08
     *
     * @param userRequest {@link UserRequest}
     * @return User {@link User}
     */
    private User createUserFactory(UserRequest userRequest) {
        if (userRequest == null) {
            return null;
        }
        User user = User.builder()
                .name(userRequest.name())
                .email(userRequest.email())
                .role(userRequest.role())
                .phone(userRequest.phone())
                .build();


        return user;
    }

    /**
     * Get assigned virtual wallets from virtual wallet service.
     * @since : 2024-05-10
     *
     * @param userId
     * @return list of Object.
     * @throws MerchantServiceException based on the validation.
     */
    @Override
    public Object getAssignedVirtualWallets(String userId) throws MerchantServiceException {
        if(!userRepository.existsById(userId)){
            log.error("user id [%s] already exists ", userId);
            throw new NotFoundException("[%s] id user".formatted(userId));
        }
        try{
            //send the request to virtual wallet service
            ResponseEntity<Object> response=restTemplate.getForEntity(getAssignedWalletsUrl,Object.class, Map.of("userId",userId));
            if(response.getStatusCode().is2xxSuccessful())
                return response.getBody();
            log.error("request fail with virtual wallet service : %s".formatted(response.getBody()));
            throw new  ExternalServiceCallException("fetching all assigned wallets failed");
        }catch (Exception e){
            log.error(e.getMessage());
            throw new ExternalServiceCallException("VWS connection failed");
        }
    }

    /**
     * Get all users and their VW information from VWS.
     * @since : 2024-05-10
     *
     * @param userId
     * @return list of Object.
     * @throws MerchantServiceException based on the validation.
     */
    @Override
    public Object getAllVirtualWallet(String userId) throws MerchantServiceException {
        User user = userRepository.findById(userId).orElseThrow(
                ()-> new NotFoundException("[%s] id user".formatted(userId))
        );
        if(!user.getRole().equals(UserRole.MANAGER)){
            log.error("This action can only be carried out by a Manager or expected role is Manager, but found [%s]", user.getRole());
            throw new UnauthorizedException();
        }
        try{
            //send the request to virtual wallet service
            ResponseEntity<Object> response=restTemplate.getForEntity(getAllWalletsUrl,Object.class, Map.of("userId",userId));
            if(response.getStatusCode().is2xxSuccessful())
                return response.getBody();
            log.error("request fail with virtual wallet service : %s".formatted(response.getBody()));
            throw new  ExternalServiceCallException("fetching all wallets failed");
        }catch (Exception e){
            log.error(e.getMessage());
            throw new ExternalServiceCallException("VWS connection failed");
        }
    }

    /**
     * Get user by user id.
     * @since : 2024-05-15
     *
     * @param userId
     * @return Optional User Object.
     */
    @Override
    public Optional<User> getUserById(String userId) {
        return userRepository.findById(userId);
    }
}
