package com.antmillion.history.mapper;

import java.math.BigDecimal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.antmillion.history.dto.BiasAlertDTO;

/**
 * 매매 편향 경고 관련 Mapper
 */
@Mapper
public interface BiasAlertMapper {
    
    /**
     * 특정 종목의 보유 정보 조회 (편향 체크용)
     * @param accountId 계좌 ID
     * @param stockCode 종목 코드
     * @return 편향 경고 정보
     */
    BiasAlertDTO selectAssetForBiasCheck(
        @Param("accountId") Long accountId, 
        @Param("stockCode") String stockCode
    );
    
    /**
     * 특정 종목의 현재가 조회 (임시 하드코딩)
     * @param stockCode 종목 코드
     * @return 현재가
     */
    BigDecimal selectCurrentPrice(@Param("stockCode") String stockCode);
}
