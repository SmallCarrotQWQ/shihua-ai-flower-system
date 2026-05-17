package com.shihua.modules.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.shihua.common.exception.BusinessException;
import com.shihua.modules.user.dto.AddressRequest;
import com.shihua.modules.user.entity.UserAddress;
import com.shihua.modules.user.mapper.UserAddressMapper;
import com.shihua.modules.user.vo.AddressVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class AddressService {
    private final UserAddressMapper addressMapper;

    public AddressService(UserAddressMapper addressMapper) {
        this.addressMapper = addressMapper;
    }

    public List<AddressVO> list(Long userId) {
        return addressMapper.selectList(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getUserId, userId)
                .orderByDesc(UserAddress::getIsDefault)
                .orderByDesc(UserAddress::getAddressId))
            .stream()
            .map(this::toVO)
            .toList();
    }

    @Transactional
    public AddressVO create(Long userId, AddressRequest request) {
        UserAddress address = new UserAddress();
        address.setUserId(userId);
        apply(address, request);
        if (Objects.equals(address.getIsDefault(), 1)) {
            clearDefault(userId);
        }
        addressMapper.insert(address);
        return toVO(address);
    }

    @Transactional
    public AddressVO update(Long userId, Long addressId, AddressRequest request) {
        UserAddress address = requireAddress(userId, addressId);
        apply(address, request);
        if (Objects.equals(address.getIsDefault(), 1)) {
            clearDefault(userId);
        }
        addressMapper.updateById(address);
        return toVO(address);
    }

    @Transactional
    public void delete(Long userId, Long addressId) {
        requireAddress(userId, addressId);
        addressMapper.deleteById(addressId);
    }

    @Transactional
    public void setDefault(Long userId, Long addressId) {
        UserAddress address = requireAddress(userId, addressId);
        clearDefault(userId);
        address.setIsDefault(1);
        addressMapper.updateById(address);
    }

    public UserAddress requireAddress(Long userId, Long addressId) {
        UserAddress address = addressMapper.selectById(addressId);
        if (address == null || !Objects.equals(address.getUserId(), userId)) {
            throw new BusinessException("Address not found");
        }
        return address;
    }

    private void apply(UserAddress address, AddressRequest request) {
        address.setReceiver(request.getReceiver());
        address.setPhone(request.getPhone());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setDetail(request.getDetail());
        address.setIsDefault(request.getIsDefault() == null ? 0 : request.getIsDefault());
    }

    private void clearDefault(Long userId) {
        addressMapper.update(null, new LambdaUpdateWrapper<UserAddress>()
            .eq(UserAddress::getUserId, userId)
            .set(UserAddress::getIsDefault, 0));
    }

    private AddressVO toVO(UserAddress address) {
        return new AddressVO(address.getAddressId(), address.getReceiver(), address.getPhone(), address.getProvince(), address.getCity(), address.getDistrict(), address.getDetail(), address.getIsDefault());
    }
}
