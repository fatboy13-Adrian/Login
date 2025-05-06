package com.user.login.Exception;

public class UsernameNotFoundException extends RuntimeException
{
    public UsernameNotFoundException(String username)
    {
        super("Username " +username+ "not found");
    }
}
