package User;

import java.io.*;
import java.net.Socket;

/**
 * Created by Ishtiaq Niloy on 28-Nov-16.
 */
class Client_User extends Thread {
    Socket socket;

    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;

    BufferedOutputStream bufferedOutputStream;
    BufferedInputStream bufferedInputStream;

    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;


    FileInputStream fileInputStream = null;
    FileOutputStream fileOutputStream = null;

    File file_source;
    File file_destination;

    Client_User(Socket s, File source, File destination){
        this.socket = s;
        this.file_source = source;
        this.file_destination = destination;

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

        long  sourceSize = file_source.length();
        System.out.println("Source file size = " +sourceSize);

        long pieceSize = 1;
        long numberOfPieces = sourceSize;

        while(numberOfPieces > 100){
            pieceSize*=5;
            numberOfPieces = sourceSize/pieceSize;
        }

        if(numberOfPieces<60){
            pieceSize=(pieceSize*3)/5;
            numberOfPieces = sourceSize/pieceSize;
        }

        if( sourceSize%pieceSize > 0){
            numberOfPieces+=1;
        }

        System.out.println("PieceSize = " + pieceSize + "\nnumberOfPieces = " + numberOfPieces);

        byte[] arr = new byte[ (int) pieceSize];

        int done;

        System.out.println("Pre calculations are done");

        try {

            objectOutputStream.writeObject( new Long(pieceSize) );
            System.out.println( "Piece size sent : " + (Boolean) objectInputStream.readObject());

            objectOutputStream.writeObject(new Long(numberOfPieces) );
            System.out.println( "Number of pieces sent : " + (Boolean) objectInputStream.readObject());

            bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream() , (int) pieceSize);
            bufferedInputStream = new BufferedInputStream(socket.getInputStream() , (int) pieceSize);

            dataOutputStream = new DataOutputStream(bufferedOutputStream);
            dataInputStream = new DataInputStream(bufferedInputStream);


            fileInputStream = new FileInputStream(file_source.toPath().toString());

            for(int i=0; i<numberOfPieces; i++){
                done = fileInputStream.read(arr, 0, (int) pieceSize);


                System.out.println("Read Bytes of piece " + i + " = " + done);
                objectOutputStream.writeObject( new Integer(done));
                System.out.println("Done of piece " + i + " sent : " + (Boolean) objectInputStream.readObject());

                System.out.println("Sending piece " + i);
                dataOutputStream.write(arr, 0, done);
                dataOutputStream.flush();
                System.out.println("Byte array sent of piece " + i + " : " + (Boolean) objectInputStream.readObject() + "\n");


            }

            System.out.println("Sending the file pieces is done...\n");


            fileOutputStream = new FileOutputStream(file_destination.toPath().toString());
            arr = new byte [ (int) pieceSize];
            for(int i = 0; i < numberOfPieces; i++){

                done = (Integer) objectInputStream.readObject();
                System.out.println("Done of piece " + i + " received = " + done);
                objectOutputStream.writeObject(new Boolean(true));

                dataInputStream.readFully(arr, 0, done);
                System.out.println("Byte array received of piece " + i);
                objectOutputStream.writeObject(new Boolean(true));

                fileOutputStream.write(arr, 0, done);

                System.out.println("File merged piece " + i + "\n");

            }

            fileOutputStream.flush();
            fileOutputStream.close();

            file_destination.createNewFile();

            System.out.println("Creating the file is done...\n");



        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }



}
