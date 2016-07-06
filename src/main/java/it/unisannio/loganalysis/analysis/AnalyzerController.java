package it.unisannio.loganalysis.analysis;

import org.renjin.sexp.ListVector;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Created by mario on 04/07/16.
 */
public class AnalyzerController {

    private static AnalyzerController instance = null;

    private ScriptEngine engine;

    public static AnalyzerController getInstance() throws FileNotFoundException, ScriptException {
        if(instance == null) instance = new AnalyzerController();
        return instance;
    }

    protected AnalyzerController() throws FileNotFoundException, ScriptException {
        engine = EngineController.getInstance().getEngine();

        // Evaluate analyzer script
        engine.eval(new FileReader(getClass().getResource("/R/analyzer.R").getPath()));
    }

    public ListVector performQuery(Query query, int[] users, long from, long to,
                                   ListVector attributes, boolean normalize) throws ScriptException {
        engine.put("users", users);
        engine.put("from", from);
        engine.put("to", to);
        engine.put("attributes", to);
        engine.put("normalize", normalize);

        switch (query) {
            case RESOURCE_USAGE:
                return (ListVector) engine.eval("resourcesUsage(users,from,to,attributes,normalize)");
            case RESOURCE_USAGE_TIME:
                return (ListVector) engine.eval("resourcesUsageTime(users,from,to,attributes,normalize)");
            case DAILY_ACTIVE_USERS:
                return (ListVector) engine.eval("dailyActiveUsers(from,to,normalize)");
            case DAILY_ACTIVE_RESOURCES:
                return (ListVector) engine.eval("dailyActiveResources(from,to,normalize)");
            case DAILY_ACTIVITIES:
                return (ListVector) engine.eval("dailyActivitiesReleatedToUsersAndResources(from,to,normalize)");
            case TIME_RANGE_USAGE:
                return (ListVector) engine.eval("timeRangesUsage(normalize)");
            case MOST_USED_OS:
                return (ListVector) engine.eval("mostUsedOse(users,normalize)");
            case RESOURCE_ADDED_PER_DAY:
                return (ListVector) engine.eval("resourceAddedPerDays(users,from,to,normalize)");
        }

        return null;
    }
}
