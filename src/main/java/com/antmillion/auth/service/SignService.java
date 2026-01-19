package com.antmillion.auth.service;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.antmillion.auth.dto.MemberAuthDTO;
import com.antmillion.auth.jwt.JwtProvider;
import com.antmillion.auth.mapper.MemberMapper;
import com.antmillion.auth.token.RefreshTokenStore;

@Service
public class SignService {

  private final MemberMapper memberMapper;
  private final PasswordEncoder passwordEncoder;
  private final JwtProvider jwtProvider;
  private final RefreshTokenStore refreshStore;

  public SignService(MemberMapper memberMapper,
                     PasswordEncoder passwordEncoder,
                     JwtProvider jwtProvider,
                     RefreshTokenStore refreshStore) {
    this.memberMapper = memberMapper;
    this.passwordEncoder = passwordEncoder;
    this.jwtProvider = jwtProvider;
    this.refreshStore = refreshStore;
  }

  public TokenPair login(String email, String rawPassword) {
	  MemberAuthDTO member = memberMapper.selectByEmail(email);
	  if (member == null) throw new IllegalArgumentException("NO_USER");

	  if (member.getPassword() == null || !passwordEncoder.matches(rawPassword, member.getPassword())) {
	    throw new IllegalArgumentException("BAD_CREDENTIALS");
	  }

	  long userId = member.getUserId();
	  Integer rankId = member.getRankId();
	  String provider = member.getProvider();

	  String accessJti = java.util.UUID.randomUUID().toString();
	  String refreshJti = java.util.UUID.randomUUID().toString();

	  String at = jwtProvider.createAccessToken(userId, rankId, provider, accessJti);
	  String rt = jwtProvider.createRefreshToken(userId, refreshJti);

	  refreshStore.save(userId, rt, jwtProvider.getRefreshTtlSeconds());

	  return new TokenPair(at, rt, jwtProvider.getAccessTtlSeconds(), jwtProvider.getRefreshTtlSeconds());
	  }

  public static class TokenPair {
    private final String accessToken;
    private final String refreshToken;
    private final long accessTtlSeconds;
    private final long refreshTtlSeconds;

    public TokenPair(String accessToken, String refreshToken, long accessTtlSeconds, long refreshTtlSeconds) {
      this.accessToken = accessToken;
      this.refreshToken = refreshToken;
      this.accessTtlSeconds = accessTtlSeconds;
      this.refreshTtlSeconds = refreshTtlSeconds;
    }
    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public long getAccessTtlSeconds() { return accessTtlSeconds; }
    public long getRefreshTtlSeconds() { return refreshTtlSeconds; }
  }
}