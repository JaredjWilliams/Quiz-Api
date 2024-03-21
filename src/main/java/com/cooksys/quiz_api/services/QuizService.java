package com.cooksys.quiz_api.services;

import java.util.List;

import com.cooksys.quiz_api.dtos.questions.QuestionRequestDto;
import com.cooksys.quiz_api.dtos.questions.QuestionResponseDto;
import com.cooksys.quiz_api.dtos.quizzes.QuizRequestDto;
import com.cooksys.quiz_api.dtos.quizzes.QuizResponseDto;
import org.springframework.http.ResponseEntity;

public interface QuizService {

  ResponseEntity<List<QuizResponseDto>> getAllQuizzes();
  ResponseEntity<QuizResponseDto> postQuiz(QuizRequestDto quiz);
  ResponseEntity<QuizResponseDto> deleteQuiz(Long id);
  ResponseEntity<QuizResponseDto> patchQuiz(Long id, String name);
  ResponseEntity<QuestionResponseDto> getRandomQuestion(Long id);
  ResponseEntity<QuizResponseDto> addQuestion(Long id, QuestionRequestDto question);
  ResponseEntity<QuestionResponseDto> deleteQuestion(Long id, Long questionID);
}
