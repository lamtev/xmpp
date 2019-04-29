package com.lamtev.xmpp.core.parsing;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLInputFactory;
import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;

class XmppStreamParserStrategyCacheTest {
    @NotNull
    private static final XmppStreamParserStrategy.Name[] names = XmppStreamParserStrategy.Name.values();
    @NotNull
    private XmppStreamParserStrategyCache cache;

    @BeforeEach
    void setup() throws Exception {
        final var factory = XMLInputFactory.newFactory();
        final var reader = factory.createXMLStreamReader(new ByteArrayInputStream(new byte[0]));

        cache = new XmppStreamParserStrategyCache(reader, (error) -> fail("Unexpected error: " + error));
    }

    @Test
    void testCacheGetReturnsNotNull() {
        for (final var name : XmppStreamParserStrategy.Name.values()) {
            assertNotNull(cache.get(name));
        }
    }

    @Test
    void testCacheDeduplicationFunctionality() {
        for (final var name : names) {
            final var strategy1 = cache.get(name);
            final var strategy2 = cache.get(name);

            assertSame(strategy1, strategy2);
        }
    }
}
