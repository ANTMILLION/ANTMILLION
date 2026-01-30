package com.antmillion.myinfo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.antmillion.auth.token.RefreshTokenStore;
import com.antmillion.myinfo.dto.MyAuthInfoDTO;
import com.antmillion.myinfo.dto.MyInfoDTO;
import com.antmillion.myinfo.dto.MyInfoView;
import com.antmillion.myinfo.mapper.MyInfoMapper;

@Service
public class MyInfoService {

    private final MyInfoMapper myInfoMapper;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenStore refreshTokenStore;

    public MyInfoService(MyInfoMapper myInfoMapper, PasswordEncoder passwordEncoder,
            RefreshTokenStore refreshTokenStore) {
        this.myInfoMapper = myInfoMapper;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenStore = refreshTokenStore;
    }

    @Transactional(readOnly = true)
    public MyInfoView getMyInfo(long userId) {
        MyInfoDTO dto = myInfoMapper.selectMyInfo(userId);
        if (dto == null) {
            throw new IllegalStateException("회원 정보를 찾을 수 없습니다.");
        }

        boolean isKakao = isKakao(dto.getProvider(), dto.getEmail());
        String emailDisplay = isKakao ? "카카오 계정" : n(dto.getEmail());
        String nickname = n(dto.getNickname());

        return new MyInfoView(nickname, emailDisplay, isKakao, dto.getJoinDate());
    }

    @Transactional
    public void changePassword(long userId, String currentPassword, String newPassword) {
        MyAuthInfoDTO auth = myInfoMapper.selectAuthInfo(userId);
        if (auth == null) {
            throw new IllegalStateException("회원 정보를 찾을 수 없습니다.");
        }

        if (isKakao(auth.getProvider(), null)) {
            throw new IllegalStateException("카카오 계정은 카카오 사이트에서 비밀번호 변경을 진행하실 수 있습니다.");
        }

        if (auth.getPassword() == null || auth.getPassword().trim().isEmpty()) {
            throw new IllegalStateException("비밀번호 정보가 없습니다.");
        }

        if (!passwordEncoder.matches(currentPassword, auth.getPassword())) {
            throw new IllegalStateException("기존 비밀번호가 올바르지 않습니다.");
        }

        String encoded = passwordEncoder.encode(newPassword);
        int updated = myInfoMapper.updatePassword(userId, encoded);
        if (updated != 1) {
            throw new IllegalStateException("비밀번호 변경에 실패했습니다.");
        }
    }

    @Transactional
    public void deleteAccount(long userId, String passwordOrEmpty) {
        MyAuthInfoDTO auth = myInfoMapper.selectAuthInfo(userId);
        if (auth == null) {
            throw new IllegalStateException("회원 정보를 찾을 수 없습니다.");
        }

        boolean isKakao = isKakao(auth.getProvider(), null);
        if (!isKakao) {
            if (passwordOrEmpty == null || passwordOrEmpty.trim().isEmpty()) {
                throw new IllegalStateException("회원탈퇴를 위해 비밀번호를 입력해 주세요.");
            }
            if (auth.getPassword() == null || !passwordEncoder.matches(passwordOrEmpty, auth.getPassword())) {
                throw new IllegalStateException("비밀번호가 올바르지 않습니다.");
            }
        }
        refreshTokenStore.delete(userId);
        
        int deleted = myInfoMapper.deleteMember(userId);
        if (deleted != 1) {
            throw new IllegalStateException("회원탈퇴에 실패했습니다.");
        }
    }

    private static boolean isKakao(String provider, String email) {
        if (provider == null) {
            return (email == null || email.trim().isEmpty());
        }
        return "KAKAO".equalsIgnoreCase(provider);
    }

    private static String n(String v) {
        return (v == null) ? "" : v.trim();
    }
}
