package User;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Main extends Application {

    final static int width = 1000;
    final static int height = 600;

    static int trackerPort = 22222;
    static String tracker_IP_Address;
    static int localPort;

    static Socket socket;
    static ObjectOutputStream oos;
    static ObjectInputStream ois;
    static ServerSocket serverSocket;


    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Info.fxml"));
        primaryStage.setTitle("Torrent by Ishtiaq Niloy");
        primaryStage.setScene(new Scene(root, width, height));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
