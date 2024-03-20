package com.cooksys.quiz_api.services;

import java.util.List;

import com.cooksys.quiz_api.dtos.QuestionResponseDto;
import com.cooksys.quiz_api.dtos.QuizResponseDto;
import com.cooksys.quiz_api.entities.Question;
import com.cooksys.quiz_api.entities.Quiz;

public interface QuizService {

  List<QuizResponseDto> getAllQuizzes();
  QuizResponseDto postQuiz(Quiz quiz);
  QuizResponseDto deleteQuiz(Long id);
  QuizResponseDto patchQuiz(Long id, String name);
  QuestionResponseDto getRandomQuestion(Long id);
  QuizResponseDto addQuestion(Long id, Question question);
  QuestionResponseDto deleteQuestion(Long id, Long questionID);
}
