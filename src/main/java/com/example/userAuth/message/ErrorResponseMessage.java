package com.example.userAuth.message;

public class ErrorResponseMessage {
    public static final String nullFirstName = "Please enter first name.";
    public static final String nullLastName = "Please enter last name.";
    public static final String nullEmail = "Please enter email.";
    public static final String nullPassword = "Please enter password.";
    public static final String noUserExistsWithId = "No user exists with Id ";
    public static final String emailDoestNotExists = "Email does not exists.";
    public static final String notValidFirstName = "Please enter a valid first name.";
    public static final String notValidLastName = "Please enter a valid last name.";
    public static final String notValidEmail = "Please enter a valid email.";
    public static final String notValidPassword = "Please enter a valid password.";
    public static final String emailIsTaken = "Email is taken.";
    public static final String oldPasswordWrong = "Old password is wrong.";
    public static final String notValidInput = "Enter a valid input.";
    public static final String serviceLayerErrorAdding = "Something went wrong in the service layer while adding the user.";
    public static final String noUsers = "No users currently.";
    public static final String serviceLayerErrorFetching = "Something went wrong in the service layer while fetching the users.";
    public static final String serviceLayerErrorDeleting = "Something went wrong while deleting the user in the service layer.";
    public static final String passwordMatches = "Password matches,please enter another password";
    public static final String passwordNotMatches = "Password does not match.";
    public static final String userNotLoggedIn = "User not logged in.";
    public static final String serviceLayerErrorUpdating = "Something went wrong while updating the password of user in the service layer";
    public static final String serviceLayerErrorLoggingIn = "Something went wrong while logging in the user in the service layer.";
    public static final String serviceLayerErrorLoggingOut = "Something went wrong while logging out the user in the service layer";
    public static final String controllerLayerErrorLoggingOut = "Something went wrong while logging out the user in the controller layer";
    public static final String controllerLayerErrorLoggingIn = "Something went wrong while logging in the user in the controller layer.";
    public static final String controllerLayerErrorUpdating = "Something went wrong while updating the password of user in the controller layer";
    public static final String controllerLayerErrorDeleting = "Something went wrong while deleting the user in the controller layer.";
    public static final String controllerLayerErrorFetching = "Something went wrong in the controller layer while fetching the users.";
    public static final String controllerLayerErrorAdding = "Something went wrong in the controller layer while adding the user.";

}
