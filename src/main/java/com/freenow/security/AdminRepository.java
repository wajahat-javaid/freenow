package com.freenow.security;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface AdminRepository extends CrudRepository<AdminDO, Long>
{

    Optional<AdminDO> findByUsername(String username);
}
