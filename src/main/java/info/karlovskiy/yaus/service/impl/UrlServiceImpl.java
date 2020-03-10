package info.karlovskiy.yaus.service.impl;

import info.karlovskiy.yaus.config.YausProperties;
import info.karlovskiy.yaus.service.UrlService;
import info.karlovskiy.yaus.util.Base62;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.Validate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UrlServiceImpl implements UrlService {

    private final StringRedisTemplate redisTemplate;
    private final YausProperties yausProperties;
    private final Timer incrementTimer;
    private final Timer saveTimer;
    private final Timer loadTimer;
    private final DistributionSummary urlSizeSummary;

    public UrlServiceImpl(StringRedisTemplate redisTemplate, YausProperties yausProperties, MeterRegistry meterRegistry) {
        this.redisTemplate = redisTemplate;
        this.yausProperties = yausProperties;
        this.incrementTimer = meterRegistry.timer("yaus.redis.increment.timer");
        this.saveTimer = meterRegistry.timer("yaus.redis.save.timer");
        this.loadTimer = meterRegistry.timer("yaus.redis.load.timer");
        this.urlSizeSummary = DistributionSummary.builder("yaus.url.size").baseUnit("symbols").register(meterRegistry);
    }

    @Override
    public String shorten(String longURL) {
        log.info("Start shorten, long url: {}", longURL);
        Validate.notEmpty(longURL, "Original long URL can not be empty");
        urlSizeSummary.record(longURL.length());

        val id = incrementTimer.record(
                () -> redisTemplate.boundValueOps(yausProperties.getIndexKeyName()).increment());
        Validate.notNull(id);

        val shortKey = Base62.encode(id);
        saveTimer.record(
                () -> redisTemplate.boundValueOps(shortKey).set(longURL));

        log.info("End shorten, encoded value: {}, id: {}", shortKey, id);
        return shortKey;
    }

    @Override
    public String load(String shortKey) {
        String url = loadTimer.record(
                () -> redisTemplate.boundValueOps(shortKey).get());
        log.info("Loaded original url: {} for short key: {}", url, shortKey);
        return url;
    }
}
