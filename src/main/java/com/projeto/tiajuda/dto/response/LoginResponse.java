package com.projeto.tiajuda.dto.response;

import com.projeto.tiajuda.entity.enums.Role;

import java.util.Collection;
import java.util.Set;

// Dados que serão retornados ao cliente após um login bem-sucedido

public class LoginResponse {

    private String token;
    private String type = "Bearer ";
    private Long id;
    private String name;
    private String email;
    private Collection<Role> roles;

    public LoginResponse() {
    }

    public LoginResponse(String token, Long id, String name, String email, Collection<Role> roles) {
        this.token = token;
        this.id = id;
        this.name = name;
        this.email = email;
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }
}
