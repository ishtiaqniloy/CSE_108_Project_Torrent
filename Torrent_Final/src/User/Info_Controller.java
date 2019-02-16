package User;

/**
 * Created by Ishtiaq Niloy on 03-Dec-16.
 */

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

import static User.Main.oos;

public class Info_Controller {

    @FXML
    private Button enterButton;

    @FXML
    private TextField trackerIP;

    @FXML
    private TextField localPort;

    @FXML
    private Label emptyLabel;

    @FXML
    void enter(ActionEvent event) {
        String tracker = trackerIP.getText();
        String local = localPort.getText();

        tracker = tracker.trim();
        local= local.trim();

        boolean flag = true;

        int counter=0;

        for(int i =0; i<tracker.length(); i++){
            if(tracker.charAt(i)=='.'){
                if(tracker.charAt(i+1)=='.'){
                    flag = false;
                    break;
                }
                counter++;
            }
            else if(tracker.charAt(i)< '0' || tracker.charAt(i) > '9'){
                flag = false;
                break;
            }

        }

        if(counter!=3){
            flag = false;
        }

        if(tracker.contentEquals("LocalHost")){
            flag=true;
        }

        if(flag){
            Main.tracker_IP_Address = tracker;
            Main.localPort = Integer.parseInt(local);

            try {
                Main.socket = new Socket(Main.tracker_IP_Address, Main.trackerPort);
                System.out.println("Connected to tracker");

                Main.oos = new ObjectOutputStream( Main.socket.getOutputStream());
                Main.ois = new ObjectInputStream(Main.socket.getInputStream() );

                Main.serverSocket = new ServerSocket(Main.localPort);
                System.out.println("Local Server Online");

                AcceptThread a = new AcceptThread(Main.serverSocket);


            } catch (Exception e) {
                flag = false;
            }

        }


        if(flag){

            try {
                Stage stage = (Stage) enterButton.getParent().getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("UI.fxml"));
                stage.setScene(new Scene(root, Main.width, Main.height));
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        else{
            emptyLabel.setText("Wrong IP Address or Unavailable Port");
            trackerIP.clear();
            localPort.clear();
        }

    }

}
