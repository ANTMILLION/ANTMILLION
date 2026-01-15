package com.antmillion.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AntRankDTO {
    private Integer rank_id;
    private String rank_type;
    private Integer required_point;
    private String rank_image;
}