package com.example.userAuth.data;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class UpdatePassword {
    @NotEmpty(message = "Password should not be empty.")
    @Size(min = 2, message = "Minimum length of password should be 2.")
    private String oldPassword;
    @NotEmpty(message = "Password should not be empty.")
    @Size(min = 2, message = "Minimum length of password should be 2.")
    private String newPassword;

    public UpdatePassword() {
    }

    public UpdatePassword(String oldPassword, String newPassword) {
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

    @Override
    public String toString() {
        return "UpdatePassword{" +
                "oldPassword='" + oldPassword + '\'' +
                ", newPassword='" + newPassword + '\'' +
                '}';
    }
}
