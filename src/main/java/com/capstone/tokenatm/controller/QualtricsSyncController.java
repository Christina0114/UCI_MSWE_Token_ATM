package com.capstone.tokenatm.controller;

import com.capstone.tokenatm.exceptions.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

@RestController
public class QualtricsSyncController {

    private static final Logger LOGGER = LoggerFactory.getLogger(QualtricsSyncController.class);
    private static final String API_KEY = "3yoP4lV2G7wmxOVtIkH6G8K5IcGDgtdUf2Ys3um9";
    private static final String API_ENDPOINT = "https://iad1.qualtrics.com/API/v3";
    private static final String surveyId = "SV_56fa1nPQlOAqnmm";

    public String getSurveyDistributionHistory() throws IOException, JSONException, BadRequestException {
        String distributionId = getDistributionId(surveyId);
        URL url = UriComponentsBuilder
                .fromUriString(API_ENDPOINT + "/distributions/" + distributionId + "/history")
                .queryParam("surveyId", surveyId)
                .build().toUri().toURL();
        StringBuffer response = apiProcess(url);
//        JSONObject resultObj = new JSONObject(String.valueOf(response));
//        JSONArray surveyDistributions = resultObj.getJSONObject("result").getJSONArray("elements");
        return response.toString();
    }

    private String getDistributionId(String surveyId) throws IOException, JSONException, BadRequestException {
        URL url = UriComponentsBuilder
                .fromUriString(API_ENDPOINT + "/distributions")
                .queryParam("surveyId", surveyId)
                .build().toUri().toURL();
        StringBuffer response = apiProcess(url);
        JSONObject resultObj = new JSONObject(String.valueOf(response));
        JSONArray elementsArray = resultObj.getJSONObject("result").getJSONArray("elements");
        if (elementsArray.length() == 0) {
            LOGGER.error("Distributions array is empty");
        }
        JSONObject distributionObj = elementsArray.getJSONObject(0);
        return distributionObj.getString("id");
    }

    public StringBuffer apiProcess(URL url) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestProperty("X-API-TOKEN", API_KEY);
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        httpURLConnection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
        String output;

        StringBuffer response = new StringBuffer();
        while ((output = in.readLine()) != null) {
            response.append(output);
        }
        in.close();
        return response;
    }
}
