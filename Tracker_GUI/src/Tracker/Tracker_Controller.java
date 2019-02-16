package Tracker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

public class Tracker_Controller implements Initializable {

    static int port = 22222;

    static TrackerThread tracker;

    @FXML
    private TextArea text;


    @FXML
    void exit(ActionEvent event) {
        tracker.close();

        System.exit(0);
    }


    @FXML
    void reset(ActionEvent event) {
        TrackerThread.hashMap.clear();
        TrackerThread.clientsTrackers.clear();
        TrackerThread.text.setText("Tracker Reset\n");
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            tracker = new TrackerThread(port , text);
            text.setText("Tracker Online\n");

        } catch (IOException e) {
            text.setText("Tracker Error\n");
            System.exit(0);
        }



    }


}
