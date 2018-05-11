package tk.dcmmc;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
// import com.kitfox.svg.app.beans.SVGPanel;
import guru.nidi.graphviz.engine.Format;
import javafx.application.Application;
import javafx.embed.swing.SwingNode;
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
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.JSVGScrollPane;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

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
    private TextField matchStr;
    @FXML
    private Label dialogLabel;
    @FXML
    private Label dialogHeading;

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainController.fxml"));
            primaryStage.setTitle("Regex expression => NFA => DFA => minimized-DFA");

            Scene scene = new Scene(root, 800, 800);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);


            regStr = (TextField) scene.lookup("#regStr");
            regStr.setOnAction((e) -> root.requestFocus());

            primaryStage.show();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * show an error dialog if the textField is empty
     */
    private void emptyStringError() {
        System.err.println("Regex expression must not empty!");
        dialogHeading.setText("Error");
        dialogLabel.setText("Regex Expression String must not empty!");
        dialog.setTransitionType(JFXDialog.DialogTransition.BOTTOM);
        acceptButton.setOnAction(action -> dialog.close());
        dialog.show(root);
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
                emptyStringError();
            } else if (regStr != null) {
                Parent root1 = FXMLLoader.load(getClass().getResource("/fxml/SVGView.fxml"));
                Scene scene = new Scene(root1, 1200, 800);
                StackPane svgPane = (StackPane) scene.lookup("#svgPane");
                try {
                    graphvizDraw(reg2NFA(regStr.getText()).first,
                            "NFA.svg",
                            null, Format.SVG);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

                File file = new File("graphs"
                        + File.separator
                        + "NFA.svg");

                Scanner sc = new Scanner(file);
                String svg = sc.useDelimiter("\\Z").next();
                svg = svg.replaceAll("stroke=\"transparent\"", "");
                // svg = svg.replaceAll("font-family=\"Times,serif\"", "font-family=\"Arial\"");
                try (PrintWriter pw = new PrintWriter(file)) {
                    pw.print(svg);
                }

                SwingNode node = new SwingNode();
//                SVGPanel pane = new SVGPanel();
//                pane.setAntiAlias(true);
//                pane.setAutosize(SVGPanel.AUTOSIZE_NONE);
//                pane.setSvgURI(file.toURI());

                JSVGCanvas canvas = new JSVGCanvas();
                canvas.setURI(file.toURI().toString());
                JSVGScrollPane pane = new JSVGScrollPane(canvas);

                SwingUtilities.invokeLater(() -> node.setContent(pane));

                GesturePane gesturePane = new GesturePane(node);

                svgPane.getChildren().add(gesturePane);

                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setAlwaysOnTop(true);
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
                emptyStringError();
            } else if (regStr != null) {
                Parent root1 = FXMLLoader.load(getClass().getResource("/fxml/SVGView.fxml"));
                Scene scene = new Scene(root1, 1200, 800);
                StackPane svgPane = (StackPane) scene.lookup("#svgPane");
                try {
                    Bag<Integer> newFinals = new Bag<>();
                    graphvizDraw(NFA2DFA(reg2NFA(regStr.getText()), newFinals).first,
                            "DFA.svg", newFinals, Format.SVG);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

                File file = new File("graphs"
                        + File.separator
                        + "DFA.svg");

                Scanner sc = new Scanner(file);
                String svg = sc.useDelimiter("\\Z").next();
                svg = svg.replaceAll("stroke=\"transparent\"", "");
                // svg = svg.replaceAll("font-family=\"Times,serif\"", "font-family=\"Arial\"");
                try (PrintWriter pw = new PrintWriter(file)) {
                    pw.print(svg);
                }

//                ImageView view = new ImageView(new Image(file.toURI().toURL().toString()));
//
//                GesturePane pane = new GesturePane(view);

                SwingNode node = new SwingNode();

                JSVGCanvas canvas = new JSVGCanvas();
                canvas.setURI(file.toURI().toString());
                JSVGScrollPane pane = new JSVGScrollPane(canvas);

                SwingUtilities.invokeLater(() -> node.setContent(pane));

                GesturePane gesturePane = new GesturePane(node);

                svgPane.getChildren().add(gesturePane);

                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setAlwaysOnTop(true);
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
                emptyStringError();
            } else if (regStr != null) {
                Parent root1 = FXMLLoader.load(getClass().getResource("/fxml/SVGView.fxml"));
                Scene scene = new Scene(root1, 1200, 800);
                StackPane svgPane = (StackPane) scene.lookup("#svgPane");
                try {
                    Bag<Integer> newFinals = new Bag<>();
                    graphvizDraw(NFA2DFA(reg2NFA(regStr.getText()), newFinals).first,
                            "DFA.svg", newFinals, Format.SVG);
                    Pointer<Bag<Integer>> ptFinals = new Pointer<>(newFinals);
                    graphvizDraw(minimizeDFA(NFA2DFA(reg2NFA(regStr.getText()),
                            newFinals), ptFinals).first, "minimized-DFA.svg", ptFinals.item,
                            Format.SVG);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

                File file = new File("graphs"
                        + File.separator
                        + "minimized-DFA.svg");

                Scanner sc = new Scanner(file);
                String svg = sc.useDelimiter("\\Z").next();
                svg = svg.replaceAll("stroke=\"transparent\"", "");
                // svg = svg.replaceAll("font-family=\"Times,serif\"", "font-family=\"Arial\"");
                try (PrintWriter pw = new PrintWriter(file)) {
                    pw.print(svg);
                }

//
//                ImageView view = new ImageView(new Image(file.toURI().toURL().toString()));
//
//                GesturePane pane = new GesturePane(view);

                SwingNode node = new SwingNode();

                JSVGCanvas canvas = new JSVGCanvas();
                canvas.setURI(file.toURI().toString());
                JSVGScrollPane pane = new JSVGScrollPane(canvas);

                SwingUtilities.invokeLater(() -> node.setContent(pane));

                GesturePane gesturePane = new GesturePane(node);

                svgPane.getChildren().add(gesturePane);

                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setAlwaysOnTop(true);
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

        if (regStr.getText().isEmpty() || matchStr.getText().isEmpty()) {
            System.err.println("regex String and match String must not empty!");
            dialogLabel.setText("regex String and match String must not empty!");
            dialog.setTransitionType(JFXDialog.DialogTransition.BOTTOM);
            acceptButton.setOnAction(action -> dialog.close());
            dialog.show(root);
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
