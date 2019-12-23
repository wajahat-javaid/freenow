package com.freenow.security;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(
    name = "admin_credentials",
    uniqueConstraints = @UniqueConstraint(name = "uc_admin_username", columnNames = {"username"}))
public class AdminDO
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime dateCreated = ZonedDateTime.now();

    @Column(nullable = false)
    @NotNull(message = "Username can not be null!")
    private String username;

    @Column(nullable = false)
    @NotNull(message = "Password can not be null!")
    private String password;

    @Column(nullable = false)
    private Boolean deleted = false;
    
    
    public Long getId()
    {
        return id;
    }


    public void setId(Long id)
    {
        this.id = id;
    }


    public String getUsername()
    {
        return username;
    }


    public void setUsername(String username)
    {
        this.username = username;
    }


    public String getPassword()
    {
        return password;
    }


    public void setPassword(String password)
    {
        this.password = password;
    }


    public Boolean getDeleted()
    {
        return deleted;
    }


    public void setDeleted(Boolean deleted)
    {
        this.deleted = deleted;
    }

    
}
