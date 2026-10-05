package coastalfcu.community.events.config.impl;

import static org.assertj.core.api.Assertions.assertThat;

import coastalfcu.events.config.impl.ScraperConfigurationImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for ScraperConfigurationImpl.
 */
class ScraperConfigurationImplTest {

    private ScraperConfigurationImpl config;

    @BeforeEach
    void setUp() {
        config = new ScraperConfigurationImpl();
    }

    @Test
    void testGetBaseUrl() {
        assertThat(config.getBaseUrl())
            .isEqualTo("https://www.coastal24.com/about/community-engagement/"
                + "coastal-in-the-community");
    }

    @Test
    void testGetFeedDescription() {
        assertThat(config.getFeedDescription())
            .isEqualTo("Community events sponsored by Coastal Credit Union");
    }

    @Test
    void testGetFeedLink() {
        assertThat(config.getFeedLink())
            .isEqualTo("https://www.coastal24.com/about/community-engagement/"
                + "coastal-in-the-community");
    }

    @Test
    void testGetFeedTitle() {
        assertThat(config.getFeedTitle())
            .isEqualTo("Coastal Credit Union Community Events");
    }

    @Test
    void testGetPageLoadSelector() {
        assertThat(config.getPageLoadSelector())
            .isEqualTo("div.events-lists");
    }

    @Test
    void testGetPageLoadTimeout() {
        assertThat(config.getPageLoadTimeout().toSecondsPart())
            .isEqualTo(10);
    }

    @Test
    void testGetRetentionDays() {
        assertThat(config.getRetentionDays())
            .isEqualTo(7);
    }
}
