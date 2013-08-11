package moj;

import java.util.HashMap;
import java.util.Map;

import com.topcoder.client.contestant.ProblemComponentModel;
import com.topcoder.shared.language.Language;
import com.topcoder.shared.problem.*;

public class moj {
    // Map used to store my tags
    private Map<String, String> m_Tags = new HashMap<String, String>();

    // Constants
    private static final String k_TESTCODE      = "$TESTCODE$";
    private static final String k_DEFAULTMAIN   = "$DEFAULTMAIN$";
    private static final String k_RUNTEST       = "$RUNTEST$";
    private static final String k_VERSION       = "\n// Powered by moj 4.18 [modified TZTester]";

    // Preferences
    private Preferences pref = new Preferences();

    public String preProcess(String source, ProblemComponentModel problem, Language lang, Renderer renderer) {
        // Set defaults for the tags in case we exit out early
        m_Tags.put(k_TESTCODE, "");
        m_Tags.put(k_RUNTEST, "");
        m_Tags.put(k_DEFAULTMAIN, "");

        // If there is source and the language matches that in the actual code, return it
        if (source.length() > 0 && 
            (!pref.getLanguageSwitchWorkaround() || LanguageAutoDetection.isMostLikely(source, lang.getName()))) {
            return source;
        }

        // See if we are needed at all and select the appropriate generator
        HarnessGenerator generator = null;
        if (lang.getName().equals("C++")) {
            generator = new CPPHarnessGenerator(problem, lang, pref);
        } else if (lang.getName().equals("Java")) {
            if (!pref.getEnableJavaSupport()) return "";
            generator = new JavaHarnessGenerator(problem, lang, pref);
        } else {
            return "";
        }

        // Re-initialize the tags
        m_Tags.clear();

        // Get the test cases
        TestCase[] testCases = problem.getTestCases();

        // Check to see if test cases are defined
        if ((testCases == null) || (testCases.length == 0)) {
            m_Tags.put(k_TESTCODE, "// *** moj WARNING *** No test cases defined for this problem");
            return "";
        }

        m_Tags.put(k_TESTCODE, generator.generateTestCode());
        m_Tags.put(k_DEFAULTMAIN, generator.generateDefaultMain());
        m_Tags.put(k_RUNTEST, generator.generateRunTest());
        return "";
    }

    public String postProcess(String source, Language lang)	{
        return source + k_VERSION;
    }

    public Map<String, String> getUserDefinedTags()	{
        return m_Tags;
    }

    public void configure() {
        new ConfigurationDialog(pref).setVisible(true);
    }
}
