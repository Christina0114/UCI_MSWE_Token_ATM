package com.capstone.tokenatm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@SpringBootApplication
public class TokenAtmApplication {


    public static void main(String[] args) throws IOException {
        URL url = new URL("https://canvas.instructure.com/api/v1/courses");
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestProperty("Authorization","Bearer "+"4407~Fz2ggZMeVHcRdXgGqLV8Q8rPclzmC1hhe30sKwvY42WHlZkaEAmsvGIU2hYeGdqP");

        httpURLConnection.setRequestProperty("Content-Type","application/json");
        httpURLConnection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
        String output;

        StringBuffer response = new StringBuffer();
        while ((output = in.readLine()) != null) {
            response.append(output);
        }

        in.close();
        System.out.println("Response:-" + response.toString());
        SpringApplication.run(TokenAtmApplication.class, args);
    }

}
