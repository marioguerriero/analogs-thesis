package it.unisannio.loganalysis.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mario on 04/07/16.
 */
public class QueryType {

    private static Map<Query, String> queries = null;

    public static String getDescription(Query query) {
        if(queries == null)
            buildQueryMap();
        return queries.get(query);
    }

    public static Collection<String> getQueryTypes() {
        ArrayList<String> types = new ArrayList<>();
        for(Query q : Query.values()) {
            types.add(QueryType.getDescription(q));
        }
        return types;
    }

    public static Query getFromDescription() {

    }

    private static void buildQueryMap() {
        queries = new HashMap<>();
        queries.put(Query.RESOURCE_USAGE, "Utilizzo delle risorse");
        queries.put(Query.RESOURCE_USAGE_TIME, "Tempo di utilizzo delle risorse");
        queries.put(Query.DAILY_ACTIVE_USERS, "Attività giornaliere degli utenti");
        queries.put(Query.DAILY_ACTIVE_RESOURCES, "Attività giornaliere sulle risorse");
        queries.put(Query.DAILY_ACTIVITIES, "Attività giornaliere");
        queries.put(Query.TIME_RANGE_USAGE, "Fasce temporali di utilizzo");
        queries.put(Query.MOST_USED_OS, "Sistemi operativi più utilizzati");
        queries.put(Query.RESOURCE_ADDED_PER_DAY, "Risorse aggiunte al giorno");
    }

}
