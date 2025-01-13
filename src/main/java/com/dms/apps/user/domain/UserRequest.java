package com.dms.apps.user.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data // 게터, 세터, toString, equals, hashCode 메서드를 자동으로 생성
public class UserRequest {
  
  @NotNull(message = "ID는 필수 입력값입니다.")
  private String userId;
  
}