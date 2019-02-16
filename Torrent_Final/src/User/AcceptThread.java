package User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * Created by Ishtiaq Niloy on 07-Dec-16.
 */
public class AcceptThread extends Thread {

    ServerSocket serverSocket;

    static ArrayList<SeedThread> seedThreads = new ArrayList<SeedThread>();

    AcceptThread(ServerSocket serverSocket){
        this.serverSocket = serverSocket;

        start();

    }

    public  void run (){

        try {
            while (true){
                Socket s;
                s = serverSocket.accept();

                System.out.println("Accepted connection from " + s.getInetAddress().getHostAddress());

                seedThreads.add( new SeedThread(s) );


            }

        }catch (SocketException e){
            System.out.println("Server Socket Closed");
        }
        catch (IOException e) {
            e.printStackTrace();
        }


    }


}
