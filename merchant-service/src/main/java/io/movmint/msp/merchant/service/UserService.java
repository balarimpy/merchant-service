package io.movmint.msp.merchant.service;

import io.movmint.msp.merchant.api.v1.model.request.UserRequest;
import io.movmint.msp.merchant.data.entity.User;
import io.movmint.msp.merchant.exception.MerchantServiceException;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User create(UserRequest userRequest) throws MerchantServiceException;

    Object getAssignedVirtualWallets(String userId) throws MerchantServiceException;

    Object getAllVirtualWallet(String userId) throws MerchantServiceException;;

    Optional<User> getUserById(String userId);
}
