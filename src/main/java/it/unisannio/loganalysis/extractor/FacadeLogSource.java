package it.unisannio.loganalysis.extractor;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Set;

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

        RepositoryConf repositoryConf = new RepositoryConf("sources");
        String classname = repositoryConf.readSources().get(type);
        Class c = Class.forName(classname);
        ILogHandler logHandler = (ILogHandler) c.getConstructor(String.class, String.class, String.class, String.class, String.class, String.class).newInstance(dialect, host, port, sourcedb, username, password);

        LogSourceHandler logSourceHandler = LogSourceHandler.getInstance();
        ModelDatabaseHandler modelDatabaseHandler;
        if(!logSourceHandler.contains(identifier)) {
            logSourceHandler.attach(identifier, logHandler);
        }

        modelDatabaseHandler = new ModelDatabaseHandler(identifier);
        modelDatabaseHandler.parseLogHandler(identifier);
    }

    public Collection<String> getDataSourcesTypes() {
        return new RepositoryConf(getClass().getResource("/sources").getPath()).readSources().keySet();
    }

    public Collection<String> getDataSources() {
        return LogSourceHandler.getInstance().getIds();
    }

}
