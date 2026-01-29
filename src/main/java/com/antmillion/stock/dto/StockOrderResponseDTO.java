package com.antmillion.stock.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class StockOrderResponseDTO extends StockOrderDTO {
    private Integer executedQuantity; // 지금까지 체결 완료된 총 수량
    private Integer remainedQuantity; // 남은 미체결량 (quantity - executedQuantity)

    // 미체결 수량 반환 로직
    public int getUnexecutedQuantity() {
        int total = (this.getQuantity() != null) ? this.getQuantity() : 0;
        int exec = (this.executedQuantity != null) ? this.executedQuantity : 0;
        return total - exec;
    }
}
