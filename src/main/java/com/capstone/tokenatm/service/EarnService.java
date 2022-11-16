package com.capstone.tokenatm.service;

import antlr.Token;
import com.capstone.tokenatm.entity.TokenCountEntity;
import com.capstone.tokenatm.exceptions.InternalServerException;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.io.IOException;
import java.util.*;

public interface EarnService {

    HashMap<Object, Object> getStudentGrades() throws IOException, JSONException;

    Map<String, Object> getCourseData() throws IOException, JSONException;

    Map<String, Double> getStudentTokenGrades() throws IOException, JSONException;

    @Retryable(value = InternalServerException.class, maxAttempts = 10, backoff = @Backoff(delay = 1_000))
    Set<String> getSurveyCompletions(String surveyId) throws InternalServerException;

    String getIdentity() throws IOException, JSONException;

    Iterable<TokenCountEntity> getAllStudentTokenCounts();

    Optional<TokenCountEntity> getStudentTokenCount(String user_id);

    void syncTokensOnDeadline() throws JSONException, IOException;

    Iterable<TokenCountEntity> manualSyncTokens() throws JSONException, IOException;
}

