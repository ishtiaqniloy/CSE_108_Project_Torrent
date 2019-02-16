package Tracker;


import javafx.scene.control.TextArea;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * Created by Ishtiaq Niloy on 03-Dec-16.
 */


public class Clients_Tracker extends Thread {
    Socket socket;
    TextArea text;
    String clientAddress;

    ObjectOutputStream oos;
    ObjectInputStream ois;

    ArrayList<String> seedFiles;


    Clients_Tracker(Socket socket, TextArea text){
        this.socket = socket;

        try {
            clientAddress = socket.getInetAddress().getHostAddress().toString();
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

        this.text = text;

        start();
    }

    public void run(){
        text.appendText("Client Connected from IP " + socket.getInetAddress().getHostAddress() + "\n" );
        try {
            while (true) {

                if ((Boolean) ois.readObject()) {
                    System.out.println("Seed actions");
                    oos.writeObject(true);

                    String fileName = (String) ois.readObject();
                    oos.writeObject(true);

                    long sourceSize = (Long) ois.readObject();
                    oos.writeObject(true);

                    long pieceSize = (Long) ois.readObject();
                    oos.writeObject(true);

                    long numberOfPieces= (Long) ois.readObject();
                    oos.writeObject(true);

                    int seederPort = (Integer) ois.readObject();
                    oos.writeObject(true);

                    Seeder sdr = new Seeder(socket.getInetAddress().getHostAddress().toString(), seederPort);
                    TorrentFile temp = new TorrentFile(fileName, sourceSize, pieceSize, numberOfPieces);

                    SeedersAndDownloaders tempSD = TrackerThread.hashMap.get(temp);

                    if(tempSD == null){
                        tempSD = new SeedersAndDownloaders();
                        tempSD.seeders.add(sdr);
                        TrackerThread.hashMap.put(temp, tempSD);

                        System.out.println("New Torrent file added to hashmap");

                    }
                    else{
                        tempSD.seeders.add(sdr);

                        System.out.println("Torrent file value updated in hashmap");


                    }

                    tempSD = TrackerThread.hashMap.get(temp);

                    System.out.println("Number of downloaders for this file : " + tempSD.downloaders.size());

                    for(int i=0; i<tempSD.downloaders.size(); i++){
                        tempSD.downloaders.get(i).objectOutputStream.reset();
//                        tempSD.downloaders.get(i).objectInputStream.reset();

                        tempSD.downloaders.get(i).objectOutputStream.writeObject(sdr.ipAddress);
//                        tempSD.downloaders.get(i).objectInputStream.readObject();

                        tempSD.downloaders.get(i).objectOutputStream.writeObject(sdr.port);
//                        tempSD.downloaders.get(i).objectInputStream.readObject();

                        System.out.println("Sent ip and port to the downloader");

                    }

                    for(Seeder s : tempSD.seeders){
                        System.out.println("Seeders : " + s.ipAddress + " " + s.port);
                    }


                }

                else{
                    System.out.println("Download actions");
                    oos.writeObject(true);

                    String fileName = (String) ois.readObject();
                    oos.writeObject(true);

                    long sourceSize = (Long) ois.readObject();
                    oos.writeObject(true);

                    long pieceSize = (Long) ois.readObject();
                    oos.writeObject(true);

                    long numberOfPieces= (Long) ois.readObject();
                    oos.writeObject(true);

                    TorrentFile temp = new TorrentFile(fileName, sourceSize, pieceSize, numberOfPieces);

                    SeedersAndDownloaders tempSD = TrackerThread.hashMap.get(temp);

                    Downloader d = new Downloader(socket, oos, ois);

                    if(tempSD == null){
                        tempSD = new SeedersAndDownloaders();
                        tempSD.downloaders.add(d);
                        TrackerThread.hashMap.put(temp, tempSD);
                        System.out.println("New Torrent file added to hashmap");

                    }

                    else{
                        tempSD.downloaders.add(d);
                        System.out.println("Torrent file updated in hashmap");
                    }

                    ArrayList <String> ips = new ArrayList<String>();
                    ArrayList <Integer> ports = new ArrayList<Integer>();

                    for(Seeder s : tempSD.seeders){
                        ips.add(s.ipAddress);
                        ports.add(s.port);
                    }

                    oos.writeObject(ips);
                    ois.readObject();
                    oos.writeObject(ports);
                    ois.readObject();

                    System.out.println("Array sent");

                }


            }
        }catch (SocketException e){

//            TrackerThread.text.appendText("Client disconnected from IP " + clientAddress + "\n");

        }
        catch (EOFException e){
//            TrackerThread.text.appendText("Client disconnected from IP " + clientAddress + "\n");
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }



    }



}
