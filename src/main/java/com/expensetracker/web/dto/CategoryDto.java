package com.expensetracker.web.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

@Value
public class CategoryDto {

  @Null
  Integer id;

  @NotBlank
  String name;

  @JsonCreator
  public CategoryDto(@JsonProperty("id") Integer id,
                     @JsonProperty("name") String name) {
    this.id = id;
    this.name = name;
  }
}
