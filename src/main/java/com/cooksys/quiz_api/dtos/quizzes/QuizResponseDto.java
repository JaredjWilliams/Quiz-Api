package com.cooksys.quiz_api.dtos.quizzes;

import java.util.List;

import com.cooksys.quiz_api.dtos.questions.QuestionResponseDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class QuizResponseDto {

  private Long id;

  private String name;

  public List<QuestionResponseDto> questions;

}
