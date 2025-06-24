package com.projeto.tiajuda.dto.response;

import com.projeto.tiajuda.entity.enums.Role;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;

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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

}
