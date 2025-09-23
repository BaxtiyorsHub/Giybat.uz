package api.giybat.uz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestTemplate {
    @Bean
    public RestTemplate restTemplateConfig() {
        return new RestTemplate();
    }
}
