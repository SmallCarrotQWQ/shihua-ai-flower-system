package com.shihua.modules.user.vo;

public record UserInfoVO(
    Long userId,
    String username,
    Integer gender,
    String phone,
    String email,
    String avatar,
    String role,
    Integer status
) {
}

