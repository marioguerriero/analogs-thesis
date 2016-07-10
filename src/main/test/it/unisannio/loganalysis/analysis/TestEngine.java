package it.unisannio.loganalysis.analysis;

import org.junit.Before;
import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import java.io.FileNotFoundException;
import java.io.FileReader;

import static org.junit.Assert.assertNotNull;

/**
 * Created by mario on 30/06/16.
 */
public class TestEngine {

    private ScriptEngineManager engineManager;
    private ScriptEngine engine;

    @Before
    public void setup() {
        engineManager = new ScriptEngineManager();

        engine = engineManager.getEngineByName("Renjin");

        assertNotNull(engine);

    }

    @Test
    public void testEngineString() throws ScriptException {
        engine.eval("df <- data.frame(x=1:10, y=(1:10)+rnorm(n=10))");
        engine.eval("print(df)");
        engine.eval("print(lm(y ~ x, df))");
    }

    @Test
    public void testEngineFile() throws FileNotFoundException, ScriptException {
        ClassLoader loader = getClass().getClassLoader();
        engine.eval(new FileReader(loader.getResource("R/db-query.R").getPath()));
        engine.eval(new FileReader(loader.getResource("R/analyzer.R").getPath()));
    }

}
