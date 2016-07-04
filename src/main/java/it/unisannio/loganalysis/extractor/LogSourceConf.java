package it.unisannio.loganalysis.extractor;import sun.rmi.runtime.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by paolomoriello on 04/07/16.
 */
public class LogSourceConf {

    private String path;

    public LogSourceConf(String path) {
        this.path = path;
    }


    public Map<String, String> readConf() {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Map<String, String> map = new HashMap();

        String line = null;
        while(scanner.hasNextLine()) {
            line = scanner.nextLine();
            String[] tokens = line.split(",");
            map.put(tokens[0], tokens[1]);

        }
        return map;
    }
}
