package com.sau.intern.dto;

import com.sau.intern.enums.StockStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockSearchDto {
    private StockStatus stockStatus;
    private Instant startDate = Instant.ofEpochMilli(0);
    private Instant endDate = Instant.now();

}
