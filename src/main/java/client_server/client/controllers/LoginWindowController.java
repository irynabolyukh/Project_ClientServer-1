package client_server.client.controllers;

import client_server.client.GlobalContext;
import client_server.domain.packet.Message;
import client_server.domain.packet.Packet;
import client_server.domain.UserCredentials;
import com.google.common.primitives.UnsignedLong;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

import static client_server.domain.packet.Message.cTypes.LOGIN;

public class LoginWindowController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    public void processLogin() throws IOException {
//        System.out.println("Process login");
        statusLabel.setText("");

        if (loginField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            statusLabel.setText("Fill out all fields to log in.");
        } else {

            UserCredentials user = new UserCredentials(loginField.getText(), DigestUtils.md5Hex(passwordField.getText()));
            Message msg = new Message(LOGIN.ordinal(), 1, user.toJSON().toString().getBytes(StandardCharsets.UTF_8));

            Packet packet = new Packet((byte) 1, UnsignedLong.valueOf(GlobalContext.packetId++), msg);
            Packet receivedPacket = GlobalContext.clientTCP.sendPacket(packet.toPacket());

            int command = receivedPacket.getBMsq().getcType();
            Message.cTypes[] val = Message.cTypes.values();
            Message.cTypes command_type = val[command];

            boolean loggedIn = false;

            if (command_type == LOGIN) {
                String message = new String(receivedPacket.getBMsq().getMessage(), StandardCharsets.UTF_8);
                JSONObject information = new JSONObject(message);

                try {
                    JSONObject object = information.getJSONObject("object");
                    GlobalContext.role = object.getString("role");
                    loggedIn = true;
                } catch (JSONException e) {
                    statusLabel.setText(information.getString("message"));
                }

            } else {
                statusLabel.setText("Failed to log in.");
            }

            if (loggedIn) {
                FXMLLoader loader = new FXMLLoader();
                Stage stage = (Stage) loginField.getScene().getWindow();
                URL url = new File("src/main/java/client_server/client/views/products_list.fxml").toURI().toURL();
                Parent root = FXMLLoader.load(url);
                Scene scene = new Scene(root);
                stage.setScene(scene);
            }
        }
    }

    @FXML
    void initialize() {

    }

    public static void logOut(Button button) throws MalformedURLException {
        FXMLLoader loader = new FXMLLoader();
        Stage stage = (Stage) button.getScene().getWindow();
        URL url = new File("src/main/java/client_server/client/views/login_window.fxml").toURI().toURL();
        Parent root = null;
        try {
            root = FXMLLoader.load(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);
        stage.setScene(scene);

        GlobalContext.role = "";
    }

    public static void addingUser(Label statusLabel) throws MalformedURLException {
        URL url = new File("src/main/java/client_server/client/views/add_user.fxml").toURI().toURL();
        Parent root = null;
        try {
            root = FXMLLoader.load(url);
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Function is not available.");
        }
        Stage stage = new Stage();
        stage.setTitle("New User");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
