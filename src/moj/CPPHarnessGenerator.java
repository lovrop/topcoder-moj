package moj;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import com.topcoder.client.contestant.ProblemComponentModel;
import com.topcoder.shared.language.Language;
import com.topcoder.shared.problem.*;

public class CPPHarnessGenerator implements HarnessGenerator {
    final ProblemComponentModel m_problem;
    final Language				m_lang;

    final Preferences           m_pref;
    final String                m_targetCompiler;
    
    static class TestCodeGenerationState {
        public Set<String> headers = new TreeSet<String>();
        public ArrayList<String> lines = new ArrayList<String>();
        
        public void add(String line) {
            lines.add(line);
        }
        
        public void addHeader(String header) {
            headers.add(header);
        }
    }

    public CPPHarnessGenerator(ProblemComponentModel problem, Language lang, Preferences pref) {
        m_problem = problem;
        m_lang = lang;
        m_pref = pref;
        m_targetCompiler = pref.getTargetCompiler();
    }

    public String generateDefaultMain() {
        return
                "#include <cstdlib>\n" +
                "int main(int argc, char *argv[]) {\n" +
                "\tif (argc == 1) {\n" +
                "\t\tmoj_harness::run_test();\n" + 
                "\t} else {\n" +
                "\t\tfor (int i=1; i<argc; ++i)\n" +
                "\t\t\tmoj_harness::run_test(std::atoi(argv[i]));\n" +
                "\t}\n" +
                "}";
    }

    public String generateRunTest() {
        return "moj_harness::run_test();";
    }

    void generateNamespaceStart(TestCodeGenerationState code) {
        code.add("namespace moj_harness {");
        // Always pull in std::string and std::vector so that types for test
        // case parameters and return values work
        code.addHeader("string");
        code.addHeader("vector");
        code.add("   using std::string;");
        code.add("   using std::vector;");
    }

    void generateRunTest(TestCodeGenerationState code) {
        code.addHeader("iostream");
        
        code.add("   int run_test_case(int);");
        code.add("   void run_test(int casenum = -1, bool quiet = false) {");

        code.add("      if (casenum != -1) {");
        code.add("         if (run_test_case(casenum) == -1 && !quiet) {");
        code.add("            std::cerr << \"Illegal input! Test case \" << casenum << \" does not exist.\" << std::endl;");
        code.add("         }");
        code.add("         return;");
        code.add("      }");
        code.add("      ");
        code.add("      int correct = 0, total = 0;");
        code.add("      for (int i=0;; ++i) {");
        code.add("         int x = run_test_case(i);");
        code.add("         if (x == -1) {");
        code.add("            if (i >= 100) break;");
        code.add("            continue;");
        code.add("         }");
        code.add("         correct += x;");
        code.add("         ++total;");
        code.add("      }");
        code.add("      ");
        code.add("      if (total == 0) {");
        code.add("         std::cerr << \"No test cases run.\" << std::endl;");
        code.add("      } else if (correct < total) {");
        code.add("         std::cerr << \"Some cases FAILED (passed \" << correct << \" of \" << total << \").\" << std::endl;");
        code.add("      } else {");
        code.add("         std::cerr << \"All \" << total << \" tests passed!\" << std::endl;");
        code.add("      }");
        code.add("   }");
        code.add("   ");
    }

    void generateOutputComparison(TestCodeGenerationState code) {
        DataType returnType = m_problem.getReturnType();
        if (returnType.getBaseName().equals("double")) {
            code.addHeader("algorithm"); // min, max
            code.addHeader("cmath");     // isinf, isnan, fabs
            String isinf, isnan;
            if (m_targetCompiler.equals(Preferences.TARGETCOMPILER_VC)) {
                isinf = "!_finite";
                isnan = "_isnan";
            } else if (m_targetCompiler.equals(Preferences.TARGETCOMPILER_GCC11)) {
                isinf = "std::isinf";
                isnan = "std::isnan";
            } else {
                isinf = "isinf";
                isnan = "isnan";
            }

            code.add("   static const double MAX_DOUBLE_ERROR = 1e-9;");
            code.add("   static bool topcoder_fequ(double expected, double result) {");
            code.add("      if (" + isnan + "(expected)) {");
            code.add("         return " + isnan + "(result);");
            code.add("      } else if (" + isinf + "(expected)) {");
            code.add("         if (expected > 0) {");
            code.add("            return result > 0 && " + isinf + "(result);");
            code.add("         } else {");
            code.add("            return result < 0 && " + isinf + "(result);");
            code.add("         }");
            code.add("      } else if (" + isnan + "(result) || " + isinf + "(result)) {");
            code.add("         return false;");
            code.add("      } else if (std::fabs(result - expected) < MAX_DOUBLE_ERROR) {");
            code.add("         return true;");
            code.add("      } else {");
            code.add("         double mmin = std::min(expected * (1.0 - MAX_DOUBLE_ERROR), expected * (1.0 + MAX_DOUBLE_ERROR));");
            code.add("         double mmax = std::max(expected * (1.0 - MAX_DOUBLE_ERROR), expected * (1.0 + MAX_DOUBLE_ERROR));");
            code.add("         return result > mmin && result < mmax;");
            code.add("      }");
            code.add("   }");
            code.add("   double moj_relative_error(double expected, double result) {");
            code.add("      if (" + isnan + "(expected) || " + isinf + "(expected) || " + isnan + "(result) || " + isinf + "(result) || expected == 0) {");
            code.add("         return 0;");
            code.add("      }");
            code.add("      return std::fabs(result-expected) / std::fabs(expected);");
            code.add("   }");
            if (returnType.getDimension() > 0) {
                code.addHeader("vector");
                code.add("   static bool topcoder_fequ(const vector<double> &a, const vector<double> &b) { if (a.size() != b.size()) return false; for (size_t i=0; i<a.size(); ++i) if (!topcoder_fequ(a[i], b[i])) return false; return true; }");
                code.add("   double moj_relative_error(const vector<double> &expected, const vector<double> &result) { double ret = 0.0; for (size_t i=0; i<expected.size(); ++i) { ret = std::max(ret, moj_relative_error(expected[i], result[i])); } return ret; }");
            }
            code.add("   ");
        }
    }

    void generateFormatResult(TestCodeGenerationState code) {
        DataType returnType = m_problem.getReturnType();
        if (returnType.getDimension() > 0) {
            code.addHeader("vector");
            code.add("   template<typename T> std::ostream& operator<<(std::ostream &os, const vector<T> &v) { os << \"{\"; for (typename vector<T>::const_iterator vi=v.begin(); vi!=v.end(); ++vi) { if (vi != v.begin()) os << \",\"; os << \" \" << *vi; } os << \" }\"; return os; }");
            if (returnType.getBaseName().equals("String")) {
                code.addHeader("string");
                code.add("   template<> std::ostream& operator<<(std::ostream &os, const vector<string> &v) { os << \"{\"; for (vector<string>::const_iterator vi=v.begin(); vi!=v.end(); ++vi) { if (vi != v.begin()) os << \",\"; os << \" \\\"\" << *vi << \"\\\"\"; } os << \" }\"; return os; }");
            }
            code.add("");
        }
    }

    void generateVerifyCase(TestCodeGenerationState code) {
        DataType returnType = m_problem.getReturnType();
        String typeName = returnType.getDescriptor(m_lang);

        code.addHeader("cstdio");
        code.addHeader("ctime");
        code.addHeader("iostream");
        code.addHeader("string");
        code.addHeader("vector");
        code.add("   int verify_case(int casenum, const " + typeName + " &expected, const " + typeName + " &received, std::clock_t elapsed) { ");
        code.add("      std::cerr << \"Example \" << casenum << \"... \"; ");
        code.add("      ");
        code.add("      string verdict;");
        code.add("      vector<string> info;");
        code.add("      char buf[100];");
        code.add("      ");
        code.add("      if (elapsed > CLOCKS_PER_SEC / 200) {");
        code.add("         std::sprintf(buf, \"time %.2fs\", elapsed * (1.0/CLOCKS_PER_SEC));");
        code.add("         info.push_back(buf);");
        code.add("      }");
        code.add("      ");

        // Print "PASSED" or "FAILED" based on the result
        if (returnType.getBaseName().equals("double")) {
            code.add("      if (topcoder_fequ(expected, received)) {");
            code.add("         verdict = \"PASSED\";");
            code.add("         double rerr = moj_relative_error(expected, received); ");
            code.add("         if (rerr > 0) {");
            code.add("            std::sprintf(buf, \"relative error %.3e\", rerr);");
            code.add("            info.push_back(buf);");
            code.add("         }");
        } else {
            code.add("      if (expected == received) {");
            code.add("         verdict = \"PASSED\";");
        }
        code.add("      } else {");
        code.add("         verdict = \"FAILED\";");
        code.add("      }");
        code.add("      ");
        code.add("      std::cerr << verdict;");
        code.add("      if (!info.empty()) {");
        code.add("         std::cerr << \" (\";");
        code.add("         for (size_t i=0; i<info.size(); ++i) {");
        code.add("            if (i > 0) std::cerr << \", \";");
        code.add("            std::cerr << info[i];");
        code.add("         }");
        code.add("         std::cerr << \")\";");
        code.add("      }");
        code.add("      std::cerr << std::endl;");
        code.add("      ");

        code.add("      if (verdict == \"FAILED\") {");
        if (returnType.getBaseName().equals("String") &&	returnType.getDimension() == 0) {
            code.add("         std::cerr << \"    Expected: \\\"\" << expected << \"\\\"\" << std::endl; ");
            code.add("         std::cerr << \"    Received: \\\"\" << received << \"\\\"\" << std::endl; ");
        } else {
            code.add("         std::cerr << \"    Expected: \" << expected << std::endl; ");
            code.add("         std::cerr << \"    Received: \" << received << std::endl; ");
        }
        code.add("      }");
        code.add("      ");
        code.add("      return verdict == \"PASSED\";");
        code.add("   }");
        code.add("");
    }

    static boolean representsEmptyArray(String s) {
        return s.replaceAll("\\s+", "").equals("{}");
    }

    void generateParameter(TestCodeGenerationState code, DataType paramType, String name, String contents, boolean isPlaceholder) {
        if (isPlaceholder) {
            contents = "";
        }

        String baseName = paramType.getBaseName();
        boolean isLong = baseName.equals("long");
        String typeName = "";
        if (paramType.getDimension() == 0) {
            // Scalar
            typeName = paramType.getDescriptor(m_lang) + " " + name;
            if (isLong) {
                contents = ConstantFormatting.formatLongForCPP(contents);
            }
        } else {
            typeName = (isLong ? "long long" : baseName.toLowerCase()) + " " + name + "[]";

            if (!isPlaceholder) {
                if (m_targetCompiler.equals(Preferences.TARGETCOMPILER_VC) &&
                        representsEmptyArray(contents)) {
                    typeName = "// " + typeName;
                    contents = "empty, commented out for VC++";
                } else if (isLong) {
                    // Vector of longs, add LL to constants
                    contents = ConstantFormatting.formatLongArrayForCPP(contents);		      
                }
            }
        }

        while (typeName.length() < 25) {
            typeName = typeName + " ";
        }

        if (!baseName.equals("String")) {
            // Compress spaces in non-strings
            contents = contents.replaceAll("\\s+", " "); 
        }

        code.add("         " + typeName + " = " + contents + ";");
    }

    String vectorize(DataType type, String name, String contents, boolean isPlaceholder) {
        if (type.getDimension() == 0) {
            return name;
        } else {
            if (!isPlaceholder && 
                    m_targetCompiler.equals(Preferences.TARGETCOMPILER_VC) &&
                    representsEmptyArray(contents)) {
                // Visual C++ empty array hack
                return type.getDescriptor(m_lang) + "()";
            }

            return type.getDescriptor(m_lang) + "(" + name + ", " + name + " + (sizeof " + name + " / sizeof " + name + "[0]))";
        }
    }

    void generateTestCase(TestCodeGenerationState code, int index, TestCase testCase, boolean isPlaceholder) {
        DataType[] paramTypes = m_problem.getParamTypes();
        String[] paramNames = m_problem.getParamNames();
        DataType returnType = m_problem.getReturnType();

        String[] inputs = testCase.getInput();
        String output = testCase.getOutput();

        /*
         * Generate code for setting up individual test cases
         * and calling the method with these parameters.
         */
        // Generate each input variable separately
        for (int i = 0; i < inputs.length; ++i) {
            generateParameter(code, paramTypes[i], paramNames[i], inputs[i], isPlaceholder);
        }

        // Generate the output variable as the last variable
        generateParameter(code, returnType, "expected__", output, isPlaceholder);

        code.add("");
        code.add("         std::clock_t start__      = std::clock();");

        // Generate the function call
        StringBuffer call = new StringBuffer();
        call.append(returnType.getDescriptor(m_lang) + " received__");
        while (call.length() < 25) {
            call.append(' ');
        }
        call.append(" = " + m_problem.getClassName() + "()." + m_problem.getMethodName() + "(");
        for (int i = 0; i < inputs.length; ++i) {
            call.append(vectorize(paramTypes[i], paramNames[i], inputs[i], isPlaceholder));
            if (i < inputs.length-1) {
                call.append(", ");
            }
        }
        call.append(");");
        code.add("         " + call);

        code.add("         return verify_case(casenum__, " + vectorize(returnType, "expected__", output, isPlaceholder) + ", received__, clock()-start__);");
    }

    void generateRunTestCase(TestCodeGenerationState code) {
        TestCase[] testCases = m_problem.getTestCases();
        
        code.add("   int run_test_case(int casenum__) {");
        code.add("      switch (casenum__) {");
        // Generate the individual test cases
        int totalCases = testCases.length + m_pref.getNumPlaceholders();
        for (int i = 0; i < totalCases; ++i) {
            if (i == testCases.length) {
                code.add("");
                code.add("      // custom cases");
                code.add("");
            }
            code.add((i >= testCases.length ? "/*" : "") + "      case " + i + ": {");
            generateTestCase(code, i, testCases[i < testCases.length ? i : 0], i >= testCases.length);
            code.add("      }" + (i >= testCases.length ? "*/" : ""));
        }

        // next
        code.add("      default:");
        code.add("         return -1;");
        code.add("      }");
        code.add("   }");
    }

    public String generateTestCode() {
        TestCodeGenerationState code = new TestCodeGenerationState();

        generateNamespaceStart(code);
        generateRunTest(code);

        generateOutputComparison(code);
        generateFormatResult(code);
        generateVerifyCase(code);
        generateRunTestCase(code);
        code.add("}");

        StringBuilder sb = new StringBuilder();
        for (String header : code.headers) {
            sb.append("#include <");
            sb.append(header);
            sb.append(">\n");
        }
        for (String s : code.lines) {
            sb.append(s);
            sb.append('\n');
        }
        String ret = sb.toString();
        ret = Pattern.compile("^               ", Pattern.MULTILINE).matcher(ret).replaceAll("\t\t\t\t\t");
        ret = Pattern.compile("^            "   , Pattern.MULTILINE).matcher(ret).replaceAll("\t\t\t\t");
        ret = Pattern.compile("^         "      , Pattern.MULTILINE).matcher(ret).replaceAll("\t\t\t");
        ret = Pattern.compile("^      "         , Pattern.MULTILINE).matcher(ret).replaceAll("\t\t");
        ret = Pattern.compile("^   "            , Pattern.MULTILINE).matcher(ret).replaceAll("\t");
        return ret;
    }
}
