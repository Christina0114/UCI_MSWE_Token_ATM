package com.capstone.tokenatm.service.impl;

import com.capstone.tokenatm.exceptions.BadRequestException;
import com.capstone.tokenatm.exceptions.InternalServerException;
import com.capstone.tokenatm.service.EarnService;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Service("EarnService")
public class EarnServiceI implements EarnService {

    //Canvas API settings
    //TODO: The API Endpoint and Bearer token is only used for testing. Please change to UCI endpoint and actual tokens in prod
    //Bearer Token for dummy canvas endpoint
    private static final String BEARER_TOKEN = "7~sKb3Kq7M9EjSgDtMhugxCEs5oD76pbJgBWAFScBliSi7Iin8QubiBHEBlrWfYunG";
    //Testing endpoint for Canvas
    private static final String CANVAS_API_ENDPOINT = "https://canvas.instructure.com/api/v1";
    //Course Id
    private static final int COURSE_ID = 3737737;
    //List of Quizzes in the first module (which needs over 70% average to earn the initial 2 tokens)
    private static List<Integer> tokenQuizIds = Arrays.asList(12427623,12476618,12476695);

    //List of surveys
    private static List<String> tokenSurveyIds = Arrays.asList("SV_8oIf0qAz5g0TFiK");

    //Qualtrics API Settings
    //TODO: The API Endpoint and API key is only used for testing. Please change to UCI endpoint and actual keys in prod
    //API Key for Qualtrics
    private static final String API_KEY = "3yoP4lV2G7wmxOVtIkH6G8K5IcGDgtdUf2Ys3um9";
    //Testing endpoint for Qualtrics
    private static final String QUALTRICS_API_ENDPOINT = "https://iad1.qualtrics.com/API/v3";
    //Survey Id
    private static final String surveyId = "SV_8oIf0qAz5g0TFiK";

    private static final String QualtricsBody = "{\"format\":\"json\",\"compress\":\"false\"}";
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    private static final Logger LOGGER = LoggerFactory.getLogger(EarnService.class);
    private static Map<String, Object> result = new HashMap<>();

    @Override
    public String sync() throws IOException, JSONException {
        return getStudentGrades().toString();
    }

    @Override
    public Map<String, Object> getStudent() throws IOException, JSONException, InternalServerException {
        ArrayList<HashMap<String, String>> users = getUsers();
        Map<String, Integer> token_summary = new HashMap<>();

        Map<String, Double> quiz_token = getStudentTokenGrades();


        for (Map.Entry<String, Double> entry : quiz_token.entrySet()) {
            String user_id = String.valueOf(entry.getKey());
            Double user_quiz = Double.valueOf(entry.getValue());
            if(user_quiz > 70.00){
                token_summary.put(user_id, token_summary.getOrDefault(user_id, 0) + 2);
            }
        }

        for (String survey_id : tokenSurveyIds) {
            List<String> student_emails = getSurveyCompletions(survey_id);
            student_emails.add("canapitest+4@gmail.com"); // fake_data
            for (HashMap<String, String> user : users) {
                if (student_emails.contains(user.get("user_email"))){
                    token_summary.put(user.get("user_id"), token_summary.getOrDefault(user.get("user_id"), 0) + 1);
                }
            }
        }

        for (HashMap<String, String> user : users) {
            user.put("token_amount", String.valueOf(token_summary.getOrDefault(user.get("user_id"),0)));
        }
        result.put("result",users);
        return result;
    }

    @Override
    public String getIdentity() throws IOException {
        URL url = UriComponentsBuilder
                .fromUriString(QUALTRICS_API_ENDPOINT + "/whoami")
                .build().toUri().toURL();
        String response = apiProcess(url, false);
        return String.valueOf(response);
    }

    private String apiProcess(URL url, Boolean isCanvas) throws IOException {
        return apiProcess(url, "", isCanvas);
    }

    private String apiProcess(URL url, String body, Boolean isCanvas) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request.Builder builder = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json");
        if (isCanvas) {
            builder.addHeader("Authorization", "Bearer " + BEARER_TOKEN);
        } else {
            builder.addHeader("X-API-TOKEN", API_KEY);
        }
        if (body.length() > 0) {
            builder.post(RequestBody.create(body, JSON));
        }
        Request request = builder.build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    @Override
    public ArrayList<HashMap<String, String>> getUsers() throws IOException, JSONException {
        URL url = new URL(CANVAS_API_ENDPOINT + "/courses/" + COURSE_ID + "/users?per_page=50&enrollment_type=student");
        String response = apiProcess(url, true);
        JSONArray result = new JSONArray(response);

        ArrayList<HashMap<String, String>> users = new ArrayList<>();
        for (int i = 0; i < result.length(); i++) {
            String user_id = ((JSONObject) result.get(i)).get("id").toString();
            String users_name = ((JSONObject) result.get(i)).get("name").toString();
            String users_email = ((JSONObject) result.get(i)).get("email").toString();
            HashMap<String, String> item = new HashMap<>();
            item.put("user_id",user_id);
            item.put("user_name",users_name);
            item.put("user_email",users_email);
            users.add(item);
        }
        return users;
    }

    @Override
    public HashMap<Object, Object> getStudentGrades() throws IOException, JSONException {
        ArrayList<HashMap<String, String>> users = getUsers();
        String users_id = users.stream().map(e -> "&student_ids%5B%5D=" + e.get("user_id")).collect(Collectors.joining(""));
        URL url = new URL(CANVAS_API_ENDPOINT + "/courses/" + COURSE_ID + "/students/submissions?exclude_response_fields%5B%5D=preview_url&grouped=1&response_fields%5B%5D=assignment_id&response_fields%5B%5D=attachments&response_fields%5B%5D=attempt&response_fields%5B%5D=cached_due_date&response_fields%5B%5D=entered_grade&response_fields%5B%5D=entered_score&response_fields%5B%5D=excused&response_fields%5B%5D=grade&response_fields%5B%5D=grade_matches_current_submission&response_fields%5B%5D=grading_period_id&response_fields%5B%5D=id&response_fields%5B%5D=late&response_fields%5B%5D=late_policy_status&response_fields%5B%5D=missing&response_fields%5B%5D=points_deducted&response_fields%5B%5D=posted_at&response_fields%5B%5D=redo_request&response_fields%5B%5D=score&response_fields%5B%5D=seconds_late&response_fields%5B%5D=submission_type&response_fields%5B%5D=submitted_at&response_fields%5B%5D=url&response_fields%5B%5D=user_id&response_fields%5B%5D=workflow_state&student_ids%5B%5D=" + users_id + "&per_page=100");
        String response = apiProcess(url, true);
        JSONArray result = new JSONArray(response);

        HashMap<Object, Object> students_data = new HashMap<>();
        for (int i = 0; i < result.length(); i++) {
            ArrayList<String> grades = new ArrayList<>();
            for (int j = 0; j < ((JSONArray) ((JSONObject) result.get(i)).get("submissions")).length(); j++) {
                String assignment_id = ((JSONObject) ((JSONArray) ((JSONObject) result.get(i)).get("submissions")).get(j)).get("assignment_id").toString();
                String score = ((JSONObject) ((JSONArray) ((JSONObject) result.get(i)).get("submissions")).get(j)).get("score").toString();
                grades.add(score + "(" + assignment_id + ")");

            }
            String user_id = ((JSONObject) result.get(i)).get("user_id").toString();
            students_data.put( "(" + user_id + ")", grades);
        }
        return students_data;
    }


    @Override
    public Map<String, Object> getCourseData() throws IOException, JSONException {
        URL url = new URL(CANVAS_API_ENDPOINT + "/courses/" + COURSE_ID + "/assignment_groups?exclude_assignment_submission_types%5B%5D=wiki_page&exclude_response_fields%5B%5D=description&exclude_response_fields%5B%5D=in_closed_grading_period&exclude_response_fields%5B%5D=needs_grading_count&exclude_response_fields%5B%5D=rubric&include%5B%5D=assignment_group_id&include%5B%5D=assignment_visibility&include%5B%5D=assignments&include%5B%5D=grades_published&include%5B%5D=post_manually&include%5B%5D=module_ids&override_assignment_dates=false&per_page=100");
        
        JSONArray response = new JSONArray(apiProcess(url, true));

        ArrayList<HashMap<Object, Object>> course_data = new ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            for (int j = 0; j < ((JSONArray) ((JSONObject) response.get(i)).get("assignments")).length(); j++) {
                HashMap<Object, Object> item = new HashMap<>();
                String assignment_id = ((JSONObject) ((JSONArray) ((JSONObject) response.get(i)).get("assignments")).get(j)).get("id").toString();
                String assignment_name = ((JSONObject) ((JSONArray) ((JSONObject) response.get(i)).get("assignments")).get(j)).get("name").toString();
                item.put("assignment_id",assignment_id);
                item.put("assignment_name",assignment_name);
                course_data.add(item);
            }
        }
        result.put("result",course_data);
        return result;
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
        for (int quizId : tokenQuizIds) {
            Map<String, Double> quizScores = getStudentQuizScores(quizId);
            quizScores.entrySet().forEach(e -> {
                String userId = e.getKey();
                averageQuizScores.put(userId, averageQuizScores.getOrDefault(userId, 0.0) + e.getValue());
            });
        }
        averageQuizScores.entrySet().forEach(e -> e.setValue(e.getValue() / tokenQuizIds.size()));
        return averageQuizScores;
    }

    /**
     * Fetch completion status of required surveys
     * See https://api.qualtrics.com/6b00592b9c013-start-response-export for details of API
     *
     * @return
     * @throws IOException
     * @throws JSONException
     * @throws BadRequestException
     */
    @Override
    public List<String> getSurveyCompletions(String surveyId) throws InternalServerException {
        try {
            URL url = UriComponentsBuilder
                    .fromUriString(QUALTRICS_API_ENDPOINT + "/surveys/" + surveyId + "/export-responses")
                    .build().toUri().toURL();
            String response = apiProcess(url, QualtricsBody, false);
            JSONObject resultObj = new JSONObject(response).getJSONObject("result");
            String progressId = resultObj.getString("progressId");
            ExportResponse exportResponse = null;
            while (true) {
                exportResponse = getExportStatus(progressId);
                LOGGER.info("Current status: " + exportResponse.status + ", Progress: " + exportResponse.getPercentComplete());
                if (exportResponse.getStatus().equals("complete")) {
                    //export success
                    return getSurveyCompletedEmailAddresses(exportResponse.getFileId());
                } else if (exportResponse.getStatus().equals("failed")) {
                    //export failed
                    LOGGER.error("Failed to download survey export, progress = " + exportResponse.getPercentComplete() + "%");
                    throw new InternalServerException("Download of survey export failed");
                } else {
                    //still in progress
                    LOGGER.info("Download in progress, current completed: " + exportResponse.getPercentComplete() + "%");
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            throw new InternalServerException("Error when processing survey data");
        }

    }

    /**
     * Fetch the current exporting progress of given progressId, should iterate until percentage is 100.0
     *
     * @param progressId
     * @return
     * @throws IOException
     * @throws JSONException
     */
    private ExportResponse getExportStatus(String progressId) throws IOException, JSONException {
        URL url = UriComponentsBuilder
                .fromUriString(QUALTRICS_API_ENDPOINT + "/surveys/" + surveyId + "/export-responses/" + progressId)
                .build().toUri().toURL();
        String response = apiProcess(url, false);
        //A more elegant way is to use the ObjectMapper, but initializing it is very costly
        JSONObject resultObj = new JSONObject(response).getJSONObject("result");
        return new ExportResponse(
                resultObj.getString("fileId"),
                resultObj.getDouble("percentComplete"),
                resultObj.getString("status"));
    }

    private List<String> getSurveyCompletedEmailAddresses(String fileId) throws IOException, JSONException {
        LOGGER.info("FileId = " + fileId);
        List<String> completedEmails = new ArrayList<>();
        URL url = UriComponentsBuilder
                .fromUriString(QUALTRICS_API_ENDPOINT + "/surveys/" + surveyId + "/export-responses/" + fileId + "/file")
                .build().toUri().toURL();
        String response = apiProcess(url, false);
        JSONArray responseList = new JSONObject(response).getJSONArray("responses");
        for (int i = 0; i < responseList.length(); i++) {
            JSONObject responseItem = responseList.getJSONObject(i).getJSONObject("values");
            String emailAddress = responseItem.getString("EmailAddress");
            completedEmails.add(emailAddress);
        }
        return completedEmails;
    }

    private class ExportResponse {
        public String getFileId() {
            return fileId;
        }

        public double getPercentComplete() {
            return percentComplete;
        }

        public String getStatus() {
            return status;
        }

        private String fileId;
        private double percentComplete;
        private String status;

        public ExportResponse(String fileId, double percentComplete, String status) {
            this.fileId = fileId;
            this.percentComplete = percentComplete;
            this.status = status;
        }
    }

    /**
     * Fetch grades of all students for a specific quiz
     *
     * @param quizId Quiz ID, can be looked up using List Assignments API
     * @return Map of quiz scores, key is student id, value is score of the quiz for this student
     * @throws IOException
     * @throws JSONException
     */
    private Map<String, Double> getStudentQuizScores(int quizId) throws IOException, JSONException {
        ArrayList<HashMap<String, String>> users = getUsers();
        String users_id = users.stream().map(e -> "&student_ids%5B%5D=" + e.get("user_id")).collect(Collectors.joining(""));
        URL url = new URL(CANVAS_API_ENDPOINT + "/courses/" + COURSE_ID + "/quizzes/" + quizId + "/submissions?exclude_response_fields%5B%5D=preview_url&grouped=1&response_fields%5B%5D=assignment_id&response_fields%5B%5D=attachments&response_fields%5B%5D=attempt&response_fields%5B%5D=cached_due_date&response_fields%5B%5D=entered_grade&response_fields%5B%5D=entered_score&response_fields%5B%5D=excused&response_fields%5B%5D=grade&response_fields%5B%5D=grade_matches_current_submission&response_fields%5B%5D=grading_period_id&response_fields%5B%5D=id&response_fields%5B%5D=late&response_fields%5B%5D=late_policy_status&response_fields%5B%5D=missing&response_fields%5B%5D=points_deducted&response_fields%5B%5D=posted_at&response_fields%5B%5D=redo_request&response_fields%5B%5D=score&response_fields%5B%5D=seconds_late&response_fields%5B%5D=submission_type&response_fields%5B%5D=submitted_at&response_fields%5B%5D=url&response_fields%5B%5D=user_id&response_fields%5B%5D=workflow_state&student_ids%5B%5D=" + users_id + "&per_page=100");
        String response = apiProcess(url, true);
        JSONObject resultObj = new JSONObject(response);
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
}
