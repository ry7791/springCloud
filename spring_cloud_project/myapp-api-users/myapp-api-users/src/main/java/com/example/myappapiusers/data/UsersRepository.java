package com.example.myappapiusers.data;

import org.springframework.data.repository.CrudRepository;

public interface UsersRepository extends CrudRepository<User1Entity, Long> {

    User1Entity findByEmail(String email);
}
