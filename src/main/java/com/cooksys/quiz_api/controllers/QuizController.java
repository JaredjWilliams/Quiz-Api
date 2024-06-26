package com.cooksys.quiz_api.controllers;

import java.util.List;

import com.cooksys.quiz_api.dtos.questions.QuestionRequestDto;
import com.cooksys.quiz_api.dtos.questions.QuestionResponseDto;
import com.cooksys.quiz_api.dtos.quizzes.QuizRequestDto;
import com.cooksys.quiz_api.dtos.quizzes.QuizResponseDto;
import com.cooksys.quiz_api.entities.Question;
import com.cooksys.quiz_api.entities.Quiz;
import com.cooksys.quiz_api.services.QuizService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/quiz")
public class QuizController {

  private final QuizService quizService;

  @GetMapping
  public List<QuizResponseDto> getAllQuizzes() {
    return quizService.getAllQuizzes();
  }

  @PostMapping
  public QuizResponseDto postQuiz(@RequestBody QuizRequestDto quizRequestDto) {
    return quizService.postQuiz(quizRequestDto);
  }

  @DeleteMapping("/{id}")
  public QuizResponseDto deleteQuiz(@PathVariable Long id) {
    return quizService.deleteQuiz(id);
  }

  @PatchMapping("/{id}/rename/{newName}")
    public QuizResponseDto patchQuiz(@PathVariable Long id, @PathVariable String newName) {
        return quizService.patchQuiz(id, newName);
    }

  @GetMapping("/{id}/random")
    public QuestionResponseDto getRandomQuestion(@PathVariable Long id) {
        return quizService.getRandomQuestion(id);
    }

    @PatchMapping("/{id}/add")
    public QuizResponseDto addQuestion(@PathVariable Long id, @RequestBody QuestionRequestDto question) {
        return quizService.addQuestion(id, question);
    }

    @DeleteMapping("/{id}/delete/{questionId}")
    public QuestionResponseDto deleteQuestion(@PathVariable Long id, @PathVariable Long questionId) {
        return quizService.deleteQuestion(id, questionId);
    }


}
