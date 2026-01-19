package com.antmillion.auth.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter@Setter
@ToString
public class MemberAuthDTO {
  private long userId;
  private String email;
  private String password;  // bcrypt 해시
  private Integer rankId;
  private String provider;

}