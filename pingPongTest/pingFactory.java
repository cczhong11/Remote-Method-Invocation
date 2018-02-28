package pingPongTest;
import rmi.*;

public class pingFactory{
    public static PingPongServer makePingServer(){
        return new PingPongServer();
    }
}