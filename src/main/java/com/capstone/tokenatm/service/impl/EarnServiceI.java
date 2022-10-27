package com.capstone.tokenatm.service.impl;

import com.capstone.tokenatm.exceptions.BadRequestException;
import com.capstone.tokenatm.service.EarnService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Service("EarnService")
public class EarnServiceI implements EarnService {
    private static final String BEARER_TOKEN = "7~sKb3Kq7M9EjSgDtMhugxCEs5oD76pbJgBWAFScBliSi7Iin8QubiBHEBlrWfYunG";
    private static final String API_ENDPOINT = "https://canvas.instructure.com/api/v1";
    private static final int COURSE_ID = 3737737;
    private static List<Integer> tokenQuizzes = Arrays.asList(12427623);
    private static final String surveyId = "SV_56fa1nPQlOAqnmm";

    private static final Logger LOGGER = LoggerFactory.getLogger(EarnService.class);
    @Override
    public String sync() throws IOException, JSONException {
        return getStudentGrades().toString();
    }

    private StringBuffer apiProcess(URL url) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + BEARER_TOKEN);
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

    @Override
    public HashMap<Object, Object> getUsers() throws IOException, JSONException {
        URL url = new URL(API_ENDPOINT + "/courses/" + COURSE_ID + "/users?per_page=50&enrollment_type=student");
        StringBuffer response = apiProcess(url);
        JSONArray result = new JSONArray(String.valueOf(response));

        HashMap<Object, Object> users = new HashMap<>();
        for (int i = 0; i < result.length(); i++) {
            String user_id = ((JSONObject) result.get(i)).get("id").toString();
            String users_name = ((JSONObject) result.get(i)).get("name").toString();
            users.put(user_id, users_name);
        }
        return users;
    }

    @Override
    public HashMap<Object, Object> getStudentGrades() throws IOException, JSONException {
        Map<Object, Object> users = getUsers();
        String users_id = users.entrySet().stream().map(e -> "&student_ids%5B%5D=" + e.getKey()).collect(Collectors.joining(""));
        URL url = new URL(API_ENDPOINT + "/courses/" + COURSE_ID + "/students/submissions?exclude_response_fields%5B%5D=preview_url&grouped=1&response_fields%5B%5D=assignment_id&response_fields%5B%5D=attachments&response_fields%5B%5D=attempt&response_fields%5B%5D=cached_due_date&response_fields%5B%5D=entered_grade&response_fields%5B%5D=entered_score&response_fields%5B%5D=excused&response_fields%5B%5D=grade&response_fields%5B%5D=grade_matches_current_submission&response_fields%5B%5D=grading_period_id&response_fields%5B%5D=id&response_fields%5B%5D=late&response_fields%5B%5D=late_policy_status&response_fields%5B%5D=missing&response_fields%5B%5D=points_deducted&response_fields%5B%5D=posted_at&response_fields%5B%5D=redo_request&response_fields%5B%5D=score&response_fields%5B%5D=seconds_late&response_fields%5B%5D=submission_type&response_fields%5B%5D=submitted_at&response_fields%5B%5D=url&response_fields%5B%5D=user_id&response_fields%5B%5D=workflow_state&student_ids%5B%5D=" + users_id + "&per_page=100");
        StringBuffer response = apiProcess(url);
        JSONArray result = new JSONArray(String.valueOf(response));

        HashMap<Object, Object> students_data = new HashMap<>();
        HashMap<Object, Object> courses_name = getCourseData();
        for (int i = 0; i < result.length(); i++) {
            ArrayList<String> grades = new ArrayList<>();
            for (int j = 0; j < ((JSONArray) ((JSONObject) result.get(i)).get("submissions")).length(); j++) {
                String assignment_id = ((JSONObject) ((JSONArray) ((JSONObject) result.get(i)).get("submissions")).get(j)).get("assignment_id").toString();
                String score = ((JSONObject) ((JSONArray) ((JSONObject) result.get(i)).get("submissions")).get(j)).get("score").toString();
                grades.add(score + "(" + courses_name.get(assignment_id) + ")");
            }
            String user_id = ((JSONObject) result.get(i)).get("user_id").toString();
            String user_name = users.get(user_id).toString();
            students_data.put(user_name + "(" + user_id + ")", grades);
        }
        return students_data;
    }


    @Override
    public HashMap<Object, Object> getCourseData() throws IOException, JSONException {
        URL url = new URL(API_ENDPOINT + "/courses/" + COURSE_ID + "/assignment_groups?exclude_assignment_submission_types%5B%5D=wiki_page&exclude_response_fields%5B%5D=description&exclude_response_fields%5B%5D=in_closed_grading_period&exclude_response_fields%5B%5D=needs_grading_count&exclude_response_fields%5B%5D=rubric&include%5B%5D=assignment_group_id&include%5B%5D=assignment_visibility&include%5B%5D=assignments&include%5B%5D=grades_published&include%5B%5D=post_manually&include%5B%5D=module_ids&override_assignment_dates=false&per_page=100");
        StringBuffer response = apiProcess(url);
        JSONArray result = new JSONArray(String.valueOf(response));

        HashMap<Object, Object> course_data = new HashMap<>();
        for (int i = 0; i < result.length(); i++) {
            for (int j = 0; j < ((JSONArray) ((JSONObject) result.get(i)).get("assignments")).length(); j++) {
                String assignment_id = ((JSONObject) ((JSONArray) ((JSONObject) result.get(i)).get("assignments")).get(j)).get("id").toString();
                String assignment_name = ((JSONObject) ((JSONArray) ((JSONObject) result.get(i)).get("assignments")).get(j)).get("name").toString();
                course_data.put(assignment_id, assignment_name);
            }
        }
        return course_data;
    }

    /**
     * Fetch grades of all quizzes that is required to earn tokens
     *
     * @return Map of grades, key is student id, value is grade of that student for this assignment
     * @throws IOException
     * @throws JSONException
     */
    @Override
    public Map<String, Double> getStudentTokenGrades() throws IOException, JSONException {
        Map<String, Double> averageQuizScores = new HashMap<>();
        for (int quizId : tokenQuizzes) {
            Map<String, Double> quizScores = getStudentQuizScores(quizId);
            quizScores.entrySet().forEach(e -> {
                String userId = e.getKey();
                averageQuizScores.put(userId, averageQuizScores.getOrDefault(userId, 0.0) + e.getValue());
            });
        }
        averageQuizScores.entrySet().forEach(e -> e.setValue(e.getValue() / tokenQuizzes.size()));
        return averageQuizScores;
    }

    @Override
    /**
     * Fetch grades of all students for a specific quiz
     *
     * @param quizId Quiz ID, can be looked up using List Assignments API
     * @return Map of quiz scores, key is student id, value is score of the quiz for this student
     * @throws IOException
     * @throws JSONException
     */
    public Map<String, Double> getStudentQuizScores(int quizId) throws IOException, JSONException {
        Map<Object, Object> users = getUsers();
        String users_id = users.entrySet().stream().map(e -> "&student_ids%5B%5D=" + e.getKey()).collect(Collectors.joining(""));
        URL url = new URL(API_ENDPOINT + "/courses/" + COURSE_ID + "/quizzes/" + quizId + "/submissions?exclude_response_fields%5B%5D=preview_url&grouped=1&response_fields%5B%5D=assignment_id&response_fields%5B%5D=attachments&response_fields%5B%5D=attempt&response_fields%5B%5D=cached_due_date&response_fields%5B%5D=entered_grade&response_fields%5B%5D=entered_score&response_fields%5B%5D=excused&response_fields%5B%5D=grade&response_fields%5B%5D=grade_matches_current_submission&response_fields%5B%5D=grading_period_id&response_fields%5B%5D=id&response_fields%5B%5D=late&response_fields%5B%5D=late_policy_status&response_fields%5B%5D=missing&response_fields%5B%5D=points_deducted&response_fields%5B%5D=posted_at&response_fields%5B%5D=redo_request&response_fields%5B%5D=score&response_fields%5B%5D=seconds_late&response_fields%5B%5D=submission_type&response_fields%5B%5D=submitted_at&response_fields%5B%5D=url&response_fields%5B%5D=user_id&response_fields%5B%5D=workflow_state&student_ids%5B%5D=" + users_id + "&per_page=100");
        StringBuffer response = apiProcess(url);
        JSONObject resultObj = new JSONObject(String.valueOf(response));
        JSONArray result = resultObj.getJSONArray("quiz_submissions");

        Map<String, Double> quizScores = new HashMap<>();
        for (int i = 0; i < result.length(); i++) {
            JSONObject jsonObject = result.getJSONObject(i);
            double kept_score = jsonObject.getDouble("kept_score"), max_score = jsonObject.getDouble("quiz_points_possible");
            double percentage_score = kept_score / max_score * 100;
            String studentId = String.valueOf(jsonObject.getInt("user_id"));
            quizScores.put(studentId, percentage_score);
        }
        return quizScores;
    }

    @Override
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
}
