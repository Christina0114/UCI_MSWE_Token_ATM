package com.capstone.tokenatm.controller;

import com.capstone.tokenatm.entity.TokenCountEntity;
import com.capstone.tokenatm.service.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class TokenCountController {
    @Autowired
    private TokenRepository tokenRepository;

    @GetMapping(path="/tokens")
    public @ResponseBody Iterable<TokenCountEntity> getAllTokens() {
        return tokenRepository.findAll();
    }

    @GetMapping(path="/tokens/{user_id}")
    public @ResponseBody Optional<TokenCountEntity> getTokenForStudent(@PathVariable Integer user_id) {
        return tokenRepository.findById(user_id);
    }
}
