package Tracker;

import java.io.ObjectInputStream;

/**
 * Created by Ishtiaq Niloy on 07-Dec-16.
 */
public class TorrentFile extends Object {
    String fileName;
    long sourceSize;
    long pieceSize ;
    long numberOfPieces;


    TorrentFile(String fileName, long sourceSize, long pieceSize, long numberOfPieces){
        this.fileName = fileName;
        this.sourceSize = sourceSize;
        this.pieceSize = pieceSize;
        this.numberOfPieces = numberOfPieces;
    }

    public boolean equals(Object obj){
        if(obj instanceof TorrentFile){
            TorrentFile temp = (TorrentFile) obj;

            if(fileName.contentEquals(temp.fileName)  && sourceSize == temp.sourceSize){
                return true;
            }

        }

        return false;
    }

    public int hashCode() {
        int hash = this.fileName.hashCode();
        hash = hash * 31 + ((Long)this.sourceSize).hashCode();
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
