package com.capstone.GhumDoZa.dto.team;

import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class TeamDto {

  private String name;
  private Set<String> members;
}
