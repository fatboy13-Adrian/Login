package com.user.login.Exception;

public class EmailNotFoundException extends RuntimeException
{
    public EmailNotFoundException(String email)
    {
        super(email+ " not found in DB");
    }
}