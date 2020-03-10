package info.karlovskiy.yaus.service;

import info.karlovskiy.yaus.config.YausProperties;
import info.karlovskiy.yaus.service.impl.UrlServiceImpl;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class UrlServiceTest {

    @Autowired
    private UrlService urlService;
    @MockBean
    private StringRedisTemplate redisTemplate;
    @MockBean
    private BoundValueOperations<String, String> valueOperations;

    @DisplayName("Shorten null URL")
    @Test
    void shortenNull() {
        val exception = assertThrows(NullPointerException.class, () -> urlService.shorten(null));
        assertEquals("Original long URL can not be empty", exception.getMessage());
    }

    @DisplayName("Shorten empty URL")
    @Test
    void shortenEmpty() {
        val exception = assertThrows(IllegalArgumentException.class, () -> urlService.shorten(""));
        assertEquals("Original long URL can not be empty", exception.getMessage());
    }

    @DisplayName("Successful shorten")
    @ParameterizedTest
    @CsvSource({
            "0,0",
            "123456,w7e",
            "654321,2Kdz",
            Long.MAX_VALUE + ",aZl8N0y58M7",
    })
    void shortenSuccess(Long id, String expectedShortKey) {
        when(redisTemplate.boundValueOps(anyString())).thenReturn(valueOperations);
        when(valueOperations.increment()).thenReturn(id);
        doNothing().when(valueOperations).set(anyString());

        val shortKey = urlService.shorten("https://www.google.com/");
        assertEquals(expectedShortKey, shortKey);
    }

    @TestConfiguration
    @Import({
            UrlServiceImpl.class,
            YausProperties.class,
            MetricsAutoConfiguration.class,
            CompositeMeterRegistryAutoConfiguration.class
    })
    static class TestContextConfiguration {
    }
}
