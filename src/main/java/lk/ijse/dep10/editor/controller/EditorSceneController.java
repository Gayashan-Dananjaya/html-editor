package lk.ijse.dep10.editor.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.web.HTMLEditor;
import javafx.stage.*;
import org.jsoup.nodes.Document;

import java.io.*;
import java.util.Optional;

public class EditorSceneController {


    public HTMLEditor txtEditor;

    private String savedContent = "<html dir=\"ltr\"><head></head><body contenteditable=\"true\"></body></html>";
    private boolean isSaved = false;
    private boolean isSaveAs = false;
    private boolean isFileSet = false;
    private File file;
    private Stage stage;
    WindowEvent windowEvent;

    public void initData(Stage stage) {
        this.stage = stage;
        stage.setOnCloseRequest(windowEvent -> {
            this.windowEvent = windowEvent;
            try {
                mnCloseOnAction(new ActionEvent());
            } catch (IOException e) {
                System.out.println("Error");
            }
        });
    }

    @FXML
    void mnAboutOnAction(ActionEvent event) throws IOException {
        Stage stageAbout = new Stage();

        stageAbout.setScene(new Scene(new FXMLLoader(this.getClass().getResource("/view/AboutScene.fxml")).load()));
        stageAbout.initModality(Modality.WINDOW_MODAL);
        stageAbout.initOwner(stage.getScene().getWindow());
        stageAbout.setTitle("About HTML Editor");
        stageAbout.show();
        stageAbout.centerOnScreen();
        stageAbout.setResizable(false);
    }

    @FXML
    void mnCloseOnAction(ActionEvent event) throws IOException {
        if (!isSaved) {
            if (txtEditor.getHtmlText().equals("<html dir=\"ltr\"><head></head><body contenteditable=\"true\"></body></html>")) {
                stage.close();
                return;
            }
            Alert alert = new Alert(Alert.AlertType.WARNING, "Do you want to save file before closing?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            alert.setTitle("Warning : Close without saving");
            Optional<ButtonType> buttonType = alert.showAndWait();
            if (buttonType.isEmpty()) {
                return;
            } else if (buttonType.get() == ButtonType.YES) {
                mnSaveOnAction(new ActionEvent());
                if (file != null) stage.close();
                else if (windowEvent != null) {
                    windowEvent.consume();
                }
            } else if (buttonType.get() == ButtonType.NO) {
                stage.close();
            } else if (buttonType.get() == ButtonType.CANCEL){
                if (windowEvent != null) {
                    windowEvent.consume();
                }
            }
        } else {
            stage.close();
        }
    }

    @FXML
    void mnNewOnAction(ActionEvent event) throws IOException {
        if (savedContent.equals(txtEditor.getHtmlText())) {
            newMethod();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Do you want to save the current file, Before opening a new file?", ButtonType.NO, ButtonType.YES, ButtonType.CANCEL);
            alert.setTitle("Warning: unsaved file");
            Optional<ButtonType> buttonType = alert.showAndWait();
            if (buttonType.isEmpty()) {
                return;
            } else if (buttonType.get() == ButtonType.NO) {
                newMethod();
            } else if (buttonType.get() == ButtonType.YES) {
                mnSaveOnAction(new ActionEvent());
                if (file != null) {
                    newMethod();
                }
            } else if (buttonType.get() == ButtonType.CANCEL) {
                return;
            } else {
                System.out.println("Check the else method in mnNewOnAction");
            }
        }
    }

    @FXML
    void mnOpenOnAction(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open a text file");
        file = fileChooser.showOpenDialog(txtEditor.getScene().getWindow());

        if (file == null) return;


        FileInputStream fis = new FileInputStream(file);
        byte[] bytes = fis.readAllBytes();
        fis.close();

        txtEditor.setHtmlText(new String(bytes));

        isSaved = true;
        isFileSet = true;
        Stage stage = (Stage) txtEditor.getScene().getWindow();
        stage.setTitle(file.getName());
    }

    @FXML
    void mnPrintOnAction(ActionEvent event) throws IOException {
    }

    @FXML
    void mnSaveOnAction(ActionEvent event) throws IOException {
        if (!isFileSet) mnSaveAsOnAction(new ActionEvent());
        else saveMethod();
    }

    public void mnSaveAsOnAction(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save a text file");
        file = null;
        file = fileChooser.showSaveDialog(txtEditor.getScene().getWindow());
        if (file != null) {
            String name = file.getAbsolutePath();
            if (!name.endsWith(".html")) {
                name += ".html";
                file = new File(name);
            }
            isFileSet = true;
            saveMethod();
        }
    }

    public void rootOnDragOver(DragEvent dragEvent) {
        dragEvent.acceptTransferModes(TransferMode.ANY);
    }

    public void rootOnDragDropped(DragEvent dragEvent) throws IOException {
        file = dragEvent.getDragboard().getFiles().get(0);

        FileInputStream fis = new FileInputStream(file);

        byte[] bytes = fis.readAllBytes();

        fis.close();

        txtEditor.setHtmlText(new String(bytes));

        isFileSet = true;
        isSaved = true;
        isSaveAs = true;
        savedContent = txtEditor.getHtmlText();
        stage.setTitle(file.getName());
        setStageTitle();
    }

    public void txtEditorOnKeyReleased(KeyEvent keyEvent) {
        setStageTitle();
    }

    public void txtEditorOnMouseReleased(MouseEvent mouseEvent) {
        setStageTitle();
    }

    private void setStageTitle() {
        if (savedContent.equals(txtEditor.getHtmlText())) {
            isSaved = true;
            if (stage.getTitle().charAt(0) == '*') {
                stage.setTitle(stage.getTitle().substring(1));
            }
        } else {
            if (stage.getTitle().charAt(0) == '*') return;
            stage.setTitle("*" + stage.getTitle());
            isSaved = false;
        }
    }

    private void saveMethod() throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        byte[] bytes = txtEditor.getHtmlText().getBytes();
        fileOutputStream.write(bytes);
        fileOutputStream.close();

        isSaved = true;
        savedContent = txtEditor.getHtmlText();

        stage.setTitle(file.getName());
    }

    private void newMethod() {
        txtEditor.setHtmlText("");
        savedContent = txtEditor.getHtmlText();
        file = null;
        isSaved = false;
        isSaveAs = false;
        isFileSet = false;

        stage.setTitle("Untitled Document");
    }
}
