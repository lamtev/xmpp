package com.lamtev.xmpp.core.parsing;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLInputFactory;
import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class XMPPStreamParserStrategyCacheTest {
    @NotNull
    private static final XMPPStreamParserStrategy.Name[] names = XMPPStreamParserStrategy.Name.values();
    @NotNull
    private XMPPStreamParserStrategyCache cache;

    @BeforeEach
    void setup() throws Exception {
        final var factory = XMLInputFactory.newFactory();
        final var reader = factory.createXMLStreamReader(new ByteArrayInputStream(new byte[0]));

        cache = new XMPPStreamParserStrategyCache(reader, (error) -> {});
    }

    @Test
    void testCacheGetReturnsNotNull() {
        for (final var name : XMPPStreamParserStrategy.Name.values()) {
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
