package Tracker;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

/**
 * Created by Ishtiaq Niloy on 28-Nov-16.
 */
class Client_Tracker extends Thread{
    Socket socket;

    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;

    BufferedOutputStream bufferedOutputStream;
    BufferedInputStream bufferedInputStream;

    DataOutputStream dataOutputStream;
    DataInputStream dataInputStream;


    FileOutputStream fileOutputStream;
    FileInputStream fileInputStream;

    Client_Tracker(Socket s){
        socket = s;
        System.out.println(socket.getLocalAddress());

        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

        start();

    }

    public void run(){
        System.out.println("Connected");

        long pieceSize=0, numberOfPieces=0;

        try{
            pieceSize = (Long) objectInputStream.readObject();
            System.out.println("Piece size got = " + pieceSize);
            objectOutputStream.writeObject(new Boolean(true));

            numberOfPieces =(Long) objectInputStream.readObject();
            System.out.println("Number of pieces got = " + numberOfPieces);
            objectOutputStream.writeObject(new Boolean(true));

            bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream() ,  (int)  pieceSize);
            bufferedInputStream = new BufferedInputStream(socket.getInputStream() ,  (int) pieceSize);

            dataOutputStream = new DataOutputStream(bufferedOutputStream);
            dataInputStream = new DataInputStream(bufferedInputStream);

            byte [] arr = new byte[ (int) pieceSize];
            int done;

            for(int i=0; i<numberOfPieces; i++){
                String tmpPath = "C:\\Users\\Ishti\\Desktop\\TMP\\tmp" + i;
                File tmp = new File(tmpPath);
                fileOutputStream = new FileOutputStream(tmp.toPath().toString());

                done = (Integer) objectInputStream.readObject();
                System.out.println("Done of piece " + i + " received = " + done);
                objectOutputStream.writeObject(new Boolean(true));


                dataInputStream.readFully(arr, 0, done);
                System.out.println("Byte array received of piece " + i);
                objectOutputStream.writeObject(new Boolean(true));

                fileOutputStream.write(arr, 0, done);
                fileOutputStream.flush();
                fileOutputStream.close();
                tmp.createNewFile();

                System.out.println("Piece " + i + " is made.\n");

            }

            System.out.println("Done making pieces...\n");

            arr = new byte[ (int) pieceSize];

            for(int i = 0; i < numberOfPieces; i++){
                String tmpPath = "C:\\Users\\Ishti\\Desktop\\TMP\\tmp" + i;
                File tmp = new File(tmpPath);


                fileInputStream = new FileInputStream(tmp.toPath().toString());

                done = fileInputStream.read(arr, 0, (int) tmp.length());

                System.out.println("Bytes read of piece " + i + " = " + done);
                objectOutputStream.writeObject(new Integer(done));
                System.out.println("done of piece " + i + " sent : " + (Boolean) objectInputStream.readObject() );

                System.out.println("Sending byte array of piece " + i);
                dataOutputStream.write(arr, 0, done);
                dataOutputStream.flush();

                System.out.println("Byte array of piece " + i + " sent : " + (Boolean) objectInputStream.readObject() );
                System.out.println("File piece sent " + i + "\n");

//                Files.delete(tmp.toPath());

            }


            System.out.println("File pieces Sent...");

        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }


}
