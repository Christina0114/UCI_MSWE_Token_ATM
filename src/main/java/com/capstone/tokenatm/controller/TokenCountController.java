package com.capstone.tokenatm.controller;

import com.capstone.tokenatm.entity.TokenCountEntity;
import com.capstone.tokenatm.exceptions.InternalServerException;
import com.capstone.tokenatm.service.EarnService;
import com.capstone.tokenatm.service.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class TokenCountController {
    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    EarnService earnService;

    @GetMapping(path="/sync") // Map ONLY POST Requests
    public @ResponseBody String sync_token () throws InternalServerException, JSONException, IOException {

        Map<String, Object> student = earnService.getStudent();
        for ( Object user : (ArrayList) student.get("result")) {
            TokenCountEntity n = new TokenCountEntity();
            Integer user_id = Integer.valueOf(((HashMap<String, String>)user).get("user_id"));
            Integer user_token = Integer.valueOf(((HashMap<String, String>)user).get("token_amount"));
            String user_name = ((HashMap<String, String>)user).get("user_name");

            n.setUser_name(user_name);
            n.setUser_id(user_id);
            n.setToken_count(user_token);
            n.setTimestamp(new Date());
            tokenRepository.save(n);
        }

        return "Saved";
    }

    @GetMapping(path="/tokens")
    public @ResponseBody Iterable<TokenCountEntity> getAllTokens() {
        return tokenRepository.findAll();
    }

    @GetMapping(path="/tokens/{user_id}")
    public @ResponseBody Optional<TokenCountEntity> getTokenForStudent(@PathVariable Integer user_id) {
        return tokenRepository.findById(user_id);
    }
}
