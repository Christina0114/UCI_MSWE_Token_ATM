package com.capstone.tokenatm.controller;

import java.util.Date;

import com.capstone.tokenatm.service.LogRepository;
import com.capstone.tokenatm.entity.SpendLogEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
// This means that this class is a Controller
public class SpendLogController {
    @Autowired // This means to get the bean called LogRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private LogRepository logRepository;

    @PostMapping(path="/add_log") // Map ONLY POST Requests
    public @ResponseBody String addLog (@RequestParam Integer user_id, @RequestParam String type, @RequestParam Integer token_count, @RequestParam String source) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request

        SpendLogEntity n = new SpendLogEntity();
        n.setUser_id(user_id);
        n.setType(type);
        n.setTokenCount(token_count);
        n.setSourcee(source);
        n.setTimestamp(new Date());
        logRepository.save(n);
        return "Saved";
    }

    @GetMapping(path="/logs")
    public @ResponseBody Iterable<SpendLogEntity> getLogs() {
        // This returns a JSON or XML with the logs
        return logRepository.findAll();
    }

    @GetMapping(path="/logs/{user_id}")
    public @ResponseBody Iterable<SpendLogEntity> getLogsForStudent(@PathVariable Integer user_id) {
        return logRepository.findByUserId(user_id);
    }
}