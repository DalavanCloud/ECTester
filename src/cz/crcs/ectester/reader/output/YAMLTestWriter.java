package cz.crcs.ectester.reader.output;

import cz.crcs.ectester.reader.Util;
import cz.crcs.ectester.reader.response.Response;
import cz.crcs.ectester.reader.test.Test;
import cz.crcs.ectester.reader.test.TestSuite;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Jan Jancar johny@neuromancer.sk
 */
public class YAMLTestWriter implements TestWriter {
    private PrintStream output;
    private List<Object> testRun;

    public YAMLTestWriter(PrintStream output) {
        this.output = output;
    }

    @Override
    public void begin(TestSuite suite) {
        output.println("---");
        testRun = new LinkedList<>();
        //TODO: output suite.name and suite.description
    }

    private Map<String, Object> responseObject(Response r) {
        Map<String, Object> responseObj = new HashMap<>();
        responseObj.put("successful", r.successful());
        responseObj.put("apdu", Util.bytesToHex(r.getAPDU().getBytes()));
        responseObj.put("natural_sw", r.getNaturalSW());
        List<Short> sws = new LinkedList<>();
        for (int i = 0; i < r.getNumSW(); ++i) {
            sws.add(r.getSW(i));
        }
        responseObj.put("sws", sws);
        responseObj.put("duration", r.getDuration());
        responseObj.put("desc", r.getDescription());
        return responseObj;
    }

    private Map<String, Object> testObject(Test t) {
        Map<String, Object> testObj = new HashMap<>();

        if (t instanceof Test.Simple) {
            Test.Simple test = (Test.Simple) t;
            testObj.put("type", "simple");
            testObj.put("response", responseObject(test.getResponse()));
        } else if (t instanceof Test.Compound) {
            Test.Compound test = (Test.Compound) t;
            testObj.put("type", "compound");
            List<Map<String, Object>> tests = new LinkedList<>();
            for (Test innerTest : test.getTests()) {
                tests.add(testObject(innerTest));
            }
            testObj.put("tests", tests);
        }

        testObj.put("desc", t.getDescription());
        testObj.put("result", t.getResultValue().name());

        return testObj;
    }

    @Override
    public void outputTest(Test t) {
        if (!t.hasRun())
            return;
        testRun.add(testObject(t));
    }

    @Override
    public void end() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        Yaml yaml = new Yaml(options);

        Map<String, List<Object>> result = new HashMap<>();
        result.put("testRun", testRun);
        String out = yaml.dump(result);

        output.println(out);
        output.println("---");
    }
}
