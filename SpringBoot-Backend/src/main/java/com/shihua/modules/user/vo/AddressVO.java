package com.shihua.modules.user.vo;

public record AddressVO(
    Long addressId,
    String receiver,
    String phone,
    String province,
    String city,
    String district,
    String detail,
    Integer isDefault
) {
}
