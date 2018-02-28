package pingPongTest;

import rmi.*;
public class PingPongServer implements pingInterface
{
    @Override
    public String ping(int idNumber)
        throws RMIException
    {
        return "Pong "+idNumber;
    }
    
}