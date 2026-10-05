package coastalfcu.events.parser.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TitleExtractorTest {

    private TitleExtractor extractor;

    @Test
    void extract_withEmptyTitle_returnsEmptyString() {
        String html = "<div><div class=\"heading\"><a class=\"stretched-link\"></a></div></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_withLinkContainingNestedElements_returnsTitle() {
        String html = "<div>"
            + "<div class=\"heading\">"
            + "<a class=\"stretched-link\">Event <span>Name</span></a>"
            + "</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("Event Name");
    }

    @Test
    void extract_withMissingHeadingClass_returnsEmptyString() {
        String html = "<div><a class=\"stretched-link\">Event Title</a></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_withMissingStretchedLinkClass_returnsEmptyString() {
        String html = "<div><div class=\"heading\"><a>Event Title</a></div></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_withMultipleHeadingDivs_returnsFirstOne() {
        String html = "<div>"
            + "<div class=\"heading\"><a class=\"stretched-link\">First Title</a></div>"
            + "<div class=\"heading\"><a class=\"stretched-link\">Second Title</a></div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("First Title");
    }

    @Test
    void extract_withMultipleLinks_returnsFirstLink() {
        String html = "<div>"
            + "<div class=\"heading\">"
            + "<a class=\"stretched-link\">First Event</a>"
            + "<a class=\"stretched-link\">Second Event</a>"
            + "</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("First Event");
    }

    @Test
    void extract_withNestedHeadingDiv_returnsTitle() {
        String html = "<div>"
            + "<div class=\"heading\">"
            + "<div><a class=\"stretched-link\">Nested Event</a></div>"
            + "</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("Nested Event");
    }

    @Test
    void extract_withNoMatchingElements_returnsEmptyString() {
        String html = "<div><p>Some content</p><span>More content</span></div>";
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
    void extract_withTitleHavingWhitespace_returnsTrimmedTitle() {
        String html = "<div><div class=\"heading\"><a class=\"stretched-link\">  Community Event "
            + " </a></div></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("Community Event");
    }

    @Test
    void extract_withValidTitle_returnsTitle() {
        String html = "<div><div class=\"heading\"><a class=\"stretched-link\">Beach "
            + "Cleanup</a></div></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("Beach Cleanup");
    }

    @Test
    void extract_withWhitespaceOnlyTitle_returnsEmptyString() {
        String html = "<div><div class=\"heading\"><a class=\"stretched-link\">   </a></div></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEmpty();
    }

    @BeforeEach
    void setUp() {
        extractor = new TitleExtractor();
    }
}
