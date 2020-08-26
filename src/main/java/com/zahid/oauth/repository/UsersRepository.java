package com.zahid.oauth.repository;


import com.zahid.oauth.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UsersRepository extends CrudRepository<User, Integer> {

    Optional<User> findByLoginId(String loginId);

}
