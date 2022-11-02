package com.capstone.tokenatm.service;

import com.capstone.tokenatm.exceptions.BadRequestException;
import com.capstone.tokenatm.exceptions.InternalServerException;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface EarnService {
    HashMap<Object, Object> getUsers() throws IOException, JSONException;

    HashMap<Object, Object> getStudentGrades() throws IOException, JSONException;

    HashMap<Object, Object> getCourseData() throws IOException, JSONException;

    Map<String, Double> getStudentTokenGrades() throws IOException, JSONException;

    @Retryable(value = InternalServerException.class, maxAttempts = 10, backoff = @Backoff(delay = 1_000))
    List<String> getSurveyCompletions(String surveyId) throws InternalServerException;

    String getIdentity() throws IOException, JSONException;

    String sync() throws IOException, JSONException;
}
