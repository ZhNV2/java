package ru.spbau.zhidkov.client;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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

    /** Starts UI */
    @Override
    public void start(Stage stage) throws IOException {
        StackPane root = new StackPane();
        Button button = new Button("start");
        button.setOnAction(event -> {
            drawTree(stage);
        });
        vBox.getChildren().addAll(hostField, portField, button);
        root.getChildren().add(vBox);
        stage.setScene(new Scene(root, 300, 250));
        stage.show();
    }

    private void drawTree(Stage stage) {
        TreeItem<Path> root1 = new TreeItemPath(Paths.get("."), true, hostField.getText(),
                Integer.valueOf(portField.getText()));
        TreeView treeView = new TreeView<>(root1);
        treeView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                TreeItemPath item = (TreeItemPath) treeView.getSelectionModel().getSelectedItem();
                if (item != null && !item.isDir()) {
                    File file = fileChooser.showOpenDialog(stage);
                    if (file != null) {
                        Client client = Client.buildClient(hostField.getText(), Integer.valueOf(portField.getText()),
                                Paths.get(System.getProperty("user.dir")));
                        try {
                            client.connect();
                            client.executeGet(item.getPath(), file.toPath());
                            client.disconnect();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        vBox.getChildren().add(treeView);
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
        private boolean firstTime = true;

        public TreeItemPath(Path path, boolean isDir, String hostname, int port) {
            super(path);
            this.path = path;
            this.isDir = isDir;
            this.hostname = hostname;
            this.port = port;

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
                Client client = Client.buildClient(hostname, port, Paths.get(System.getProperty("user.dir")));
                client.connect();
                Map<Path, Boolean> files = client.executeList(path);
                client.disconnect();
                for (Map.Entry<Path, Boolean> entry : files.entrySet()) {
                    children.add(new TreeItemPath(entry.getKey(), entry.getValue(), hostname, port));
                }
                return children;

            } catch (IOException e) {
                e.printStackTrace();
                return FXCollections.emptyObservableList();
            }
        }
    }
}
