package moj;

class ConstantFormatting {
    public static String formatLongForCPP(String str) {
        return formatSingleLong(str, "LL");
    }

    public static String formatLongArrayForCPP(String str) {
        return formatLongArray(str, "LL");
    } 

    public static String formatLongForJava(String str) {
        return formatSingleLong(str, "L");
    }

    public static String formatLongArrayForJava(String str) {
        return formatLongArray(str, "L");
    } 

    private static String formatSingleLong(String str, String suffix) {
        if (str.trim().equals("")) {
            return "";
        }
        long value = Long.valueOf(str);
        if (value > Integer.MAX_VALUE || value < Integer.MIN_VALUE) {
            str += suffix;
        }
        return str;		
    }

    private static String formatLongArray(String str, String suffix) {
        // Vector of longs, add LL to constants
        String[] tokens = str.split("[^0-9-]");
        StringBuffer fixed = new StringBuffer();
        boolean first = true;
        for (String token : tokens) {
            if (token.isEmpty()) {
                continue;
            }
            if (!first) {
                fixed.append(", ");
            }
            first = false;
            fixed.append(formatSingleLong(token, suffix));
        }
        return "{" + fixed.toString() + "}";
    }
}
