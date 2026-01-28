package com.antmillion.stock.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SyncResult {
    private int added;
    private int deleted;
}
