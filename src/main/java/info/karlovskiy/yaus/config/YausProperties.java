package info.karlovskiy.yaus.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "yaus")
public class YausProperties {

    private List<String> schemes = Arrays.asList("http", "https");
    private String indexKeyName = "yaus";
    private boolean allowLocalUrls = true;

    public String[] getSchemesArray() {
        return schemes.toArray(new String[0]);
    }

}
