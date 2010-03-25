package moj.mocks;

import com.topcoder.client.contestant.ProblemComponentModel;
import com.topcoder.client.contestant.ProblemModel;
import com.topcoder.netCommon.contestantMessages.response.data.ComponentChallengeData;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.ProblemComponent;
import com.topcoder.shared.problem.TestCase;

public class ProblemComponentModelMock implements ProblemComponentModel {
   
   public String getClassName() {
      return "ProblemComponentModelMock";
   }

   public ProblemComponent getComponent() {
      return null;
   }

   public ComponentChallengeData getComponentChallengeData() {
      return null;
   }

   public Integer getComponentTypeID() {
      return null;
   }

   public String getDefaultSolution() {
      return null;
   }

   public Long getID() {
      return null;
   }

   public String getMethodName() {
      return "mockMethod";
   }

   public String[] getParamNames() {
      return new String[]{};
   }

   public DataType[] getParamTypes() {
      return new DataType[]{};
   }

   public Double getPoints() {
      return null;
   }

   public ProblemModel getProblem() {
      return null;
   }

   public DataType getReturnType() {
      return null;
   }

   public TestCase[] getTestCases() {
      return null;
   }

   public boolean hasTestCases() {
      return false;
   }

}
