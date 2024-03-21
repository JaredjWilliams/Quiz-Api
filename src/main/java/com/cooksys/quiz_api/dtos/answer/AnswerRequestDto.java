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

//package com.cooksys.lemonadestand.model;
//
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@NoArgsConstructor
//@Data
//public class LemonadeRequestDto {
//    private double lemonJuice;
//    private double water;
//    private double sugar;
//    private int iceCubes;
//}
