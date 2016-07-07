package it.unisannio.loganalysis.analysis;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mario on 04/07/16.
 */
public class QueryTypeHandler {

    private static Map<QueryType, String> queries = null;

    public static String getDescription(QueryType query) {
        if(queries == null)
            buildQueryMap();
        return queries.get(query);
    }

    public static Collection<String> getQueryTypes() {
        if(queries == null)
            buildQueryMap();
        return queries.values();
    }

    public static QueryType getFromDescription(String description) {
        for(QueryType q : queries.keySet())
            if(queries.get(q).equals(description))
                return q;
        return null;
    }

    public static Collection<QueryType> getQueries() {
        if(queries == null)
            buildQueryMap();
        return queries.keySet();
    }

    private static void buildQueryMap() {
        queries = new HashMap<>();
        queries.put(QueryType.RESOURCE_USAGE, "Utilizzo delle risorse");
        queries.put(QueryType.RESOURCE_USAGE_TIME, "Tempo di utilizzo delle risorse");
        queries.put(QueryType.DAILY_ACTIVE_USERS, "Utenti attivi giornalmente");
        queries.put(QueryType.DAILY_ACTIVE_RESOURCES, "Risorse utilizzate giornalmente");
        queries.put(QueryType.DAILY_ACTIVITIES, "Attività giornaliere");
        queries.put(QueryType.TIME_RANGE_USAGE, "Fasce temporali di utilizzo");
        queries.put(QueryType.MOST_USED_OS, "Sistemi operativi più utilizzati");
        queries.put(QueryType.RESOURCE_ADDED_PER_DAY, "Risorse aggiunte al giorno");
    }

}
