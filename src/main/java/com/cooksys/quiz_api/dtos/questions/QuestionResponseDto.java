package com.cooksys.quiz_api.dtos.questions;

import java.util.List;

import com.cooksys.quiz_api.dtos.answer.AnswerResponseDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class QuestionResponseDto {

  private Long id;

  private String text;

  private List<AnswerResponseDto> answers;

}
