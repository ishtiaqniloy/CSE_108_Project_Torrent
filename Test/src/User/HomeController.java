package User;

import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;

import java.io.*;
import java.net.Socket;

import javafx.event.ActionEvent;


public class HomeController {

    int port = 3600;

    @FXML
    private MenuBar menuBar;

    @FXML
    private Menu removeButtion;

    @FXML
    private MenuItem createButton;

    @FXML
    private Menu addButton;
    @FXML
    private AnchorPane Home;
    @FXML
    private MenuItem exitButton;
    @FXML
    private Button copyNetworkButton;
    @FXML
    private Button copyButton;
    @FXML
    private MenuItem copyMenuButton;
    @FXML
    private Menu fileMenu;
    @FXML
    void Create(ActionEvent event) {

        System.out.println("Inside Create Method");

        DirectoryChooser directoryChooser = new DirectoryChooser();

        FileChooser fileChooser = new FileChooser();

        File newTorrent = fileChooser.showSaveDialog(createButton.getParentPopup().getOwnerWindow());

        System.out.println(newTorrent);

        /*try {
            newTorrent.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }*/


    }


    @FXML
    void Add(ActionEvent event) {

    }

    @FXML
    void Remove(ActionEvent event) {

    }

    @FXML
    void copy(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;

        fileChooser.setTitle("Select Source");
        File file1 = fileChooser.showOpenDialog(copyMenuButton.getParentPopup().getOwnerWindow());

        fileChooser.setTitle("Select Destination");
        File file2 = fileChooser.showSaveDialog(copyMenuButton.getParentPopup().getOwnerWindow());

        long  sourceSize = file1.length();
        System.out.println(sourceSize);

        int pieceSize = 1;
        int numberOfPieces = (int) sourceSize;

        while(numberOfPieces > 100){
            pieceSize*=5;
            numberOfPieces = (int) sourceSize/pieceSize;
        }

        if(numberOfPieces<60){
            pieceSize=(pieceSize*3)/5;
            numberOfPieces = (int) sourceSize/pieceSize;
        }

        if((int) sourceSize%pieceSize > 0){
            numberOfPieces+=1;
        }

        System.out.println("PieceSize = " + pieceSize + "\nnumberOfPieces = " + numberOfPieces);

        byte[] arr = new byte[pieceSize];

        long totalDone=0;
        int done;


        try {
            inputStream = new FileInputStream(file1.toPath().toString());


            for(int i=0; i<numberOfPieces; i++){
                done = inputStream.read(arr, 0, pieceSize);
                totalDone+=done;

                String tmpPath = "C:\\Users\\Ishti\\Desktop\\TMP\\tmp" + i;
                File tmp = new File(tmpPath);

                outputStream = new FileOutputStream(tmp.toPath().toString());

                outputStream.write(arr, 0, done);

                outputStream.flush();
                outputStream.close();

                tmp.createNewFile();

                System.out.println("Piece " + i + " is made.");

            }

            System.out.println("Dividing the file is done...");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            outputStream = new FileOutputStream(file2.toPath().toString());

            for(int i = 0; i < numberOfPieces; i++){
                String tmpPath = "C:\\Users\\Ishti\\Desktop\\TMP\\tmp" + i;
                File tmp = new File(tmpPath);

                inputStream = new FileInputStream(tmp.toPath().toString());

                done = inputStream.read(arr, 0, pieceSize);

                outputStream.write(arr, 0, done);

                System.out.println("File merged piece " + i);

            }

            outputStream.flush();
            outputStream.close();

            file2.createNewFile();

            System.out.println("Creating the file is done");
            System.exit(0);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    @FXML
    void copyNetwork(ActionEvent event) { FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Select Source");
        File source = fileChooser.showOpenDialog(copyMenuButton.getParentPopup().getOwnerWindow());

        fileChooser.setTitle("Select Destination");
        File destination = fileChooser.showSaveDialog(copyMenuButton.getParentPopup().getOwnerWindow());



        try {
            Socket s = new Socket("LocalHost", port);

            Client_User c1 = new Client_User(s, source, destination);

            System.out.println("Connected");

            //c1.join();

           //System.exit(0);



        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @FXML
    void exit(ActionEvent event) {
        System.exit(0);
    }


}
