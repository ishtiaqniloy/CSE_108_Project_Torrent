package User;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.ResourceBundle;
import java.util.Scanner;

public class UI_Controller implements Initializable{
    FileChooser fileChooser;
    DirectoryChooser directoryChooser;

    static ArrayList<Torrent> torrents;
    static ObservableList <TTClass> observableTorrentList;
    static TableView<TTClass> table;

    @FXML
    private Button createButton;
    @FXML
    private Button addButton;
    @FXML
    private Button removeButton;
    @FXML
    private Button exitButton;

    @FXML
    private TableView<TTClass> torrentTable;
    @FXML
    private TableColumn<TTClass, String> nameColumn;
    @FXML
    private TableColumn<TTClass, String> statusColumn;
    @FXML
    private TableColumn<TTClass, String> sizeColumn;
    @FXML
    private TableColumn<TTClass, ProgressBar> progressColumn;
    @FXML
    private TableColumn<TTClass, Integer> seedsColumn;

    @FXML
    private TableColumn<?, ?> downloadedColumn;
    @FXML
    private TableColumn<?, ?> remainingColumn;


    @FXML
    void createNewTorrent(ActionEvent event) {
        fileChooser = new FileChooser();

        fileChooser.setTitle("Select File");
        File source = fileChooser.showOpenDialog(createButton.getParent().getScene().getWindow());

        long  sourceSize = source.length();
        System.out.println("Source file size = " +sourceSize);

        long pieceSize = 1;
        long numberOfPieces = sourceSize;

        while(numberOfPieces > 200 ){
            pieceSize*=5;
            numberOfPieces = sourceSize/pieceSize;
        }

        if(numberOfPieces<120){
            pieceSize=(pieceSize*3)/5;
            numberOfPieces = sourceSize/pieceSize;
        }

        if( sourceSize%pieceSize > 0){
            numberOfPieces+=1;
        }

        System.out.println("PieceSize = " + pieceSize + "\nnumberOfPieces = " + numberOfPieces);

        directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Destination");

        File destinationDirectory = directoryChooser.showDialog(createButton.getParent().getScene().getWindow());

        File torrentFile = new File(destinationDirectory , source.getName()+".tnt");


        try {
            torrentFile.createNewFile();

            PrintWriter writer = new PrintWriter(torrentFile);

            writer.println(source.getName());
            writer.println(sourceSize);
            writer.println(pieceSize);
            writer.println(numberOfPieces);

            writer.flush();
            writer.close();
            torrentFile.createNewFile();

            System.out.println("Torrent File Created");


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @FXML
    void addTorrent(ActionEvent event) {
        fileChooser = new FileChooser();

        fileChooser.setTitle("Select Torrent File");
        File torrent = fileChooser.showOpenDialog(addButton.getParent().getScene().getWindow());

        String fileName;
        long sourceSize;
        long numberOfPieces;
        long pieceSize;
        File destinationDirectory;

        try {
            FileInputStream inputStream = new FileInputStream(torrent.toPath().toString());
            Scanner sc = new Scanner(inputStream);

            fileName = sc.nextLine();
            sourceSize = sc.nextLong();
            pieceSize = sc.nextLong();
            numberOfPieces = sc.nextLong();

            inputStream.close();

//            System.out.println(fileName);
//            System.out.println(sourceSize);
//            System.out.println(numberOfPieces);
//            System.out.println(pieceSize);

            directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Destination");
            destinationDirectory = directoryChooser.showDialog(addButton.getParent().getScene().getWindow());

            System.out.println(destinationDirectory.toPath().toString());

//             File aaa = new File(destinationDirectory ,  "A.pdf.txt" );
//             Scanner scn = new Scanner(new FileInputStream(aaa.toPath().toString()));
//
//            System.out.println("Scn : " + scn.nextLine());


            File[] arr =  destinationDirectory.listFiles();

            boolean found = false;
            for(File f: arr){
                if(f.getName().contentEquals(fileName) && f.length() == sourceSize) {
                    found = true;
                    //System.out.println("Seed");
                    Torrent temp = new Torrent(fileName, sourceSize, numberOfPieces, pieceSize, destinationDirectory, true);
                    torrents.add(temp);

                    break;
                }

            }

            if(!found){
                //System.out.println("Download");
                Torrent temp = new Torrent(fileName, sourceSize, numberOfPieces, pieceSize, destinationDirectory, false);
                torrents.add(temp);

            }


        }catch (InputMismatchException e){
            System.out.println("File type mismatch");
        }
        catch (NullPointerException e){
            System.out.println("Directory not found.");
        }
        catch (IOException e) {
            e.printStackTrace();
        }


    }

    @FXML
    void removeTorrent(ActionEvent event) {
       int removeIndex = table.getSelectionModel().getSelectedIndex();
       TTClass removeClass = observableTorrentList.get(removeIndex);

       observableTorrentList.remove(removeIndex);
       table.setItems(observableTorrentList);

       Torrent removeTorrent = removeClass.torrent;
       removeTorrent.delete();
       removeIndex = torrents.indexOf(removeTorrent);

       for(int i = 0; i<AcceptThread.seedThreads.size(); i++){
           if(AcceptThread.seedThreads.get(i).torrentFile.equals(removeTorrent.torrentFile)){
               AcceptThread.seedThreads.get(i).delete();
               i--;
           }

       }
        for(int i = 0; i<Torrent.downloadThreads.size(); i++){
            if(Torrent.downloadThreads.get(i).torrentFile.equals(removeTorrent.torrentFile)){
                Torrent.downloadThreads.get(i).delete();
                i--;
            }

        }

        torrents.remove(removeIndex);



    }

    @FXML
    void exit(ActionEvent event) {
        try {
            Main.socket.close();
            Main.serverSocket.close();
        } catch (IOException e) {
            System.out.println("Socket Error");
        }

        System.exit(0);

    }




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        torrents = new ArrayList<Torrent>();

        table = torrentTable;

        observableTorrentList = FXCollections.observableArrayList();

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        progressColumn.setCellValueFactory(new PropertyValueFactory<>("progress"));
        seedsColumn.setCellValueFactory(new PropertyValueFactory<>("seeds") );

        torrentTable.setItems(observableTorrentList);


    }

    synchronized static void setMethod(int option, Torrent thisTorrent, int m){ //opt 1 = set download, 2 = set merge

        if(option==1){
            for(int i=0; i<thisTorrent.piecesStatus.size(); i++){
                if(thisTorrent.piecesStatus.get(i) == 2)
                    m++;
            }
            //System.out.println("Download pieces : " + x);

            long downloadedBytes = m*thisTorrent.pieceSize;
            downloadedBytes = downloadedBytes < thisTorrent.sourceSize? downloadedBytes : thisTorrent.sourceSize;
            //System.out.println("Downloaded Bytes : " + downloadedBytes);

            int i = UI_Controller.observableTorrentList.indexOf(thisTorrent.tempRow);
            if(i>=0){

                TTClass mRow = UI_Controller.observableTorrentList.get(i);
                mRow.setProgress(1.0*downloadedBytes/thisTorrent.sourceSize);
                mRow.setSeeds(thisTorrent.numberOfSeeders);

//            UI_Controller.observableTorrentList.remove(i);
//            UI_Controller.observableTorrentList.add(i, mRow);

                UI_Controller.table.setItems(UI_Controller.observableTorrentList);

            }
        }

        else if(option == 2){
            System.out.println("Merged pieces : " + m + " of " + thisTorrent.fileName);

            int i = UI_Controller.observableTorrentList.indexOf(thisTorrent.tempRow);
            if(i>=0){

                TTClass mRow = UI_Controller.observableTorrentList.get(i);
                mRow.setProgress(1.0*m/thisTorrent.numberOfPieces);
                mRow.setStatus("Merging");

                if(!mRow.getStatus().contentEquals("Merging") || m==1){
                    System.out.println("File Merge Start of " + mRow.getName());
                    mRow.setStatus("Merging");
                    UI_Controller.observableTorrentList.remove(i);
                    UI_Controller.observableTorrentList.add( mRow);
                    UI_Controller.table.setItems(UI_Controller.observableTorrentList);
                }

                if(m==thisTorrent.numberOfPieces){
                    UI_Controller.observableTorrentList.remove(i);
                    int j = torrents.indexOf(thisTorrent);
                    if(j>=0){
                        torrents.remove(j);
                    }

                }

                UI_Controller.table.setItems(UI_Controller.observableTorrentList);

            }

        }


    }



}
