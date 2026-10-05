package coastalfcu.events.parser.impl;

import static coastalfcu.events.parser.impl.CssSelectors.DETAILS;
import static coastalfcu.events.parser.impl.HtmlConstants.HTML_LINE_BREAK;
import static java.util.Objects.requireNonNull;

import org.jsoup.nodes.Element;

/**
 * Extracts event date from HTML element.
 */
public final class DateExtractor {

    /**
     * Extract date from event element.
     *
     * @param eventElement the event element
     * @return the date or empty string if not found
     * @throws NullPointerException if element is null
     */
    public String extractDateString(Element eventElement) {
        requireNonNull(eventElement, "element must not be null");
        Element detailsElement = eventElement.selectFirst(DETAILS);
        if (detailsElement != null) {
            String detailsText = detailsElement.html();
            if (detailsText.contains(HTML_LINE_BREAK)) {
                String[] parts = detailsText.split(HTML_LINE_BREAK);
                if (parts.length > 0) {
                    return parts[0].trim();
                }
            } else {
                return detailsElement.text().trim();
            }
        }
        return "";
    }
}
