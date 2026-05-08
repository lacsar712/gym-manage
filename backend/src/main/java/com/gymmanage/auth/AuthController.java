package com.gymmanage.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gymmanage.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AdminUserMapper adminUserMapper;
    private final JwtUtil jwtUtil;

    public AuthController(AdminUserMapper adminUserMapper, JwtUtil jwtUtil) {
        this.adminUserMapper = adminUserMapper;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        AdminUser u = adminUserMapper.selectOne(new LambdaQueryWrapper<AdminUser>()
                .eq(AdminUser::getUsername, req.getUsername().trim()));
        if (u == null || !u.getPassword().equals(req.getPassword())) {
            return ApiResponse.error(10001, "invalid username or password");
        }
        String token = jwtUtil.generate(u.getUsername());
        LoginResponse resp = new LoginResponse();
        resp.setToken(token);
        resp.setTokenType("Bearer");
        return ApiResponse.ok(resp);
    }
}

