 # Token ATM
This is the API *RESTAPIDocs* for Token ATM.
This project is based on SpringBoot. 

## Endpoints

Open endpoints do need  Authentication.
We use JWT token here to do the Authentication.


### Students related
* `GET baseURL/students/`
  - return a list of students' id and students' names, so that these data can be used to map the grades
  - example response:
    ```json
    {
        "11111111": "Amy Vas",
        "22222222": "Bob Joseph",
        "33333333": "Candy Schmitt",
    }
    ```

### Course related
* `GET baseURL/courses/`
  * return the all assignments' names and ids for certain course 
  * example response
    ```json
      {
      "26429802": "Quiz 1 Short Answer",
      "26429777": "Assignment 2",
      "33337369": "Token ATM Quiz",
      "26429771": "Assignment 1",
      "29391538": "Grade Upload Test",
      "26429772": "Quiz 1 MC",
      "27704218": "Discussion 1",
      "27709616": "Group Discussion",
      "33213450": "Single Student Test Assignment"
      }
      ```

### Grades related
* `GET baseURL/grades/`
    * return the all grades for each student for certain course
      * example response
        ```json
         {
          "Amy (22222222)":
         [
          "70.0(Assignment 1)",
          "null(Quiz 1 MC)",
          "null(Assignment 2)",
          "null(Quiz 1 Short Answer)",
          "10.0(Discussion 1)",
          "10.0(Group Discussion)",
          "2.0(Grade Upload Test)",
          "null(Token ATM Quiz)" 
        ],
         "Bob (22222222)": [
          "90.0(Assignment 1)",
          "null(Quiz 1 MC)",
          "null(Assignment 2)",
          "null(Quiz 1 Short Answer)",
          "10.0(Discussion 1)",
          "10.0(Group Discussion)",
          "5.0(Grade Upload Test)",
          "null(Token ATM Quiz)"
         ]}
        ```
  ### Survey  related
* `GET baseURL/survey_distributions/`
    * return the all grades for each students for certain course