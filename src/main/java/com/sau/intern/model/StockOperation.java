package com.sau.intern.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sau.intern.dto.StockDemand;
import com.sau.intern.enums.StockStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.Instant;
import java.util.List;

import static com.sau.intern.enums.StockStatus.APPROVED;
import static com.sau.intern.enums.StockStatus.TRANSFER_STARTED;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Where(clause = "is_deleted is not true")
@SQLDelete(sql = "UPDATE stock_operation SET is_deleted = true WHERE id = ?", check = ResultCheckStyle.COUNT)
@Audited
public class StockOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotAudited
    @OneToMany(mappedBy = "stockOperation", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<StockDeviceReason> stockDeviceReason;
    @Enumerated(EnumType.STRING)
    private StockStatus stockStatus;
    private String transferFrom;
    private String transferTo;
    private String updatedBy;
    @CreationTimestamp
    private Instant createDate;
    @JsonIgnore
    private Boolean isDeleted;

    public StockOperation(StockDemand stockDemand, User updater) {
        this.stockStatus = TRANSFER_STARTED;
        this.transferFrom = stockDemand.getFrom();
        this.transferTo = stockDemand.getTo();
        this.updatedBy = String.valueOf(updater.getId());
    }

    public void approve(User updater, List<Long> deviceIds) {
        this.stockDeviceReason.stream().filter(s -> deviceIds.contains(s.getDeviceId())).forEach(s -> s.setApproved(true));
        if (this.stockDeviceReason.stream().allMatch(StockDeviceReason::isApproved))
            this.stockStatus = APPROVED;
        this.updatedBy = String.valueOf(updater.getId());
    }

    public StockOperation setDeviceReasons(List<StockDeviceReason> deviceReasonList) {
        deviceReasonList.forEach(d -> d.setStockOperation(this));
        this.stockDeviceReason = deviceReasonList;
        return this;
    }
}