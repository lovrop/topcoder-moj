package moj.mocks;

import java.util.HashMap;

import com.topcoder.shared.language.CPPLanguage;
import com.topcoder.shared.language.JavaLanguage;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.InvalidTypeException;

public class DataTypeFactoryMock {
   static public DataType getDataType(String description) throws InvalidTypeException {
      int id = 0;
      HashMap<Integer, String> typeMapping = new HashMap<Integer, String>();
      int cpp  = CPPLanguage.CPP_LANGUAGE.getId();
      int java = JavaLanguage.JAVA_LANGUAGE.getId();
      
      typeMapping.put(java, description);
      if (description.equals("int")) {
         id = 1;
         typeMapping.put(cpp, "int");
      } else if (description.equals("int[]")) {
         id = 2;
         typeMapping.put(cpp, "vector<int>");
      } else if (description.equals("String")) {
         id = 3;
         typeMapping.put(cpp, "string");
      } else if (description.equals("String[]")) {
         id = 4;
         typeMapping.put(cpp, "vector<string>");
      } else if (description.equals("double")) {
         id = 5;
         typeMapping.put(cpp, "double");
      } else if (description.equals("double[]")) {
         id = 6;
         typeMapping.put(cpp, "vector<double>");
      } else if (description.equals("long")) {
         id = 7;
         typeMapping.put(cpp, "long long");
      } else if (description.equals("long[]")) {
         id = 8;
         typeMapping.put(cpp, "vector<long long>");
      } else {
         throw new InvalidTypeException("invalid base type \"" + description + "\"");
      }
      return new DataType(id, description, typeMapping);
   }
}
