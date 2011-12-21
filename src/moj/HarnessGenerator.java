package moj;

public interface HarnessGenerator {
    String generateTestCode();
    String generateDefaultMain();
    String generateRunTest();
}
