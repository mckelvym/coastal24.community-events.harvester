package coastalfcu.events.parser.impl;

import static coastalfcu.events.parser.impl.CssSelectors.IMAGE;
import static coastalfcu.events.parser.impl.HtmlConstants.ABS_SRC_ATTR;
import static coastalfcu.events.parser.impl.HtmlConstants.EMPTY;
import static java.util.Objects.requireNonNull;

import org.jsoup.nodes.Element;

/**
 * Extracts event image URL from HTML element.
 */
public final class ImageExtractor {

    /**
     * Extract image URL from event element.
     *
     * @param element the event element
     * @return the image URL or empty string if not found
     * @throws NullPointerException if element is null
     */
    public String extract(Element element) {
        requireNonNull(element, "element must not be null");
        Element imageElement = element.selectFirst(IMAGE);
        if (imageElement != null) {
            String src = imageElement.attr(ABS_SRC_ATTR);
            if (!src.isEmpty()) {
                return src;
            }
        }
        return EMPTY;
    }
}
