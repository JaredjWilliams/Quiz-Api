package com.cooksys.quiz_api.dtos.questions;

import com.cooksys.quiz_api.entities.Answer;
import com.cooksys.quiz_api.entities.Quiz;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@Data
public class QuestionRequestDto {
    private String text;
    private boolean deleted = false;
    private Quiz quiz;
    private List<Answer> answers;

}
