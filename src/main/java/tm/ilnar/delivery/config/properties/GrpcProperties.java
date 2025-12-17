package tm.ilnar.delivery.config.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;


@Component
@ConfigurationProperties(prefix = "app.grpc")
@Validated
@Data
public class GrpcProperties {

    @Valid
    private GeoService geoService = new GeoService();

    @Data
    public static class GeoService {

        @NotBlank
        private String host;

        @Min(1)
        @Max(65535)
        private int port;
    }
}
