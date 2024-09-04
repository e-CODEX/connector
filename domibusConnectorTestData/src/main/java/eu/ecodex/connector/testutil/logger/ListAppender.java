/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.testutil.logger;

/*
 * copied from log4j2 sources, to avoid including log4j2-tests.jar which is not managed by spring
 * will also include some additional methods for tests
 *
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.impl.MutableLogEvent;

/**
 * This appender is primarily used for testing. Use in a real environment is discouraged as the List
 * could eventually grow to cause an OutOfMemoryError.
 *
 * <p>This appender is not thread-safe.
 *
 * <p>This appender will use {@link Layout#toByteArray(LogEvent)}.
 */
@Plugin(
    name = "List", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE,
    printObject = true
)
public class ListAppender extends AbstractAppender {
    // Use CopyOnWriteArrayList?
    final List<LogEvent> events = new ArrayList<>();
    private final List<String> messages = new ArrayList<>();
    final List<byte[]> data = new ArrayList<>();
    private final boolean newLine;
    private final boolean raw;
    private static final String WINDOWS_LINE_SEP = "\r\n";
    /**
     * CountDownLatch for asynchronous logging tests. Example usage:
     *
     * <pre>
     * &#64;Rule
     * public LoggerContextRule context = new LoggerContextRule("log4j-list.xml");
     * private ListAppender listAppender;
     *
     * &#64;Before
     * public void before() throws Exception {
     *     listAppender = context.getListAppender("List");
     * }
     *
     * &#64;Test
     * public void testSomething() throws Exception {
     *     listAppender.countDownLatch = new CountDownLatch(1);
     *
     *     Logger logger = LogManager.getLogger();
     *     logger.info("log one event anynchronously");
     *
     *     // wait for the appender to finish processing this event (wait max 1 second)
     *     listAppender.countDownLatch.await(1, TimeUnit.SECONDS);
     *
     *     // now assert something or do follow-up tests...
     * }
     * </pre>
     */
    public static final CountDownLatch countDownLatch = null;

    /**
     * The ListAppender class represents an appender that stores log events in a list.
     *
     * <p>This class extends the AbstractAppender class and provides functionality to append log
     * events to the list. It supports options to control the formatting and behavior of the log
     * events.
     *
     * @param name The name of the ListAppender.
     */
    public ListAppender(final String name) {
        super(name, null, null);
        newLine = false;
        raw = false;
    }

    /**
     * The ListAppender class represents an appender that stores log events in a list.
     *
     * <p>This class extends the AbstractAppender class and provides functionality to append log
     * events to the list. It supports options to control the formatting and behavior of the log
     * events.
     *
     * @param name The name of the ListAppender.
     * @param filter The filter to determine if a log event should be appended.
     * @param layout The layout to format the log event.
     * @param newline Whether to insert newline characters between log events.
     * @param raw Whether to store log events as raw byte arrays.
     */
    public ListAppender(
        final String name, final Filter filter, final Layout<? extends Serializable> layout,
        final boolean newline, final boolean raw) {
        super(name, filter, layout);
        this.newLine = newline;
        this.raw = raw;
        if (layout != null) {
            final byte[] bytes = layout.getHeader();
            if (bytes != null) {
                write(bytes);
            }
        }
    }

    @Override
    public synchronized void append(final LogEvent event) {
        final Layout<? extends Serializable> layout = getLayout();
        if (layout == null) {
            if (event instanceof MutableLogEvent mutableLogEvent) {
                // must take snapshot or subsequent calls to logger.log() will modify this event
                events.add(mutableLogEvent.createMemento());
            } else {
                events.add(event);
            }
        } else {
            write(layout.toByteArray(event));
        }
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }

    void write(final byte[] bytes) {
        if (raw) {
            data.add(bytes);
            return;
        }
        final var str = new String(bytes);
        if (newLine) {
            var index = 0;
            while (index < str.length()) {
                int end;
                final var wend = str.indexOf(WINDOWS_LINE_SEP, index);
                final var lend = str.indexOf('\n', index);
                int length;
                if (wend >= 0 && wend < lend) {
                    end = wend;
                    length = 2;
                } else {
                    end = lend;
                    length = 1;
                }
                if (index == end) {
                    if (!messages.get(messages.size() - length).isEmpty()) {
                        messages.add("");
                    }
                } else if (end >= 0) {
                    messages.add(str.substring(index, end));
                } else {
                    messages.add(str.substring(index));
                    break;
                }
                index = end + length;
            }
        } else {
            messages.add(str);
        }
    }

    @Override
    public boolean stop(final long timeout, final TimeUnit timeUnit) {
        setStopping();
        super.stop(timeout, timeUnit, false);
        final Layout<? extends Serializable> layout = getLayout();
        if (layout != null) {
            final byte[] bytes = layout.getFooter();
            if (bytes != null) {
                write(bytes);
            }
        }
        setStopped();
        return true;
    }

    /**
     * Clears the events, messages, and data lists of the ListAppender.
     *
     * @return the ListAppender instance after clearing the lists
     */
    public synchronized ListAppender clear() {
        events.clear();
        messages.clear();
        data.clear();
        return this;
    }

    public synchronized List<LogEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }

    public synchronized List<String> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    /**
     * Polls the messages list for it to grow to a given minimum size at most timeout timeUnits and
     * return a copy of what we have so far.
     */
    public List<String> getMessages(final int minSize, final long timeout, final TimeUnit timeUnit)
        throws InterruptedException {
        final long endMillis = System.currentTimeMillis() + timeUnit.toMillis(timeout);
        while (messages.size() < minSize && System.currentTimeMillis() < endMillis) {
            Thread.sleep(100);
        }
        return Collections.unmodifiableList(messages);
    }

    public synchronized List<byte[]> getData() {
        return Collections.unmodifiableList(data);
    }

    public static ListAppender createAppender(
        final String name, final boolean newLine, final boolean raw,
        final Layout<? extends Serializable> layout, final Filter filter) {
        return new ListAppender(name, filter, layout, newLine, raw);
    }

    @PluginBuilderFactory
    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * The Builder class represents a builder for creating an instance of ListAppender.
     *
     * <p>It implements the org.apache.logging.log4j.core.util.Builder interface and provides
     * methods to set the configuration properties of the ListAppender. Once all the properties
     * are set, the build() method can be called to create the ListAppender instance.
     *
     * <p>The following properties can be configured using the builder:
     * - name: The name of the ListAppender.
     * - entryPerNewLine: Whether to insert newline characters between log events.
     * - raw: Whether to store log events as raw byte arrays.
     * - layout: The layout to format the log event.
     * - filter: The filter to determine if a log event should be appended.
     */
    public static class Builder
        implements org.apache.logging.log4j.core.util.Builder<ListAppender> {
        @PluginBuilderAttribute
        @Required
        private String name;
        @PluginBuilderAttribute
        private boolean entryPerNewLine;
        @PluginBuilderAttribute
        private boolean raw;
        @PluginElement("Layout")
        private Layout<? extends Serializable> layout;
        @PluginElement("Filter")
        private Filter filter;

        public Builder setName(final String name) {
            this.name = name;
            return this;
        }

        public Builder setEntryPerNewLine(final boolean entryPerNewLine) {
            this.entryPerNewLine = entryPerNewLine;
            return this;
        }

        public Builder setRaw(final boolean raw) {
            this.raw = raw;
            return this;
        }

        public Builder setLayout(final Layout<? extends Serializable> layout) {
            this.layout = layout;
            return this;
        }

        public Builder setFilter(final Filter filter) {
            this.filter = filter;
            return this;
        }

        @Override
        public ListAppender build() {
            return new ListAppender(name, filter, layout, entryPerNewLine, raw);
        }
    }

    /**
     * Gets the named ListAppender if it has been registered.
     *
     * @param name the name of the ListAppender
     * @return the named ListAppender or {@code null} if it does not exist
     */
    public static ListAppender getListAppender(final String name) {
        return (LoggerContext.getContext(false)).getConfiguration()
                                                .getAppender(name);
    }

    @Override
    public String toString() {
        return "ListAppender [events=" + events + ", messages=" + messages + ", data=" + data
            + ", newLine=" + newLine
            + ", raw=" + raw + ", countDownLatch=" + countDownLatch + ", getHandler()="
            + getHandler()
            + ", getLayout()=" + getLayout() + ", getName()=" + getName() + ", ignoreExceptions()="
            + ignoreExceptions() + ", getFilter()=" + getFilter() + ", getState()=" + getState()
            + "]";
    }
}
