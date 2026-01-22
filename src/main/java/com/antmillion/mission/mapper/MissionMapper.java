package com.antmillion.mission.mapper;

import com.antmillion.mission.dto.QuizChoiceDTO;
import com.antmillion.mission.dto.QuizLogDTO;
import com.antmillion.mission.dto.QuizQuestionDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MissionMapper {
    // 오늘의 퀴즈 조회
    List<QuizQuestionDTO> selectQuizByDate(@Param("quizDate") String quizDate);

    // 퀴즈 보기 조회 (quizId 기준)
    List<QuizChoiceDTO> selectQuizChoicesByQuizIds(@Param("quizIds") List<Long> quizIds);

    // 이미 푼 문제인지 확인
    int countSolvedHistory(@Param("userId") Long userId, @Param("quizId") Long quizId);

    // 퀴즈 풀이 이력 저장
    void insertQuizLog(QuizLogDTO quizLog);

    // 정답 번호 조회
    Integer selectAnswerByQuizId(@Param("quizId") Long quizId);

    // 어떤 문제를 풀었는지 확인
    List<Long> selectTodaySolvedQuizIds(@Param("userId") Long userId);

    // 퀴즈 포인트 조회
    int selectQuizPointByQuizId(@Param("quizId") Long quizId);

    // 오늘 미션 완료 여부 조회
    int countTodaySolvedQuiz(@Param("userId") Long userId);
}