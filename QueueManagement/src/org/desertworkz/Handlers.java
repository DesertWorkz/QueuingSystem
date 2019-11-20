package org.desertworkz;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.desertworkz.misc.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

class Handlers {
    // ticket num, details             e.g. { ticket-201, {'counter':'C1', 'serviceType': 'card collections', etc...} }
    private static MyQueue ticketQueue = new MyQueue();
    // save this to file/database
    private static HashMap<String, ArrayList<String>> counterServiceOffered = new HashMap<>();
    static private int genericCounter = 0;

    static void addCounterService(String service, String[] counters){
        ArrayList<String> temp = new ArrayList<>();
        for (String c :
                counters) {
            temp.add(c);
        }
        counterServiceOffered.put(service, temp);
    }

    static class newTicket implements HttpHandler {
        @Override
        public void handle(HttpExchange he) throws IOException {
            String response;
            he.getResponseHeaders().set("Content-Type", "text/plain");
            OutputStream os = he.getResponseBody();
            if (he.getRequestURI().getQuery() != null) {
                String parametersUnparsed = he.getRequestURI().getQuery();
                HashMap<String, String> parsed = Utils.parseQuery(parametersUnparsed);
                if (parsed.containsKey("serviceType") && parsed.get("serviceType").length() > 0) {
                    HashMap<String, String> details = new HashMap<>();
                    details.put("serviceType", parsed.get("serviceType"));
                    details.put("possibleCounters", getPossibleCounters(parsed.get("serviceType")));
                    // generate ticket number
                    genericCounter++;
                    HashMap<String, HashMap<String, String>> temp = new HashMap<>();
                    temp.put("ticket-" + genericCounter, details);
                    System.out.println(temp.toString());
                    // add to queue
                    ticketQueue.enqueue(temp);
                    // return ticket number along with service
                    response = "{ticketNumber: ticket-" + genericCounter + ",serviceType:" + parsed.get("serviceType") + "}";
                    he.sendResponseHeaders(200, response.length());
                    os.write(response.getBytes());
                } else {
                    response = "No service type defined";
                    he.sendResponseHeaders(200, response.length());
                    os.write(response.getBytes());
                }
            } else {
                os.write("No query detected".getBytes());
            }
            os.close();
        }
    }

    static class nextTicket implements HttpHandler {
        @Override
        public void handle(HttpExchange he) throws IOException {
            // find possible tickets for this counter
            String response;
            he.getResponseHeaders().set("Content-Type", "text/plain");
            OutputStream os = he.getResponseBody();
            if (he.getRequestURI().getQuery() != null) {
                String parametersUnparsed = he.getRequestURI().getQuery();
                HashMap<String, String> parsed = Utils.parseQuery(parametersUnparsed);
                if (parsed.containsKey("counterId") && parsed.get("counterId").length() > 0) {
                    // get next for this specific counter; might have to skip first in queue
                    HashMap<String, HashMap<String, String>> ticket = getNextCustomerForCounter(parsed.get("counterId"));
                    if(ticket != null){
                        String ticketNum = (String)ticket.keySet().toArray()[0];
                        response = "{counterId: " +parsed.get("counterId")+ ","+
                                " error: false,"+
                                " ticketNumber: " +ticketNum+ ","+
                                " serviceType: "+ticket.get(ticketNum).get("serviceType")+"}";
                    }else{
                        response = "{error: true, reason: 'No customers left in queue'}";
                    }

                } else {
                    response = "{error: true, reason: 'No service type defined'}";
                }
            } else {
                response = "{error: true, reason: 'No query detected'}";
            }
            he.sendResponseHeaders(200, response.length());
            os.write(response.getBytes());
            os.close();
        }
    }

    /**
     *
     * @param serviceType to find counters for
     * @return counters that offer that service
     */
    static private String getPossibleCounters(String serviceType) {
        StringBuilder result = new StringBuilder();
        if (counterServiceOffered.containsKey(serviceType)) {
            for (String s :
                    counterServiceOffered.get(serviceType)) {
                result.append(s).append("`");
            }
        }
        return result.toString();
    }

    /**
     *
     * @param counterId counter which is requesting for customer
     * @return next ticket map for the counter; null if none left
     */
    private static  HashMap<String, HashMap<String, String>> getNextCustomerForCounter(String counterId){
        HashMap<String, HashMap<String, String>> head = ticketQueue.peek();
        if(head == null) {
            return null;
        }
        String key = (String)head.keySet().toArray()[0];
        // search through 'possibleCounters'
        String possibleCountersRaw = head.get(key).get("possibleCounters");
        String[] counterList = possibleCountersRaw.split("`"); // ` is delimiter
        for (String c :
                counterList) {
            if(c.equals(counterId)){
                // remove from queue
                ticketQueue.dequeue();
                return head; // ticket number
            }
        }
        // reaches here if the head is not for the provided counter
        // traverse queue to find ticket that can be handled by this counter
        return ticketQueue.specialTraverse(counterId);
    }
}
