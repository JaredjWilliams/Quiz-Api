package com.cooksys.quiz_api.services;

import java.util.List;

import com.cooksys.quiz_api.dtos.questions.QuestionRequestDto;
import com.cooksys.quiz_api.dtos.questions.QuestionResponseDto;
import com.cooksys.quiz_api.dtos.quizzes.QuizRequestDto;
import com.cooksys.quiz_api.dtos.quizzes.QuizResponseDto;

public interface QuizService {

  List<QuizResponseDto> getAllQuizzes();
  QuizResponseDto postQuiz(QuizRequestDto quiz);
  QuizResponseDto deleteQuiz(Long id);
  QuizResponseDto patchQuiz(Long id, String name);
  QuestionResponseDto getRandomQuestion(Long id);
  QuizResponseDto addQuestion(Long id, QuestionRequestDto question);
  QuestionResponseDto deleteQuestion(Long id, Long questionID);
}
