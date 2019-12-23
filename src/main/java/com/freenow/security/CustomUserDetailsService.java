package com.freenow.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.freenow.dataaccessobject.DriverRepository;
import com.freenow.domainobject.DriverDO;

public class CustomUserDetailsService implements UserDetailsService
{

    private static final String ROLE_USER = "ROLE_USER";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    @Autowired
    DriverRepository driverRepository;

    @Autowired
    AdminRepository adminRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        Optional<DriverDO> driver = driverRepository.findByUsername(username);
        if (driver.isPresent())
        {
            DriverDO driverDetails = driver.get();
            return new CustomUserPrincipal(driverDetails.getUsername(), "{noop}"+driverDetails.getPassword(), ROLE_USER);
        }
        else
        {
            Optional<AdminDO> adminData = adminRepository.findByUsername(username);
            if (adminData.isPresent())
            {
                AdminDO admin = adminData.get();
                CustomUserPrincipal customUserPrincipal = new CustomUserPrincipal(admin.getUsername(), "{noop}"+admin.getPassword(), ROLE_ADMIN+","+ROLE_USER);
                return customUserPrincipal;
            }
        }

        throw new UsernameNotFoundException(String.format("User '%s' not found", username));
    }

}
