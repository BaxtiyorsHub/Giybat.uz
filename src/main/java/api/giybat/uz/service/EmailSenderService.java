package api.giybat.uz.service;

import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {

    public String sendRegistration(String username) {


        return "Verification code sent to your email";

    }
}
