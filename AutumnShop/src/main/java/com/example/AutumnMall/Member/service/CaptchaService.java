package com.example.AutumnMall.Member.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;

@Service
public class CaptchaService {
    @Value("${recaptcha.verify_url}")
    private String url;
    @Value("${recaptcha.secret_key}")
    private String key;

    private static final double HALF = 0.5;

    public boolean verifyToken(String token) {
        try {
            HttpHeaders httpHeaders= new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("secret", key);
            map.add("response", token);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, httpHeaders);
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class); // 해당 url로 token과 secret key를 전송. 유효성 검증.

            JsonObject jsonObject = JsonParser.parseString(response.getBody()).getAsJsonObject();
            return String.valueOf(jsonObject.get("success")).equals("true") && Double.parseDouble(String.valueOf(jsonObject.get("score"))) >= HALF;
            // success이거나 점수가 0.5 이상인 경우 통과
        } catch (Exception e) {
            return false;
        }
    }

}