package com.capstone.tokenatm.controller;

import com.capstone.tokenatm.exceptions.BadRequestException;
import com.capstone.tokenatm.exceptions.InternalServerException;
import com.capstone.tokenatm.service.EarnService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class EarnController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EarnService.class);

    @Autowired
    EarnService earnService;

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
        public List<String> getSurveyExport(
    ) {
        try {
            return earnService.getSurveyCompletions("SV_8oIf0qAz5g0TFiK");
        } catch (InternalServerException e) {
            LOGGER.error("Internal Server Exception");
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/sync")
    public String sync(
    ){
        try {
            return earnService.sync();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/users")
    public HashMap<Object, Object> users(
    ){
        try {
            return earnService.getUsers();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/grades")
    public HashMap<Object, Object> getStudentsData(
    ){
        try {
            return earnService.getStudentGrades();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/token_grades")
    public Map<String, Double> getTokenGrades(
    ){
        try {
            return earnService.getStudentTokenGrades();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/courses")
    public HashMap<Object, Object> getCourseData(
    ){
        try {
            return earnService.getCourseData();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
