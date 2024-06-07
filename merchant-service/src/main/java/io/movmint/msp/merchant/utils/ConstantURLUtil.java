package io.movmint.msp.merchant.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


/*
* All the third party urls that the middleware need to call will be defined in this class
* */
@Service
public final class ConstantURLUtil {

    private static String baseUrl;

    public static String VW_Batch;
    @Autowired
    public ConstantURLUtil(@Value("${virtual-wallet-service.domain}") String baseUrl) {
        ConstantURLUtil.baseUrl = baseUrl;
        VW_Batch = baseUrl + "virtual-wallet/batch";
    }
}

