package info.karlovskiy.yaus.controller;

import info.karlovskiy.yaus.model.action.ShortenUrlAction;
import info.karlovskiy.yaus.service.UrlService;
import info.karlovskiy.yaus.service.ValidationService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

@Controller
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;
    private final ValidationService validationService;

    @GetMapping("/")
    @Timed(value = "yaus.page.home.timer")
    public String homePage(Model model) {
        model.addAttribute("shortenUrlAction", new ShortenUrlAction());
        return "index";
    }

    @PostMapping("/")
    @Timed(value = "yaus.page.shorten.timer")
    public String create(@Valid ShortenUrlAction createOwnerAction, Errors errors,
                         HttpServletRequest request, Model model) {
        if (errors.hasErrors()) {
            return "index";
        }
        val longURL = createOwnerAction.getLongURL();
        val valid = validationService.validateURL(longURL);
        if (!valid) {
            errors.rejectValue("longURL", "errors.invalidURL");
            return "index";
        }

        val shortKey = urlService.shorten(longURL);
        StringBuilder urlBuilder = new StringBuilder();
        val scheme = request.getScheme();
        urlBuilder.append(scheme).append("://").append(request.getServerName());
        val port = request.getServerPort();
        // Append the port number if it's not standard for the scheme
        if (port != (scheme.equals("http") ? 80 : 443)) {
            urlBuilder.append(":").append(port);
        }
        urlBuilder.append(request.getContextPath()).append("/f/").append(shortKey);
        model.addAttribute("shortURL", urlBuilder.toString());
        return "index";
    }

    @GetMapping("/f/{shortKey}")
    @Timed(value = "yaus.page.forward.timer")
    public ResponseEntity forward(@PathVariable String shortKey) throws URISyntaxException {
        val url = urlService.load(shortKey);
        if (StringUtils.isEmpty(url)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        URI original = new URI(url);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(original);
        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
    }

}
