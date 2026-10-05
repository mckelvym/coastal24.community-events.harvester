package coastalfcu.events.parser.impl;

/**
 * Constants for CSS selectors used in HTML parsing.
 *
 * <p>This class centralizes all CSS selector strings used throughout the
 * parser implementation to avoid magic strings and improve maintainability.
 */
public final class CssSelectors {

    /**
     * Selector for event items on the main page.
     */
    public static final String EVENT_ITEM = "div.item-event.card-item";

    /**
     * Selector for event title element.
     */
    public static final String TITLE = "div.heading a.stretched-link";

    /**
     * Selector for event link element.
     */
    public static final String LINK = "div.heading a.stretched-link";

    /**
     * Selector for event details container.
     */
    public static final String DETAILS = "div.details";

    /**
     * Selector for event description.
     */
    public static final String DESCRIPTION = "div.body > div:not(.heading):not(.details)";

    /**
     * Selector for event image.
     */
    public static final String IMAGE = "div.image img";

    /**
     * Selector for page load indicator.
     */
    public static final String PAGE_LOAD_SELECTOR = "div.events-lists";

    private CssSelectors() {
        // Utility class - prevent instantiation
    }
}
