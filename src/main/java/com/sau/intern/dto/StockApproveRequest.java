package com.sau.intern.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StockApproveRequest {
    private Long stockId;
    private List<Long> deviceIds;
}
