package com.lamtev.xmpp.core.util;

import com.lamtev.xmpp.core.XmppStanza;
import com.lamtev.xmpp.core.XmppStanza.IqQuery.Item;
import com.lamtev.xmpp.core.XmppStanza.IqTypeAttribute;
import com.lamtev.xmpp.core.XmppStanza.TypeAttribute;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.lamtev.xmpp.core.XmppStanza.Kind.IQ;

public final class XmppStanzas {
    @NotNull
    public static XmppStanza errorOf(@NotNull final XmppStanza stanza, @NotNull final XmppStanza.Error.Type type, @NotNull final XmppStanza.Error.DefinedCondition definedCondition) {
        return new XmppStanza(
                stanza.kind(),
                stanza.from(),
                stanza.to(),
                stanza.id(),
                TypeAttribute.of(stanza.kind(), "error"),
                null,
                XmppStanza.Error.of(stanza.kind(), type, definedCondition)
        );
    }

    @NotNull
    public static XmppStanza rosterResultOf(@NotNull final XmppStanza stanza, @NotNull final List<Item> items) {
        return new XmppStanza(
                IQ,
                stanza.from(),
                null,
                stanza.id(),
                IqTypeAttribute.RESULT,
                null,
                new XmppStanza.IqQuery(
                        XmppStanza.IqQuery.ContentNamespace.ROSTER,
                        null,
                        items
                )
        );
    }
}
