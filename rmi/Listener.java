package rmi;
import java.lang.reflect.Method;
import java.io.*;
import java.net.*;

public class Listener<T> implements Runnable {
  private Skeleton<T> skeleton;
  public Listener(Skeleton<T> skeleton) { this.skeleton = skeleton; }
  public void run(){
    while (!skeleton.isStopped ) {
      Socket clientSocket = null;
        try {
           
          clientSocket = skeleton.serverSocket.accept();
          
        } catch (IOException e) {
           return;
        }
        skeleton.runningMThread =
              new Thread(new Worker(clientSocket, skeleton.get_server()));
          skeleton.runningMThread.start();
      }

    }
    // System.out.println("Server Stopped");
  
}