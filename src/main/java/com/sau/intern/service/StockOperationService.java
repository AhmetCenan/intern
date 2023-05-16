package com.sau.intern.service;

import com.sau.intern.dto.*;
import com.sau.intern.model.StockDeviceReason;
import com.sau.intern.model.StockOperation;
import com.sau.intern.model.User;
import com.sau.intern.repository.StockOperationRepository;
import com.sau.intern.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockOperationService {

    private final StockOperationRepository stockOperationRepository;
    private final UserRepository userRepository;


    public StockOperationResponse getById(Long id) {
        StockOperation stockOperation = stockOperationRepository.findById(id).orElseThrow(() -> new RuntimeException());
        return convertToResponse(stockOperation);
    }

    public List<StockOperationResponse> search(StockSearchDto search) {
        checkDatesIsNull(search);
        return stockOperationRepository.search(search).stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public PageableResponse<StockOperationResponse.StockDeviceReasonDto> searchDevices(StockSearchDto searchDto) {
        checkDatesIsNull(searchDto);
        List<StockDeviceReason> sdr = stockOperationRepository.searchDevices(searchDto);
        List<Long> deviceIds = sdr.stream().map(StockDeviceReason::getDeviceId).collect(Collectors.toList());
        List<StockOperationResponse.StockDeviceReasonDto> result = sdr.stream().map(s -> new StockOperationResponse.StockDeviceReasonDto(s, deviceIds)).collect(Collectors.toList());
        return new PageableResponse<>(result.size(), result);
    }

    private void checkDatesIsNull(StockSearchDto search) {
        if (Objects.isNull(search.getStartDate())) {
            search.setStartDate(Instant.ofEpochMilli(0));
        }
        if (Objects.isNull(search.getEndDate())) {
            search.setEndDate(Instant.now());
        }
    }

    public StockDemand transferDevice(StockDemand stockDemand, Principal principal) {
        var deviceList = stockDemand.getDeviceReasonList().stream().map(StockDeviceReason::getDeviceId).collect(Collectors.toList());
        List<Map<Long, String>> errorImeiList = new ArrayList<>();
        deviceList.forEach(device -> {
            try {
                checkDeviceLocationAndStockStatus(stockDemand.getFrom(), stockDemand.getTo(), device);
            } catch (RuntimeException e) {
                errorImeiList.add(Map.of(device, e.getMessage()));
                stockDemand.getDeviceReasonList().removeIf(stockDeviceReason -> stockDeviceReason.getDeviceId().equals(device));
            }
        });
        deviceList = deviceList
                .stream()
                .filter(device -> errorImeiList.stream().noneMatch(errorImei -> errorImei.containsKey(device)))
                .collect(Collectors.toList());
        if (errorImeiList.isEmpty() || stockDemand.isSure())
            return sendDevices(stockDemand, deviceList, principal);
        throw new RuntimeException(errorImeiList.toString());
    }

    public StockDemand sendDevices(StockDemand stockDemand, List<Long> deviceList, Principal principal) {
        if (deviceList.isEmpty())
            throw new RuntimeException();
        User user = userRepository.getById(Long.parseLong(principal.getName()));
        StockOperation stockOperation = new StockOperation(stockDemand, user);
        stockOperationRepository.save(stockOperation);
        stockOperationRepository.save(stockOperation.setDeviceReasons(stockDemand.getDeviceReasonList()));
        return stockDemand;
    }

    public List<StockApproveRequest> approveDevices(List<StockApproveRequest> approveRequest, Principal principal) {
        User user = userRepository.getById(Long.parseLong(principal.getName()));
        checkDevices(approveRequest);
        approveRequest.forEach(request -> {
            StockOperation stockOperation = stockOperationRepository.findById(request.getStockId()).orElseThrow(() -> new RuntimeException());
            stockOperation.approve(user, request.getDeviceIds());
            stockOperationRepository.save(stockOperation);
        });
        return approveRequest;
    }

    private StockOperationResponse convertToResponse(StockOperation stockOperation) {
        List<Long> deviceIds = stockOperation.getStockDeviceReason().stream().map(StockDeviceReason::getDeviceId).collect(Collectors.toList());
        return new StockOperationResponse(stockOperation, deviceIds);
    }

    private void checkDeviceLocationAndStockStatus(String from, String to, Long device) {
        //Cihazın lokasyon kontrolleri
    }

    private void checkDevices(List<StockApproveRequest> requests) {
        requests.forEach(request -> {
            StockOperation stockOperation = stockOperationRepository.findById(request.getStockId()).orElseThrow(() -> new RuntimeException());
            if (stockOperation.getStockDeviceReason().stream()
                    .filter(s -> !s.isApproved())
                    .map(StockDeviceReason::getDeviceId).noneMatch(request.getDeviceIds()::contains))
                throw new RuntimeException(Map.of(stockOperation.getId(), request.getDeviceIds()).toString());
        });
    }
}
