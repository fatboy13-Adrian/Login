package com.user.login.Exception;

public class UserNotFoundException extends RuntimeException
{
    public UserNotFoundException(String userId)
    {
        super("User ID " +userId+ "not found");
    }
}
