package ru.spbau.zhidkov.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.spbau.zhidkov.utils.FilesList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/** Class providing graphical interface */
public class MainGuiClient extends Application {

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    private FileChooser fileChooser = new FileChooser();
    private TextField hostField = new TextField("host");
    private TextField portField = new TextField("port");
    private VBox vBox = new VBox();
    private Client client = null;
    private boolean started = false;
    private Path currentPath = Paths.get(".");
    private ListView<String> listView = null;


    /** Starts UI */
    @Override
    public void start(Stage stage) throws IOException {
        StackPane root = new StackPane();
        Button buttonStart = new Button("start");
        buttonStart.setOnAction(event -> {
            if (started) {
                return;
            }
            started = true;
            try {
                startClient();

                showList(stage);
            } catch (IOException e) {
                showErrorAlert();
            }
        });
        Button buttonExit = new Button("exit");
        buttonExit.setOnAction(event -> {
            try {
                if (client != null) {
                    client.disconnect();
                }
                Platform.exit();
            } catch (Exception e) {
                showErrorAlert();
            }
        });
        vBox.getChildren().addAll(hostField, portField, buttonStart, buttonExit);
        root.getChildren().add(vBox);
        stage.setScene(new Scene(root, 300, 250));
        stage.show();
    }

    private void showList(Stage stage) throws IOException {
        vBox.getChildren().clear();
        listView = new ListView<>();
        final Map<Path, FilesList.FileType> files = client.executeList(currentPath);
        for (Map.Entry<Path, FilesList.FileType> entry : files.entrySet()) {
            String path = entry.getKey().toString();
            if (entry.getValue().equals(FilesList.FileType.FOLDER) &&
                    !String.valueOf(path.charAt(path.length() - 1)).equals(File.separator)) {
                path += File.separator;
            }
            listView.getItems().add(path);
        }
        listView.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() != KeyCode.ENTER) {
                return;
            }
            String item = listView.getSelectionModel().getSelectedItem();
            Path path = Paths.get(item);
            switch (files.get(path)) {
                case FILE: {
                    fileChooser.setTitle("Save file as");
                    File file = fileChooser.showSaveDialog(stage);
                    if (file != null) {
                        try {
                            client.executeGet(path, file.toPath());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
                case FOLDER: {
                    currentPath = Paths.get(currentPath.toString(), path.toString());
                    try {
                        showList(stage);
                    } catch (IOException e) {
                        showErrorAlert();
                    }
                    break;
                }
            }
        });
        vBox.getChildren().add(listView);
    }

    private void startClient() throws IOException {
        client = Client.buildClient(hostField.getText(), Integer.valueOf(portField.getText()),
                Paths.get(System.getProperty("user.dir")));
        client.connect();
    }

    private void showErrorAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("Error!");
        alert.setContentText("Problems, try again");
    }
}
