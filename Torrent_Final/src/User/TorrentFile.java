package User;

import java.io.File;
import java.io.Serializable;

/**
 * Created by Ishtiaq Niloy on 07-Dec-16.
 */
public class TorrentFile extends Object implements Serializable{
    String fileName;
    long sourceSize;
    long pieceSize ;
    long numberOfPieces;

    File directory;


    TorrentFile(String fileName, long sourceSize, long pieceSize, long numberOfPieces, File directory) {
        this.fileName = fileName;
        this.sourceSize = sourceSize;
        this.pieceSize = pieceSize;
        this.numberOfPieces = numberOfPieces;
        this.directory = directory;
    }

    public boolean equals(Object obj){
        if(obj instanceof TorrentFile){
            TorrentFile temp = (TorrentFile) obj;

            if(fileName == temp.fileName && sourceSize == temp.sourceSize && directory.toPath().toString().contentEquals(temp.directory.toPath().toString())){
                return true;
            }

        }

        return false;
    }

    public int hashCode() {
        int hash = this.fileName.hashCode();
        hash = hash * 31 + ((Long)this.sourceSize).hashCode();
        hash = hash * 31 + directory.toPath().toString().hashCode();
        return hash;
    }

//    public static void main(String[] args){
//        TorrentFile t1 = new TorrentFile("aaaaa" , 1223);
//        TorrentFile t2 = new TorrentFile("aaaaa", 1223);
//
//        System.out.println(t1.equals(t2));
//        System.out.println(t1.hashCode());
//        System.out.println(t2.hashCode());
//
//    }

}
