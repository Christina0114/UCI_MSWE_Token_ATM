package com.capstone.tokenatm.service;

import com.capstone.tokenatm.exceptions.BadRequestException;
import org.springframework.boot.configurationprocessor.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public interface EarnService {
    HashMap<Object, Object> getUsers() throws IOException, JSONException;

    HashMap<Object, Object> getStudentGrades() throws IOException, JSONException;

    HashMap<Object, Object> getCourseData() throws IOException, JSONException;

    Map<String, Double> getStudentTokenGrades() throws IOException, JSONException;

    Map<String, Double> getStudentQuizScores(int quizId) throws IOException, JSONException;

    String getSurveyDistributionHistory() throws IOException, JSONException, BadRequestException;

    String sync() throws IOException, JSONException;
}

