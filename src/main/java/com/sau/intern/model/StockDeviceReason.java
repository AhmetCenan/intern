package com.sau.intern.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "stock_device_reason", uniqueConstraints = {
        @UniqueConstraint(name = "deviceIdStockOpr", columnNames = {"deviceId", "stock_operation_id"})
})
public class StockDeviceReason {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    private Long deviceId;
    private String reason;
    private String description;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonBackReference
    private StockOperation stockOperation;
    private boolean approved;
}
