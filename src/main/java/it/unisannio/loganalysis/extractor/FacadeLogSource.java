package it.unisannio.loganalysis.extractor;

import it.unisannio.loganalysis.extractor.model.Model;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * Created by paolomoriello on 04/07/16.
 */
public class FacadeLogSource {

    private static FacadeLogSource facadeLogSource;

    public FacadeLogSource() {

    }

    public static FacadeLogSource getInstance() {
        if(facadeLogSource == null)
            facadeLogSource = new FacadeLogSource();
        return facadeLogSource;
    }

    public void addDataSource(String type, String dialect, String host, String port, String sourcedb, String username, String password)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {

        String identifier = host+"_"+port+"_"+sourcedb;

        LogSourceConf logSourceConf = new LogSourceConf(getClass().getResource("/sources").getPath());
        String classname = logSourceConf.readSources().get(type);
        Class c = Class.forName(classname);
        ILogHandler logHandler = (ILogHandler) c.getConstructor(String.class, String.class, String.class, String.class, String.class, String.class).newInstance(dialect, host, port, sourcedb, username, password);

        LogSourceHandler logSourceHandler = LogSourceHandler.getInstance();

        if(!logSourceHandler.contains(identifier)) {
            logSourceHandler.attach(identifier, logHandler);
        }

        Model m = logSourceHandler.parseLogHandler(identifier);
        ModelDatabaseHandler modelDatabaseHandler = new ModelDatabaseHandler(identifier);
        modelDatabaseHandler.fillDatabase(m);
    }

    public Collection<String> getDataSourcesTypes() {
        return new LogSourceConf(getClass().getResource("/sources").getPath()).readSources().keySet();
    }

    public Collection<String> getDataSources() {
        return LogSourceHandler.getInstance().getIds();
    }

}
