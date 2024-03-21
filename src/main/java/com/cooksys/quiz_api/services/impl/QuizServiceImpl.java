package com.cooksys.quiz_api.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.cooksys.quiz_api.dtos.questions.QuestionRequestDto;
import com.cooksys.quiz_api.dtos.questions.QuestionResponseDto;
import com.cooksys.quiz_api.dtos.quizzes.QuizRequestDto;
import com.cooksys.quiz_api.dtos.quizzes.QuizResponseDto;
import com.cooksys.quiz_api.entities.Question;
import com.cooksys.quiz_api.entities.Quiz;
import com.cooksys.quiz_api.exceptions.BadRequestException;
import com.cooksys.quiz_api.exceptions.NotFoundException;
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
  public List<QuizResponseDto> getAllQuizzes() {

    if (quizRepository.findByDeletedFalse().isEmpty()) {
      throw new NotFoundException("No quizzes found");
    }

    return quizMapper.entitiesToDtos(quizRepository.findByDeletedFalse());
  }

  @Override
  public QuizResponseDto postQuiz(QuizRequestDto quizRequestDto) {

    if (quizRequestDto.getName().isEmpty()) {
      throw new BadRequestException("Name field cannot be empty");
    }

    Quiz savedQuiz = quizRepository.save(quizMapper.dtoToEntity(quizRequestDto));
    saveQuestions(savedQuiz);

    return quizMapper.entityToDto(quizRepository.getById(savedQuiz.getId()));
  }

  @Override
  public QuizResponseDto deleteQuiz(Long id) {

    Optional<Quiz> quiz = quizRepository.findById(id);

    if (quiz.isEmpty()) {
      throw new NotFoundException("Quiz not found for id: " + id);
    }

    Quiz foundQuiz = quiz.get();

    if (foundQuiz.isDeleted()) {
      throw new NotFoundException("Quiz has been deleted for id: " + id);
    }

    softDeleteQuiz(foundQuiz);

    return quizMapper.entityToDto(foundQuiz);
  }

  @Override
  public QuizResponseDto patchQuiz(Long id, String name) {
    Optional<Quiz> quiz = quizRepository.findById(id);

    if (quiz.isEmpty()) {
      throw new NotFoundException("Quiz not found for id: " + id);
    }

    Quiz foundQuiz = quiz.get();

    if (foundQuiz.isDeleted()) {
      throw new NotFoundException("Quiz has been deleted for id: " + id);
    }

    if (name.isBlank()) {
      throw new BadRequestException("Name cannot only contain black spaces.");
    }

    foundQuiz.setName(name);

    return quizMapper.entityToDto(quizRepository.save(foundQuiz));
  }

  @Override
  public QuestionResponseDto getRandomQuestion(Long id) {
    Quiz quiz = quizRepository.findById(id).orElseThrow();

    if (quiz.isDeleted()) {
      throw new NotFoundException("Quiz has been deleted for id: " + id);
    }

    List<Question> undeletedQuestions = new ArrayList<>();

    for (Question question : quiz.getQuestions()) {
      if (!question.isDeleted()) {
        undeletedQuestions.add(question);
      }
    }

    List<QuestionResponseDto> questions = questionMapper.entitiesToDtos(undeletedQuestions);
    
    return questions.get(new Random().nextInt(questions.size()));
  }

  @Override
  public QuizResponseDto addQuestion(Long id, QuestionRequestDto question) {
    Question questionEntity = questionMapper.dtoToEntity(question);
    Optional<Quiz> quiz = quizRepository.findById(id);

    if (questionEntity.getText().isBlank()) {
      throw new BadRequestException("Question text cannot be empty");
    }

    if (quiz.isEmpty()) {
      throw new NotFoundException("Quiz not found");
    }

    Quiz foundQuiz = quiz.get();

    if (foundQuiz.isDeleted()) {
      throw new NotFoundException("Quiz has been deleted");
    }

    questionEntity.setQuiz(foundQuiz);
    questionRepository.save(questionEntity);
    saveAnswers(questionEntity);

    return quizMapper.entityToDto(quizRepository.save(foundQuiz));
  }

  @Override
  public QuestionResponseDto deleteQuestion(Long id, Long questionID) {
    Optional<Question> question = questionRepository.findById(questionID);

    if (question.isEmpty()) {
      throw new NotFoundException("Question not found");
    }

    Question foundQuestion = question.get();

    if (foundQuestion.isDeleted()) {
      throw new NotFoundException("Question has been deleted for id: " + questionID);
    }

    softDeleteQuestion(foundQuestion);

    return questionMapper.entityToDto(foundQuestion);
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

  private void softDelete(Object obj) {
    if (obj instanceof Quiz) {
      softDeleteQuiz((Quiz) obj);
    } else if (obj instanceof Question) {
      softDeleteQuestion((Question) obj);
    }
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
