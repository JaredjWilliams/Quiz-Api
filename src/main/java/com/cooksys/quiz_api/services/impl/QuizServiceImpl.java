package com.cooksys.quiz_api.services.impl;

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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

  private final QuizRepository quizRepository;
  private final QuestionRepository questionRepository;
  private final AnswerRepository answerRepository;

  private final QuizMapper quizMapper;
  private final QuestionMapper questionMapper;

  private Quiz getQuiz(Long id) {
    Optional<Quiz> quiz = quizRepository.findById(id);

    if (quiz.isEmpty()) {
      throw new NotFoundException("Quiz not found for id: " + id);
    }

    if (quiz.get().isDeleted()) {
      throw new NotFoundException("Quiz has been deleted for id: " + id);
    }

    return quiz.get();
  }

  private void validateQuizRequest(String name) {
    if (name.isBlank()) {
      throw new BadRequestException("Name field cannot be empty");
    }
  }
  @Override
  public List<QuizResponseDto> getAllQuizzes() {

    if (quizRepository.findByDeletedFalse().isEmpty()) {
      throw new NotFoundException("No quizzes found");
    }

    return quizMapper.entitiesToDtos(quizRepository.findByDeletedFalse());
  }

  @Override
  public QuizResponseDto postQuiz(QuizRequestDto quizRequestDto) {
    validateQuizRequest(quizRequestDto.getName());

    Quiz savedQuiz = quizRepository.save(quizMapper.dtoToEntity(quizRequestDto));
    saveQuestions(savedQuiz);

    return quizMapper.entityToDto(quizRepository.getById(savedQuiz.getId()));
  }

  @Override
  public QuizResponseDto deleteQuiz(Long id) {
    Quiz quiz = getQuiz(id);
    softDeleteQuiz(quiz);

    return quizMapper.entityToDto(quiz);
  }

  @Override
  public QuizResponseDto patchQuiz(Long id, String name) {
    validateQuizRequest(name);
    
    Quiz quiz = getQuiz(id);
    quiz.setName(name);

    return quizMapper.entityToDto(quizRepository.save(quiz));
  }

  @Override
  public QuestionResponseDto getRandomQuestion(Long id) {
    Quiz quiz = getQuiz(id);

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
    Quiz quiz = getQuiz(id);

    questionEntity.setQuiz(quiz);
    questionRepository.save(questionEntity);
    saveAnswers(questionEntity);

    return quizMapper.entityToDto(quizRepository.save(quiz));
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
