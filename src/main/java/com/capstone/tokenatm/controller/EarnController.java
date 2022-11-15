package com.capstone.tokenatm.controller;

import com.capstone.tokenatm.entity.TokenCountEntity;
import com.capstone.tokenatm.exceptions.InternalServerException;
import com.capstone.tokenatm.service.EarnService;
import com.capstone.tokenatm.service.Student;
import com.capstone.tokenatm.service.TokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

@RestController
public class EarnController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EarnService.class);

    @Autowired
    EarnService earnService;

    @GetMapping(path="/tokens/{user_id}")
    public @ResponseBody Optional<TokenCountEntity> getTokenForStudent(@PathVariable Integer user_id) {
        return earnService.getStudentTokenCount(user_id);
    }

    //For testing if Qualtrics is working
    @GetMapping("/whoami")
    public String whoami(
    ) throws InternalServerException {
        try {
            return earnService.getIdentity();
        } catch (JSONException | IOException e) {
            LOGGER.error(e.toString());
            throw new InternalServerException();
        }
    }

    @GetMapping("/survey_export")
        public Set<String> getSurveyExport(
    ) throws InternalServerException {
        return earnService.getSurveyCompletions("SV_8oIf0qAz5g0TFiK");
    }

    @GetMapping("/grades")
    public HashMap<Object, Object> getStudentsData(
    )  throws InternalServerException {
        try {
            return earnService.getStudentGrades();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    @GetMapping("/students")
    public Iterable<TokenCountEntity> getStudents(
    ) {
        return earnService.getAllStudentTokenCounts();
    }

    @GetMapping("/token_grades")
    public Map<String, Double> getTokenGrades(
    ) throws InternalServerException {
        try {
            return earnService.getStudentTokenGrades();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    @GetMapping("/courses")
    public Map<String, Object> getCourseData(
    ) throws InternalServerException {
        try {
            return earnService.getCourseData();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }
}