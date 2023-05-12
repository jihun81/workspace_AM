package com.metanetglobal.knowledge.worker.common;

import com.squareup.otto.Bus;

/**
 * OTTO Bus Singleton
 *
 * @author      namki.an
 * @version     1.0.0
 * @see         Bus
 */
public class BusProvider {
    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }
}
