package com.cooksys.quiz_api.mappers;

import java.util.List;

import com.cooksys.quiz_api.dtos.quizzes.QuizRequestDto;
import com.cooksys.quiz_api.dtos.quizzes.QuizResponseDto;
import com.cooksys.quiz_api.entities.Quiz;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { QuestionMapper.class })
public interface QuizMapper {

  QuizResponseDto entityToDto(Quiz entity);

  List<QuizResponseDto> entitiesToDtos(List<Quiz> entities);

  Quiz dtoToEntity(QuizRequestDto dto);

  List<Quiz> dtosToEntities(List<QuizRequestDto> dtos);

}
