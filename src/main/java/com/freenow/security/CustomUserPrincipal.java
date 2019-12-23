package com.freenow.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserPrincipal implements UserDetails
{

    private static final long serialVersionUID = 6499927494696336380L;

    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;


    public CustomUserPrincipal()
    {}


    public CustomUserPrincipal(String username, String password, String role)
    {
        this.username = username;
        this.password = password;
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        if (role.contains(","))
        {
            Arrays.stream(role.split(",")).map(s -> new SimpleGrantedAuthority(s)).forEach(grantedAuthorities::add);
        }
        else
        {
            grantedAuthorities.add(new SimpleGrantedAuthority(role));
        }

        this.authorities = grantedAuthorities;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return authorities;
    }


    @Override
    public String getPassword()
    {
        return password;
    }


    @Override
    public String getUsername()
    {
        return username;
    }


    @Override
    public boolean isAccountNonExpired()
    {
        return true;
    }


    @Override
    public boolean isAccountNonLocked()
    {
        return true;
    }


    @Override
    public boolean isCredentialsNonExpired()
    {
        return true;
    }


    @Override
    public boolean isEnabled()
    {
        return true;
    }

}
