package info.karlovskiy.yaus.service;

public interface ValidationService {

    /**
     * Validate URL
     *
     * @param url URL
     * @return {@code true} - if provided URL is valid, {@code false} - otherwise
     */
    boolean validateURL(String url);

}
