package com.cooksys.quiz_api.mappers;

import java.util.List;

import com.cooksys.quiz_api.dtos.questions.QuestionRequestDto;
import com.cooksys.quiz_api.dtos.questions.QuestionResponseDto;
import com.cooksys.quiz_api.entities.Question;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { AnswerMapper.class })
public interface QuestionMapper {

  QuestionResponseDto entityToDto(Question entity);

  List<QuestionResponseDto> entitiesToDtos(List<Question> entities);

  Question dtoToEntity(QuestionRequestDto dto);

  List<Question> dtosToEntities(List<QuestionRequestDto> dtos);

}
