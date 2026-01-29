package com.antmillion.stock.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SyncResult {
    private int beforeCount;
    private int afterCount;
    private int insertedCount;
    private int deletedCount;
    private int totalDownloaded;
}
