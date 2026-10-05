package coastalfcu.events.parser.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DescriptionExtractorTest {

    private DescriptionExtractor extractor;

    @Test
    void extract_withEmptyBodyDiv_returnsEmptyString() {
        String html = "<div>"
            + "<div class=\"body\">"
            + "<div></div>"
            + "</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_withHtmlEntities_preservesEntities() {
        String html = "<div>"
            + "<div class=\"body\">"
            + "<div>Description with &amp; symbols &lt;test&gt;</div>"
            + "</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("Description with &amp; symbols &lt;test&gt;");
    }

    @Test
    void extract_withMultipleDivsInBody_returnsFirstMatchingDiv() {
        String html = "<div>"
            + "<div class=\"body\">"
            + "<div class=\"heading\">Event Title</div>"
            + "<div class=\"details\">Event Details</div>"
            + "<div>Main content here</div>"
            + "</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("Main content here");
    }

    @Test
    void extract_withNestedHtml_preservesStructure() {
        String html = "<div>"
            + "<div class=\"body\">"
            + "<div><p>Paragraph <a href=\"#\">with link</a></p></div>"
            + "</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).contains("<p>").contains("<a href");
    }

    @Test
    void extract_withNoBodyDiv_returnsEmptyString() {
        String html = "<div><p>Some content</p></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_withNullElement_throwsNullPointerException() {
        assertThatThrownBy(() -> extractor.extract(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("element must not be null");
    }

    @Test
    void extract_withOnlyHeadingAndDetails_returnsEmptyString() {
        String html = "<div>"
            + "<div class=\"body\">"
            + "<div class=\"heading\">Title</div>"
            + "<div class=\"details\">Details</div>"
            + "</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_withValidBodyDiv_returnsHtml() {
        String html = "<div>"
            + "<div class=\"body\">"
            + "<div>Event description with <b>formatting</b></div>"
            + "</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("Event description with <b>formatting</b>");
    }

    @Test
    void extract_withWhitespace_returnsTrimmedHtml() {
        String html = "<div>"
            + "<div class=\"body\">"
            + "<div>  Event content  </div>"
            + "</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("Event content");
    }

    @BeforeEach
    void setUp() {
        extractor = new DescriptionExtractor();
    }
}
