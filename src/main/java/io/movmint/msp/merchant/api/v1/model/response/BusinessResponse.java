package io.movmint.msp.merchant.api.v1.model.response;

import io.movmint.msp.merchant.data.entity.Business;
import io.movmint.msp.merchant.data.enums.BusinessStatus;
import io.movmint.msp.merchant.data.enums.BusinessType;
import java.util.UUID;
import lombok.Getter;

@Getter
public class BusinessResponse {
    private String id;
    private String name;
    private BusinessType type;
    private BusinessStatus status;
    private UUID wspId;

    public static BusinessResponse create(final Business business) {
        BusinessResponse response = new BusinessResponse();
        if (business != null) {
            response.id = business.getId();
            response.name = business.getName();
            response.status = business.getStatus();
            response.type = business.getType();
            response.wspId = business.getWspId();
        }
        return response;
    }
}
