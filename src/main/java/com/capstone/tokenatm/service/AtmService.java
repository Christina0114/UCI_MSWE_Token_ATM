package com.capstone.tokenatm.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.capstone.tokenatm.controller.TokenSyncController;
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

    @Autowired
    TokenSyncController atmController;

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

    @GetMapping("/users")
    public HashMap<Object, Object> users(
    ){
        try {
            return atmController.getUsers();
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
