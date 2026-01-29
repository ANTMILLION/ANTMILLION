package com.antmillion.kis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForeignerOrganization {
	@JsonProperty("frgn_fake_ntby_qty")
    private String foreignNetQty; // 외국인수량(가집계)
    
    @JsonProperty("orgn_fake_ntby_qty")
    private String organizationNetQty;   // 기관수량(가집계)
    
    @JsonProperty("sum_fake_ntby_qty")
    private String sumFrgnOrgnNetQty;   // 합산수량(가집계)

}
