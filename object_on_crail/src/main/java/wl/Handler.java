package wl;

import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
/**
 * handler implements HttpHandler
 */
public class Handler implements HttpHandler {
    public ObjectStorage obs;

    public Handler() throws Exception {
        obs = new ObjectStorage();
        obs.openSystem();
    }

    @Override
    public void handle(HttpExchange arg0) throws IOException {
        try {
            switch (arg0.getRequestMethod()) {
                case "Get":
                    //todo
                    obs.Get(arg0);
                    break;
                case "Put":
                    
                    arg0.sendResponseHeaders(obs.Put(arg0), 0);
                    break;
                default:
                    System.err.printf("unsurpport method: %s", arg0.getRequestMethod());
                    break;
            }


        } catch (Exception e) {
            
            e.printStackTrace();
        }
    }
    
}
