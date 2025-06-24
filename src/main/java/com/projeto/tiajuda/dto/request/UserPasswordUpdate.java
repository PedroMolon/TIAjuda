package com.projeto.tiajuda.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserPasswordUpdate {

    @NotBlank(message = "A senha antiga não pode estar em branco.")
    private String oldPassword;

    @NotBlank(message = "A nova senha não pode estar em branco.")
    @Size(min = 6, message = "A nova senha deve ter no mínimo 6 caracteres.")
    private String newPassword;

    public UserPasswordUpdate() {
    }

    public UserPasswordUpdate(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

}
