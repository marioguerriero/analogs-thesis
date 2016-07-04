package it.unisannio.loganalysis.analysis;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Created by mario on 04/07/16.
 */
public class QueryController {

    private static QueryController instance = null;

    private ScriptEngine engine;
    private String dbSource;


    public static QueryController getInstance() throws FileNotFoundException, ScriptException {
        if(instance == null) instance = new QueryController();
        return instance;
    }

    protected QueryController() throws FileNotFoundException, ScriptException {
        ScriptEngineManager engineManager = new ScriptEngineManager();
        engine = engineManager.getEngineByName("Renjin");

        // Evaluate db-query script
        engine.eval(new FileReader(getClass().getResource("R/db-query.R").getPath()));
    }

    public String getDbSource() {
        return dbSource;
    }

    public void setDbSource(String dbSource) {
        this.dbSource = dbSource;
    }

    /**
     * Utilizzato per caricare le tabelle del database selezionato
     * in memoria. Tale operazione richiede tempo ed è preferibile
     * che venga eseguita in maniera asincrona.
     */
    public void loadTables() throws NullDataSourceException, ScriptException {
        if(dbSource == null)
            throw new NullDataSourceException("Source database must be set");
        engine.put("dbsource", dbSource);
        engine.eval("buildTables(dbsource)");
    }

    private class NullDataSourceException extends Exception {
        public NullDataSourceException(String message) {
            super(message);
        }
    }
}
