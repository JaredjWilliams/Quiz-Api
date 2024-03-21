package com.cooksys.quiz_api.dtos.quizzes;

import com.cooksys.quiz_api.entities.Question;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class QuizRequestDto {
    private String name;
    private boolean deleted = false;
    private List<Question> questions;
}
