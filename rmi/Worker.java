package rmi;
import java.lang.reflect.Method;
import java.io.*;
import java.net.*;

/**
 * Worker thread to accept socket and read object
 * @author tczhong
 *
 * @param <T>
 */
public class Worker <T> implements Runnable {
    private Socket socket;
    private T server;
    private Method method;
    public Worker(Socket socket, T server) {
      this.socket = socket;
      this.server = server;
    }
    public void run(){
        ObjectOutputStream out=null;
        ObjectInputStream in=null;
        Object[] parameters=null;
        String methodStr="";
      try {
         out=
            new ObjectOutputStream(this.socket.getOutputStream());
         in= new ObjectInputStream(this.socket.getInputStream());
        out.flush();
        Object method0 = in.readObject();
        parameters = (Object[])in.readObject(); 
        in.readObject();
        methodStr = (String)method0;
        Method[] methods = this.server.getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodStr)) {
                this.method = method;
            }
        }
    }catch(Exception e){
        
        return;
    }
    Object result;
    try{
        
        if(parameters!=null)
         result= this.method.invoke(this.server, parameters);
        else{
             result = this.method.invoke(this.server);
        }
        
      } catch (Exception e) {
        
        result = e.getCause(); 
      }
      try{
        out.writeObject(result);
        out.writeObject(null);
        in.close();
        out.close();
        socket.close();
        
      }
      catch(Exception e){
          return;
      }
      
    }
  }