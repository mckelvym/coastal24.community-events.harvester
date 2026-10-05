package coastalfcu.events.parser.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests for DateExtractor.
 * Tests date extraction from div.details with br tag parsing.
 */
class DateExtractorTest {

    private DateExtractor extractor;

    @Test
    void extract_DateString_withBrAtStart_returnsEmptyString() {
        String html = """
            <div>
                <div class="details">
                    <br>December 15, 2025
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        // First part before br is empty
        assertThat(result).isEmpty();
    }

    @Test
    void extract_DateString_withComplexDate_extractsCorrectly() {
        String html = """
            <div>
                <div class="details">
                    Saturday, December 25, 2025 at 3:00 PM<br>Holiday Celebration
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEqualTo("Saturday, December 25, 2025 at 3:00 PM");
    }

    @Test
    void extract_DateString_withDateRange_extractsCorrectly() {
        String html = """
            <div>
                <div class="details">
                    Dec 15-20, 2025<br>Week-long event
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEqualTo("Dec 15-20, 2025");
    }

    @Test
    void extract_DateString_withDetailsAndBr_returnsFirstPart() {
        String html = """
            <div>
                <div class="details">
                    December 15, 2025<br>Community Event
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEqualTo("December 15, 2025");
    }

    @Test
    void extract_DateString_withDetailsNoBr_returnsFullText() {
        String html = """
            <div>
                <div class="details">
                    December 15, 2025
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEqualTo("December 15, 2025");
    }

    @Test
    void extract_DateString_withEmptyDetails_returnsEmptyString() {
        String html = """
            <div>
                <div class="details"></div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_DateString_withHtmlEntitiesInDate_extractsRawHtml() {
        // The extractor works on HTML content, not parsed text
        String html = """
            <div>
                <div class="details">
                    Dec 15 &amp; 16, 2025<br>Multi-day event
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEqualTo("Dec 15 &amp; 16, 2025");
    }

    @Test
    void extract_DateString_withMultipleBrTags_returnsFirstPart() {
        String html = """
            <div>
                <div class="details">
                    December 15, 2025<br>Location<br>Description
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEqualTo("December 15, 2025");
    }

    @Test
    void extract_DateString_withMultipleDetailsElements_usesFirst() {
        String html = """
            <div>
                <div class="details">
                    December 15, 2025<br>First Event
                </div>
                <div class="details">
                    January 20, 2026<br>Second Event
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEqualTo("December 15, 2025");
    }

    @Test
    void extract_DateString_withNestedDetails_findsCorrectly() {
        String html = """
            <div>
                <div class="outer">
                    <div class="details">
                        January 5, 2026<br>Event Info
                    </div>
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEqualTo("January 5, 2026");
    }

    @Test
    void extract_DateString_withNoDetails_returnsEmptyString() {
        String html = "<div><p>No details here</p></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_DateString_withNullElement_throwsNullPointerException() {
        assertThatThrownBy(() -> extractor.extractDateString(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("element must not be null");
    }

    @Test
    void extract_DateString_withOnlyBrTag_returnsEmptyString() {
        String html = """
            <div>
                <div class="details"><br></div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_DateString_withOnlyWhitespace_trimsToEmpty() {
        String html = """
            <div>
                <div class="details">   </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_DateString_withSpecialCharactersInDate_extractsCorrectly() {
        String html = """
            <div>
                <div class="details">
                    Dec. 15th, 2025<br>Event
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEqualTo("Dec. 15th, 2025");
    }

    @Test
    void extract_DateString_withTextAfterBrOnly_returnsEmptyString() {
        String html = """
            <div>
                <div class="details"><br>After text</div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_DateString_withWhitespaceAfterBr_trimsCorrectly() {
        String html = """
            <div>
                <div class="details">
                    December 15, 2025<br>  Community Event
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEqualTo("December 15, 2025");
    }

    @Test
    void extract_DateString_withWhitespaceBeforeBr_trimsCorrectly() {
        String html = """
            <div>
                <div class="details">
                    December 15, 2025  <br>Community Event
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractDateString(element);

        assertThat(result).isEqualTo("December 15, 2025");
    }

    @BeforeEach
    void setUp() {
        extractor = new DateExtractor();
    }
}
