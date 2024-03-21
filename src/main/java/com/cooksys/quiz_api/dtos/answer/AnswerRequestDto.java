package com.cooksys.quiz_api.dtos.answer;

import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class AnswerRequestDto {
    private String text;
    private boolean correct = false;
    private boolean deleted = false;
}
