package info.karlovskiy.yaus.service;

import info.karlovskiy.yaus.config.YausProperties;
import info.karlovskiy.yaus.service.impl.ValidationServiceImpl;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
class ValidationServiceTest {

    @Autowired
    private ValidationService validationService;
    @SpyBean
    private YausProperties yausProperties;

    @DisplayName("Validate null URL")
    @Test
    void validateNull() {
        val exception = assertThrows(NullPointerException.class, () -> validationService.validateURL(null));
        assertEquals("URL can not be empty", exception.getMessage());
    }

    @DisplayName("Validate empty URL")
    @Test
    void validateEmpty() {
        val exception = assertThrows(IllegalArgumentException.class, () -> validationService.validateURL(""));
        assertEquals("URL can not be empty", exception.getMessage());
    }

    @DisplayName("Validate some valid and invalid cases")
    @ParameterizedTest
    @CsvSource({
            "a,false",
            "Z,false",
            "asdf,false",
            "ftp://www.google.com/,false",
            "file://localhost/etc/fstab,false",
            "https://www.google.com/,true",
            "http://www.google.com/,true",
    })
    void validate(String url, boolean expected) {
        boolean actual = validationService.validateURL(url);
        assertEquals(expected, actual);
    }

    @DisplayName("Validate local urls")
    @ParameterizedTest
    @CsvSource({
            "https://localhost:8080/,false,true",
            "http://localhost:8080/,false,true",
            "https://www.google.com/,true,true",
            "https://www.google.com/,true,true",
    })
    void validateLocalURLs(String url, boolean expectedDisallowLocalUrls, boolean expectedAllowLocalUrls) {
        Mockito.when(yausProperties.isAllowLocalUrls()).thenReturn(false);
        boolean actualDisallowLocalHosts = validationService.validateURL(url);
        assertEquals(expectedDisallowLocalUrls, actualDisallowLocalHosts);

        Mockito.when(yausProperties.isAllowLocalUrls()).thenReturn(true);
        boolean actualAllowLocalUrls = validationService.validateURL(url);
        assertEquals(expectedAllowLocalUrls, actualAllowLocalUrls);
    }

    @TestConfiguration
    @Import({
            ValidationServiceImpl.class,
            YausProperties.class
    })
    static class TestContextConfiguration {
    }
}
