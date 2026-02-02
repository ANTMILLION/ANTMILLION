package com.antmillion.quiz.service;

import com.antmillion.auth.mapper.MemberMapper;
import com.antmillion.quiz.dto.*;
import com.antmillion.quiz.mapper.QuizMapper;
import com.antmillion.user.service.AntRankService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {
    private final QuizMapper quizMapper;
    private final MemberMapper memberMapper;
    private final AntRankService antRankService;

    @Override
    public List<QuizQuestionResponseDTO> getDailyQuiz(Long userId) {
        // 오늘의 경제 퀴즈 문제(오늘 날짜 && 안 푼) 조회
        List<QuizQuestionDTO> unsolvedQuestions = quizMapper.selectUnsolvedQuizByDate(userId, LocalDate.now().toString());

        // 퀴즈가 없으면 빈 리스트 반환 (에러 방지)
        if (unsolvedQuestions.isEmpty()) {
            return new ArrayList<>();
        }

        // 퀴즈 ID들만 추출 (보기 조회를 위해)
        List<Long> quizIds = unsolvedQuestions.stream()
                .map(QuizQuestionDTO::getQuizId)
                .collect(Collectors.toList());

        // 해당 퀴즈들의 보기(Choice) 전체 조회
        Map<Long, List<QuizChoiceDTO>> choicesMap = quizMapper.selectQuizChoicesByQuizIds(quizIds)
                .stream()
                .collect(Collectors.groupingBy(QuizChoiceDTO::getQuizId));

        return unsolvedQuestions.stream().map(q -> QuizQuestionResponseDTO.builder()
                .quizId(q.getQuizId())
                .type(q.getType())
                .question(q.getQuestion())
                .point(q.getPoint())
                .quizDate(q.getQuizDate())
                .choices(choicesMap.getOrDefault(q.getQuizId(), new ArrayList<>()))
                .build()
        ).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public QuizSubmissionResponseDTO checkAndLogAnswer(Long userId, QuizSubmissionRequestDTO requestDTO) {
        // 해당 퀴즈의 정답 조회
        QuizQuestionDTO resultInfo = quizMapper.selectQuizResultByQuizId(requestDTO.getQuizId());
        if (resultInfo == null || resultInfo.getAnswer() == null) {
            throw new IllegalArgumentException("존재하지 않는 퀴즈입니다.");
        }

        // 채점
        boolean isCorrect = resultInfo.getAnswer().equals(requestDTO.getChoiceNo());
        // 오답 응답
        if (!isCorrect) {
            return QuizSubmissionResponseDTO.builder()
                    .isCorrect(false)
                    .point(0)
                    .message("다시 한번 생각해 보세요.")
                    .build();
        }
        int count = quizMapper.countSolvedHistory(userId, requestDTO.getQuizId());
        if (count == 0) {
            QuizLogDTO logDTO = QuizLogDTO.builder()
                    .userId(userId)
                    .quizId(requestDTO.getQuizId())
                    .build();
            quizMapper.insertQuizLog(logDTO);

            // 포인트 지급
            memberMapper.updateUserPoint(userId, resultInfo.getPoint());

            // 랭크 갱신
            int newPoint = memberMapper.selectUserPoint(userId);
            antRankService.updateUserRank(userId, newPoint);
        }
        // 정답 응답
        return QuizSubmissionResponseDTO.builder()
                .isCorrect(true)
                .point(resultInfo.getPoint())
                .message(resultInfo.getExplanation())
                .build();
    }

    @Override
    public QuizProgressResponseDTO getQuizProgress(Long userId) {
        int solvedCount = quizMapper.countTodaySolvedQuiz(userId);
        int totalCount = 2;
        int progress = Math.min((solvedCount * 50) / totalCount, 100);

        return QuizProgressResponseDTO.builder()
                .solvedCount(solvedCount)
                .totalCount(totalCount)
                .progress(progress)
                .build();
    }
}