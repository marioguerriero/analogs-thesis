package it.unisannio.loganalysis.extractor;import java.lang.reflect.InvocationTargetException;
import java.util.Set;

/**
 * Created by paolomoriello on 04/07/16.
 */
public class FacadeLogSource {

    public FacadeLogSource() { }

    public void addDataSource(String type, String dialect, String host, String port, String sourcedb, String username, String password)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {

        String identifier = dialect+"://"+host+":"+port+"/"+sourcedb;

        String classname = new LogSourceConf("sources").readConf().get(type);
        Class c = Class.forName(classname);
        ILogHandler logHandler = (ILogHandler) c.getConstructor(String.class, String.class, String.class, String.class, String.class, String.class, String.class).newInstance(identifier, dialect, host, port, sourcedb, username, password);
        LogSourceHandler.getInstance().attach(identifier, logHandler);

        ModelDatabaseHandler.getInstance().parseLogHandler(logHandler);
    }

    public Set<String> getDataSources() {
        return new LogSourceConf("sources").readConf().keySet();
    }

    public void closeSession() {
        ModelDatabaseHandler.getInstance().closeSession();
    }

    public static void main(String[] arg) {
        String db1 = "moodle";
        String host1 = "localhost";
        String port1 = "5432";
        String dialect1 = "postgresql";

        String host2 = "localhost";
        String port2 = "3306";
        String dialect2 = "mysql";
        String db2 = "bugs";

        FacadeLogSource facadeLogSource = new FacadeLogSource();

        try {
            facadeLogSource.addDataSource("moodle", dialect1, host1, port1, db1, "postgres", "2009MORI05");
            facadeLogSource.addDataSource("bugzilla", dialect2, host2, port2, db2, "thesis", "thesis");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
