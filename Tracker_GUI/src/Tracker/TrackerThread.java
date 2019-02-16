package Tracker;

import com.sun.org.apache.xpath.internal.SourceTree;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Ishtiaq Niloy on 04-Dec-16.
 */
public class TrackerThread extends Thread {
    private ServerSocket serverSocket;
    static TextArea text;

    static ArrayList<Clients_Tracker> clientsTrackers = new ArrayList <Clients_Tracker>();

    static ConcurrentHashMap<TorrentFile, SeedersAndDownloaders > hashMap = new ConcurrentHashMap<TorrentFile, SeedersAndDownloaders>();

    TrackerThread(int port, TextArea textArea) throws IOException {
        this.text =   textArea;

        serverSocket= new ServerSocket(port);

        start();

    }

    void close(){
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void run (){


            try {
                while (true){

                    clientsTrackers.add( new Clients_Tracker( serverSocket.accept() , text ) );


                }

            }catch(SocketException e){
                System.out.println("Tracker Closed");
            }
            catch (IOException e) {
                e.printStackTrace();

            }


    }






}
