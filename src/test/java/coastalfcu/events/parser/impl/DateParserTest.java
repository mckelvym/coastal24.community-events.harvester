package coastalfcu.events.parser.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for DateParser.
 * Verifies date parsing with multiple format strategies and date ranges.
 */
class DateParserTest {

    private DateParser parser;

    @Test
    void parseDateRange_withDateRangeAndPipeSuffix_returnsBothDates() {
        LocalDate[] result = parser.parseDateRange("Dec 10, 2030 — Dec 15, 2030 | 10:00 AM");

        assertThat(result[0]).isEqualTo(LocalDate.of(2030, 12, 10));
        assertThat(result[1]).isEqualTo(LocalDate.of(2030, 12, 15));
    }

    // Tests for parse() method

    @Test
    void parseDateRange_withDateRange_returnsBothDates() {
        LocalDate[] result = parser.parseDateRange("Dec 10, 2030 — Dec 15, 2030");

        assertThat(result[0]).isEqualTo(LocalDate.of(2030, 12, 10));
        assertThat(result[1]).isEqualTo(LocalDate.of(2030, 12, 15));
    }

    @Test
    void parseDateRange_withEmptyString_returnsNullArray() {
        LocalDate[] result = parser.parseDateRange("");

        assertThat(result[0]).isNull();
        assertThat(result[1]).isNull();
    }

    @Test
    void parseDateRange_withNull_returnsNullArray() {
        LocalDate[] result = parser.parseDateRange(null);

        assertThat(result[0]).isNull();
        assertThat(result[1]).isNull();
    }

    @Test
    void parseDateRange_withSingleDate_returnsStartDateOnly() {
        LocalDate[] result = parser.parseDateRange("Dec 15, 2025");

        assertThat(result[0]).isEqualTo(LocalDate.of(2025, 12, 15));
        assertThat(result[1]).isNull();
    }

    @Test
    void parseForRetention_withDateRange_returnsEndDate() {
        LocalDate result = parser.parseForRetention("Dec 10, 2030 — Dec 15, 2030");

        assertThat(result).isEqualTo(LocalDate.of(2030, 12, 15));
    }

    @Test
    void parseForRetention_withNull_returnsNull() {
        LocalDate result = parser.parseForRetention(null);

        assertThat(result).isNull();
    }

    @Test
    void parseForRetention_withSingleDate_returnsDate() {
        LocalDate result = parser.parseForRetention("Dec 15, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 12, 15));
    }

    @Test
    void parse_withAbbreviatedMonthFormat_returnsLocalDate() {
        LocalDate result = parser.parse("Dec 15, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 12, 15));
    }

    @Test
    void parse_withBlankString_returnsNull() {
        LocalDate result = parser.parse("   ");

        assertThat(result).isNull();
    }

    // Tests for parseDateRange() method

    @Test
    void parse_withEmptyString_returnsNull() {
        LocalDate result = parser.parse("");

        assertThat(result).isNull();
    }

    @Test
    void parse_withFullMonthFormat_returnsLocalDate() {
        LocalDate result = parser.parse("December 15, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 12, 15));
    }

    @Test
    void parse_withInvalidFormat_returnsNull() {
        LocalDate result = parser.parse("not a date");

        assertThat(result).isNull();
    }

    @Test
    void parse_withIsoFormat_returnsLocalDate() {
        LocalDate result = parser.parse("2025-12-15");

        assertThat(result).isEqualTo(LocalDate.of(2025, 12, 15));
    }

    @Test
    void parse_withNull_returnsNull() {
        LocalDate result = parser.parse(null);

        assertThat(result).isNull();
    }

    // Tests for parseForRetention() method

    @Test
    void parse_withPipeSuffix_removesAndParses() {
        LocalDate result = parser.parse("Dec 15, 2025 | 10:00 AM");

        assertThat(result).isEqualTo(LocalDate.of(2025, 12, 15));
    }

    @Test
    void parse_withSlashFormat_returnsLocalDate() {
        LocalDate result = parser.parse("12/15/2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 12, 15));
    }

    @BeforeEach
    void setUp() {
        parser = new DateParser();
    }
}
