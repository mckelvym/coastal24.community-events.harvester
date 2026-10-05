package coastalfcu.events.parser;

import coastalfcu.events.domain.EventItem;
import java.util.Optional;
import org.jsoup.nodes.Element;

/**
 * Interface for parsing event information from HTML elements.
 */
public interface EventParser {

    /**
     * Parse an event from an HTML element.
     *
     * @param eventElement the HTML element containing event data
     * @return an Optional containing the parsed EventItem, or empty if parsing fails
     */
    Optional<EventItem> parseEvent(Element eventElement);
}
