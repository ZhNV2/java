package ru.spbau.zhidkov.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import java.util.Optional;

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
                buildTree(stage);
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

    private void buildTree(Stage stage) throws IOException {
        startClient();
        TreeItem<Path> root1 = new TreeItemPath(Paths.get("."), true, hostField.getText(),
                Integer.valueOf(portField.getText()), client);
        TreeView treeView = new TreeView<>(root1);
        treeView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                TreeItemPath item = (TreeItemPath) treeView.getSelectionModel().getSelectedItem();
                if (item != null && !item.isDir()) {
                    fileChooser.setTitle("Save file as");
                    File file = fileChooser.showSaveDialog(stage);
                    if (file != null) {
                        try {
                            client.executeGet(item.getPath(), file.toPath());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        vBox.getChildren().add(treeView);
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

    private static class TreeItemPath extends TreeItem<Path> {

        public boolean isDir() {
            return isDir;
        }

        private boolean isDir;

        public Path getPath() {
            return path;
        }


        private Path path;
        private String hostname;
        private int port;
        private Client client;
        private boolean firstTime = true;

        public TreeItemPath(Path path, boolean isDir, String hostname, int port, Client client) {
            super(path);
            this.path = path;
            this.isDir = isDir;
            this.hostname = hostname;
            this.port = port;
            this.client = client;
        }

        @Override
        public ObservableList<TreeItem<Path>> getChildren() {
            if (firstTime) {
                firstTime = false;
                super.getChildren().setAll(buildChildren());
            }
            return super.getChildren();
        }

        @Override
        public boolean isLeaf() {
            return !isDir;
        }

        private ObservableList<TreeItem<Path>> buildChildren() {
            try {
                ObservableList<TreeItem<Path>> children = FXCollections.observableArrayList();
                Map<Path, FilesList.FileType> files = client.executeList(path);
                for (Map.Entry<Path, FilesList.FileType> entry : files.entrySet()) {
                    children.add(new TreeItemPath(entry.getKey(), entry.getValue().equals(FilesList.FileType.FOLDER), hostname, port, client));
                }
                return children;
            } catch (IOException e) {
                e.printStackTrace();
                return FXCollections.emptyObservableList();
            }
        }
    }
}
