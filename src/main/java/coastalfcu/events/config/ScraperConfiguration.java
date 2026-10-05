package coastalfcu.events.config;

import java.time.Duration;

/**
 * Configuration interface for web scraping operations.
 */
public interface ScraperConfiguration {

    /**
     * Get the base URL to scrape.
     *
     * @return the base URL
     */
    String getBaseUrl();

    /**
     * Get the RSS feed description.
     *
     * @return feed description
     */
    String getFeedDescription();

    /**
     * Get the RSS feed link.
     *
     * @return feed link
     */
    String getFeedLink();

    /**
     * Get the RSS feed title.
     *
     * @return feed title
     */
    String getFeedTitle();

    /**
     * Get the CSS selector to wait for on page load.
     *
     * @return the CSS selector
     */
    String getPageLoadSelector();

    /**
     * Gets the timeout duration for page loads.
     *
     * @return the timeout duration
     */
    Duration getPageLoadTimeout();

    /**
     * Get the number of days to retain events.
     *
     * @return number of days
     */
    int getRetentionDays();

    /**
     * Gets the user agent string for HTTP requests.
     *
     * @return the user agent string
     */
    String getUserAgent();
}
