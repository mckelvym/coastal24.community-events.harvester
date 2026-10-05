package coastalfcu.events.parser.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ImageExtractorTest {

    private ImageExtractor extractor;

    @Test
    void extract_withAbsoluteUrl_returnsFullUrl() {
        String html = "<div>"
            + "<div class=\"image\">"
            + "<img src=\"https://example.com/image.png\" />"
            + "</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("https://example.com/image.png");
    }

    @Test
    void extract_withEmptyImageDiv_returnsEmptyString() {
        String html = "<div>"
            + "<div class=\"image\"></div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_withEmptySrc_returnsEmptyString() {
        String html = "<div>"
            + "<div class=\"image\">"
            + "<img src=\"\" />"
            + "</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_withMultipleImages_returnsFirst() {
        String html = "<div>"
            + "<div class=\"image\">"
            + "<img src=\"/first.jpg\" />"
            + "<img src=\"/second.jpg\" />"
            + "</div>"
            + "</div>";
        Element element = Jsoup.parse(html, "https://www.coastal24.com").body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("https://www.coastal24.com/first.jpg");
    }

    @Test
    void extract_withNoImageDiv_returnsEmptyString() {
        String html = "<div><p>No image</p></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_withNoImgTag_returnsEmptyString() {
        String html = "<div>"
            + "<div class=\"image\">"
            + "<p>Text content</p>"
            + "</div>"
            + "</div>";
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
    void extract_withValidImage_returnsAbsoluteUrl() {
        String html = "<div>"
            + "<div class=\"image\">"
            + "<img src=\"/events/photo.jpg\" />"
            + "</div>"
            + "</div>";
        Element element = Jsoup.parse(html, "https://www.coastal24.com").body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("https://www.coastal24.com/events/photo.jpg");
    }

    @BeforeEach
    void setUp() {
        extractor = new ImageExtractor();
    }
}
