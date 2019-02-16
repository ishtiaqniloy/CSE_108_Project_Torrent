package User;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Ishtiaq Niloy on 07-Dec-16.
 */

public class Torrent extends  Thread{
    Socket socket;
    ObjectOutputStream oos;
    ObjectInputStream ois;
    String fileName;
    long sourceSize;
    long numberOfPieces;
    long pieceSize;
    File directory;
    boolean isSeeding;
    int numberOfSeeders;

    ArrayList<Seeder> seeders;
    static ArrayList<DownloadThread> downloadThreads = new ArrayList<DownloadThread>();
    ConcurrentHashMap<Integer, Integer> piecesStatus;       //0 = not attempted, 1= attempted, 2= done
    boolean doneDownloading;

    TTClass tempRow;
    TorrentFile torrentFile;

    Torrent(String fileName, long sourceSize, long numberOfPieces, long pieceSize, File directory, boolean isSeeding){
        numberOfSeeders = 0;
        this.fileName = fileName;
        this.sourceSize = sourceSize;
        this.numberOfPieces = numberOfPieces;
        this. pieceSize = pieceSize;
        this.directory = directory;
        this.isSeeding = isSeeding;

        seeders = new ArrayList<Seeder>();

        piecesStatus = new ConcurrentHashMap<Integer , Integer>();

        try {
            socket = new Socket(Main.tracker_IP_Address, Main.trackerPort);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());

            System.out.println("Connected to tracker");

        } catch (IOException e) {
            System.out.println("Tracker Offline");

            e.printStackTrace();
        }


        start();
    }

    public void delete(){

        if (piecesStatus.containsValue(1)){
            System.out.println("Deleting");
            File tempDirectory = new File(directory, "tmp" + torrentFile.fileName.toString() + ".folder" );
            tempDirectory.mkdirs();
            tempDirectory.delete();

        }

        System.out.println("Ending Thread");

        interrupt();

    }

    public void run(){
        torrentFile = new TorrentFile(fileName, sourceSize, pieceSize, numberOfPieces, directory);

        if(isSeeding){
            System.out.println("Seeding");

            tempRow = new TTClass(fileName, "Seeding", sourceSize, 100.0, numberOfSeeders, directory, this);

            int i = UI_Controller.observableTorrentList.indexOf(tempRow);
            if(i>=0){
                TTClass mRow = UI_Controller.observableTorrentList.get(i);
                System.out.println("Changing status to Seeding of " + mRow.getName());
                mRow.setStatus("Seeding");

                UI_Controller.observableTorrentList.remove(i);
                UI_Controller.observableTorrentList.add(mRow);


            }
            else{
                UI_Controller.observableTorrentList.add(tempRow);
            }

            UI_Controller.table.setItems(UI_Controller.observableTorrentList);

            doneDownloading = true;

            for(i=0; i<numberOfPieces; i++){
                piecesStatus.put(i, 2);
            }

            try {
                oos.writeObject(true);
                ois.readObject();

                oos.writeObject(fileName);
                ois.readObject();

                oos.writeObject(sourceSize);
                ois.readObject();

                oos.writeObject(pieceSize);
                ois.readObject();

                oos.writeObject(numberOfPieces);
                ois.readObject();

                oos.writeObject(Main.localPort);
                ois.readObject();


            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

        else{
            System.out.println("Downloading");

            tempRow = new TTClass(fileName, "Downloading", sourceSize, 0.0, 0, directory, this);

            int i = UI_Controller.observableTorrentList.indexOf(tempRow);
            if(i>=0){

                TTClass mRow = UI_Controller.observableTorrentList.get(i);
                System.out.println("Changing status to Downloading of " + mRow.getName());
                mRow.setStatus("Downloading");

                UI_Controller.observableTorrentList.remove(i);
                UI_Controller.observableTorrentList.add(mRow);

                UI_Controller.table.setItems(UI_Controller.observableTorrentList);
            }
            else{
                UI_Controller.observableTorrentList.add(tempRow);
            }

            UI_Controller.table.setItems(UI_Controller.observableTorrentList);

            doneDownloading = false;

            for(i=0; i<numberOfPieces; i++){
                piecesStatus.put(i, 0);
            }

            try {
                oos.writeObject(false);
                ois.readObject();

                oos.writeObject(fileName);
                ois.readObject();

                oos.writeObject(sourceSize);
                ois.readObject();

                oos.writeObject(pieceSize);
                ois.readObject();

                oos.writeObject(numberOfPieces);
                ois.readObject();

                ArrayList<Seeder> seeders = new ArrayList<Seeder>();

                ArrayList <String> ips ;
                ArrayList <Integer> ports ;

                ips = (ArrayList<String>) ois.readObject();
                oos.writeObject(true);


                ports = (ArrayList<Integer>) ois.readObject();
                oos.writeObject(true);

                if(ips == null || ports == null || ips.size()==0 || ports.size() == 0){
                    System.out.println("No seeder available");
                }

                else if(doneDownloading){
                    System.out.println("Already downloaded");
                }

                else{

                    for(i=0; i<ips.size(); i++){
                        Seeder temp = new Seeder(ips.get(i), ports.get(i));
                        seeders.add(temp);

                    }

                    for(Seeder s : seeders){

                        if(s.port != Main.localPort ){
                            System.out.println("Downloading from " + s.ipAddress + " with port : " + s.port);
                           downloadThreads.add( new DownloadThread(this, s.ipAddress, s.port, torrentFile) );
                            numberOfSeeders++;
                            }


                    }

                    i = UI_Controller.observableTorrentList.indexOf(tempRow);
                    if(i>=0){
                        TTClass mRow = UI_Controller.observableTorrentList.get(i);
                        mRow.setSeeds(numberOfSeeders);
                        System.out.println("Changing number of seeds of " + mRow.getName());

                        UI_Controller.observableTorrentList.remove(i);
                        UI_Controller.observableTorrentList.add(mRow);

                        UI_Controller.table.setItems(UI_Controller.observableTorrentList);

                    }

                }

                while (!doneDownloading){
                    System.out.println("Waiting for new Seeders");

                    String ip;
                    int port;

                    ip = (String) ois.readObject();
                    //oos.writeObject(true);
                    //System.out.println("Got ip address : " + ip);

                    port = (Integer) ois.readObject();
                    //oos.writeObject(true);
                    //System.out.println("Got port number : " + port);

                    Seeder tempS = new Seeder(ip, port);
                    seeders.add(tempS);

                    if(!doneDownloading && tempS.port!=Main.localPort ){
                        System.out.println("New Seeder found!");
                        downloadThreads.add( new DownloadThread(this, tempS.ipAddress, tempS.port, torrentFile) );
                        numberOfSeeders++;

                        i = UI_Controller.observableTorrentList.indexOf(tempRow);
                        if(i>=0){

                            TTClass mRow = UI_Controller.observableTorrentList.get(i);
                            mRow.setSeeds(numberOfSeeders);
                            System.out.println("Changing number of seeds of " + mRow.getName());

                            UI_Controller.observableTorrentList.remove(i);
                            UI_Controller.observableTorrentList.add(mRow);

                            UI_Controller.table.setItems(UI_Controller.observableTorrentList);

                        }

                    }

                }




            }catch (ConnectException e){
                System.out.println("Client Offline");
            }
            catch(SocketException e){
                System.out.println("Tracker Disconnected");
            }
            catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


        }


    }


}
