package cz.crcs.ectester.standalone.output;

import cz.crcs.ectester.common.output.BaseTextTestWriter;
import cz.crcs.ectester.common.test.TestSuite;
import cz.crcs.ectester.common.test.Testable;
import cz.crcs.ectester.standalone.ECTesterStandalone;
import cz.crcs.ectester.standalone.test.base.StandaloneTestable;
import cz.crcs.ectester.standalone.test.suites.StandaloneTestSuite;

import java.io.PrintStream;

/**
 * @author Jan Jancar johny@neuromancer.sk
 */
public class TextTestWriter extends BaseTextTestWriter {
    public TextTestWriter(PrintStream output) {
        super(output);
    }

    private String causeString(Object cause) {
        if (cause == null) {
            return "";
        } else if (cause instanceof Exception) {
            Exception ex = ((Exception) cause);
            return " -> " + ex.getClass().getCanonicalName() + " : " + ex.getMessage();
        } else {
            return cause.toString();
        }
    }

    @Override
    protected String testableString(Testable t) {
        if (t instanceof StandaloneTestable) {
            StandaloneTestable<?> testable = (StandaloneTestable) t;
            String stage = testable.getStage().name();
            String exception = causeString(testable.getException());
            String errorCause = causeString(testable.errorCause());
            return stage + exception + errorCause;
        }
        return "";
    }

    @Override
    protected String deviceString(TestSuite suite) {
        if (suite instanceof StandaloneTestSuite) {
            StandaloneTestSuite standaloneSuite = (StandaloneTestSuite) suite;
            StringBuilder sb = new StringBuilder();
            sb.append("═══ ECTester version: " + ECTesterStandalone.VERSION).append(System.lineSeparator());
            sb.append("═══ ").append(standaloneSuite.getLibrary().name()).append(System.lineSeparator());
            return sb.toString();
        }
        return "";
    }
}