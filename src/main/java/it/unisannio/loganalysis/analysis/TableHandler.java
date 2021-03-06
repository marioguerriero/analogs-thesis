package it.unisannio.loganalysis.analysis;

import org.renjin.sexp.ListVector;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Created by mario on 04/07/16.
 */
public class TableHandler {

    private static TableHandler instance = null;

    private ScriptEngine engine;
    private String dbSource;


    public static TableHandler getInstance() throws FileNotFoundException, ScriptException {
        if(instance == null) instance = new TableHandler();
        return instance;
    }

    protected TableHandler() throws FileNotFoundException, ScriptException {
        engine = EngineController.getInstance().getEngine();

        // Evaluate db-query script
        engine.eval(new FileReader(getClass().getResource("/R/db-query.R").getPath()));
    }

    public ListVector getUsers() throws ScriptException {
        return (ListVector) engine.eval("getUsers()");
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

    public class NullDataSourceException extends Exception {
        public NullDataSourceException(String message) {
            super(message);
        }
    }
}
