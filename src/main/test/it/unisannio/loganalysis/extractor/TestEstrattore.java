package it.unisannio.loganalysis.extractor;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

/**
 * Created by paolomoriello on 04/07/16.
 */
public class TestEstrattore {

    private FacadeLogSource facadeLogSource;

    @Before
    public void setUp() {
        facadeLogSource = new FacadeLogSource();
    }

    @Test
    public void testBuildModel() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String db1 = "moodle";
        String host1 = "localhost";
        String port1 = "5432";
        String dialect1 = "postgresql";

        String host2 = "localhost";
        String port2 = "3306";
        String dialect2 = "mysql";
        String db2 = "bugs";

        facadeLogSource.addDataSource("moodle", dialect1, host1, port1, db1, "postgres", "mario");
        facadeLogSource.addDataSource("bugzilla", dialect2, host2, port2, db2, "root", "mario");

        assertEquals(facadeLogSource.getDataSources().size(), 2);
    }
}
