package com.lamtev.xmpp.core.util;

import com.lamtev.xmpp.core.XmppStanza;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

import static com.lamtev.xmpp.core.XmppStanza.Kind.IQ;

public final class XmppStanzas {
    @NotNull
    public static XmppStanza errorOf(@NotNull final XmppStanza stanza, @NotNull final XmppStanza.Error.Type type, @NotNull final XmppStanza.Error.DefinedCondition definedCondition) {
        return new XmppStanza(
                stanza.kind(),
                stanza.from(),
                stanza.to(),
                stanza.id(),
                XmppStanza.TypeAttribute.of(stanza.kind(), "error"),
                null,
                XmppStanza.Error.of(stanza.kind(), type, definedCondition)
        );
    }

    @NotNull
    public static XmppStanza rosterResultOf(@NotNull final XmppStanza stanza, @NotNull final List<String> jids) {
        return new XmppStanza(
                IQ,
                stanza.from(),
                null,
                stanza.id(),
                XmppStanza.IqTypeAttribute.RESULT,
                null,
                new XmppStanza.IqQuery(
                        XmppStanza.IqQuery.ContentNamespace.ROSTER,
                        null,
                        jids.stream()
                                .map(XmppStanza.IqQuery.Item::new)
                                .collect(Collectors.toList())
                )
        );
    }
}
