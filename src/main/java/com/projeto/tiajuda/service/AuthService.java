package com.projeto.tiajuda.service;

import com.projeto.tiajuda.configuration.security.TokenService;
import com.projeto.tiajuda.dto.request.LoginRequest;
import com.projeto.tiajuda.dto.request.RegisterRequest;
import com.projeto.tiajuda.dto.response.LoginResponse;
import com.projeto.tiajuda.dto.response.UserResponse;
import com.projeto.tiajuda.entity.User;
import com.projeto.tiajuda.exceptions.CustomAuthenticationException;
import com.projeto.tiajuda.exceptions.UserAlreadyExistsException;
import com.projeto.tiajuda.mapper.UserMapper;
import com.projeto.tiajuda.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserMapper userMapper;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, TokenService tokenService, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userMapper = userMapper;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email já cadastrado. Por favor, utilize outro");
        }

        User user = userMapper.toEntity(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        return userMapper.toUserResponse(savedUser);
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        try {
            UsernamePasswordAuthenticationToken useAndPass = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

            Authentication authentication = authenticationManager.authenticate(useAndPass);

            User user = (User) authentication.getPrincipal();

            String token = tokenService.generateToken(user);

            return userMapper.toLoginResponse(user, token);

        } catch (AuthenticationException exception) {
            throw new CustomAuthenticationException("Email ou senha inválidos");
        }
    }
}
