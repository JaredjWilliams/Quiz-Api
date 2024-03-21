package com.cooksys.quiz_api.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.cooksys.quiz_api.dtos.questions.QuestionRequestDto;
import com.cooksys.quiz_api.dtos.questions.QuestionResponseDto;
import com.cooksys.quiz_api.dtos.quizzes.QuizRequestDto;
import com.cooksys.quiz_api.dtos.quizzes.QuizResponseDto;
import com.cooksys.quiz_api.entities.Question;
import com.cooksys.quiz_api.entities.Quiz;
import com.cooksys.quiz_api.mappers.QuestionMapper;
import com.cooksys.quiz_api.mappers.QuizMapper;
import com.cooksys.quiz_api.repositories.AnswerRepository;
import com.cooksys.quiz_api.repositories.QuestionRepository;
import com.cooksys.quiz_api.repositories.QuizRepository;
import com.cooksys.quiz_api.services.QuizService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
  public ResponseEntity<List<QuizResponseDto>> getAllQuizzes() {

    if (quizRepository.findByDeletedFalse().isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>(quizMapper.entitiesToDtos(quizRepository.findByDeletedFalse()), HttpStatus.OK);
  }

  @Override
  public ResponseEntity<QuizResponseDto> postQuiz(QuizRequestDto quizRequestDto) {

    if (quizRequestDto.getName() == null) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    Quiz savedQuiz = quizRepository.save(quizMapper.dtoToEntity(quizRequestDto));
    saveQuestions(savedQuiz);

    return new ResponseEntity<>(quizMapper.entityToDto(quizRepository.getById(savedQuiz.getId())), HttpStatus.CREATED) ;
  }

  @Override
  public ResponseEntity<QuizResponseDto> deleteQuiz(Long id) {

    Quiz quiz = quizRepository.findById(id).orElseThrow();

    if (quiz.isDeleted()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    softDeleteQuiz(quiz);

    return new ResponseEntity<>(quizMapper.entityToDto(quiz), HttpStatus.NO_CONTENT) ;
  }

  @Override
  public ResponseEntity<QuizResponseDto> patchQuiz(Long id, String name) {
    Quiz quiz = quizRepository.findById(id).orElseThrow();

    if (quiz.isDeleted()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    if (name.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    quiz.setName(name);

    return new ResponseEntity<>(quizMapper.entityToDto(quizRepository.save(quiz)), HttpStatus.OK) ;
  }

  @Override
  public ResponseEntity<QuestionResponseDto> getRandomQuestion(Long id) {
    Quiz quiz = quizRepository.findById(id).orElseThrow();

    if (quiz.isDeleted()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    List<Question> undeletedQuestions = new ArrayList<>();

    for (Question question : quiz.getQuestions()) {
      if (!question.isDeleted()) {
        undeletedQuestions.add(question);
      }
    }

    List<QuestionResponseDto> questions = questionMapper.entitiesToDtos(undeletedQuestions);
    
    return new ResponseEntity<>(questions.get(new Random().nextInt(questions.size())), HttpStatus.OK) ;
  }

  @Override
  public ResponseEntity<QuizResponseDto> addQuestion(Long id, QuestionRequestDto question) {
    Question questionEntity = questionMapper.dtoToEntity(question);
    Quiz quiz = quizRepository.findById(id).orElseThrow();

    if (quiz.isDeleted()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    questionEntity.setQuiz(quiz);
    questionRepository.save(questionEntity);
    saveAnswers(questionEntity);

    return new ResponseEntity<>(quizMapper.entityToDto(quizRepository.save(quiz)), HttpStatus.CREATED) ;
  }

  @Override
  public ResponseEntity<QuestionResponseDto> deleteQuestion(Long id, Long questionID) {
    Question question = questionRepository.findById(questionID).orElseThrow();
    softDeleteQuestion(question);

    return new ResponseEntity<>(questionMapper.entityToDto(question), HttpStatus.NO_CONTENT) ;
  }

  private void softDeleteQuestion(Question question) {
    question.setDeleted(true);
    softDeleteAnswers(question);
    questionRepository.save(question);
  }

  private void softDeleteQuiz(Quiz quiz) {
    quiz.setDeleted(true);
    softDeleteQuestions(quiz);
    quizRepository.save(quiz);
  }

  private void softDeleteQuestions(Quiz quiz) {
    quiz.getQuestions().forEach(question -> {
      question.setDeleted(true);
      softDeleteAnswers(question);
      questionRepository.save(question);
    });
  }

  private void softDeleteAnswers(Question question) {
    question.getAnswers().forEach(answer -> {
      answer.setDeleted(true);
      answerRepository.save(answer);
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
