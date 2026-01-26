package com.antmillion.mission.service;

import com.antmillion.auth.mapper.MemberMapper;
import com.antmillion.mission.dto.*;
import com.antmillion.mission.mapper.MissionMapper;
import com.antmillion.user.dto.UserRankResponseDTO;
import com.antmillion.user.service.AntRankService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MissionServiceImpl implements MissionService {
    private final MissionMapper missionMapper;
    private final MemberMapper memberMapper;
    private final AntRankService antRankService;

    // 로그인한 유저 ID를 가져오는 메서드
    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return Long.valueOf(auth.getPrincipal().toString());
        }
        return null;
    }

    @Override
    public List<QuizQuestionResponseDTO> getDailyQuiz() {
        Long userId = getCurrentUserId();

        // 오늘 날짜 구하기 (yyyy-MM-dd)
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 오늘의 퀴즈 문제(Question) 조회
        List<QuizQuestionDTO> questions = missionMapper.selectQuizByDate(today);

        // 퀴즈가 없으면 빈 리스트 반환 (에러 방지)
        if (questions.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> solvedQuizIds = missionMapper.selectTodaySolvedQuizIds(userId);

        // 안 푼 문제만 필터링
        List<QuizQuestionDTO> unsolvedQuestions = questions.stream()
                .filter(q -> !solvedQuizIds.contains(q.getQuizId()))
                .collect(Collectors.toList());

        // 오늘 미션 완료면 빈 리스트 반환 (에러 방지)
        if (unsolvedQuestions.isEmpty()) {
            return new ArrayList<>();
        }

        // 퀴즈 ID들만 추출 (보기 조회를 위해)
        List<Long> quizIds = unsolvedQuestions.stream()
                .map(QuizQuestionDTO::getQuizId)
                .collect(Collectors.toList());

        // 해당 퀴즈들의 보기(Choice) 전체 조회
        List<QuizChoiceDTO> choices = missionMapper.selectQuizChoicesByQuizIds(quizIds);

        // 보기들을 quizId를 키(Key)로 하여 그룹화 (퀴즈 ID로 객관식 보기 찾기 가능)
        Map<Long, List<QuizChoiceDTO>> choicesMap = choices.stream()
                .collect(Collectors.groupingBy(QuizChoiceDTO::getQuizId));

        // 결과 DTO (Question + Choices)
        List<QuizQuestionResponseDTO> responseList = new ArrayList<>();

        for (QuizQuestionDTO q : unsolvedQuestions) {
            // 해당 문제의 보기 리스트 가져오기 (없으면 빈 리스트)
            List<QuizChoiceDTO> quizChoices = choicesMap.getOrDefault(q.getQuizId(), new ArrayList<>());

            // ResponseDTO 생성
            QuizQuestionResponseDTO responseDTO = QuizQuestionResponseDTO.builder()
                    .quizId(q.getQuizId())
                    .type(q.getType())
                    .question(q.getQuestion())
                    .point(q.getPoint())
                    .quizDate(q.getQuizDate())
                    .choices(quizChoices)
                    .build();
            responseList.add(responseDTO);
        }
        return responseList;
    }

    @Override
    @Transactional
    public QuizSubmissionResponseDTO checkAndLogAnswer(QuizSubmissionRequestDTO requestDTO) {
        // 해당 퀴즈의 정답 조회
        QuizResultDTO resultInfo = missionMapper.selectQuizResultByQuizId(requestDTO.getQuizId());
        if (resultInfo == null || resultInfo.getAnswer() == null) {
            throw new IllegalArgumentException("존재하지 않는 퀴즈입니다.");
        }

        // 채점
        boolean isCorrect = resultInfo.getAnswer().equals(requestDTO.getChoiceNo());
        if(isCorrect) {
            try {
                int count = missionMapper.countSolvedHistory(requestDTO.getUserId(), requestDTO.getQuizId());
                if (count == 0) {
                    QuizLogDTO logDTO = QuizLogDTO.builder()
                            .userId(requestDTO.getUserId())
                            .quizId(requestDTO.getQuizId())
                            .build();
                    missionMapper.insertQuizLog(logDTO);

                    // 포인트 지급
                    memberMapper.updateUserPoint(requestDTO.getUserId(), resultInfo.getPoint());

                    // 랭크 갱신
                    Long userId = requestDTO.getUserId();
                    int newPoint = memberMapper.selectUserPoint(userId);
                    antRankService.updateUserRank(userId, newPoint);
                }
                // 정답 응답
                return QuizSubmissionResponseDTO.builder()
                        .isCorrect(true)
                        .point(resultInfo.getPoint())
                        .message(resultInfo.getExplanation())
                        .build();
            } catch (Exception e) {
                System.err.println("퀴즈 로그 저장 중 오류: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("퀴즈 처리 중 오류가 발생했습니다.", e);
            }
        }
        // 오답 응답
        return QuizSubmissionResponseDTO.builder()
                .isCorrect(false)
                .point(0)
                .message("다시 한번 생각해 보세요.")
                .build();
    }

    @Override
    public UserRankResponseDTO getUserMissionStatus(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID가 필요합니다.");
        }

        try {
            return antRankService.getUserRankInfo(userId);
        } catch (Exception e) {
            System.err.println("미션 상태 조회 중 오류: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("미션 상태 조회 중 오류가 발생했습니다.", e);
        }
    }

    public boolean isTodayMissionCompleted(Long userId) {
        int solvedCount = missionMapper.countTodaySolvedQuiz(userId);
        return solvedCount >= 2;
    }

    public int getTodaySolvedCount(Long userId) {
        return missionMapper.countTodaySolvedQuiz(userId);
    }
}