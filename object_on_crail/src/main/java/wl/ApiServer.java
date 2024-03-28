package wl;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;





public class ApiServer {
    public static void main(String[] args) throws Exception {
        HttpServer apiServer = HttpServer.create(new InetSocketAddress(8080), 0);
        Handler objHandler = new Handler();
        apiServer.createContext("/ObjectStorage", objHandler);
        apiServer.setExecutor(Executors.newFixedThreadPool(10));
        apiServer.start();

    }
    
}
