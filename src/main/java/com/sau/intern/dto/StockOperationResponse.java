package com.sau.intern.dto;

import com.sau.intern.enums.StockStatus;
import com.sau.intern.model.StockDeviceReason;
import com.sau.intern.model.StockOperation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class StockOperationResponse {
    private Long id;
    private List<StockDeviceReasonDto> stockDeviceReason;
    private StockStatus stockStatus;
    private String transferFrom;
    private String transferTo;
    private String updatedBy;
    private Instant createDate;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class StockDeviceReasonDto {
        private Long device;
        private Long stockOperationId;
        private String reason;
        private String description;
        private boolean approved;

        public StockDeviceReasonDto(StockDeviceReason stockDeviceReason, List<Long> deviceDtoList) {
            this.stockOperationId = stockDeviceReason.getStockOperation().getId();
            this.reason = stockDeviceReason.getReason();
            this.description = stockDeviceReason.getDescription();
            this.approved = stockDeviceReason.isApproved();
            this.device = deviceDtoList.stream().filter(d -> d.equals(stockDeviceReason.getDeviceId()))
                    .findFirst().orElseThrow(() -> new RuntimeException());
        }
    }

    public StockOperationResponse(StockOperation stockOperation, List<Long> deviceDtoList) {
        this.id = stockOperation.getId();
        this.stockStatus = stockOperation.getStockStatus();
        this.transferFrom = stockOperation.getTransferFrom();
        this.transferTo = stockOperation.getTransferTo();
        this.updatedBy = stockOperation.getUpdatedBy();
        this.createDate = stockOperation.getCreateDate();
        this.stockDeviceReason = stockOperation.getStockDeviceReason().stream().map(s -> new StockDeviceReasonDto(s, deviceDtoList)).collect(Collectors.toList());
    }
}