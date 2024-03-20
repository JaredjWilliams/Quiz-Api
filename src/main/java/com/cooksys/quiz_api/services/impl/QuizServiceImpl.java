package com.cooksys.quiz_api.services.impl;

import java.util.List;
import java.util.Random;

import com.cooksys.quiz_api.dtos.QuestionResponseDto;
import com.cooksys.quiz_api.dtos.QuizResponseDto;
import com.cooksys.quiz_api.entities.Question;
import com.cooksys.quiz_api.entities.Quiz;
import com.cooksys.quiz_api.mappers.QuestionMapper;
import com.cooksys.quiz_api.mappers.QuizMapper;
import com.cooksys.quiz_api.repositories.AnswerRepository;
import com.cooksys.quiz_api.repositories.QuestionRepository;
import com.cooksys.quiz_api.repositories.QuizRepository;
import com.cooksys.quiz_api.services.QuizService;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

  private final QuizRepository quizRepository;
  private final QuestionRepository questionRepository;
  private final AnswerRepository answerRepository;

  private final QuizMapper quizMapper;
  private final QuestionMapper questionMapper;

  @Override
  public List<QuizResponseDto> getAllQuizzes() {
    return quizMapper.entitiesToDtos(quizRepository.findAll());
  }

  @Override
  public QuizResponseDto postQuiz(Quiz quiz) {
    Quiz savedQuiz = quizRepository.save(quiz);
    saveQuestions(savedQuiz);
    return quizMapper.entityToDto(quizRepository.getById(savedQuiz.getId()));
  }

  @Override
  public QuizResponseDto deleteQuiz(Long id) {
    Quiz quiz = quizRepository.findById(id).orElseThrow();
    deleteQuestions(quiz);
    return quizMapper.entityToDto(quiz);
  }

  @Override
  public QuizResponseDto patchQuiz(Long id, String name) {
    Quiz quiz = quizRepository.findById(id).orElseThrow();
    quiz.setName(name);
    return quizMapper.entityToDto(quizRepository.save(quiz));
  }

  @Override
  public QuestionResponseDto getRandomQuestion(Long id) {
    Quiz quiz = quizRepository.findById(id).orElseThrow();
    List<QuestionResponseDto> questions = questionMapper.entitiesToDtos(quiz.getQuestions());
    return questions.get(new Random().nextInt(questions.size()));
  }

  @Override
  public QuizResponseDto addQuestion(Long id, Question question) {
    Quiz quiz = quizRepository.findById(id).orElseThrow();
    question.setQuiz(quiz);
    questionRepository.save(question);
    saveAnswers(question);
    return quizMapper.entityToDto(quizRepository.save(quiz));
  }

  @Override
  public QuestionResponseDto deleteQuestion(Long id, Long questionID) {
    Question question = questionRepository.findById(questionID).orElseThrow();
    deleteAnswers(question);
    questionRepository.deleteById(questionID);

    return questionMapper.entityToDto(question);
  }

  private void deleteQuestions(Quiz quiz) {
    quiz.getQuestions().forEach(question -> {
      deleteAnswers(question);
      questionRepository.deleteById(question.getId());
    });
  }

  private void deleteAnswers(Question question) {
    question.getAnswers().forEach(answer -> {
      answerRepository.deleteById(answer.getId());
    });
  }

  private void saveQuestions(Quiz quiz) {
    quiz.getQuestions().forEach(question -> {
      question.setQuiz(quiz);
      questionRepository.save(question);
      saveAnswers(question);
    });
  }

  private void saveAnswers(Question question) {
    question.getAnswers().forEach(answer -> {
      answer.setQuestion(question);
      answerRepository.save(answer);
    });
  }
}
