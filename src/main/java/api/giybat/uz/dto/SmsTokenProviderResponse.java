package api.giybat.uz.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SmsTokenProviderResponse {
    private String message;
    private Data data;
    private String token_type;

    @Getter
    @Setter
    public static class Data {
        private String token;
    }
}
