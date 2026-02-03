package com.antmillion.quiz.mapper;

import com.antmillion.quiz.dto.QuizChoiceDTO;
import com.antmillion.quiz.dto.QuizLogDTO;
import com.antmillion.quiz.dto.QuizQuestionDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface QuizMapper {
    List<QuizQuestionDTO> selectUnsolvedQuizByDate(@Param("userId") Long userId, @Param("quizDate") String quizDate);

    // 퀴즈 보기 조회 (quizId 기준)
    List<QuizChoiceDTO> selectQuizChoicesByQuizIds(@Param("quizIds") List<Long> quizIds);

    // 이미 푼 문제인지 확인
    int countSolvedHistory(@Param("userId") Long userId, @Param("quizId") Long quizId);

    // 퀴즈 풀이 이력 저장
    void insertQuizLog(QuizLogDTO quizLog);

    // 오늘 미션 완료 여부 조회
    int countTodaySolvedQuiz(@Param("userId") Long userId);

    // 퀴즈 정답, 포인트, 설명 조회
    QuizQuestionDTO selectQuizResultByQuizId(@Param("quizId") Long quizId);
}