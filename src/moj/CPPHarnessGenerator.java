package moj;

import java.util.ArrayList;
import java.util.regex.Pattern;

import com.topcoder.client.contestant.ProblemComponentModel;
import com.topcoder.shared.language.Language;
import com.topcoder.shared.problem.*;

public class CPPHarnessGenerator implements HarnessGenerator {
	final ProblemComponentModel m_problem;
	final Language				    m_lang;

	final Preferences           m_pref;
	final String                m_targetCompiler;
	
	public CPPHarnessGenerator(ProblemComponentModel problem, Language lang, Preferences pref) {
		m_problem = problem;
		m_lang = lang;
		m_pref = pref;
		m_targetCompiler = pref.getTargetCompiler();
	}
	
	public String generateDefaultMain() {
		return
		   "int main(int argc, char *argv[]) {\n" +
		   "\tif (argc == 1) {\n" +
		   "\t\tmoj_harness::run_test();\n" + 
	       "\t} else {\n" +
	       "\t\tfor (int i=1; i<argc; ++i)\n" +
	       "\t\t\tmoj_harness::run_test(atoi(argv[i]));\n" +
	       "\t}\n" +
	       "}";
	}
	
	public String generateRunTest() {
		return "moj_harness::run_test();";
	}
	
	void generateNamespaceStart(ArrayList<String> code) {
		code.add("namespace moj_harness {");
	}
	
	void generateRunTest(ArrayList<String> code) {
		code.add("   int run_test_case(int);");
		code.add("   void run_test(int casenum = -1, bool quiet = false) {");

		code.add("      if (casenum != -1) {");
		code.add("         if (run_test_case(casenum) == -1 && !quiet) {");
		code.add("            cerr << \"Illegal input! Test case \" << casenum << \" does not exist.\" << endl;");
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
		code.add("         cerr << \"No test cases run.\" << endl;");
		code.add("      } else if (correct < total) {");
		code.add("         cerr << \"Some cases FAILED (passed \" << correct << \" of \" << total << \").\" << endl;");
		code.add("      } else {");
		code.add("         cerr << \"All \" << total << \" tests passed!\" << endl;");
		code.add("      }");
		code.add("   }");
		code.add("   ");
	}
	
	void generateOutputComparison(ArrayList<String> code) {
		DataType returnType = m_problem.getReturnType();
		if (returnType.getBaseName().equals("double")) {
			if (m_targetCompiler.equals(Preferences.TARGETCOMPILER_VC)) {
				code.add("   bool isinf(const double x) { return !_finite(x); }");
				code.add("   bool isnan(const double x) { return _isnan(x); }");
			}
			
			code.add("   static const double MAX_DOUBLE_ERROR = 1e-9; static bool topcoder_fequ(double expected, double result) { if (isnan(expected)) { return isnan(result); } else if (isinf(expected)) { if (expected > 0) { return result > 0 && isinf(result); } else { return result < 0 && isinf(result); } } else if (isnan(result) || isinf(result)) { return false; } else if (fabs(result - expected) < MAX_DOUBLE_ERROR) { return true; } else { double mmin = min(expected * (1.0 - MAX_DOUBLE_ERROR), expected * (1.0 + MAX_DOUBLE_ERROR)); double mmax = max(expected * (1.0 - MAX_DOUBLE_ERROR), expected * (1.0 + MAX_DOUBLE_ERROR)); return result > mmin && result < mmax; } }");
			code.add("   double moj_relative_error(double expected, double result) { if (isnan(expected) || isinf(expected) || isnan(result) || isinf(result) || expected == 0) return 0; return fabs(result-expected) / fabs(expected); }");
			if (returnType.getDimension() > 0) {
				code.add("   static bool topcoder_fequ(const vector<double> &a, const vector<double> &b) { if (a.size() != b.size()) return false; for (size_t i=0; i<a.size(); ++i) if (!topcoder_fequ(a[i], b[i])) return false; return true; }");
				code.add("   double moj_relative_error(const vector<double> &expected, const vector<double> &result) { double ret = 0.0; for (size_t i=0; i<expected.size(); ++i) { ret = max(ret, moj_relative_error(expected[i], result[i])); } return ret; }");
			}
			code.add("   ");
		}
	}
	
	void generateFormatResult(ArrayList<String> code) {
		DataType returnType = m_problem.getReturnType();
		if (returnType.getDimension() > 0) {
  	       code.add("   template<typename T> ostream& operator<<(ostream &os, const vector<T> &v) { os << \"{\"; for (typename vector<T>::const_iterator vi=v.begin(); vi!=v.end(); ++vi) { if (vi != v.begin()) os << \",\"; os << \" \" << *vi; } os << \" }\"; return os; }");
  	       if (returnType.getBaseName().equals("String")) {
  	    	   code.add("   template<> ostream& operator<<(ostream &os, const vector<string> &v) { os << \"{\"; for (vector<string>::const_iterator vi=v.begin(); vi!=v.end(); ++vi) { if (vi != v.begin()) os << \",\"; os << \" \\\"\" << *vi << \"\\\"\"; } os << \" }\"; return os; }");
  	       }
  	       code.add("");
		}
	}
	
	void generateVerifyCase(ArrayList<String> code) {
		DataType returnType = m_problem.getReturnType();
		String typeName = returnType.getDescriptor(m_lang);

		code.add("   int verify_case(int casenum, const " + typeName + " &expected, const " + typeName + " &received, clock_t elapsed) { ");
		code.add("      cerr << \"Example \" << casenum << \"... \"; ");
		code.add("      ");
		code.add("      string verdict;");
		code.add("      vector<string> info;");
		code.add("      char buf[100];");
		code.add("      ");
		code.add("      if (elapsed > CLOCKS_PER_SEC / 200) {");
		code.add("         sprintf(buf, \"time %.2fs\", elapsed * (1.0/CLOCKS_PER_SEC));");
		code.add("         info.push_back(buf);");
		code.add("      }");
		code.add("      ");
		
		// Print "PASSED" or "FAILED" based on the result
		if (returnType.getBaseName().equals("double")) {
			code.add("      if (topcoder_fequ(expected, received)) {");
			code.add("         verdict = \"PASSED\";");
			code.add("         double rerr = moj_relative_error(expected, received); ");
			code.add("         if (rerr > 0) {");
			code.add("            sprintf(buf, \"relative error %.3e\", rerr);");
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
		code.add("      cerr << verdict;");
		code.add("      if (!info.empty()) {");
		code.add("         cerr << \" (\";");
		code.add("         for (int i=0; i<(int)info.size(); ++i) {");
		code.add("            if (i > 0) cerr << \", \";");
		code.add("            cerr << info[i];");
		code.add("         }");
		code.add("         cerr << \")\";");
		code.add("      }");
		code.add("      cerr << endl;");
		code.add("      ");

		code.add("      if (verdict == \"FAILED\") {");
		if (returnType.getBaseName().equals("String") &&	returnType.getDimension() == 0) {
			code.add("         cerr << \"    Expected: \\\"\" << expected << \"\\\"\" << endl; ");
			code.add("         cerr << \"    Received: \\\"\" << received << \"\\\"\" << endl; ");
		} else {
			code.add("         cerr << \"    Expected: \" << expected << endl; ");
			code.add("         cerr << \"    Received: \" << received << endl; ");
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
	
   static String addLLToIntConstant(String str) {
      if (str.trim().equals("")) {
         return "";
      }
      long value = Long.valueOf(str);
      if (value > Integer.MAX_VALUE || 
          value < Integer.MIN_VALUE) {
         str += "LL";
      }
      return str;
   }

	void generateParameter(ArrayList<String> code, DataType paramType, String name, String contents, boolean isPlaceholder) {
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
	         contents = addLLToIntConstant(contents);
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
		         String[] tokens = contents.split("[^0-9-]");
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
		            fixed.append(addLLToIntConstant(token));
		         }
		         contents = "{" + fixed.toString() + "}";
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

	void generateTestCase(ArrayList<String> code, int index, TestCase testCase, boolean isPlaceholder) {
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
		code.add("         clock_t start__           = clock();");
		
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

		code.add("         return verify_case(casenum, " + vectorize(returnType, "expected__", output, isPlaceholder) + ", received__, clock()-start__);");
	}
	
	void generateRunTestCase(ArrayList<String> code) {
		TestCase[] testCases = m_problem.getTestCases();
		
		code.add("   int run_test_case(int casenum) {");
		code.add("      switch (casenum) {");
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
		ArrayList<String> code = new ArrayList<String>();

		generateNamespaceStart(code);
		generateRunTest(code);

		generateOutputComparison(code);
		generateFormatResult(code);
		generateVerifyCase(code);
		generateRunTestCase(code);
		code.add("}");

		StringBuffer sb = new StringBuffer();
		for (String s : code) {
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
