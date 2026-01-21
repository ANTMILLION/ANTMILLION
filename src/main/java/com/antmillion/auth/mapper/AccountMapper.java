package com.antmillion.auth.mapper;

import org.apache.ibatis.annotations.Param;
import com.antmillion.user.dto.AccountDTO;

public interface AccountMapper {

    int insertAccount(AccountDTO dto);
    
    AccountDTO selectByUserId(@Param("userId") Long userId);

    AccountDTO selectByAccountId(@Param("accountId") Long accountId);

    int updateBalance(@Param("accountId") Long accountId,
                      @Param("balance") Long balance);
}