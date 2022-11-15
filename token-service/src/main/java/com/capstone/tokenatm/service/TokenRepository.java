package com.capstone.tokenatm.service;

import com.capstone.tokenatm.entity.TokenCountEntity;
import org.springframework.data.repository.CrudRepository;

public interface TokenRepository extends CrudRepository<TokenCountEntity, Integer> {

}
