package listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Custom TestNG Listener for detailed test execution tracking
 */
public class TestListener implements ITestListener {

    private long startTime;
    private long endTime;

    @Override
    public void onStart(ITestContext context) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println(" TEST SUITE STARTED: " + context.getName());
        System.out.println(" Start Time: " + getCurrentTime());
        System.out.println("Total Tests to Run: " + context.getAllTestMethods().length);
        System.out.println("=".repeat(80) + "\n");
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println(" TEST SUITE FINISHED: " + context.getName());
        System.out.println(" End Time: " + getCurrentTime());
        System.out.println("\n TEST SUMMARY:");
        System.out.println("   Passed Tests: " + context.getPassedTests().size());
        System.out.println("   Failed Tests: " + context.getFailedTests().size());
        System.out.println("    Skipped Tests: " + context.getSkippedTests().size());
        System.out.println("=".repeat(80) + "\n");
    }

    @Override
    public void onTestStart(ITestResult result) {
        startTime = System.currentTimeMillis();
        System.out.println("\n" + "-".repeat(60));
        System.out.println(" STARTING TEST: " + result.getMethod().getMethodName());
        System.out.println(" Class: " + result.getTestClass().getRealClass().getSimpleName());
        System.out.println(" Start Time: " + getCurrentTime());
        System.out.println("-".repeat(60));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("-".repeat(60));
        System.out.println(" TEST PASSED: " + result.getMethod().getMethodName());
        System.out.println("  Duration: " + duration + " ms (" + (duration / 1000.0) + " seconds)");
        System.out.println("-".repeat(60) + "\n");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("-".repeat(60));
        System.out.println(" TEST FAILED: " + result.getMethod().getMethodName());
        System.out.println("️  Duration: " + duration + " ms");
        System.out.println(" Error Message: " + result.getThrowable().getMessage());
        System.out.println("\n Stack Trace:");
        result.getThrowable().printStackTrace();
        System.out.println("-".repeat(60) + "\n");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("-".repeat(60));
        System.out.println("  TEST SKIPPED: " + result.getMethod().getMethodName());
        if (result.getThrowable() != null) {
            System.out.println(" Reason: " + result.getThrowable().getMessage());
        }
        System.out.println("-".repeat(60) + "\n");
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        System.out.println(" TEST FAILED BUT WITHIN SUCCESS PERCENTAGE: " + result.getMethod().getMethodName());
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }
}