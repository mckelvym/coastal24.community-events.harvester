package coastalfcu.events.feed;

import static coastalfcu.events.feed.RssElementNames.CHANNEL;
import static coastalfcu.events.feed.RssElementNames.DESCRIPTION;
import static coastalfcu.events.feed.RssElementNames.ENCLOSURE;
import static coastalfcu.events.feed.RssElementNames.ENCODING_UTF8;
import static coastalfcu.events.feed.RssElementNames.EVENT_NAMESPACE_URI;
import static coastalfcu.events.feed.RssElementNames.EV_ENDDATE;
import static coastalfcu.events.feed.RssElementNames.EV_STARTDATE;
import static coastalfcu.events.feed.RssElementNames.GUID;
import static coastalfcu.events.feed.RssElementNames.IMAGE_JPEG_TYPE;
import static coastalfcu.events.feed.RssElementNames.INDENT_AMOUNT;
import static coastalfcu.events.feed.RssElementNames.IS_PERMALINK_ATTR;
import static coastalfcu.events.feed.RssElementNames.ITEM;
import static coastalfcu.events.feed.RssElementNames.LAST_BUILD_DATE;
import static coastalfcu.events.feed.RssElementNames.LINK;
import static coastalfcu.events.feed.RssElementNames.PUB_DATE;
import static coastalfcu.events.feed.RssElementNames.RSS;
import static coastalfcu.events.feed.RssElementNames.RSS_VERSION;
import static coastalfcu.events.feed.RssElementNames.TITLE;
import static coastalfcu.events.feed.RssElementNames.TRUE_VALUE;
import static coastalfcu.events.feed.RssElementNames.TYPE_ATTR;
import static coastalfcu.events.feed.RssElementNames.URL_ATTR;
import static coastalfcu.events.feed.RssElementNames.VERSION_ATTR;
import static coastalfcu.events.feed.RssElementNames.XMLNS_EV_ATTR;
import static coastalfcu.events.feed.RssElementNames.XSLT_INDENT_PROPERTY;
import static java.util.Objects.requireNonNull;

import coastalfcu.events.config.ScraperConfiguration;
import coastalfcu.events.domain.EventItem;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implementation of RssFeedManager for RSS 2.0 feeds.
 */
public class RssFeedManagerImpl implements RssFeedManager {

    private static final DateTimeFormatter DATE_DISPLAY_FORMAT =
        DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
    private static final Logger LOG =
        LoggerFactory.getLogger(RssFeedManagerImpl.class);
    private final ScraperConfiguration config;
    private final EventFilter eventFilter;
    private final XmlSecurityConfigurer securityConfigurer;

    /**
     * Create a new RssFeedManagerImpl.
     *
     * @param config scraper configuration
     * @throws NullPointerException if config is null
     */
    public RssFeedManagerImpl(ScraperConfiguration config) {
        this.config = requireNonNull(config, "config must not be null");
        this.eventFilter = new EventFilter(config);
        this.securityConfigurer = new XmlSecurityConfigurer();
    }

    private void addChannelMetadata(Document doc, Element channel) {
        appendChild(doc, channel, TITLE, config.getFeedTitle());
        appendChild(doc, channel, LINK, config.getFeedLink());
        appendChild(doc, channel, DESCRIPTION, config.getFeedDescription());

        appendChild(doc, channel, LAST_BUILD_DATE,
            ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME));
    }

    /**
     * Adds a description element wrapped in CDATA, omitting it when empty.
     *
     * @param doc         the XML document
     * @param item        the item element to add to
     * @param description the description HTML or text
     */
    private void addDescriptionElement(Document doc, Element item,
                                       String description) {
        if (description.isEmpty()) {
            return;
        }
        Element element = doc.createElement(DESCRIPTION);
        element.appendChild(doc.createCDATASection(description));
        item.appendChild(element);
    }

    /**
     * Adds the machine-readable event dates (RSS Event module) used for retention.
     *
     * @param doc   the XML document
     * @param item  the item element to add to
     * @param event the event whose dates to add
     */
    private void addEventDateElements(Document doc, Element item,
                                      EventItem event) {
        Element startDate = doc.createElement(EV_STARTDATE);
        startDate.setTextContent(event.eventDateStart().toString());
        item.appendChild(startDate);
        if (event.eventDateEnd() != null) {
            Element endDate = doc.createElement(EV_ENDDATE);
            endDate.setTextContent(event.eventDateEnd().toString());
            item.appendChild(endDate);
        }
    }

    /**
     * Adds the item GUID, which is always the event URL and therefore a permalink.
     *
     * @param doc   the XML document
     * @param item  the item element to add to
     * @param event the event whose GUID to add
     */
    private void addGuidElement(Document doc, Element item, EventItem event) {
        Element guid = doc.createElement(GUID);
        guid.setAttribute(IS_PERMALINK_ATTR, TRUE_VALUE);
        guid.setTextContent(event.guid());
        item.appendChild(guid);
    }

    private void addItem(Document doc, Element channel, EventItem event) {
        Element item = doc.createElement(ITEM);
        channel.appendChild(item);

        // Add title with date in parentheses
        String title = formatTitle(event);
        appendChild(doc, item, TITLE, title);
        appendChild(doc, item, LINK, event.link());
        addGuidElement(doc, item, event);
        addEventDateElements(doc, item, event);

        // Add pubDate as current time (harvest time)
        appendChild(doc, item, PUB_DATE,
            ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME));

        // Always added: the formatted description carries the event date used for retention
        addDescriptionElement(doc, item, formatDescription(event));

        // Add image as enclosure element (RSS 2.0 standard)
        if (event.hasImage()) {
            Element enclosure = doc.createElement(ENCLOSURE);
            enclosure.setAttribute(URL_ATTR, event.imageUrl());
            enclosure.setAttribute(TYPE_ATTR, IMAGE_JPEG_TYPE);
            item.appendChild(enclosure);
        }
    }

    private void appendChild(
        Document doc,
        Element parent,
        String tagName,
        String textContent) {
        if (textContent == null || textContent.isEmpty()) {
            return;
        }
        Element element = doc.createElement(tagName);
        element.setTextContent(textContent);
        parent.appendChild(element);
    }

    private String formatDateRange(EventItem event) {
        LocalDate start = event.eventDateStart();
        LocalDate end = event.eventDateEnd();

        if (end != null) {
            return start.format(DATE_DISPLAY_FORMAT) + " — " + end.format(DATE_DISPLAY_FORMAT);
        }
        return start.format(DATE_DISPLAY_FORMAT);
    }

    private String formatDescription(EventItem event) {
        StringBuilder sb = new StringBuilder();

        String dateDisplay = formatDateRange(event);
        sb.append("<strong>Date:</strong> ").append(dateDisplay).append("<br/>");

        String location = event.location();
        if (location != null && !location.isEmpty()) {
            sb.append("<strong>Location:</strong> ").append(location)
                .append("<br/><br/>");
        }

        sb.append(event.sanitizedDescription());

        return sb.toString();
    }

    private String formatTitle(EventItem event) {
        String dateDisplay = formatDateRange(event);
        return event.title() + " (" + dateDisplay + ")";
    }

    @Override
    public void generateFeed(
        String filePath,
        List<EventItem> newEvents,
        String existingFilePath)
        throws Exception {
        requireNonNull(filePath, "filePath must not be null");
        requireNonNull(newEvents, "newEvents must not be null");
        requireNonNull(existingFilePath, "existingFilePath must not be null");
        LOG.info("Generating RSS feed with {} new events", newEvents.size());

        DocumentBuilderFactory factory =
            securityConfigurer.createSecureDocumentBuilderFactory();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        Element rss = doc.createElement(RSS);
        rss.setAttribute(VERSION_ATTR, RSS_VERSION);
        rss.setAttribute(XMLNS_EV_ATTR, EVENT_NAMESPACE_URI);
        doc.appendChild(rss);

        Element channel = doc.createElement(CHANNEL);
        rss.appendChild(channel);

        addChannelMetadata(doc, channel);

        // Add new events (sorted by eventDateStart descending), skipping any past retention
        List<EventItem> sortedEvents = new ArrayList<>(newEvents);
        sortedEvents.sort(Comparator.comparing(EventItem::eventDateStart).reversed());

        for (EventItem event : sortedEvents) {
            if (eventFilter.shouldKeep(event)) {
                addItem(doc, channel, event);
            }
        }

        importExistingEvents(doc, channel, new File(existingFilePath));

        File file = new File(filePath);
        writeXmlToFile(doc, file);

        LOG.info("RSS feed written to {}", file.getAbsolutePath());
    }

    /**
     * Imports items from the existing feed, dropping those past the retention period.
     *
     * <p>Errors are logged rather than thrown so a scheduled run still publishes new events.
     *
     * @param doc              the new feed document
     * @param channel          the channel to append items to
     * @param existingFeedFile the existing feed file (may not exist)
     */
    private void importExistingEvents(Document doc, Element channel,
                                      File existingFeedFile) {
        if (!existingFeedFile.exists()) {
            return;
        }
        try {
            DocumentBuilder builder =
                securityConfigurer.createSecureDocumentBuilderFactory().newDocumentBuilder();
            NodeList items = builder.parse(existingFeedFile).getElementsByTagName(ITEM);
            int imported = 0;
            for (int i = 0; i < items.getLength(); i++) {
                Element item = (Element) items.item(i);
                if (eventFilter.shouldKeep(item)) {
                    Node importedNode = doc.importNode(item, true);
                    removeWhitespaceNodes(importedNode);
                    channel.appendChild(importedNode);
                    imported++;
                }
            }
            LOG.info("Imported {} existing events, dropped {} past retention",
                imported, items.getLength() - imported);
        } catch (Exception e) {
            LOG.error("Failed to import existing events from {}: {}",
                existingFeedFile, e.getMessage(), e);
        }
    }

    @Override
    public Set<String> loadExistingGuids(String filePath)
        throws Exception {
        requireNonNull(filePath, "filePath must not be null");
        Set<String> guids = new HashSet<>();
        File file = new File(filePath);

        if (!file.exists()) {
            LOG.info("No existing RSS file found at {}", file.getAbsolutePath());
            return guids;
        }

        DocumentBuilderFactory factory =
            securityConfigurer.createSecureDocumentBuilderFactory();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(file);

        NodeList items = doc.getElementsByTagName(ITEM);
        for (int idx = 0; idx < items.getLength(); idx++) {
            Element item = (Element) items.item(idx);
            NodeList guidNodes = item.getElementsByTagName(GUID);
            if (guidNodes.getLength() > 0) {
                String guid = guidNodes.item(0).getTextContent();
                guids.add(guid);
            }
        }

        LOG.info("Loaded {} existing GUIDs", guids.size());
        return guids;
    }

    /**
     * Removes whitespace-only text nodes from a DOM tree.
     *
     * <p>This is necessary to ensure proper indentation when writing XML.
     *
     * @param node The root node to clean
     */
    private void removeWhitespaceNodes(Node node) {
        Deque<Node> stack = new ArrayDeque<>();
        stack.push(node);

        while (!stack.isEmpty()) {
            Node current = stack.pop();
            NodeList children = current.getChildNodes();

            for (int i = children.getLength() - 1; i >= 0; i--) {
                Node child = children.item(i);
                if (child.getNodeType() == Node.TEXT_NODE) {
                    if (child.getTextContent().trim().isEmpty()) {
                        current.removeChild(child);
                    }
                } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                    stack.push(child);
                }
            }
        }
    }

    private void writeXmlToFile(Document doc, File file)
        throws TransformerException, IOException {
        TransformerFactory transformerFactory =
            securityConfigurer.createSecureTransformerFactory();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, ENCODING_UTF8);
        transformer.setOutputProperty(XSLT_INDENT_PROPERTY, INDENT_AMOUNT);

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(outputStream);
            transformer.transform(source, result);
        }
    }
}
