package coastalfcu.events;

import coastalfcu.events.config.ScraperConfiguration;
import coastalfcu.events.config.impl.ScraperConfigurationImpl;
import coastalfcu.events.domain.EventItem;
import coastalfcu.events.feed.RssFeedManager;
import coastalfcu.events.feed.RssFeedManagerImpl;
import coastalfcu.events.parser.EventParser;
import coastalfcu.events.parser.impl.EventParserImpl;
import coastalfcu.events.scraper.EventScraper;
import coastalfcu.events.scraper.impl.EventScraperImpl;
import coastalfcu.events.webdriver.ChromeDriverManager;
import coastalfcu.events.webdriver.PageLoader;
import coastalfcu.events.webdriver.WebDriverManager;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * Main application for scraping and generating RSS feed.
 */
public final class EventsHarvesterApplication {

    private static final String DEFAULT_OUTPUT_FILE = "events.xml";
    private static final Logger LOG =
        LoggerFactory.getLogger(EventsHarvesterApplication.class);

    private EventsHarvesterApplication() {
        // utility
    }

    private static void configureLogging() {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    /**
     * Main entry point.
     *
     * @param args command line arguments (optional output filename)
     */
    public static void main(String[] args) {
        configureLogging();

        String outputFilePath = args.length > 0 ? args[0] : DEFAULT_OUTPUT_FILE;

        LOG.info("Starting Coastal24 Events Harvester");
        LOG.info("Output file: {}", outputFilePath);

        ScraperConfiguration config = new ScraperConfigurationImpl();
        RssFeedManager feedManager = new RssFeedManagerImpl(config);

        try {
            LOG.info("Loading existing feed");
            Set<String> existingGuids = feedManager.loadExistingGuids(outputFilePath);
            LOG.info("Found {} existing events", existingGuids.size());

            try (WebDriverManager driverManager = new ChromeDriverManager(config)) {
                PageLoader pageLoader =
                    new PageLoader(driverManager.getDriver(), config.getPageLoadTimeout());
                EventParser eventParser = new EventParserImpl();
                EventScraper scraper = new EventScraperImpl(
                    config, pageLoader, eventParser);

                List<EventItem> newEvents = scraper.scrapeEvents(existingGuids);
                LOG.info("Scraped {} new events", newEvents.size());

                feedManager.generateFeed(outputFilePath, newEvents, outputFilePath);
                LOG.info("RSS feed generation complete");

                LOG.info("Harvesting completed successfully");
            }
        } catch (Exception ex) {
            LOG.error("Error loading existing GUIDs", ex);
            System.exit(1);
        }
    }
}
