package pingPongTest;

import java.net.*;
import rmi.Stub;
import rmi.RMIException;

public class PingPongClient{
	public static void main(String[] args)
	{
		if (args.length != 2)
		{
			System.out.println("Please input ip + port");
			return;
		}
		String ip = args[0];
		int port = 14736;
		InetSocketAddress addr = new InetSocketAddress(ip, port);
		pingInterface ppserver = Stub.create(pingInterface.class, addr);
		
		//test for 4 times
		for (int i = 0; i < 4; i++)
		{
			String returned_str = "";
			try
			{
				returned_str = ppserver.ping(i);
				String reference = "Pong " + i;
				if (!reference.equals(returned_str))
				{
					System.out.println("The test " + i + " has failed");
					return;
				}
			}catch(RMIException e)
			{
				System.out.println("The test " + i + " has failed");
				e.printStackTrace();
				return;
			}
		}
		System.out.println("4 Tests Completed, 0 Test Failed.");
	}
}