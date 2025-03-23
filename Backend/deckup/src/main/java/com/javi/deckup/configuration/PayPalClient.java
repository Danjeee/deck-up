package com.javi.deckup.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.*;

public class PayPalClient {

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.api.url}")
    private String baseUrl;

    public String getAccessToken() throws Exception {
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("grant_type", "client_credentials")
                .build();

        String credentials = Credentials.basic(clientId, clientSecret);

        Request request = new Request.Builder()
                .url(baseUrl + "/v1/oauth2/token")
                .post(formBody)
                .header("Authorization", credentials)
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new RuntimeException("Failed to get access token: " + response.body().string());
        }

        return new ObjectMapper()
                .readTree(response.body().string())
                .get("access_token")
                .asText();
    }
    
    public String getBaseUrl() {
        return baseUrl;
    }
}

