package com.antmillion.mission.service;

import com.antmillion.mission.dto.QuizChoiceDTO;
import com.antmillion.mission.dto.QuizQuestionDTO;
import com.antmillion.mission.dto.QuizQuestionResponseDTO;
import com.antmillion.mission.mapper.MissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    @Override
    public List<QuizQuestionResponseDTO> getDailyQuiz() {
        // 오늘 날짜 구하기 (yyyy-MM-dd)
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 오늘의 퀴즈 문제(Question) 조회
        List<QuizQuestionDTO> questions = missionMapper.selectQuizByDate(today);

        // 퀴즈가 없으면 빈 리스트 반환 (에러 방지)
        if (questions.isEmpty()) {
            return new ArrayList<>();
        }

        // 퀴즈 ID들만 추출 (보기 조회를 위해)
        List<Long> quizIds = questions.stream()
                .map(QuizQuestionDTO::getQuizId)
                .collect(Collectors.toList());

        // 해당 퀴즈들의 보기(Choice) 전체 조회
        List<QuizChoiceDTO> choices = missionMapper.selectQuizChoicesByQuizIds(quizIds);

        // 보기들을 quizId를 키(Key)로 하여 그룹화 (퀴즈 ID로 객관식 보기 찾기 가능)
        Map<Long, List<QuizChoiceDTO>> choicesMap = choices.stream()
                .collect(Collectors.groupingBy(QuizChoiceDTO::getQuizId));

        // 결과 DTO (Question + Choices)
        List<QuizQuestionResponseDTO> responseList = new ArrayList<>();

        for (QuizQuestionDTO q : questions) {
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
}