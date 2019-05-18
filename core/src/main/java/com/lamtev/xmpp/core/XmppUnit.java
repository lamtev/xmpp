package com.lamtev.xmpp.core;

public interface XmppUnit {
    int CODE_STREAM_HEADER = 0;
    int CODE_STREAM_FEATURES = 1;
    int CODE_STREAM_CLOSE = 2;
    int CODE_STANZA = 3;
    int CODE_ERROR = 4;
    int CODE_SASL_AUTH = 5;
    int CODE_SASL_AUTH_SUCCESS = 6;
    int CODE_SASL_AUTH_FAILURE = 7;

    /**
     * Unique sequential code associated with concrete XmppUnit implementation.
     * <p>
     * May be used for high-performance "switch" (or if-else-if) like in example below:
     *
     * <pre>
     * {@code
     *
     * interface XMPPUnitAssociatedAction {
     *     void perform(XmppUnit unit);
     * }
     *
     * class Example {
     *     private XMPPUnitAssociatedAction[] actions = new XMPPUnitAssociatedAction[] {
     *       (unit) -> {}, // code == 0
     *       (unit) -> {}, // code == 1
     *       ...
     *       (unit) -> {}  // code == n
     *     };
     *
     *     void example(XmppUnit unit) {
     *         actions[unit.code()].perform(unit);
     *
     *         // instead of
     *         // if (unit instanceof FirstXmppUnit) {
     *         //    //invoke method expecting instance of FirstXmppUnit
     *         //} else if (unit instanceof SecondXmppUnit) {
     *         //    //invoke method expecting instance of SecondXmppUnit
     *         //} else if (...) {
     *         //
     *         //} else if (unit instanceof NthXmppUnit) {
     *         //     //invoke method expecting instance of NthXmppUnit
     *         //}
     *     }
     *
     * }
     *
     * }
     * </pre>
     *
     * @return Unique sequential code
     */
    int code();
}
