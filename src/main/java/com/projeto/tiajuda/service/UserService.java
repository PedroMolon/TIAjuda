package com.projeto.tiajuda.service;

import com.projeto.tiajuda.configuration.security.JWTUserData;
import com.projeto.tiajuda.dto.request.UserPasswordUpdate;
import com.projeto.tiajuda.dto.request.UserUpdateRequest;
import com.projeto.tiajuda.dto.response.UserResponse;
import com.projeto.tiajuda.entity.User;
import com.projeto.tiajuda.entity.enums.Role;
import com.projeto.tiajuda.exceptions.CustomAuthenticationException;
import com.projeto.tiajuda.exceptions.UserNotFoundException;
import com.projeto.tiajuda.mapper.UserMapper;
import com.projeto.tiajuda.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toUserResponse)
                .orElseThrow(() -> new UserNotFoundException("Usuário com id não encontrado: " + id));
    }

    private User getAutheticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            throw new UserNotFoundException("Usuário não autenticado");
        }

        if (authentication.getPrincipal() instanceof JWTUserData jwtUserData) {
            String userEmail = jwtUserData.email();

            return userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado: " + userEmail));
        } else {
            throw new CustomAuthenticationException("Usuário não autenticado ou inválido.");
        }
    }

    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User authenticatedUser = getAutheticatedUser();

        if (!authenticatedUser.getId().equals(id)) {
            throw new CustomAuthenticationException("Você não tem permissão para atualizar este usuário.");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário com id não encontrado: " + id));

        if (!user.getEmail().equals(request.getEmail())) {
            Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                throw new UserNotFoundException("Email já está em uso por outro usuário.");
            }
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        user = userRepository.save(user);

        return UserMapper.toUserResponse(user);
    }

    @Transactional
    public UserResponse updateUserPassword(Long id, UserPasswordUpdate request) {
        User authenticatedUser = getAutheticatedUser();

        if (!authenticatedUser.getId().equals(id)) {
            throw new CustomAuthenticationException("Você não tem permissão para atualizar este usuário.");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário com id não encontrado: " + id));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new CustomAuthenticationException("Senha antiga não confere.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        user = userRepository.save(user);

        return UserMapper.toUserResponse(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User authenticatedUser = getAutheticatedUser();

        if (!authenticatedUser.getId().equals(id) && !authenticatedUser.getRoles().contains(Role.ADMIN)) {
            throw new CustomAuthenticationException("Você não tem permissão para deletar este usuário.");
        }

        User userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário com id não encontrado: " + id));

        if (userToDelete.getRoles().contains(Role.ADMIN) && authenticatedUser.getId().equals(id)) {
            throw new CustomAuthenticationException("Você não pode deletar um usuário com a função ADMIN.");
        }

        userToDelete.setActive(false);
        userRepository.save(userToDelete);
    }

}
