package moj;

import java.util.*;
import java.util.regex.Pattern;

public class LanguageAutoDetection {
    final static String[] CPLUSPLUS_MARKERS = {
        "#\\s*include", "#\\s*define", 
        "private:", "public:", "protected:",
        "struct\\s",
        "using\\s+namespace",
        "template\\s*<",
        "inline\\s*",
        "vector\\s*<",
        "::",
    };
    final static String[] JAVA_MARKERS = {
        "import\\s+java\\.",
        "public \\w",
        "String\\s*\\[\\]", "int\\s*\\[\\]", "long\\s*\\[\\]",
        "HashMap", "TreeMap"
    };
    final static String[] CSHARP_MARKERS = {
        "using\\s+System",
        "string\\s*\\[\\]",
        "\\[,\\]", "\\[,,\\]", "\\[,,,\\]", "\\[,,,,\\]",
    };


    static String filterComments(String source) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<source.length();) {
            if (i+2 <= source.length()) {
                if (source.substring(i, i+2).equals("//")) {
                    int nextpos = source.indexOf("\n", i+2);
                    if (nextpos == -1) break;
                    i = nextpos;
                    continue;
                } else if (source.substring(i, i+2).equals("/*")){
                    int nextpos = source.indexOf("*/", i+2);
                    if (nextpos == -1) break;
                    sb.append(' ');
                    i = nextpos+2;
                    continue;
                }
            } 

            sb.append(source.charAt(i++));
        }
        return sb.toString();
    }

    static boolean isMostLikely(String source, String candidate) {

        source = filterComments(source);

        Map<String, String[]> markers = new TreeMap<String, String[]>();

        markers.put("C++", CPLUSPLUS_MARKERS);
        markers.put("Java", JAVA_MARKERS);
        markers.put("C#", CSHARP_MARKERS);

        System.err.printf("moj language auto detection:");

        int best = 0;
        String ret = "C++";
        boolean first = true;

        for (String language : markers.keySet()) {
            int matched = 0, total = 0;

            for (String m : markers.get(language)) {
                if (Pattern.compile(m, Pattern.MULTILINE).matcher(source).find())
                    ++matched;
                ++total;
            }

            if (matched > best ||
                matched == best && language.equals(candidate)) {
                best = matched;
                ret = language;
            }

            if (!first) System.err.printf(",");
            first = false;
            System.err.printf(" %s %d markers", language, matched);
        }

        System.err.printf("\n");

        return ret.equals(candidate);

    }
}
