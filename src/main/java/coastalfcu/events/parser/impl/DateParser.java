package coastalfcu.events.parser.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Centralized date parsing utility
 */
public final class DateParser {

    private static final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
        DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("M/d/yyyy", Locale.US),
        DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.US),
        DateTimeFormatter.ISO_LOCAL_DATE,
        DateTimeFormatter.RFC_1123_DATE_TIME
    );
    private static final String DATE_RANGE_SEPARATOR = "—";
    private static final String DATE_SUFFIX_SEPARATOR = "\\|";
    private static final Logger LOG = LoggerFactory.getLogger(DateParser.class);

    /**
     * Parses a date string to LocalDate using multiple format strategies.
     * Cleans the input by removing any suffix after "|".
     *
     * @param dateStr the date string to parse
     * @return the parsed LocalDate, or null if parsing fails
     */
    public LocalDate parse(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }

        String cleanDate = dateStr.split(DATE_SUFFIX_SEPARATOR)[0].trim();

        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(cleanDate, formatter);
            } catch (DateTimeParseException e) {
                // Try next formatter
            }
        }

        LOG.warn("Could not parse date '{}' with any known format", dateStr);
        return null;
    }

    /**
     * Parses a date range string and returns start and end dates.
     * Date ranges are formatted as "Dec 10, 2030 — Dec 15, 2030".
     *
     * @param dateStr the date string which may be a range
     * @return array of two LocalDates: [startDate, endDate], where endDate may be null
     */
    public LocalDate[] parseDateRange(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return new LocalDate[]{null, null};
        }

        String datePart = dateStr.split(DATE_SUFFIX_SEPARATOR)[0].trim();

        if (datePart.contains(DATE_RANGE_SEPARATOR)) {
            String[] parts = datePart.split(DATE_RANGE_SEPARATOR);
            LocalDate startDate = parse(parts[0].trim());
            LocalDate endDate = parts.length > 1 ? parse(parts[1].trim()) : null;
            return new LocalDate[]{startDate, endDate};
        }

        LocalDate singleDate = parse(datePart);
        return new LocalDate[]{singleDate, null};
    }

    /**
     * Parses an event date from a string, handling date ranges by returning the end date.
     * Used by EventFilter to determine if an event should be kept.
     *
     * @param dateStr the date string which may be a range
     * @return the end date if range, otherwise the single date, or null if parsing fails
     */
    public LocalDate parseForRetention(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }

        String datePart = dateStr.split(DATE_SUFFIX_SEPARATOR)[0].trim();

        if (datePart.contains(DATE_RANGE_SEPARATOR)) {
            String[] parts = datePart.split(DATE_RANGE_SEPARATOR);
            // Use end date for retention check
            if (parts.length > 1) {
                return parse(parts[parts.length - 1].trim());
            }
        }

        return parse(datePart);
    }
}
