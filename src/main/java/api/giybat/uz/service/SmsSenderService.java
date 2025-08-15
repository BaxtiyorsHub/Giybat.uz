package api.giybat.uz.service;

import org.springframework.stereotype.Service;

@Service
public class SmsSenderService {

    public String sendRegistrationSMS(String username) {

        return "Verification code sent";
    }
}
