package com.capstone.tokenatm.service;

<<<<<<< HEAD
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

    Optional<TokenCountEntity> getStudentTokenCount(Integer user_id);

    void syncTokensOnDeadline() throws JSONException, IOException;
=======
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
>>>>>>> 674a9d9 (add remove template)
}

