package com.lamtev.xmpp.core;

public interface XmppUnit {
    int CODE_STREAM_HEADER = 0;
    int CODE_STREAM_FEATURES = 1;
    int CODE_STANZA = 2;
    int CODE_ERROR = 3;
    int CODE_SASL_AUTH_SUCCESS = 4;

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
     *         // if (unit instanceof FirstXMPPUnit) {
     *         //    //invoke method expecting instance of FirstXMPPUnit
     *         //} else if (unit instanceof SecondXMPPUnit) {
     *         //    //invoke method expecting instance of SecondXMPPUnit
     *         //} else if (...) {
     *         //
     *         //} else if (unit instanceof NthXMPPUnit) {
     *         //     //invoke method expecting instance of NthXMPPUnit
     *         //}
     *     }
     *
     * }
     *
     * </pre>
     *
     * @return Unique sequential code
     */
    int code();
}
