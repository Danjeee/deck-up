package com.javi.deckup.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.Logger;
import lombok.RequiredArgsConstructor;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
@RequiredArgsConstructor
public class PayPalService {
    private static final Logger log = (Logger) LoggerFactory.getLogger(PayPalService.class);
	
    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.api.url}")
    private String paypalApiUrl;

    private final ObjectMapper objectMapper;

    private String getAccessToken() throws IOException {
        OkHttpClient client = new OkHttpClient();

        String credential = Credentials.basic(clientId, clientSecret);

        Request request = new Request.Builder()
                .url(paypalApiUrl + "/v1/oauth2/token")
                .post(RequestBody.create("grant_type=client_credentials", MediaType.get("application/x-www-form-urlencoded")))
                .header("Authorization", credential)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Error al obtener el token de PayPal: " + response);
            }

            Map<String, Object> responseMap = objectMapper.readValue(response.body().string(), Map.class);
            return responseMap.get("access_token").toString();
        }
    }
    public String createPayment(String amount, String currency, String description, String returnUrl, String cancelUrl) throws IOException {
        String accessToken = getAccessToken();

        OkHttpClient client = new OkHttpClient();

        Map<String, Object> payment = new HashMap<>();
        payment.put("intent", "CAPTURE");
        Map<String, Object> amountDetails = new HashMap<>();
        amountDetails.put("currency_code", currency);
        amountDetails.put("value", amount);
        payment.put("purchase_units", new Object[]{Map.of("amount", amountDetails, "description", description)});
        payment.put("application_context", Map.of("return_url", returnUrl, "cancel_url", cancelUrl));

        String jsonRequest = objectMapper.writeValueAsString(payment);

        RequestBody body = RequestBody.create(jsonRequest, MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(paypalApiUrl + "/v2/checkout/orders")
                .post(body)
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Error al crear el pago en PayPal: " + response);
            }
            return response.body().string(); // Devuelve la respuesta como un JSON
        }
    }
}
