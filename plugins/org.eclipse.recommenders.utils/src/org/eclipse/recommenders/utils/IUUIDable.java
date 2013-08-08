package org.eclipse.recommenders.utils;

import java.util.UUID;

public interface IUUIDable {

    /**
     * @return a UUID stable for the entire lifetime of this object
     */
    UUID getUUID();
}
