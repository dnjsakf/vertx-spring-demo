package com.dms.apps.user.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // 게터, 세터, toString, equals, hashCode 메서드를 자동으로 생성
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 포함한 생성자 자동 생성
public class UserVO {
  private String userId;
  private String loginId;
  private String loginPwd;
  private String userName;
  private String userEmail;
  private String sortOrder;
  private String useYn;
  private String regUserId;
  private String regDttm;
  private String updUserId;
  private String updDttm;
}
