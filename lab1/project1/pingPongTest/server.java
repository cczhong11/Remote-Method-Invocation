package pingPongTest;

import rmi.*;
import java.net.*;
import rmi.RMIException;

public class server{
    public static void main(String[] args) {
        int port = 14736;
        PingPongServer server = pingFactory.makePingServer();
        Skeleton<PingPongServer> skeleton = new Skeleton<PingPongServer>(PingPongServer.class,server,new InetSocketAddress(port));
        try{
            skeleton.start();
        }
        catch(RMIException e){
            System.out.println("network issue");
        }
    }
}