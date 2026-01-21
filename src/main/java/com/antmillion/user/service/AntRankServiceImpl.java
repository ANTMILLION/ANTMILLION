package com.antmillion.user.service;

import com.antmillion.auth.mapper.MemberMapper;
import com.antmillion.user.dto.AntRankDTO;
import com.antmillion.user.dto.UserRankResponseDTO;
import com.antmillion.user.mapper.AntRankMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AntRankServiceImpl implements AntRankService {

    private final AntRankMapper antRankMapper;
    private final MemberMapper memberMapper;

    @Override
    @Transactional
    public UserRankResponseDTO calculateRankStatus(Long userId, int currentPoint) {
        List<AntRankDTO> ranks = antRankMapper.selectAllRanks();

        // 초기값 설정
        String rankName = ranks.get(0).getRankType();
        String rankImage = ranks.get(0).getRankImage();
        int currentRankId = ranks.get(0).getRankId();
        int nextRankPoint = 0;
        int currentRankStartPoint = 0;
        boolean isMaxRank = false;

        // 사용자 점수와 비교하여 랭크 찾기
        for (int i = 0; i < ranks.size(); i++) {
            AntRankDTO rank = ranks.get(i);

            // 사용자 점수가 기준점보다 높거나 같으면 이 랭크 당첨
            if (currentPoint >= rank.getRequiredPoint()) {
                rankName = rank.getRankType();
                rankImage = rank.getRankImage();
                currentRankId = rank.getRankId();
                currentRankStartPoint = rank.getRequiredPoint();

                // 다음 단계 목표 설정
                if (i + 1 < ranks.size()) {
                    nextRankPoint = ranks.get(i + 1).getRequiredPoint();
                } else {
                    isMaxRank = true; // 다음 단계 없음 (만렙)
                    nextRankPoint = 10000;
                }
            } else {
                break; // 사용자 점수보다 높은 기준이 나오면 종료
            }
        }

        if (userId != null) {
            memberMapper.updateUserRank(userId, currentRankId);
        }

        // 남은 점수 계산
        int neededPoint = isMaxRank ? 0 : (nextRankPoint - currentPoint);
        if (currentPoint >= 10000) neededPoint = 0;

        return UserRankResponseDTO.builder()
                .currentPoint(currentPoint)
                .rankName(rankName)
                .rankImage(rankImage)
                .nextRankPoint(nextRankPoint)
                .neededPoint(neededPoint)
                .currentRankStartPoint(currentRankStartPoint)
                .build();
    }

    @Override
    @Transactional
    public UserRankResponseDTO getUserRankInfo(Long userId) {
        int currentPoint = memberMapper.selectUserPoint(userId);
        String nickName = memberMapper.selectUserNickName(userId);

        // 기존 랭크 계산 로직 재사용
        UserRankResponseDTO responseDTO = calculateRankStatus(userId, currentPoint);
        responseDTO.assignNickName(nickName);
        return responseDTO;
    }
}