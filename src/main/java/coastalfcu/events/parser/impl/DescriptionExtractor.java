package coastalfcu.events.parser.impl;

import static coastalfcu.events.parser.impl.CssSelectors.DESCRIPTION;
import static coastalfcu.events.parser.impl.HtmlConstants.EMPTY;
import static java.util.Objects.requireNonNull;

import org.jsoup.nodes.Element;

/**
 * Extracts event description from HTML element.
 */
public final class DescriptionExtractor {

    /**
     * Extract description from event element.
     *
     * @param element the event element
     * @return the description or empty string if not found
     */
    public String extract(Element element) {
        requireNonNull(element, "element must not be null");
        Element bodyElement = element.selectFirst(DESCRIPTION);
        if (bodyElement == null) {
            return EMPTY;
        }
        return bodyElement.html().trim();
    }
}
