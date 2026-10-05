package coastalfcu.events.parser.impl;

import static coastalfcu.events.parser.impl.CssSelectors.DETAILS;
import static coastalfcu.events.parser.impl.CssSelectors.LINK;
import static coastalfcu.events.parser.impl.HtmlConstants.ABS_HREF_ATTR;
import static coastalfcu.events.parser.impl.HtmlConstants.HTML_LINE_BREAK;
import static java.util.Objects.requireNonNull;

import coastalfcu.events.domain.EventItem;
import coastalfcu.events.parser.EventParser;
import java.time.LocalDate;
import java.util.Optional;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses and extracts event fields.
 */
public final class EventParserImpl implements EventParser {

    private static final Logger LOG =
        LoggerFactory.getLogger(EventParserImpl.class);

    private final DateExtractor dateExtractor;
    private final DateParser dateParser;
    private final DescriptionExtractor descriptionExtractor;
    private final ImageExtractor imageExtractor;
    private final TitleExtractor titleExtractor;

    /**
     * Create a new EventParserImpl with default extractors.
     */
    public EventParserImpl() {
        this.titleExtractor = new TitleExtractor();
        this.dateExtractor = new DateExtractor();
        this.dateParser = new DateParser();
        this.descriptionExtractor = new DescriptionExtractor();
        this.imageExtractor = new ImageExtractor();
    }

    private String extractLink(Element element) {
        Element linkElement = element.selectFirst(LINK);
        if (linkElement != null) {
            String href = linkElement.attr(ABS_HREF_ATTR);
            if (!href.isEmpty()) {
                return href;
            }
        }
        return "";
    }

    private String extractLocation(Element element) {
        Element detailsElement = element.selectFirst(DETAILS);
        if (detailsElement != null) {
            String detailsText = detailsElement.html();
            if (detailsText.contains(HTML_LINE_BREAK)) {
                String[] parts = detailsText.split(HTML_LINE_BREAK);
                if (parts.length > 1) {
                    return parts[1].trim();
                }
            }
        }
        return "";
    }

    @Override
    public Optional<EventItem> parseEvent(Element eventElement) {
        requireNonNull(eventElement, "element must not be null");
        String title = titleExtractor.extract(eventElement);
        String link = extractLink(eventElement);
        String dateString = dateExtractor.extractDateString(eventElement);
        String location = extractLocation(eventElement);
        String description = descriptionExtractor.extract(eventElement);
        String imageUrl = imageExtractor.extract(eventElement);

        LocalDate[] dateRange = dateParser.parseDateRange(dateString);
        LocalDate eventDateStart = dateRange[0];
        LocalDate eventDateEnd = dateRange[1];

        if (eventDateStart == null) {
            LOG.warn("Skipping event due to unparseable date '{}': {}", dateString, title);
            return Optional.empty();
        }

        return Optional.of(new EventItem(
            link,                // id (using link as id)
            title,
            link,
            description,
            eventDateStart,
            eventDateEnd,
            imageUrl,
            location
        ));
    }
}
