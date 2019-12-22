package com.freenow.datatransferobject;

import javax.validation.constraints.NotNull;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.freenow.domainvalue.GeoCoordinate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DriverDTO extends ResourceSupport
{

    @JsonIgnore
    private Long driverId;

    @NotNull(message = "Username can not be null!")
    private String username;

    @NotNull(message = "Password can not be null!")
    private String password;

    private GeoCoordinate coordinate;


    private DriverDTO()
    {}


    private DriverDTO(Long driverId, String username, String password, GeoCoordinate coordinate, Link link)
    {

        this.driverId = driverId;
        this.username = username;
        this.password = password;
        this.coordinate = coordinate;
        add(link);
    }


    public static DriverDTOBuilder newBuilder()
    {
        return new DriverDTOBuilder();
    }


    public String getUsername()
    {
        return username;
    }


    public String getPassword()
    {
        return password;
    }


    public GeoCoordinate getCoordinate()
    {
        return coordinate;
    }


    public Long getDriverId()
    {
        return driverId;
    }


    public void setDriverId(Long driverId)
    {
        this.driverId = driverId;
    }

    public static class DriverDTOBuilder
    {
        private Long driverId;
        private String username;
        private String password;
        private GeoCoordinate coordinate;
        Link link;


        public DriverDTOBuilder setId(Long id)
        {
            this.driverId = id;
            return this;
        }


        public DriverDTOBuilder setUsername(String username)
        {
            this.username = username;
            return this;
        }


        public DriverDTOBuilder setPassword(String password)
        {
            this.password = password;
            return this;
        }


        public DriverDTOBuilder setCoordinate(GeoCoordinate coordinate)
        {
            this.coordinate = coordinate;
            return this;
        }


        public DriverDTO createDriverDTO()
        {
            return new DriverDTO(driverId, username, password, coordinate, link);
        }


        public void setCarLink(Link link)
        {
            this.link = link;
        }

    }

}
