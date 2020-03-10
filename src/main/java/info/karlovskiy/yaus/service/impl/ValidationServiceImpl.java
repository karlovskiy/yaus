package info.karlovskiy.yaus.service.impl;

import info.karlovskiy.yaus.config.YausProperties;
import info.karlovskiy.yaus.service.ValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.Validate;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.stereotype.Service;

import static org.apache.commons.validator.routines.UrlValidator.ALLOW_LOCAL_URLS;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidationServiceImpl implements ValidationService {

    private final YausProperties yausProperties;

    @Override
    public boolean validateURL(String url) {
        Validate.notEmpty(url, "URL can not be empty");
        val schemes = yausProperties.getSchemesArray();
        val allowLocalUrls = yausProperties.isAllowLocalUrls();
        val validator = allowLocalUrls ? new UrlValidator(schemes, ALLOW_LOCAL_URLS) : new UrlValidator(schemes);
        boolean valid = validator.isValid(url);
        return valid;
    }

}
