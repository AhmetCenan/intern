package com.sau.intern.dto;

import com.sau.intern.model.StockDeviceReason;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StockDemand {
    private List<StockDeviceReason> deviceReasonList;
    private String from;
    private String to;
    private boolean sure;
}
