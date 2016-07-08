package it.unisannio.loganalysis.analysis;

import org.renjin.sexp.ListVector;

import javax.script.ScriptEngine;
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

    public ListVector performQuery(QueryType query, Integer[] users, long from, long to,
                                   ListVector attributes, boolean normalize) throws ScriptException {

        engine.put("users", users);
        engine.put("from", from);
        engine.put("to", to);
        engine.put("attributes", attributes);
        engine.put("normalize", normalize);

        switch (query) {
            case RESOURCE_USAGE:
                ////////////
                return (ListVector) engine.eval(
                        "resourcesUsage(users=users,from=from,to=to,attributes=attributes,normalize=normalize)");
            case RESOURCE_USAGE_TIME:
                ////////////
                return (ListVector) engine.eval(
                        "resourcesUsageTime(users=users,from=from,to=to,attributes=attributes,normalize=normalize)");
            case DAILY_ACTIVE_USERS:
                return (ListVector) engine.eval(
                        "dailyActiveUsers(from=from,to=to,normalize=normalize)");
            case DAILY_ACTIVE_RESOURCES:
                return (ListVector) engine.eval(
                        "dailyActiveResources(from=from,to=to,normalize=normalize)");
            case DAILY_ACTIVITIES:
                return (ListVector) engine.eval(
                        "dailyActivitiesReleatedToUsersAndResources(from=from,to=to,normalize=normalize)");
            case TIME_RANGE_USAGE:
                return (ListVector) engine.eval(
                        "timeRangesUsage(normalize=normalize)");
            case MOST_USED_OS:
                return (ListVector) engine.eval(
                        "mostUsedOS(users=users,normalize=normalize)");
            case RESOURCE_ADDED_PER_DAY:
                /////// Tutti 0?
                return (ListVector) engine.eval(
                        "resourceAddedPerDays(users=users,from=from,to=to,normalize=normalize)");
        }

        return null;
    }
}
