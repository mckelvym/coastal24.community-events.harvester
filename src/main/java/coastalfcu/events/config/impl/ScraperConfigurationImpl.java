package coastalfcu.events.config.impl;

import static coastalfcu.events.parser.impl.CssSelectors.PAGE_LOAD_SELECTOR;

import coastalfcu.events.config.ScraperConfiguration;
import java.time.Duration;

/**
 * Configuration for scraping Coastal24 community events.
 */
public class ScraperConfigurationImpl implements ScraperConfiguration {

    private static final String BASE_URL =
        "https://www.coastal24.com/about/community-engagement/coastal-in-the-community";
    private static final String FEED_DESCRIPTION =
        "Community events sponsored by Coastal Credit Union";
    private static final String FEED_TITLE = "Coastal Credit Union Community Events";
    private static final Duration PAGE_LOAD_TIMEOUT = Duration.ofSeconds(10);
    private static final int RETENTION_DAYS = 7;
    private static final String USER_AGENT =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
            + "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    @Override
    public String getBaseUrl() {
        return BASE_URL;
    }

    @Override
    public String getFeedDescription() {
        return FEED_DESCRIPTION;
    }

    @Override
    public String getFeedLink() {
        return getBaseUrl();
    }

    @Override
    public String getFeedTitle() {
        return FEED_TITLE;
    }

    @Override
    public String getPageLoadSelector() {
        return PAGE_LOAD_SELECTOR;
    }

    @Override
    public Duration getPageLoadTimeout() {
        return PAGE_LOAD_TIMEOUT;
    }

    @Override
    public int getRetentionDays() {
        return RETENTION_DAYS;
    }

    @Override
    public String getUserAgent() {
        return USER_AGENT;
    }
}
