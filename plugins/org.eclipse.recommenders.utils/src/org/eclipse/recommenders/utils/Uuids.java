package org.eclipse.recommenders.utils;

import static org.apache.commons.lang3.SystemUtils.getUserHome;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Files;

public class Uuids {

    private static UUID anonymousId;

    public static UUID getAnonymousId() {
        if (anonymousId == null) {
            try {
                anonymousId = readOrCreateUUID();
            } catch (IOException e) {
                Throwables.propagate(e);
            }
        }
        return anonymousId;
    }

    private static UUID readOrCreateUUID() throws IOException {
        File f = new File(getUserHome(), ".eclipse/org.eclipse.recommenders.privacy/anonymousId");
        if (f.exists()) {
            String uuid = Files.readFirstLine(f, Charsets.UTF_8);
            return UUID.fromString(uuid);
        } else {
            f.getParentFile().mkdirs();
            UUID uuid = UUID.randomUUID();
            Files.write(uuid.toString(), f, Charsets.UTF_8);
            return uuid;
        }
    }
}
