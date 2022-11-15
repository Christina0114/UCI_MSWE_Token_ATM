package com.capstone.tokenatm.service.impl;

import antlr.Token;
import com.capstone.tokenatm.entity.TokenCountEntity;
import com.capstone.tokenatm.exceptions.BadRequestException;
import com.capstone.tokenatm.service.EarnService;
<<<<<<< HEAD
=======
import com.capstone.tokenatm.service.Student;
import com.capstone.tokenatm.service.TokenRepository;
import okhttp3.*;
>>>>>>> 0280f97 (add code from main again)
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Service;
<<<<<<< HEAD
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
=======
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.util.UriComponentsBuilder;

import javax.imageio.IIOException;
import javax.persistence.GenerationType;
import javax.swing.text.html.Option;
>>>>>>> 0280f97 (add code from main again)
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

@Service("EarnService")
public class EarnServiceI implements EarnService {
    private static final String BEARER_TOKEN = "7~sKb3Kq7M9EjSgDtMhugxCEs5oD76pbJgBWAFScBliSi7Iin8QubiBHEBlrWfYunG";
    private static final String CANVAS_API_ENDPOINT = "https://canvas.instructure.com/api/v1";
    private static final String Qualtrics_API_ENDPOINT = "https://iad1.qualtrics.com/API/v3";

    private static final int COURSE_ID = 3737737;
<<<<<<< HEAD
    private static List<Integer> tokenQuizzes = Arrays.asList(12427623);
    private static final String surveyId = "SV_56fa1nPQlOAqnmm";

    private static final String API_KEY = "3yoP4lV2G7wmxOVtIkH6G8K5IcGDgtdUf2Ys3um9";

    private static final Logger LOGGER = LoggerFactory.getLogger(EarnService.class);
=======

    //Qualtrics API Settings
    //TODO: The API Endpoint and API key is only used for testing. Please change to UCI endpoint and actual keys in prod
    //API Key for Qualtrics
    private static final String API_KEY = "3yoP4lV2G7wmxOVtIkH6G8K5IcGDgtdUf2Ys3um9";
    //Testing endpoint for Qualtrics
    private static final String QUALTRICS_API_ENDPOINT = "https://iad1.qualtrics.com/API/v3";

    private static final String QualtricsBody = "{\"format\":\"json\",\"compress\":\"false\"}";
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    private static final Logger LOGGER = LoggerFactory.getLogger(EarnService.class);
    private static Map<String, Object> result = new HashMap<>();


    //List of Quizzes in the first module (which needs over 70% average to earn the initial 2 tokens)
    private static List<Integer> tokenQuizIds = Arrays.asList(12427623, 12476618, 12476695);

    //List of surveys
    private static List<String> tokenSurveyIds = Arrays.asList("SV_8oIf0qAz5g0TFiK");

    @Autowired
    private TokenRepository tokenRepository;

    //Token earning deadlines
    private static final List<Date> survey_deadlines = new ArrayList<>();
    private static Date module_deadline;

    static {
        //Set deadlines for surveys
        List<int[]> deadline_time_list = Arrays.asList(
                new int[]{2022, 10, 14, 23, 45}
        );
        for (int[] deadline : deadline_time_list) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(deadline[0], deadline[1], deadline[2], deadline[3], deadline[4]);
            survey_deadlines.add(calendar.getTime());
        }

        //Set deadline for Module 1
        Calendar module_cal = Calendar.getInstance();
        module_cal.set(2022, 9, 26);
        module_deadline = module_cal.getTime();
    }

    /**
     * Fetch grades of all quizzes that is required to earn tokens
     *
     * @return Map of grades, key is student id, value is grade of that student for this assignment
     * @throws IOException
     * @throws JSONException
     */
>>>>>>> 0280f97 (add code from main again)
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

    public void init() throws JSONException, IOException {
        Map<Integer, Student> studentMap = getStudents();
        studentMap.entrySet().stream().forEach(e -> {
            Student student = e.getValue();
            TokenCountEntity entity = getEntityFromStudent(student);
            entity.setToken_count(0);
            tokenRepository.save(entity);
        });
    }

    private TokenCountEntity getEntityFromStudent(Student student) {
        TokenCountEntity entity = new TokenCountEntity();
        entity.setUser_id(student.getId());
        entity.setUser_name(student.getName());
        entity.setUser_email(student.getEmail());
        entity.setTimestamp(new Date());
        return entity;
    }

    private void updateTokenEntity(Map<Integer, Student> studentMap, Integer user_id, int add_count) {
        Student student = studentMap.getOrDefault(user_id, null);
        if (student == null) {
            LOGGER.error("Error: Student " + user_id + " does not exist in enrollment list");
            return;
        }
        Optional<TokenCountEntity> optional = tokenRepository.findById(user_id);
        TokenCountEntity entity = null;
        if (optional.isPresent()) {
            entity = optional.get();
            entity.setToken_count(entity.getToken_count() + add_count);
        } else {
            entity = getEntityFromStudent(student);
            entity.setToken_count(add_count);
        }
        tokenRepository.save(entity);
    }
    @Async
    @Override
    public void syncTokensOnDeadline() throws JSONException, IOException {
        init();
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        TaskScheduler scheduler = new ConcurrentTaskScheduler(executorService);

        //Schedule Module 1
        scheduler.schedule(() -> {
            Map<String, Double> quizGrades = null;
            Map<Integer, Student> studentMap = null;
            Set<Integer> usersToUpdate = new HashSet<>();//List of user_ids that should +2 tokens
            System.out.println("Running Module 1");
            try {
                quizGrades = getStudentTokenGrades();
                studentMap = getStudents();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            if (quizGrades != null && studentMap != null) {
                for (Map.Entry<String, Double> entry : quizGrades.entrySet()) {
                    String user_id = String.valueOf(entry.getKey());
                    Double quiz_aver = Double.valueOf(entry.getValue());
                    if (quiz_aver >= 70.00) {
                        usersToUpdate.add(Integer.parseInt(user_id));
                    }
                }

                for (Integer user_id : usersToUpdate) {
                    updateTokenEntity(studentMap, user_id, 2);
                }
            }
        }, module_deadline);

        for (int i = 0; i < tokenSurveyIds.size(); i++) {
            String surveyId = tokenSurveyIds.get(i);
            Date deadline = survey_deadlines.get(i);
            scheduler.schedule(() -> {
                System.out.println("Fetching Qualtrics Survey " + surveyId);
                Map<Integer, Student> studentMap = null;
                Set<Integer> usersToUpdate = new HashSet<>();//List of user_ids that should +1 token
                Set<String> completed_emails = new HashSet<>();
                try {
                    completed_emails = getSurveyCompletions(surveyId);
                    studentMap = getStudents();
                    System.out.println("Student Map: " + studentMap);
                } catch (InternalServerException | IOException | JSONException e) {
                    e.printStackTrace();
                }
                completed_emails.add("canapitest+4@gmail.com"); // fake_data
                completed_emails.add("canapitest+5@gmail.com"); // fake_data
                completed_emails.add("canapitest+6@gmail.com"); // fake_data
                completed_emails.add("canapitest+7@gmail.com"); // fake_data
                for (Map.Entry<Integer, Student> entry : studentMap.entrySet()) {
                    Student student = entry.getValue();
                    if (completed_emails.contains(student.getEmail())) {
                        usersToUpdate.add(student.getId());
                    }
                }

                for (Integer userId : usersToUpdate) {
                    updateTokenEntity(studentMap, userId, 1);
                }
            }, deadline);
        }
    }

<<<<<<< HEAD
    private StringBuffer apiProcess(URL url, Boolean isCanvas) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
=======
    @Override
    public Iterable<TokenCountEntity> getAllStudentTokenCounts() {
        return tokenRepository.findAll();
    }

    @Override
    public Optional<TokenCountEntity> getStudentTokenCount(Integer user_id) {
        return tokenRepository.findById(user_id);
    }

    @Override
    public String getIdentity() throws IOException {
        URL url = UriComponentsBuilder
                .fromUriString(QUALTRICS_API_ENDPOINT + "/whoami")
                .build().toUri().toURL();
        String response = apiProcess(url, false);
        return response;
    }

    private String apiProcess(URL url, Boolean isCanvas) throws IOException {
        return apiProcess(url, "", isCanvas);
    }

    private String apiProcess(URL url, String body, Boolean isCanvas) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request.Builder builder = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json");
>>>>>>> 0280f97 (add code from main again)
        if (isCanvas) {
            httpURLConnection.setRequestProperty("Authorization", "Bearer " + BEARER_TOKEN);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
        } else {
            httpURLConnection.setRequestProperty("X-API-TOKEN", API_KEY);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
        }
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

<<<<<<< HEAD
    @Override
    public HashMap<Object, Object> getUsers() throws IOException, JSONException {
=======
    private Map<Integer, Student> getStudents() throws IOException, JSONException {
>>>>>>> 0280f97 (add code from main again)
        URL url = new URL(CANVAS_API_ENDPOINT + "/courses/" + COURSE_ID + "/users?per_page=50&enrollment_type=student");
        StringBuffer response = apiProcess(url, true);
        JSONArray result = new JSONArray(String.valueOf(response));

<<<<<<< HEAD
        HashMap<Object, Object> users = new HashMap<>();
        for (int i = 0; i < result.length(); i++) {
            String user_id = ((JSONObject) result.get(i)).get("id").toString();
            String users_name = ((JSONObject) result.get(i)).get("name").toString();
            users.put(user_id, users_name);
=======
        Map<Integer, Student> studentMap = new HashMap<>();
        for (int i = 0; i < result.length(); i++) {
            Integer id = (Integer) ((JSONObject) result.get(i)).get("id");
            String name = ((JSONObject) result.get(i)).get("name").toString();
            String email = ((JSONObject) result.get(i)).get("email").toString();
            studentMap.put(id, new Student(id, name, email));
>>>>>>> 0280f97 (add code from main again)
        }
        return studentMap;
    }

    @Override
    public HashMap<Object, Object> getStudentGrades() throws IOException, JSONException {
<<<<<<< HEAD
        Map<Object, Object> users = getUsers();
        String users_id = users.entrySet().stream().map(e -> "&student_ids%5B%5D=" + e.getKey()).collect(Collectors.joining(""));
=======
        Map<Integer, Student> students = getStudents();
        String users_id = students.entrySet().stream().map(e -> "&student_ids%5B%5D=" + e.getValue().getId()).collect(Collectors.joining(""));
>>>>>>> 0280f97 (add code from main again)
        URL url = new URL(CANVAS_API_ENDPOINT + "/courses/" + COURSE_ID + "/students/submissions?exclude_response_fields%5B%5D=preview_url&grouped=1&response_fields%5B%5D=assignment_id&response_fields%5B%5D=attachments&response_fields%5B%5D=attempt&response_fields%5B%5D=cached_due_date&response_fields%5B%5D=entered_grade&response_fields%5B%5D=entered_score&response_fields%5B%5D=excused&response_fields%5B%5D=grade&response_fields%5B%5D=grade_matches_current_submission&response_fields%5B%5D=grading_period_id&response_fields%5B%5D=id&response_fields%5B%5D=late&response_fields%5B%5D=late_policy_status&response_fields%5B%5D=missing&response_fields%5B%5D=points_deducted&response_fields%5B%5D=posted_at&response_fields%5B%5D=redo_request&response_fields%5B%5D=score&response_fields%5B%5D=seconds_late&response_fields%5B%5D=submission_type&response_fields%5B%5D=submitted_at&response_fields%5B%5D=url&response_fields%5B%5D=user_id&response_fields%5B%5D=workflow_state&student_ids%5B%5D=" + users_id + "&per_page=100");
        StringBuffer response = apiProcess(url, true);
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
<<<<<<< HEAD
            String user_name = users.get(user_id).toString();
            students_data.put(user_name + "(" + user_id + ")", grades);
=======
            students_data.put("(" + user_id + ")", grades);
>>>>>>> 0280f97 (add code from main again)
        }
        return students_data;
    }


    @Override
    public HashMap<Object, Object> getCourseData() throws IOException, JSONException {
        URL url = new URL(CANVAS_API_ENDPOINT + "/courses/" + COURSE_ID + "/assignment_groups?exclude_assignment_submission_types%5B%5D=wiki_page&exclude_response_fields%5B%5D=description&exclude_response_fields%5B%5D=in_closed_grading_period&exclude_response_fields%5B%5D=needs_grading_count&exclude_response_fields%5B%5D=rubric&include%5B%5D=assignment_group_id&include%5B%5D=assignment_visibility&include%5B%5D=assignments&include%5B%5D=grades_published&include%5B%5D=post_manually&include%5B%5D=module_ids&override_assignment_dates=false&per_page=100");
        StringBuffer response = apiProcess(url, true);
        JSONArray result = new JSONArray(String.valueOf(response));

<<<<<<< HEAD
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
=======
        JSONArray response = new JSONArray(apiProcess(url, true));

        ArrayList<HashMap<Object, Object>> course_data = new ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            for (int j = 0; j < ((JSONArray) ((JSONObject) response.get(i)).get("assignments")).length(); j++) {
                HashMap<Object, Object> item = new HashMap<>();
                String assignment_id = ((JSONObject) ((JSONArray) ((JSONObject) response.get(i)).get("assignments")).get(j)).get("id").toString();
                String assignment_name = ((JSONObject) ((JSONArray) ((JSONObject) response.get(i)).get("assignments")).get(j)).get("name").toString();
                item.put("assignment_id", assignment_id);
                item.put("assignment_name", assignment_name);
                course_data.add(item);
            }
        }
        result.put("result", course_data);
        return result;
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
    public Set<String> getSurveyCompletions(String surveyId) throws InternalServerException {
        try {
            URL url = UriComponentsBuilder
                    .fromUriString(QUALTRICS_API_ENDPOINT + "/surveys/" + surveyId + "/export-responses")
                    .build().toUri().toURL();
            String response = apiProcess(url, QualtricsBody, false);
            JSONObject resultObj = new JSONObject(response).getJSONObject("result");
            String progressId = resultObj.getString("progressId");
            ExportResponse exportResponse = null;
            while (true) {
                exportResponse = getExportStatus(surveyId, progressId);
                LOGGER.info("Current status: " + exportResponse.status + ", Progress: " + exportResponse.getPercentComplete());
                if (exportResponse.getStatus().equals("complete")) {
                    //export success
                    return getSurveyCompletedEmailAddresses(surveyId, exportResponse.getFileId());
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
    private ExportResponse getExportStatus(String surveyId, String progressId) throws IOException, JSONException {
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

    private Set<String> getSurveyCompletedEmailAddresses(String surveyId, String fileId) throws IOException, JSONException {
        LOGGER.info("FileId = " + fileId);
        Set<String> completedEmails = new HashSet<>();
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

>>>>>>> 0280f97 (add code from main again)
    /**
     * Fetch grades of all students for a specific quiz
     *
     * @param quizId Quiz ID, can be looked up using List Assignments API
     * @return Map of quiz scores, key is student id, value is score of the quiz for this student
     * @throws IOException
     * @throws JSONException
     */
<<<<<<< HEAD
    public Map<String, Double> getStudentQuizScores(int quizId) throws IOException, JSONException {
        Map<Object, Object> users = getUsers();
        String users_id = users.entrySet().stream().map(e -> "&student_ids%5B%5D=" + e.getKey()).collect(Collectors.joining(""));
=======
    private Map<String, Double> getStudentQuizScores(int quizId) throws IOException, JSONException {
        Map<Integer, Student> students = getStudents();
        String users_id = students.entrySet().stream().map(e -> "&student_ids%5B%5D=" + e.getValue().getId()).collect(Collectors.joining(""));
>>>>>>> 0280f97 (add code from main again)
        URL url = new URL(CANVAS_API_ENDPOINT + "/courses/" + COURSE_ID + "/quizzes/" + quizId + "/submissions?exclude_response_fields%5B%5D=preview_url&grouped=1&response_fields%5B%5D=assignment_id&response_fields%5B%5D=attachments&response_fields%5B%5D=attempt&response_fields%5B%5D=cached_due_date&response_fields%5B%5D=entered_grade&response_fields%5B%5D=entered_score&response_fields%5B%5D=excused&response_fields%5B%5D=grade&response_fields%5B%5D=grade_matches_current_submission&response_fields%5B%5D=grading_period_id&response_fields%5B%5D=id&response_fields%5B%5D=late&response_fields%5B%5D=late_policy_status&response_fields%5B%5D=missing&response_fields%5B%5D=points_deducted&response_fields%5B%5D=posted_at&response_fields%5B%5D=redo_request&response_fields%5B%5D=score&response_fields%5B%5D=seconds_late&response_fields%5B%5D=submission_type&response_fields%5B%5D=submitted_at&response_fields%5B%5D=url&response_fields%5B%5D=user_id&response_fields%5B%5D=workflow_state&student_ids%5B%5D=" + users_id + "&per_page=100");
        StringBuffer response = apiProcess(url, true);
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
<<<<<<< HEAD

    @Override
    public String getSurveyDistributionHistory() throws IOException, JSONException, BadRequestException {
        String distributionId = getDistributionId(surveyId);
        URL url = UriComponentsBuilder
                .fromUriString(Qualtrics_API_ENDPOINT + "/distributions/" + distributionId + "/history")
                .queryParam("surveyId", surveyId)
                .build().toUri().toURL();
        StringBuffer response = apiProcess(url,false);
//        JSONObject resultObj = new JSONObject(String.valueOf(response));
//        JSONArray surveyDistributions = resultObj.getJSONObject("result").getJSONArray("elements");
        return response.toString();
    }

    private String getDistributionId(String surveyId) throws IOException, JSONException, BadRequestException {
        URL url = UriComponentsBuilder
                .fromUriString(Qualtrics_API_ENDPOINT + "/distributions")
                .queryParam("surveyId", surveyId)
                .build().toUri().toURL();
        StringBuffer response = apiProcess(url, false);
        JSONObject resultObj = new JSONObject(String.valueOf(response));
        JSONArray elementsArray = resultObj.getJSONObject("result").getJSONArray("elements");
        if (elementsArray.length() == 0) {
            LOGGER.error("Distributions array is empty");
        }
        JSONObject distributionObj = elementsArray.getJSONObject(0);
        return distributionObj.getString("id");
    }
}
=======
}
>>>>>>> 0280f97 (add code from main again)
