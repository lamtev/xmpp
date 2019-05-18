package com.lamtev.xmpp.core.serialization;

import com.lamtev.xmpp.core.*;
import com.lamtev.xmpp.core.XmppStanza.IqQuery.Item;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;

import static com.lamtev.xmpp.core.XmppStanza.IqQuery.Item.Subscription.*;
import static com.lamtev.xmpp.core.XmppStanza.Kind.IQ;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class XmppUnitSerializerTest {
    @NotNull
    private static final XmppUnitSerializer serializer = new XmppUnitSerializer("UTF-8");

    @Test
    void testStreamHeaderSerialization() throws IOException {
        final var expectedStreamHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<stream:stream " +
                "from=\"juliet@im.example.com\" " +
                "to=\"im.example.com\" " +
                "version=\"1.0\" " +
                "xml:lang=\"en\" " +
                "xmlns=\"jabber:client\" " +
                "xmlns:stream=\"http://etherx.jabber.org/streams\">";

        final var streamHeader = new XmppStreamHeader("juliet@im.example.com", "im.example.com", null, 1.0f, XmppStreamHeader.ContentNamespace.CLIENT);

        try (final var baos = new ByteArrayOutputStream()) {
            baos.writeBytes(serializer.serialize(streamHeader));

            assertEquals(expectedStreamHeader, baos.toString(UTF_8));
        }
    }

    @Test
    void testStreamCloseTagSerialization() throws IOException {
        final var expectedStreamCloseTag = "</stream>";

        try (final var baos = new ByteArrayOutputStream()) {
            baos.writeBytes(serializer.serialize(XmppStreamCloseTag.instance()));

            assertEquals(expectedStreamCloseTag, baos.toString(UTF_8));
        }
    }

    @Test
    void testStreamFeaturesSASLSerialization() throws IOException {
        final var expectedStreamFeatures = "<stream:features>" +
                "<mechanisms xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">" +
                "<mechanism>PLAIN</mechanism>" +
                "</mechanisms>" +
                "</stream:features>";

        final var streamFeatures = XmppStreamFeatures.of(XmppStreamFeatures.Type.SASLMechanism.PLAIN);

        try (final var baos = new ByteArrayOutputStream()) {
            baos.writeBytes(serializer.serialize(streamFeatures));

            assertEquals(expectedStreamFeatures, baos.toString(UTF_8));
        }
    }

    @Test
    void testStreamFeaturesResourceBindingSerialization() throws IOException {
        final var expectedStreamFeatures = "<stream:features>" +
                "<bind xmlns=\"urn:ietf:params:xml:ns:xmpp-bind\"/>" +
                "</stream:features>";

        final var streamFeatures = XmppStreamFeatures.of(XmppStreamFeatures.Type.RESOURCE_BINDING);

        try (final var baos = new ByteArrayOutputStream()) {
            baos.writeBytes(serializer.serialize(streamFeatures));

            assertEquals(expectedStreamFeatures, baos.toString(UTF_8));
        }
    }

    @Test
    void testSaslAuthSuccessSerialization() throws IOException {
        final var expectedSuccess = "<success xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\"></success>";

        try (final var baos = new ByteArrayOutputStream()) {
            baos.writeBytes(serializer.serialize(XmppSaslAuthSuccess.instance()));

            assertEquals(expectedSuccess, baos.toString(UTF_8));
        }
    }

    @Test
    void testSaslAuthFailureSerialization() throws IOException {
        final var expectedFailure = "<failure xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\"><not-authorized/></failure>";

        try (final var baos = new ByteArrayOutputStream()) {
            baos.writeBytes(serializer.serialize(XmppSaslAuthFailure.instance()));

            assertEquals(expectedFailure, baos.toString(UTF_8));
        }
    }

    @Test
    void testIqStanzaBindResourceSerialization() throws IOException {
        final var expectedIqStanza = "<iq id=\"yhc13a95\" type=\"set\">" +
                "<bind xmlns=\"urn:ietf:params:xml:ns:xmpp-bind\">" +
                "<resource>balcony</resource>" +
                "</bind>" +
                "</iq>";

        try (final var baos = new ByteArrayOutputStream()) {
            baos.writeBytes(serializer.serialize(new XmppStanza(
                    IQ,
                    "yhc13a95",
                    XmppStanza.TypeAttribute.of(IQ, "set"),
                    new XmppStanza.IqBind("balcony", null)
            )));

            assertEquals(expectedIqStanza, baos.toString(UTF_8));
        }
    }

    @Test
    void testIqStanzaBindJidSerialization() throws IOException {
        final var expectedIqStanza = "<iq id=\"yhc13a95\" type=\"set\">" +
                "<bind xmlns=\"urn:ietf:params:xml:ns:xmpp-bind\">" +
                "<jid>juliet@im.example.com/balcony</jid>" +
                "</bind>" +
                "</iq>";

        try (final var baos = new ByteArrayOutputStream()) {
            baos.writeBytes(serializer.serialize(new XmppStanza(
                    IQ,
                    "yhc13a95",
                    XmppStanza.TypeAttribute.of(IQ, "set"),
                    new XmppStanza.IqBind(null, "juliet@im.example.com/balcony")
            )));

            assertEquals(expectedIqStanza, baos.toString(UTF_8));
        }
    }

    @Test
    void testMessageStanzaBodySerialization() throws IOException {
        final var expectedStanza = "<message from=\"romeo@example.net/orchard\" " +
                "id=\"ju2ba41c\" " +
                "to=\"juliet@im.example.com/balcony\" " +
                "type=\"chat\" " +
                "xml:lang=\"en\">" +
                "<body>Neither, fair saint, if either thee dislike.</body>" +
                "</message>";

        try (final var baos = new ByteArrayOutputStream()) {
            baos.writeBytes(serializer.serialize(new XmppStanza(
                    XmppStanza.Kind.MESSAGE,
                    "juliet@im.example.com/balcony",
                    "romeo@example.net/orchard",
                    "ju2ba41c",
                    XmppStanza.TypeAttribute.of(XmppStanza.Kind.MESSAGE, "chat"),
                    "en",
                    new XmppStanza.MessageBody("Neither, fair saint, if either thee dislike.")
            )));

            assertEquals(expectedStanza, baos.toString(UTF_8));
        }
    }

    @Test
    void testRosterResultSerialization() throws IOException {
        final var expectedStanza = "<iq id=\"hu2bac18\" " +
                "to=\"juliet@example.com/balcony\" " +
                "type=\"result\">" +
                "<query xmlns=\"jabber:iq:roster\" ver=\"ver11\">" +
                "<item jid=\"romeo@example.net\" " +
                "name=\"Romeo\" " +
                "subscription=\"both\"/>" +
                "<item jid=\"mercutio@example.com\" " +
                "name=\"Mercutio\" " +
                "subscription=\"from\"/>" +
                "<item jid=\"benvolio@example.net\" " +
                "name=\"Benvolio\" " +
                "subscription=\"to\"/>" +
                "</query>" +
                "</iq>";

        try (final var baos = new ByteArrayOutputStream()) {
            baos.writeBytes(serializer.serialize(new XmppStanza(
                    IQ,
                    "juliet@example.com/balcony",
                    null,
                    "hu2bac18",
                    XmppStanza.TypeAttribute.of(IQ, "result"),
                    null,
                    new XmppStanza.IqQuery(
                            XmppStanza.IqQuery.SupportedContentNamespace.ROSTER,
                            "ver11",
                            List.of(
                                    new Item("romeo@example.net", "Romeo", BOTH),
                                    new Item("mercutio@example.com", "Mercutio", FROM),
                                    new Item("benvolio@example.net", "Benvolio", TO)
                            )
                    )
            )));

            assertEquals(expectedStanza, baos.toString(UTF_8));
        }
    }

    @Test
    void testRosterSetSerialization() throws IOException {
        final var expectedStanza = "<iq id=\"hu2bac18\" " +
                "to=\"juliet@example.com/balcony\" " +
                "type=\"set\">" +
                "<query xmlns=\"jabber:iq:roster\" ver=\"ver11\">" +
                "<item jid=\"romeo@example.net\" " +
                "name=\"\">" +
                "<group>Family</group>" +
                "<group>Job</group>" +
                "</item>" +
                "</query>" +
                "</iq>";

        try (final var baos = new ByteArrayOutputStream()) {
            baos.writeBytes(serializer.serialize(new XmppStanza(
                    IQ,
                    "juliet@example.com/balcony",
                    null,
                    "hu2bac18",
                    XmppStanza.TypeAttribute.of(IQ, "set"),
                    null,
                    new XmppStanza.IqQuery(
                            XmppStanza.IqQuery.SupportedContentNamespace.ROSTER,
                            "ver11",
                            List.of(new Item("romeo@example.net", "", new LinkedHashSet<>(List.of("Family", "Job"))))
                    )
            )));

            assertEquals(expectedStanza, baos.toString(UTF_8));
        }
    }

    @Test
    void testRosterErrorItemNotFound() throws IOException {
        final var expectedStanza = "<iq id=\"1d474603-25ff-41ca-9a4c-a41f04e3cf42\" type=\"error\">" +
                "<error type=\"cancel\">" +
                "<item-not-found xmlns=\"urn:ietf:params:xml:ns:xmpp-stanzas\"></item-not-found>" +
                "</error>" +
                "</iq>";

        try (final var baos = new ByteArrayOutputStream()) {
            baos.writeBytes(serializer.serialize(new XmppStanza(
                    IQ,
                    null,
                    null,
                    "1d474603-25ff-41ca-9a4c-a41f04e3cf42",
                    XmppStanza.TypeAttribute.of(IQ, "error"),
                    null,
                    XmppStanza.Error.of(IQ, XmppStanza.Error.Type.CANCEL, XmppStanza.Error.DefinedCondition.ITEM_NOT_FOUND)
            )));

            assertEquals(expectedStanza, baos.toString(UTF_8));
        }
    }

    @Test
    void testIqStanzaUnsupportedSerialization() throws IOException {
        final var expectedIqStanza = "<iq id=\"553f51bc-58c6-4425-942c-bd4365734e19\" type=\"result\"><vCard xmlns=\"vcard-temp\"/></iq>";

        try (final var baos = new ByteArrayOutputStream()) {
            baos.writeBytes(serializer.serialize(new XmppStanza(
                    IQ,
                    "553f51bc-58c6-4425-942c-bd4365734e19",
                    XmppStanza.TypeAttribute.of(IQ, "result"),
                    new XmppStanza.UnsupportedElement("vCard", "vcard-temp", XmppStanza.TopElement.CODE_IQ_UNSUPPORTED)
            )));

            assertEquals(expectedIqStanza, baos.toString(UTF_8));
        }
    }

    @Test
    void testIqStanzaQueryUnsupportedElementSerialization() throws IOException {
        final var expectedIqQueryStanza = "<iq id=\"8467485a\" type=\"result\"><query xmlns=\"jabber:iq:private\"><storage xmlns=\"storage:bookmarks\"/></query></iq>";

        try (final var baos = new ByteArrayOutputStream()) {
            baos.writeBytes(serializer.serialize(new XmppStanza(
                    IQ,
                    "8467485a",
                    XmppStanza.TypeAttribute.of(IQ, "result"),
                    new XmppStanza.IqQuery(
                            new XmppStanza.IqQuery.UnsupportedContentNamespace("jabber:iq:private"),
                            null,
                            List.of(new XmppStanza.IqQuery.UnsupportedElement("storage", "storage:bookmarks"))
                    )
            )));

            assertEquals(expectedIqQueryStanza, baos.toString(UTF_8));
        }
    }
}
