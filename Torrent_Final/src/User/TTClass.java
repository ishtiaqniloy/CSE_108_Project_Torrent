package User;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ProgressBar;
import javafx.util.Callback;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Ishtiaq Niloy on 11-Dec-16.
 */
public class TTClass extends Object{
    String name;
    long size;
    String status;
    ProgressBar progress;
    int seeds;

    String destinationDirectory;
    Torrent torrent;

    public TTClass(String name, String status, long size, double progress, int seeds, File destinationDirectory, Torrent torrent){
        this.name = name;
        this.status = status;
        this.size = size;

        this.progress = new ProgressBar(progress);
        this.progress.setMinWidth(210);
        this.progress.setMaxWidth(228);
        this.progress.setPrefWidth(226);
        this.progress.setVisible(true);

        this.seeds = seeds;

        this.destinationDirectory = destinationDirectory.toPath().toString();
        this.torrent = torrent;


    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
        this.status = new String(status);
    }

    public String getSize(){
        String temp ;
        DecimalFormat df = new DecimalFormat("#.##");
        if(size<1024){
            temp = size + " B";
        }
        else if(size <= 1024*1024){
            temp = df.format(1.0 * size/1024) + " KB";
        }
        else if(size <= 1024*1024*1024){
            temp = df.format(1.0 * size/ (1024*1024 ) ) + " MB";
        }
        else if(size <= 1024*1024*1024){
            temp = df.format(1.0 * size/ (1024*1024 ) ) + " MB";
        }
        else{
            temp = df.format(1.0 * size/ (1024*1024*1024 ) ) + " GB";
        }


        return temp;
    }
    public void setSize(long size){
        this.size = size;
    }

    public ProgressBar getProgress(){
        return progress;
    }
    public void setProgress(double progress){
        this.progress.setProgress(1.0*progress);
    }

    public int getSeeds(){
        return seeds;
    }
    public void setSeeds(int seeds){
        this.seeds = seeds;
    }

    boolean equalsTorrent(TorrentFile t){
        if(name.contentEquals(t.fileName) && size==t.sourceSize && destinationDirectory.contentEquals(t.directory.toPath().toString()) ){
            return true;
        }
        else{
            return false;
        }

    }

    public boolean equals(Object obj){
        if(obj instanceof TTClass){
            TTClass temp = (TTClass) obj;

            if(name.contentEquals(temp.name) && size == temp.size && destinationDirectory.contentEquals(temp.destinationDirectory)){
                return true;
            }

        }

        return false;
    }

    public int hashCode() {
        int hash = this.name.hashCode();
        hash = hash * 31 + ((Long)this.size).hashCode();
        hash = hash * 31 + destinationDirectory.hashCode();
        return hash;
    }


}
