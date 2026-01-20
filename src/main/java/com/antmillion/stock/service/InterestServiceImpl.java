package com.antmillion.stock.service;

import com.antmillion.stock.dto.InterestDTO;
import com.antmillion.stock.mapper.InterestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InterestServiceImpl implements InterestService {

    private final InterestMapper interestMapper;

    @Override
    @Transactional
    public boolean addInterest(String stockCode, Long accountId) {
        //이미 존재하는지 확인
        if (isInterest(stockCode, accountId)) {
            return false;
        }

        InterestDTO interestDTO = InterestDTO.builder()
                .stockCode(stockCode)
                .accountId(accountId)
                .build();

        return interestMapper.insertInterest(interestDTO) > 0;
    }

    @Override
    @Transactional
    public boolean removeInterest(String stockCode, Long accountId) {
        return interestMapper.deleteInterest(stockCode, accountId) > 0;
    }

    @Override
    @Transactional
    public boolean toggleInterest(String stockCode, Long accountId) {
        if (isInterest(stockCode, accountId)) {
            return removeInterest(stockCode, accountId);
        } else {
            return addInterest(stockCode, accountId);
        }
    }

    @Override
    public List<String> getInterestStockCodes(Long accountId) {
        return interestMapper.selectInterestStockCodesByAccountId(accountId);
    }

    @Override
    public boolean isInterest(String stockCode, Long accountId) {
        return interestMapper.countInterest(stockCode, accountId) > 0;
    }
}
