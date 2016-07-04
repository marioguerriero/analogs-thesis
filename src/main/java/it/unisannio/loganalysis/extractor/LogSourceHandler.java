package it.unisannio.loganalysis.extractor;import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by paolomoriello on 01/07/16.
 */
public class LogSourceHandler {

    private static LogSourceHandler logSourceHandler;
    private Map<String, ILogHandler> sourceHandlers;

    public static LogSourceHandler getInstance() {
        if(logSourceHandler == null)
            logSourceHandler = new LogSourceHandler();
        return logSourceHandler;
    }

    public LogSourceHandler() {
        sourceHandlers = new HashMap<>();
    }

    public void attach(String id, ILogHandler sourceHandler) {
        sourceHandlers.put(id, sourceHandler);
    }

    public void detach(String id) {
        sourceHandlers.remove(id);
    }

    public ILogHandler getSourceHandler(String id) {
        return sourceHandlers.get(id);
    }

    public Collection<ILogHandler> getSourceHandlers() {
        return sourceHandlers.values();
    }
}
