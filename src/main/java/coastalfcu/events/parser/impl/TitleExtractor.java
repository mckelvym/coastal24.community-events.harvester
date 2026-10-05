package coastalfcu.events.parser.impl;

import static coastalfcu.events.parser.impl.CssSelectors.TITLE;
import static java.util.Objects.requireNonNull;

import org.jsoup.nodes.Element;

/**
 * Extracts event title from HTML element.
 */
public final class TitleExtractor {

    /**
     * Extract title from event element.
     *
     * @param element the event element
     * @return the title or empty string if not found
     * @throws NullPointerException if element is null
     */
    public String extract(Element element) {
        requireNonNull(element, "element must not be null");
        Element titleElement = element.selectFirst(TITLE);
        if (titleElement != null) {
            String title = titleElement.text();
            if (!title.isEmpty()) {
                return title;
            }
        }
        return "";
    }
}
