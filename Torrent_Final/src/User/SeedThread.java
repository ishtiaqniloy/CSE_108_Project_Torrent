package User;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

import static User.UI_Controller.torrents;

/**
 * Created by Ishtiaq Niloy on 07-Dec-16.
 */
public class SeedThread extends Thread{
    Socket socket;
    TorrentFile torrentFile;

    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;

    BufferedOutputStream bufferedOutputStream;
    BufferedInputStream bufferedInputStream;

    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;


    FileInputStream fileInputStream = null;
    FileOutputStream fileOutputStream = null;

    File sourceDirectory;
    File source;

    SeedThread(Socket socket){
        this.socket = socket;

        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

        start();
    }

    void delete(){
        interrupt();

    }

    public void run(){
        System.out.println("Running new Seed Thread");

        try {
            sourceDirectory = null;

            torrentFile = (TorrentFile) objectInputStream.readObject();
            objectOutputStream.writeObject(true);

            bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream() , (int) torrentFile.pieceSize);
            bufferedInputStream = new BufferedInputStream(socket.getInputStream() , (int) torrentFile.pieceSize);

            dataOutputStream = new DataOutputStream(bufferedOutputStream);
            dataInputStream = new DataInputStream(bufferedInputStream);


            for (int i =0; i<UI_Controller.torrents.size(); i++){
                if(torrents.get(i).fileName.contentEquals(torrentFile.fileName) && torrents.get(i).sourceSize == torrentFile.sourceSize){
                    sourceDirectory = torrents.get(i).directory;
                    break;
                }

            }

            if (sourceDirectory == null){
                System.out.println("Removed already");
                socket.close();
                return;
            }

            source = new File(sourceDirectory, torrentFile.fileName);

            if (!source.exists()){
                System.out.println("File not Found");
                socket.close();
                return;
            }

            byte[] arr = new byte[ (int) torrentFile.pieceSize];

            int done;

            while(source.exists()){
                fileInputStream = new FileInputStream(source.toPath().toString());

                boolean b = (Boolean)objectInputStream.readObject();
                if(b==false){
                    System.out.println("Seed thread completed");
                    socket.close();
                    return;

                }

                objectOutputStream.writeObject(true);

                int pieceNumber = (Integer) objectInputStream.readObject();
                objectOutputStream.writeObject(true);

                fileInputStream.skip(pieceNumber*torrentFile.pieceSize);

                done = fileInputStream.read(arr, 0, (int) torrentFile.pieceSize);

                objectOutputStream.writeObject( new Integer(done));
                objectInputStream.readObject();

                dataOutputStream.write(arr, 0, done);
                dataOutputStream.flush();
                objectInputStream.readObject();

                System.out.println("Successfully sent piece " + pieceNumber + " of " + torrentFile.fileName);


            }

        }catch (SocketException e) {
            System.out.println("Connection Lost");
        }
        catch (EOFException e){

        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }


}
