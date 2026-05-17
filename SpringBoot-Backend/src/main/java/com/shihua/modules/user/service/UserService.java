package com.shihua.modules.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shihua.common.exception.BusinessException;
import com.shihua.modules.user.dto.ChangePasswordRequest;
import com.shihua.modules.user.dto.LoginRequest;
import com.shihua.modules.user.dto.RegisterRequest;
import com.shihua.modules.user.dto.UpdateProfileRequest;
import com.shihua.modules.user.entity.SysUser;
import com.shihua.modules.user.mapper.SysUserMapper;
import com.shihua.modules.user.vo.LoginVO;
import com.shihua.modules.user.vo.UserInfoVO;
import com.shihua.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(SysUserMapper userMapper, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public LoginVO register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }
        if (findByUsername(request.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }

        SysUser user = new SysUser();
        user.setUsername(request.getUsername().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setGender(request.getGender() == null ? 0 : request.getGender());
        user.setPhone(request.getPhone());
        user.setRole("USER");
        user.setStatus(1);
        userMapper.insert(user);

        String token = jwtService.generateToken(user.getUserId(), user.getUsername(), user.getRole());
        return new LoginVO(token, toUserInfo(user));
    }

    public LoginVO login(LoginRequest request) {
        SysUser user = findByUsername(request.getUsername());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }

        String token = jwtService.generateToken(user.getUserId(), user.getUsername(), user.getRole());
        return new LoginVO(token, toUserInfo(user));
    }

    public UserInfoVO getById(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return toUserInfo(user);
    }

    @Transactional
    public UserInfoVO updateProfile(Long userId, UpdateProfileRequest request) {
        SysUser user = requireUser(userId);
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setAvatar(request.getAvatar());
        userMapper.updateById(user);
        return toUserInfo(user);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        SysUser user = requireUser(userId);
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userMapper.updateById(user);
    }

    private SysUser findByUsername(String username) {
        return userMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username.trim()));
    }

    private SysUser requireUser(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    private UserInfoVO toUserInfo(SysUser user) {
        return new UserInfoVO(
            user.getUserId(),
            user.getUsername(),
            user.getGender(),
            user.getPhone(),
            user.getEmail(),
            user.getAvatar(),
            user.getRole(),
            user.getStatus()
        );
    }
}
