package com.jchat.com.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComMenuListSearchResDto {
    private Long roleId; // 롤권한 아이디
    private Long menuId; // 메뉴 아이디
    private Long parentMenuId; // 부모 메뉴 아이디
    private String menuCd; // 메뉴 코드 01:폴더/02:파일
    private String menuName; // 메뉴 이름
    private String menuUrl; // 메뉴 URL
    private Long menuOrder; // 메뉴 순서
    private Long menuDepth; // 메뉴 뎁스
    private String menuDesc; // 메뉴 설명
    private String sortPath; // 소팅
}
