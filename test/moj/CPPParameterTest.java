package moj;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import moj.mocks.*;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.topcoder.shared.language.CPPLanguage;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.InvalidTypeException;

@RunWith(Parameterized.class)
public class CPPParameterTest {
   private String typename, value, expected;
   
   public CPPParameterTest(String typename, String varval, String expected) {
      this.typename = typename;
      this.value = varval;
      this.expected = expected;
   }
   
   CPPHarnessGenerator generator;
   ArrayList<String> code;
   
   @Before public void setUp() {
      generator = new CPPHarnessGenerator(
         new ProblemComponentModelMock(),
         CPPLanguage.CPP_LANGUAGE,
         new PreferencesMock()
      );
      code = new ArrayList<String>();
   }
   
   static String compressSpace(String str) {
      return str.replaceAll("\\s+", " ").trim();
   }
   
   @Parameters
   public static List<Object[]> data() {
      Object[][] data = new Object[][] { 
         {"int", "-2147483648", "int var = -2147483648;"},
        
         {"String", "\"test string\"", "string var = \"test string\";"},
        
         {"double", "1.345e08", "double var = 1.345e08;"},
        
         {"long", "123",         "long long var = 123;"},
         {"long", "-9999999999", "long long var = -9999999999LL;"},
         {"long", "2147483648",  "long long var = 2147483648LL;"},
        
         {"int[]", "{-2147483648, 2147483647, 0, -1, 555}", "int var[] = {-2147483648, 2147483647, 0, -1, 555};"},
         {"int[]", "{}",  "int var[] = {};"},
        
         {"String[]", "{\"a\",\n \"\",\n \"test test\"}", "string var[] = {\"a\", \"\", \"test test\"};"},
        
         {"double[]", "{ 1e9, -3.e-012, -4, 5 }", "double var[] = { 1e9, -3.e-012, -4, 5 };"},
        
         {"long[]", "{ 0, -1, 1, 2147483648,\n-2147483649, 9223372036854775807, -9223372036854775808}", "long long var[] = {0, -1, 1, 2147483648LL, -2147483649LL, 9223372036854775807LL, -9223372036854775808LL};"},
      };
      return Arrays.asList(data);
   }
   
   @Test public void test() throws InvalidTypeException {
      DataType dt = DataTypeFactoryMock.getDataType(this.typename);
      generator.generateParameter(code, dt, "var", this.value, false);
      String result = compressSpace(code.get(0).toString());
      assertEquals(this.expected, result);
   }
}
