package info.karlovskiy.yaus.service;

public interface UrlService {

    /**
     * Shorten long URL, save and return short key for it
     *
     * @param longURL original long URL
     * @return short key
     */
    String shorten(String longURL);

    /**
     * Load original URL by short key
     *
     * @param shortKey short key
     * @return original URL
     */
    String load(String shortKey);

}
