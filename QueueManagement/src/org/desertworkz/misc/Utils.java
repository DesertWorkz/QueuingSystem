package org.desertworkz.misc;

import java.util.HashMap;

public class Utils {

    /**
     * @param query the http uri query to be parsed
     * @return Map object with key/value pairs according to query
     */
    static public HashMap<String, String> parseQuery(String query){
        // https://stackoverflow.com/a/17472462
        // Example query: 'name=joe&age=10'
        HashMap<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            }else{
                result.put(entry[0], "");
            }
        }
        return result;

    }
}
