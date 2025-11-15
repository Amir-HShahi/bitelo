package dev.burgerman.bitelo.services;

import java.time.Duration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import dev.burgerman.bitelo.model.Otp;
import dev.burgerman.bitelo.model.User;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SmsService {
    private final WebClient webClient;

    @Value("${notification.api.url}")
    private String url;

    @Value("${notification.api.timeout}")
    private int timeoutInSeconds;

    @Value("${notification.api.templateId}")
    private Long templateId;

    @Value("${notification.api.username}")
    private String username;

    @Value("${notification.api.password}")
    private String password;

    public SmsService(WebClient.Builder webClientBuilder, OtpService otpService) {
        this.webClient = webClientBuilder
                .codecs(config -> config.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
    }

    public void sendOtpCode(User user, Otp otp) {
        if (user.getPhoneCountryCode().equals("98")) {
            iranSendCode(otp);
        } else {
            globalSendCode(otp);
        }
    }

    private void iranSendCode(Otp otp) {
        try {
            String recipient = otp.getUser().getPhoneNumber();
            String text = otp.getCode();

            log.info("Sending SMS to Iranian number: {}", recipient);

            webClient.post()
                    .uri(url)
                    .bodyValue(Map.of(
                            "username", username,
                            "password", password,
                            "to", recipient,
                            "text", text,
                            "bodyId", templateId))
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(timeoutInSeconds))
                    .doOnNext(response -> log.info("SMS sent successfully: {}", response))
                    .doOnError(error -> log.error("Failed to send SMS: {}", error.getMessage()))
                    .subscribe();

        } catch (Exception e) {
            log.error("Error while sending Iran SMS", e);
        }
    }

    private void globalSendCode(Otp otp) {

    }
}