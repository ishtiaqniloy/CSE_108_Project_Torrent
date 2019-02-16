package Tracker;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Ishtiaq Niloy on 24-Nov-16.
 */
public class Tracker {
    private static ServerSocket torrent_serverSocket;
    final public static int tracker_port = 3600;

    public static void main (String [] args){

        try {
            torrent_serverSocket = new ServerSocket(tracker_port);
            System.out.println("Tracker Online");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try{
            Socket socket =  torrent_serverSocket.accept();

            Client_Tracker c1 = new Client_Tracker(socket);

            c1.join();


        }catch (IOException e){
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}