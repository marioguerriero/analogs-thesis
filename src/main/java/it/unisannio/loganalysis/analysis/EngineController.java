package it.unisannio.loganalysis.analysis;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * Created by mario on 05/07/16.
 */
public class EngineController {
    private ScriptEngine engine;

    private static EngineController instance = null;

    public static EngineController getInstance() {
        if(instance == null) instance = new EngineController();
        return instance;
    }

    protected EngineController() {
        ScriptEngineManager engineManager = new ScriptEngineManager();
        engine = engineManager.getEngineByName("Renjin");
    }

    public ScriptEngine getEngine() {
        return engine;
    }
}
