package com.capstone.tokenatm.service;

import com.capstone.tokenatm.entity.TokenCountEntity;
import com.capstone.tokenatm.exceptions.InternalServerException;
import org.springframework.boot.configurationprocessor.json.JSONException;


import java.io.IOException;
import java.util.*;

public interface EarnService {

    HashMap<Object, Object> getStudentGrades() throws IOException, JSONException;

    Map<String, Object> getCourseData() throws IOException, JSONException;

    Map<String, Double> getStudentTokenGrades() throws IOException, JSONException;

    Set<String> getSurveyCompletions(String surveyId) throws InternalServerException;

    String getIdentity() throws IOException, JSONException;

    Iterable<TokenCountEntity> getAllStudentTokenCounts();

    Optional<TokenCountEntity> getStudentTokenCount(Integer user_id);

    void syncTokensOnDeadline() throws JSONException, IOException;
}
