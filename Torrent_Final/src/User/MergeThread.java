package User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Ishtiaq Niloy on 08-Dec-16.
 */
public class MergeThread extends Thread {
    File destinationDirectory;
    File tempDirectory;
    File outputFile;
    File inputFile;
    TorrentFile torrentFile;

    Torrent thisTorrent;

    FileInputStream fileInputStream;
    FileOutputStream fileOutputStream;

    MergeThread(File destinationDirectory, File tempDirectory, TorrentFile torrentFile, Torrent thisTorrent){
        this.destinationDirectory = destinationDirectory;
        this.tempDirectory = tempDirectory;
        this.torrentFile = torrentFile;
        this.thisTorrent = thisTorrent;

        start();

    }

    public void run(){
        System.out.println("Merging file " + torrentFile.fileName);

        try {
            outputFile = new File(destinationDirectory, torrentFile.fileName);
            outputFile.createNewFile();

            fileOutputStream = new FileOutputStream(outputFile.toPath().toString());

            int done=0;
            byte[] arr = new byte[ (int) torrentFile.pieceSize];

            for(int i=0; i<torrentFile.numberOfPieces; i++){
                //System.out.println("Merging piece " + i + " of " + torrentFile.fileName);

                inputFile = new File(tempDirectory.toPath().toString() + "\\" + torrentFile.fileName + new Integer( i).toString() );
                fileInputStream = new FileInputStream(inputFile.toPath().toString());

                done = fileInputStream.read(arr, 0, (int) inputFile.length());

                fileOutputStream.write(arr, 0, done);

                fileInputStream.close();
                inputFile.delete();

               UI_Controller.setMethod(2, thisTorrent, i+1);

            }
            tempDirectory.delete();

            fileOutputStream.flush();
            fileOutputStream.close();

            outputFile.createNewFile();

            System.out.println("Creating the file is done...\n");

            Torrent torrent = new Torrent(torrentFile.fileName, torrentFile.sourceSize, torrentFile.numberOfPieces, torrentFile.pieceSize, torrentFile.directory, true);

            UI_Controller.torrents.add(torrent);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}