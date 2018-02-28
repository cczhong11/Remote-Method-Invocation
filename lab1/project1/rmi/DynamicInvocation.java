package rmi;

import java.net.*;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;



public class DynamicInvocation<T> implements InvocationHandler{
	
	//the information stored in each handler to handle requests
	private Class<T> remote_interface;
	private InetSocketAddress remote_addr;
	
	public DynamicInvocation(InetSocketAddress addr, Class<T> c)
	{
		remote_interface = c;
		remote_addr = addr;
	}
	
	/**
	 * my_toString: report the name of remote interface implemented by the stub
         *           and remote address(hostname + port) of the skeleton
	 * @param proxy
	 *        the proxy being invoked
	 */
	private String my_toString(Object proxy)
	{
		DynamicInvocation<T> DynamicHandler = (DynamicInvocation<T>)Proxy.getInvocationHandler(proxy);
		return  DynamicHandler.remote_interface.getName() + " " + DynamicHandler.remote_addr.toString();
	}
	
	/**
	 * my_hashCode: when the client is requesting the hashCode function of Stub,
	 *            the proxy object should implement the hashCode function rather
	 *            than sending it to the server
	 * @param proxy
	 *        the proxy being invoked
	 */
	private int my_hashCode(Object proxy)
	{
		return my_toString(proxy).hashCode();
	}
	
	/**
	 * two stubs are considered equal if they implement the same interface & connect to 
       	 * the same skeleton
    	 * Note: should deal with null situation
	 * @param proxy
	 *        the proxy being invoked
	 * @param args
	 *        the arguments of equals function
	 */
	private boolean my_equals(Object proxy, Object[] args)
	{
		try
		{
			if (my_hashCode(proxy) == my_hashCode(args[0])) return true;
		}
		catch (Exception e)
		{
			return false;
		}
		return false;
	}
	
	/**
	 * when a method is called, invoke and open a new connection
	 * @param proxy 
	 * 		  the proxy object created for the stub, the method is invoked on
	 * @param method
	 *        the method transferred to the server
	 * @param args
	 *        the method arguments
	 */
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable
	{
		//check if it is the stub's self function
		String method_name = method.getName();
		if (method_name.equals("hashCode"))
		{
			return my_hashCode(proxy);
		}
		if (method_name == "equals")
		{
			return my_equals(proxy, args);
		}
		if (method_name == "toString")
		{
			return my_toString(proxy);
		}
		
		//otherwise it should forward the request to the server
		//open a connection with the remote server
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		Socket client = null;
		try
		{
			InetAddress server = remote_addr.getAddress();
			int port = remote_addr.getPort();
			client = new Socket(server, port);
			out = new ObjectOutputStream(client.getOutputStream());
			out.flush();
		}catch(Exception e)
		{
			client.close();
			System.out.println("Fail to create output stream to server.");
			throw new RMIException("RMI: Fail to create output stream to server.");
		}
		
		//create the input stream
		try
		{
			in = new ObjectInputStream(client.getInputStream());
		}catch(Exception e)
		{
			client.close();
			
			throw new RMIException("RMI: Fail to create input stream to server.");
		}
		
		//send
		try
		{
			out.writeObject(method.getName());
			out.writeObject(args);
			out.writeObject(null);
			out.flush();
		}catch(Exception e)
		{
			client.close();
			in.close();
			out.close();
			System.out.println("Error in sending the data!");
			throw new RMIException("RMI: Error in sending the data!");
		}
		
		//receive
		Object returned_obj;
		try
		{
			returned_obj = (Object)in.readObject();
		}catch(Exception e)
		{
			client.close();
			in.close();
			out.close();
			System.out.println("Error in receiving the data!");
			throw new RMIException("Error in receiving the data!");
		}
		
		//raise the same exception
		if (returned_obj instanceof Exception)
		{
			throw (java.lang.Throwable)returned_obj;
		}

		//close connection
		try
		{
			out.close();
			in.close();
			client.close();
		}catch(Exception e)
		{
			System.out.println("Error in closing socket");
			throw new RMIException("Error in closing socket.");
		}
		
		//return the object to client
		return returned_obj;
	}	
}			
