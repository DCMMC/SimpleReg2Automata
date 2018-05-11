package tk.dcmmc;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import net.kurobako.gesturefx.GesturePane;
import java.io.File;
import java.io.IOException;

import static tk.dcmmc.Reg2Automata.*;

/**
 * Reg2Automata GUI application
 *
 * @since 1.9
 * @author DCMMC
 */
public class Reg2AutomataGUI extends Application {
    @FXML
    private StackPane root;
    @FXML
    private TextField regStr;
    @FXML
    private JFXDialog dialog;
    @FXML
    private JFXButton acceptButton;
    @FXML
    private JFXButton NFA;
    @FXML
    private JFXButton DFA;
    @FXML
    private JFXButton miniDFA;
    @FXML
    private TextField matchStr;
    @FXML
    private JFXButton match;
    @FXML
    private Label dialogLabel;
    @FXML
    private Label dialogHeading;

    private StackPane svgPane;

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainController.fxml"));
            primaryStage.setTitle("Regex expression => NFA => DFA => minimized-DFA");

            primaryStage.setScene(new Scene(root, 800, 800));
            primaryStage.setResizable(false);

            primaryStage.show();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * show the result svg
     * @param event
     *      click event
     */
    @FXML
    public void showNFA(ActionEvent event) {
        try {
            if(regStr != null && regStr.getText().isEmpty()) {
                System.err.println("Regex expression must not empty!");
                dialogLabel.setText("Regex Expression String must not empty!");
                dialogHeading.setText("Error");
                dialog.setTransitionType(JFXDialog.DialogTransition.BOTTOM);
                acceptButton.setOnAction(action -> dialog.close());
                dialog.show(root);
            } else if (regStr != null) {
                Parent root1 = FXMLLoader.load(getClass().getResource("/fxml/SVGView.fxml"));
                Scene scene = new Scene(root1, 1200, 800);
                StackPane svgPane = (StackPane) scene.lookup("#svgPane");
                try {
                    graphvizDraw(reg2NFA(regStr.getText()).first,
                            "NFA.png",
                            null);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

                File file = new File("graphs"
                        + File.separator
                        + "NFA.png");


                ImageView view = new ImageView(new Image(file.toURI().toURL().toString()));

                GesturePane pane = new GesturePane(view);

                svgPane.getChildren().add(pane);

                Stage stage = new Stage();
                stage.setScene(scene);
                stage.show();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @FXML
    public void showDFA(ActionEvent event) {
        try {
            if(regStr != null && regStr.getText().isEmpty()) {
                System.err.println("Regex expression must not empty!");
                dialogHeading.setText("Error");
                dialogLabel.setText("Regex Expression String must not empty!");
                dialog.setTransitionType(JFXDialog.DialogTransition.BOTTOM);
                acceptButton.setOnAction(action -> dialog.close());
                dialog.show(root);
            } else if (regStr != null) {
                Parent root1 = FXMLLoader.load(getClass().getResource("/fxml/SVGView.fxml"));
                Scene scene = new Scene(root1, 1200, 800);
                StackPane svgPane = (StackPane) scene.lookup("#svgPane");
                try {
                    Bag<Integer> newFinals = new Bag<>();
                    graphvizDraw(NFA2DFA(reg2NFA(regStr.getText()), newFinals).first,
                            "DFA.png", newFinals);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

                File file = new File("graphs"
                        + File.separator
                        + "DFA.png");

                ImageView view = new ImageView(new Image(file.toURI().toURL().toString()));

                GesturePane pane = new GesturePane(view);

                svgPane.getChildren().add(pane);

                Stage stage = new Stage();
                stage.setScene(scene);
                stage.show();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @FXML
    public void showMiniDFA(ActionEvent event) {
        try {
            if(regStr != null && regStr.getText().isEmpty()) {
                System.err.println("Regex expression must not empty!");
                dialogHeading.setText("Error");
                dialogLabel.setText("Regex Expression String must not empty!");
                dialog.setTransitionType(JFXDialog.DialogTransition.BOTTOM);
                acceptButton.setOnAction(action -> dialog.close());
                dialog.show(root);
            } else if (regStr != null) {
                Parent root1 = FXMLLoader.load(getClass().getResource("/fxml/SVGView.fxml"));
                Scene scene = new Scene(root1, 1200, 800);
                StackPane svgPane = (StackPane) scene.lookup("#svgPane");
                try {
                    Bag<Integer> newFinals = new Bag<>();
                    graphvizDraw(NFA2DFA(reg2NFA(regStr.getText()), newFinals).first,
                            "DFA.png", newFinals);
                    Pointer<Bag<Integer>> ptFinals = new Pointer<>(newFinals);
                    graphvizDraw(minimizeDFA(NFA2DFA(reg2NFA(regStr.getText()),
                            newFinals), ptFinals).first, "minimized-DFA.png", ptFinals.item);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

                File file = new File("graphs"
                        + File.separator
                        + "minimized-DFA.png");


                ImageView view = new ImageView(new Image(file.toURI().toURL().toString()));

                GesturePane pane = new GesturePane(view);

                svgPane.getChildren().add(pane);

                Stage stage = new Stage();
                stage.setScene(scene);
                stage.show();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * simulate DFA or NFA
     * @param event
     *      click event
     */
    @FXML
    public void match(ActionEvent event) {
        dialogHeading.setText("Match Result");

        if (regStr.getText().isEmpty() || match.getText().isEmpty()) {
            System.err.println("regex String and match String must not empty!");
            dialogLabel.setText("regex String and match String must not empty!");
            dialog.setTransitionType(JFXDialog.DialogTransition.BOTTOM);
            acceptButton.setOnAction(action -> dialog.close());
            dialog.show(root);
            dialog.show();
        } else {
            dialog.setTransitionType(JFXDialog.DialogTransition.BOTTOM);
            acceptButton.setOnAction(action -> dialog.close());
            dialog.show(root);
            dialogLabel.setText(Reg2Automata.simulateDFA(matchStr.getText(), regStr.getText()).toString());
        }
    }

    /**
     * GUI entry
     * @param args
     *          no args
     */
    public static void main(String[] args) {
        launch(args);
    }
}///~
