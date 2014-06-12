package org.opengis.cite.geopackage10.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class PatternUtils {
    public static List<Pattern> compileAll(int flags, String... patterns) {
        List<Pattern> allowedTypes = new ArrayList<Pattern>();
        for(String s : patterns) {
            allowedTypes.add(Pattern.compile(s, flags));
        }
        return allowedTypes;
    }

    public static boolean matchesAny(String text, Collection<Pattern> patterns) {
        for(Pattern pattern : patterns ) {
            if(pattern.matcher(text).matches()) {
                return true;
            }
        }
        return false;
    }
}
