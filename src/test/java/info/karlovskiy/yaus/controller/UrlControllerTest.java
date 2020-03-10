package info.karlovskiy.yaus.controller;

import info.karlovskiy.yaus.config.YausProperties;
import info.karlovskiy.yaus.service.UrlService;
import info.karlovskiy.yaus.service.ValidationService;
import lombok.var;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@WebMvcTest(UrlController.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UrlService urlService;
    @MockBean
    private ValidationService validationService;

    @DisplayName("Forward test")
    @Test
    void forwardTest() throws Exception {
        var testShortKey = "w7e";
        var originalUrl = "https://www.google.com/";
        when(urlService.load(eq(testShortKey))).thenReturn(originalUrl);
        mockMvc.perform(MockMvcRequestBuilders.get("/f/{testShortKey}", testShortKey))
                .andExpect(status().isSeeOther())
                .andExpect(redirectedUrl(originalUrl));
    }

    @TestConfiguration
    @Import({
            YausProperties.class
    })
    static class TestContextConfiguration {
    }
}
