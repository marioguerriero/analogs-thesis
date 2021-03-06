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

        engine.put("usr", users != null && users.length > 0 ? users : null);
        engine.put("from", from);
        engine.put("to", to);
        engine.put("attrs", attributes != null && attributes.length() > 0 ? attributes : null);
        engine.put("normalize", normalize);

        System.out.println(users);

        switch (query) {
            case RESOURCE_USAGE:
                return (ListVector) engine.eval(
                        "resourcesUsage(usr=usr,from=from,to=to,attrs=attrs,normalize=normalize)");
            case RESOURCE_USAGE_TIME:
                return (ListVector) engine.eval(
                        "resourcesUsageTime(usr=usr,from=from,to=to,attrs=attrs,normalize=normalize)");
            case DAILY_ACTIVE_USERS:
                return (ListVector) engine.eval(
                        "dailyActiveUsers(from=from,to=to,attrs=attrs,normalize=normalize)");
            case DAILY_ACTIVE_RESOURCES:
                return (ListVector) engine.eval(
                        "dailyActiveResources(from=from,to=to,attrs=attrs,normalize=normalize)");
            case DAILY_ACTIVITIES:
                return (ListVector) engine.eval(
                        "dailyActivitiesReleatedToUsersAndResources(from=from,to=to,attrs=attrs,normalize=normalize)");
            case TIME_RANGE_USAGE:
                return (ListVector) engine.eval(
                        "timeRangesUsage(attrs=attrs,normalize=normalize)");
            case MOST_USED_OS:
                return (ListVector) engine.eval(
                        "mostUsedOS(usr=usr,attrs=attrs,normalize=normalize)");
            case RESOURCE_ADDED_PER_DAY:
                return (ListVector) engine.eval(
                        "resourceAddedPerDays(usr=usr,from=from,to=to,attrs=attrs,normalize=normalize)");
        }

        return null;
    }
}
