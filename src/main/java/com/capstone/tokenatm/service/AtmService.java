package com.capstone.tokenatm.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.capstone.tokenatm.controller.QualtricsSyncController;
import com.capstone.tokenatm.controller.TokenSyncController;
import com.capstone.tokenatm.exceptions.BadRequestException;
import com.capstone.tokenatm.exceptions.InternalServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by khwanchanok on 4/6/2018 AD.
 */

@RestController
public class AtmService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AtmService.class);
    @Autowired
    TokenSyncController atmController;
    @Autowired
    QualtricsSyncController qualtricsController;

    @GetMapping("/survey_distributions")
    public Map<Object, Object> whoami(
    ) throws BadRequestException, InternalServerException {
        try {
            LOGGER.info(qualtricsController.getSurveyDistributionHistory());
        } catch (JSONException | IOException e) {
            LOGGER.error(e.toString());
            throw new InternalServerException();
        } catch (BadRequestException e) {
            throw e;
        }
        return null;
    }

    @GetMapping("/sync")
    public String sync(
            ){
        try {
            return atmController.sync();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/students")
    public HashMap<Object, Object> users(
    ){
        try {
            return atmController.getStudents();
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
            return atmController.getStudentGrades();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Fetch grades related to Token earning assignments
    @GetMapping("/token_grades")
    public Map<String, Double> getTokenGrades(
    ){
        try {
            return atmController.getStudentTokenGrades();
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
            return atmController.getCourseData();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
