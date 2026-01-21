package com.antmillion.mypage.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.antmillion.mypage.mapper.MyPageMapper;

@Service
public class MyPageServiceImpl implements MyPageService {

    @Autowired
    private MyPageMapper myPageMapper;

    @Override
    public List<Map<String, Object>> getStockHoldings(Long accountId) {
        return myPageMapper.selectStockHoldings(accountId);
    }
}