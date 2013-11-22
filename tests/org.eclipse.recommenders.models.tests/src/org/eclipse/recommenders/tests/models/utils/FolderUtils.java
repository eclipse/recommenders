package org.eclipse.recommenders.tests.models.utils;

import java.io.File;

public class FolderUtils {
    
    public static File dir(String... dirs) {
        File file = File.listRoots()[0];
        for (String dir : dirs) {
            file = new File(file, dir);
        }
        return file;
    }
}
