package com.lamtev.xmpp.core.io;

import com.lamtev.xmpp.core.XMPPStreamFeature;
import com.lamtev.xmpp.core.XMPPUnit;
import com.lamtev.xmpp.core.construction.XMPPUnitConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.util.EnumSet;

public class XMPPOutputStream implements AutoCloseable {
    @NotNull
    private final OutputStream out;
    @NotNull
    private final XMPPUnitConstructor constructor = new XMPPUnitConstructor();

    public XMPPOutputStream(@NotNull final OutputStream out) {
        this.out = out;
    }

    public void open(@Nullable EnumSet<XMPPStreamFeature> features) {

    }

    public void sendUnit(@NotNull final XMPPUnit unit) {
        try {
            out.write(constructor.construct(unit));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {

    }
}
