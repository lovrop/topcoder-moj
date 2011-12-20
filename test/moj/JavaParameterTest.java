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

import com.topcoder.shared.language.JavaLanguage;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.InvalidTypeException;

@RunWith(Parameterized.class)
public class JavaParameterTest {
   private String typename, value, expected;
   
   public JavaParameterTest(String typename, String varval, String expected) {
      this.typename = typename;
      this.value = varval;
      this.expected = expected;
   }
   
   JavaHarnessGenerator generator;
   ArrayList<String> code;
   
   @Before public void setUp() {
      generator = new JavaHarnessGenerator(
         new ProblemComponentModelMock(),
         JavaLanguage.JAVA_LANGUAGE,
         new PreferencesMock()
      );
      code = new ArrayList<String>();
   }
   
   static String compressSpaceBeforeEquals(String str) {
      return str.replaceFirst("\\s*=", " =").trim();
   }
   
   @Parameters
   public static List<Object[]> data() {
      Object[][] data = new Object[][] { 
         {"int", "-2147483648", "int var = -2147483648;"},
        
         {"String", "\"test string\"", "String var = \"test string\";"},
         {"String", "\"   multiple  spaces  \"", "String var = \"   multiple  spaces  \";"},
         {"String", "\"  a  =  3  \"", "String var = \"  a  =  3  \";"},
        
         {"double", "1.345e08", "double var = 1.345e08;"},
        
         {"long", "123",         "long var = 123;"},
         {"long", "-9999999999", "long var = -9999999999L;"},
         {"long", "2147483648",  "long var = 2147483648L;"},
        
         {"int[]", "{-2147483648, 2147483647, 0, -1, 555}", "int[] var = {-2147483648, 2147483647, 0, -1, 555};"},
         {"int[]", "{}",  "int[] var = {};"},
        
         {"String[]", "{\"a\",\n \"\",\n \"test test\"}", "String[] var = {\"a\",\n \"\",\n \"test test\"};"},
         {"String[]", "{\"spaces  space\", \"a   a\"}", "String[] var = {\"spaces  space\", \"a   a\"};"},
        
         {"double[]", "{ 1e9, -3.e-012, -4, 5 }", "double[] var = { 1e9, -3.e-012, -4, 5 };"},
        
         {"long[]", "{ 0, -1, 1, 2147483648,\n-2147483649, 9223372036854775807, -9223372036854775808}", "long[] var = {0, -1, 1, 2147483648L, -2147483649L, 9223372036854775807L, -9223372036854775808L};"},
      };
      return Arrays.asList(data);
   }
   
   @Test public void test() throws InvalidTypeException {
      DataType dt = DataTypeFactoryMock.getDataType(this.typename);
      generator.generateParameter(code, dt, "var", this.value, false);
      String result = compressSpaceBeforeEquals(code.get(0).toString());
      assertEquals(this.expected, result);
   }
}
