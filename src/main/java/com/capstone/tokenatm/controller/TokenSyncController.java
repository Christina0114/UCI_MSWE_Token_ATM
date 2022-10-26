package com.capstone.tokenatm.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenSyncController {

    private static final String BEARER_TOKEN = "7~sKb3Kq7M9EjSgDtMhugxCEs5oD76pbJgBWAFScBliSi7Iin8QubiBHEBlrWfYunG";
    private static final String API_ENDPOINT = "https://canvas.instructure.com/api/v1";
    private static final int COURSE_ID = 3737737;
    private static List<Integer> tokenQuizzes = Arrays.asList(12427623);
    public String sync() throws IOException, JSONException {
        String result = getStudentGrades().toString();
        return result;
    }

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

    /**
     * Fetch grades of all students for a specific assignment
     * @param assignmentId Assignment ID, can be looked up using List Assignments API
     * @return Map of grades, key is student id, value is grade of that student for this assignment
     * @throws IOException
     * @throws JSONException
     */
    private HashMap<Object, Object> getStudentAssignmentGrades(int assignmentId) throws IOException, JSONException {
        Map<Object, Object> users = getUsers();
        //TODO: fetch grades of quizzes
        return null;
    }

    /**
     * Fetch grades of all quizzes that is required to earn tokens
     * @return Map of grades, key is student id, value is grade of that student for this assignment
     * @throws IOException
     * @throws JSONException
     */
    public HashMap<Object, Object> getStudentTokenGrades() throws IOException, JSONException {
        return null;
    }

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

    public StringBuffer apiProcess(URL url) throws IOException {
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
}
