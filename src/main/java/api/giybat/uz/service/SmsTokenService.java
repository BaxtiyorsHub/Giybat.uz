package api.giybat.uz.service;

import api.giybat.uz.config.RestTemplate;
import api.giybat.uz.dto.SmsProviderTokenDTO;
import api.giybat.uz.dto.SmsTokenProviderResponse;
import api.giybat.uz.entity.SmsTokenEntity;
import api.giybat.uz.repository.SmsTokenRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SmsTokenService {

    private final SmsTokenRepository smsTokenRepository;
    private final RestTemplate restTemplate;

    @Value("${sms.eskiz.email}")
    private String email;
    @Value("${sms.eskiz.password}")
    private String password;

    private String token = "https://notify.eskiz.uz/api/";

    public String getToken() {
        Optional<SmsTokenEntity> optional = smsTokenRepository.findTopByOrderByCreatedDateDesc();
        if (optional.isPresent()) {
            SmsTokenEntity smsTokenEntity = optional.get();
            LocalDateTime tokenDate = smsTokenEntity.getCreatedDate();
            LocalDateTime now = LocalDateTime.now();
            long days = Duration.between(tokenDate, now).toDaysPart();

            if (days >= 30) {
                // create new and return token
                return createToken();
            } else if (days == 29) {
                // refresh token
                return refreshToken(smsTokenEntity.getToken());
            } else {
                return smsTokenEntity.getToken();
            }
        }
        // if token not exists create new one
        return createToken();
    }

    private String createToken() {
        SmsProviderTokenDTO smsProviderTokenDTO = new SmsProviderTokenDTO();
        smsProviderTokenDTO.setEmail(email);
        smsProviderTokenDTO.setPassword(password);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        RequestEntity<SmsProviderTokenDTO> request = RequestEntity
                .post(token + "auth/login")
                .headers(headers)
                .body(smsProviderTokenDTO);

        var response = restTemplate.exchange(request, SmsTokenProviderResponse.class);
        String token = response.getBody().getData().getToken();

        SmsTokenEntity entity = new SmsTokenEntity();
        entity.setToken(token);
        entity.setCreatedDate(LocalDateTime.now());
        repository.save(entity);

        return token;
    }

    public String refreshToken(String oldToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + oldToken);
        headers.set("Content-Type", "application/json");

        RequestEntity<Void> request = RequestEntity
                .patch(token + "auth/refresh")
                .headers(headers)
                .build();

        var response = restTemplate.exchange(request, SmsTokenProviderResponse.class);
        String newToken = response.getBody().getData().getToken();

        SmsTokenEntity entity = new SmsTokenEntity();
        entity.setToken(newToken);
        entity.setCreatedDate(LocalDateTime.now());
        repository.save(entity);

        return newToken;
    }

    private Var restExchange(RequestEntity<Void> request, SmsTokenProviderResponse response) {

    }
}
