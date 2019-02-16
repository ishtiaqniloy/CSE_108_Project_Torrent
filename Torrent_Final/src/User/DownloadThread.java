package User;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by Ishtiaq Niloy on 07-Dec-16.
 */
public class DownloadThread extends Thread{
    Torrent thisTorrent;

    Socket socket;
    TorrentFile torrentFile;

    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;

    BufferedOutputStream bufferedOutputStream;
    BufferedInputStream bufferedInputStream;

    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;


    FileOutputStream fileOutputStream = null;

    File destinationDirectory;
    File tempDirectory;
    File destination;

    DownloadThread(Torrent thisTorrent, String ipAddress, int port, TorrentFile torrentFile){
        System.out.println("Constructing new Download Thread");

        this.thisTorrent = thisTorrent;
        this.torrentFile = torrentFile;
        this.destinationDirectory = torrentFile.directory;

        this.tempDirectory = new File(destinationDirectory, "tmp" + torrentFile.fileName.toString() + ".folder" );
        tempDirectory.mkdirs();

        try {
            this.socket = new Socket(ipAddress, port);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());

        }catch (ConnectException e){
            System.out.println("Client Offline");
        }
        catch (IOException e) {
            System.out.println("Server offline");
            thisTorrent.numberOfSeeders--;
            e.printStackTrace();
        }

        start();

    }

    void delete(){
        interrupt();
    }

    public void run() {

        System.out.println("Running new Download Thread");
        int pieceNumber = 0;

        try {
            objectOutputStream.writeObject(torrentFile);
            objectInputStream.readObject();

            bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream() , (int) torrentFile.pieceSize);
            bufferedInputStream = new BufferedInputStream(socket.getInputStream() , (int) torrentFile.pieceSize);

            dataOutputStream = new DataOutputStream(bufferedOutputStream);
            dataInputStream = new DataInputStream(bufferedInputStream);

            byte[] arr = new byte[ (int) torrentFile.pieceSize];

            int done;

            while(thisTorrent.piecesStatus.containsValue(0)){
                pieceNumber = 0;

                for(int i=0 ; i<thisTorrent.piecesStatus.size(); i++){
                    if(thisTorrent.piecesStatus.get(i).equals(new Integer(0))){
                        pieceNumber=i;
                        thisTorrent.piecesStatus.put(i,1);
                        break;

                    }
                }

                objectOutputStream.writeObject(true);
                objectInputStream.readObject();

                //System.out.println("requesting for piece " + pieceNumber + " of " + torrentFile.fileName);

                objectOutputStream.writeObject(pieceNumber);
                objectInputStream.readObject();

                destination = new File(tempDirectory.toPath().toString() + "\\" + torrentFile.fileName + new Integer( pieceNumber).toString() );
                destination.createNewFile();

                fileOutputStream = new FileOutputStream(destination);

                done = (Integer) objectInputStream.readObject();
                objectOutputStream.writeObject(true);


                dataInputStream.readFully(arr, 0, done);
                objectOutputStream.writeObject(true);

                fileOutputStream.write(arr, 0, done);
                fileOutputStream.flush();
                fileOutputStream.close();
                destination.createNewFile();

                System.out.println("Piece " + pieceNumber + " is made of file " + torrentFile.fileName);

                thisTorrent.piecesStatus.put(pieceNumber,2);

                UI_Controller.setMethod(1, thisTorrent, 0);

            }

            if (!thisTorrent.piecesStatus.containsValue(0) && !thisTorrent.piecesStatus.containsValue(1)){

                if(thisTorrent.doneDownloading==false){
                    thisTorrent.doneDownloading = true;
                    System.out.println("File download complete : " + thisTorrent.fileName);

                    MergeThread m = new MergeThread(destinationDirectory, tempDirectory, torrentFile, thisTorrent);
                }

                objectOutputStream.writeObject(false);

                System.out.println("Ending download thread");
                socket.close();

            }


        }catch (NullPointerException e){

        }
        catch(EOFException e){
            thisTorrent.piecesStatus.put(pieceNumber,0);
            System.out.println("File not found by seeder");
            thisTorrent.numberOfSeeders--;
        }
        catch (SocketException e){
            thisTorrent.piecesStatus.put(pieceNumber,0);
            System.out.println("Connection lost from seeder");
            thisTorrent.numberOfSeeders--;
        }
        catch (IOException e) {
            thisTorrent.piecesStatus.put(pieceNumber,0);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            thisTorrent.piecesStatus.put(pieceNumber,0);
            e.printStackTrace();
        }


    }
}
