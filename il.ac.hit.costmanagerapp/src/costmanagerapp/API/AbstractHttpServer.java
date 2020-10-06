package costmanagerapp.API;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import costmanagerapp.lib.UsersPlatformException;

public abstract class AbstractHttpServer<T> implements HttpHandler {

    public static HttpServer httpServer;
    public int port;

    public int getPort(){return port;}
    public abstract void start() throws UsersPlatformException;
    public abstract void stop();

}
