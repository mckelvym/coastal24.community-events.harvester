package coastalfcu.events.feed;

import coastalfcu.events.domain.EventItem;
import java.util.List;
import java.util.Set;

/**
 * Interface for managing RSS feed operations.
 */
public interface RssFeedManager {

    /**
     * Generate an RSS feed with new and existing events.
     *
     * @param filePath         the path to the output RSS file
     * @param newEvents        list of new events to add
     * @param existingFilePath the path to the existing RSS file to import from
     * @throws Exception if generation fails
     */
    void generateFeed(String filePath, List<EventItem> newEvents,
                      String existingFilePath)
        throws Exception;

    /**
     * Load existing event GUIDs from an RSS file.
     *
     * @param filePath the path to the RSS file
     * @return set of existing GUIDs
     * @throws Exception if loading fails
     */
    Set<String> loadExistingGuids(String filePath)
        throws Exception;
}
