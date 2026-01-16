package com.antmillion.mappers;

import com.antmillion.mission.dto.QuizLogDTO;
import com.antmillion.mission.dto.QuizQuestionDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/root-context.xml"})
public class MissionMapperTest {

    @Autowired
    private MissionMapper missionMapper;
    private final Long testUser = 1L;
    private final Long quiz1Id = 1L;
    private final Long quiz2Id = 2L;

    @Test
    public void 테스트_오늘의_퀴즈_조회() {
        String testDate = "2026-01-16";
        List<QuizQuestionDTO> quizList = missionMapper.selectQuizByDate(testDate);

        System.out.println("조회된 퀴즈 수: " + quizList.size());
        quizList.forEach(q -> {
            System.out.println("문제: " + q.getQuestion());
            System.out.println("보기 개수: " + q.getChoices().size());
        });
    }

    // 퀴즈를 하나도 안 맞혔을 때
    @Test
    public void 테스트_미션_진행중_0개() {
        // 일부러 insert를 하지 않음
        int todayTotal = missionMapper.countTodaySolved(testUser);

        System.out.println("--- [시나리오: 0개] ---");
        System.out.println("현재 맞춘 개수: " + todayTotal);
        System.out.println("미션 상태: " + (todayTotal >= 2 ? "완료" : "미완료 (진행 중)"));

        org.junit.Assert.assertTrue(todayTotal < 2);
    }

    // 퀴즈를 1개만 맞혔을 때
    @Test
    public void 테스트_미션_진행중_1개() {
        // 1번 퀴즈만 맞혔다고 가정
        QuizLogDTO log1 = QuizLogDTO.builder().userId(testUser).quizId(1L).build();
        missionMapper.insertQuizLog(log1);

        // 1번 문제 풀이 이력 확인
        int history1 = missionMapper.countSolvedHistory(testUser, quiz1Id);
        System.out.println("1번 문제 풀이 기록(기대값 1): " + history1);
        org.junit.Assert.assertEquals(1, history1);

        // 오늘 총 맞춘 개수 확인
        int todayTotal = missionMapper.countTodaySolved(testUser);

        System.out.println("--- [시나리오: 1개] ---");
        System.out.println("현재 맞춘 개수: " + todayTotal);
        System.out.println("미션 상태: " + (todayTotal >= 2 ? "완료" : "미완료 (1개 더 풀어야 함)"));
        org.junit.Assert.assertEquals(1, todayTotal);
    }

    // 퀴즈를 2개 모두 맞혔을 때 (미션 완료)
    @Test
    public void 테스트_미션_완료_2개() {
        QuizLogDTO log1 = QuizLogDTO.builder().userId(testUser).quizId(1L).build();
        QuizLogDTO log2 = QuizLogDTO.builder().userId(testUser).quizId(2L).build();

        missionMapper.insertQuizLog(log1);
        missionMapper.insertQuizLog(log2);

        // [추가] 1번 문제 풀이 이력 확인
        int history1 = missionMapper.countSolvedHistory(testUser, quiz1Id);
        System.out.println("1번 문제 풀이 기록(기대값 1): " + history1);
        org.junit.Assert.assertEquals(1, history1);

        // [추가] 2번 문제 풀이 이력 확인
        int history2 = missionMapper.countSolvedHistory(testUser, quiz2Id);
        System.out.println("2번 문제 풀이 기록(기대값 1): " + history2);
        org.junit.Assert.assertEquals(1, history2);

        int todayTotal = missionMapper.countTodaySolved(testUser);

        System.out.println("--- [시나리오: 2개] ---");
        System.out.println("현재 맞춘 개수: " + todayTotal);
        boolean isClear = (todayTotal >= 2);
        System.out.println("미션 상태: " + (isClear ? "미션 완료" : "오류"));

        org.junit.Assert.assertTrue(isClear);
    }
}