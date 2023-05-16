package com.sau.intern.repository;

import com.sau.intern.dto.StockSearchDto;
import com.sau.intern.model.StockDeviceReason;
import com.sau.intern.model.StockOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StockOperationRepository extends JpaRepository<StockOperation, Long> {

    @Query("select sdr from StockDeviceReason sdr join sdr.stockOperation s where " +
            "(:#{#search.stockStatus} is null or (s.stockStatus = :#{#search.stockStatus})) and " +
            "(s.createDate between :#{#search.startDate} and :#{#search.endDate}) order by s.createDate")
    List<StockDeviceReason> searchDevices(@Param("search") StockSearchDto searchDto);
    @Query("select distinct(s) from StockOperation s join s.stockDeviceReason sdr where " +
            "(:#{#search.stockStatus} is null or (s.stockStatus = :#{#search.stockStatus})) and " +
            "(s.createDate between :#{#search.startDate} and :#{#search.endDate}) order by s.createDate ")
    List<StockOperation> search(@Param("search") StockSearchDto search);
}