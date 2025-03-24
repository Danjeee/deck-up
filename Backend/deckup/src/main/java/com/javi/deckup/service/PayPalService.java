package com.javi.deckup.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javi.deckup.model.dto.UsuarioDTO;
import com.javi.deckup.repository.dao.PaymentRepository;
import com.javi.deckup.repository.entity.Payment;

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
    
    @Autowired
    PaymentRepository pr;
	
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
    public String createPayment(String amount, String currency, String description, String returnUrl, String cancelUrl, UsuarioDTO user, Integer cant) throws IOException {
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
            String responseBody = response.body().string();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            String orderId = jsonNode.get("id").asText();
            String approvalLink = jsonNode.get("links").get(1).get("href").asText(); // Asumiendo que el link de aprobación está en el segundo elemento

            // Guarda el orderId en la base de datos
            Payment pago = Payment.builder()
            						.orderid(orderId)
            						.status("CREATED")
            						.user(UsuarioDTO.convertToEntity(user))
            						.claimed(false)
            						.cant(cant)
            						.build();
            pr.save(pago);
            return responseBody;
        }
    }
    
    private void verifyPayments(String orderId) throws IOException {
    	OkHttpClient client = new OkHttpClient();
        String accessToken = getAccessToken();
        if (accessToken == null) {
            throw new RuntimeException("Acceso con token denegado");
        }

        Request request = new Request.Builder()
                .url("https://api-m.sandbox.paypal.com/v2/checkout/orders/" + orderId)
                .header("Authorization", "Bearer " + accessToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
            	throw new RuntimeException("La llamada a la api falló");
            }

            JsonNode orderDetails = objectMapper.readTree(response.body().string());
            String status = orderDetails.get("status").asText();
            Payment p = pr.findByOrderId(orderId);
            p.setStatus(status);
        }
    }
	public Integer getAllVerifiedPayments(UsuarioDTO user) {
		Integer total = 0;
		for (Payment i : pr.findByUser(user.getId())) {
			try {
				verifyPayments(i.getOrderid());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for (Payment i : pr.findUnclaimedVerifiedPaymentsByUser(user.getId())) {
			total += i.getCant();
			i.setClaimed(true);
			pr.save(i);
		}
		return total;
		
	}	
}
