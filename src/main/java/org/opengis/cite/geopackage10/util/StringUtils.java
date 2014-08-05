package org.opengis.cite.geopackage10.util;

import java.util.Arrays;
import java.util.List;

public class StringUtils {
    public static String join(String separator, String... strings) {
        return join(separator, Arrays.asList(strings));
    }

    public static String join(String separator, List<String> strings) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < strings.size(); i++) {
            String s = strings.get(i);
            if (s != null) {
                if (b.length() > 0) {
                    b.append(separator);
                }
                b.append(s);
            }
        }
        return b.toString();
    }
}
