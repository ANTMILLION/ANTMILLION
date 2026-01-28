package com.antmillion.auth.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter@Setter
@ToString
public class AuthMemberDTO {
  private long userId;
  private String email;
  private String password;
  private String nickname;
  private Integer rankId;
  private Integer point;
  private String provider;

}