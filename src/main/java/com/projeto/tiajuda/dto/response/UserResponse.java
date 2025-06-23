package com.projeto.tiajuda.dto.response;

import com.projeto.tiajuda.entity.enums.Role;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;

// Representa os dados que será enviado de volta ao cliente após um registro bem sucedido

public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private Collection<Role> roles;
    private Instant createdAt;
    private Instant updatedAt;

    public UserResponse(Long id, String name, String email, Collection<Role> roles, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.roles = (roles != null) ? new ArrayList<>(roles) : new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
