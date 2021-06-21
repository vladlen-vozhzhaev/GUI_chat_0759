package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class Controller {
    DataOutputStream out;
    @FXML
    TextField textField;
    @FXML
    TextArea textArea;
    @FXML
    Button connectBtn;
    @FXML
    TextArea userListTextArea;
    @FXML
    private void send(){
        try {
            String text = textField.getText();
            textField.clear();
            textField.requestFocus();
            textArea.appendText(text+"\n");
            out.writeUTF(text);
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }
    @FXML
    private void connect(){
        try {
            connectBtn.setDisable(true);
            Socket socket = new Socket("localhost",8188); // Создаём сокет, для подключения к серверу
            out = new DataOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Thread thread = new Thread(new Runnable() { // Создаём поток, для приёма сообщений от сервера
                @Override
                public void run() {
                    String response = null;
                    while (true){
                        try {
                            response = in.readObject().toString(); // Принимаем сообщение от сервера
                            if(response.startsWith("**userList**")){
                                String[] usersName = response.split("//"); //**userList**//user1//user2//user3
                                userListTextArea.setText("");
                                for (String name: usersName){
                                    userListTextArea.appendText(name+"\n");
                                }
                            }else textArea.appendText(response+"\n"); //Печатаем на консоль принятое сообщение от сервера
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}