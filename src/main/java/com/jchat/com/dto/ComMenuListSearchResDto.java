package com.jchat.com.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComMenuListSearchResDto {
    private Integer id; // 아이디
    private Integer parentId; // 부모 아이디
    private String menuName; // 메뉴 이름
    private String menuUrl; // 메뉴 URL
    private Integer menuOrder; // 메뉴 순서
    private Integer depth; // 뎁스
    private String description; // 메뉴 설명
    private String useYn; // 사용여부
}
