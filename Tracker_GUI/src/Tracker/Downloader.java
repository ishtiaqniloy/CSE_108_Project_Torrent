package Tracker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by Ishtiaq Niloy on 08-Dec-16.
 */
public class Downloader {
    Socket socket;
    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;

    Downloader(Socket socket, ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream){
        this.socket = socket;
        this.objectOutputStream = objectOutputStream;
        this.objectInputStream = objectInputStream;


    }

}
