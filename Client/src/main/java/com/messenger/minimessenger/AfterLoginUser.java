package com.messenger.minimessenger;

import CustomExceptions.UserNotFoundException;
import Data.ChatInfo;
import Data.Message;
import Roles.User;
import Utils.CompareChats;
import Utils.Encrypter;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.UploadErrorException;
import com.dropbox.core.v2.users.FullAccount;
import com.messenger.minimessenger.Threads.MessagesDownloader;
import com.messenger.minimessenger.Threads.MessagesTracker;
import com.messenger.minimessenger.chat_control.ChatSelectionController;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
//import com.dropbox.*;
import Data.Message.*;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.apache.commons.io.FilenameUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ResourceBundle;

import java.io.*;
import java.net.URL;
import java.util.*;
//import

public class AfterLoginUser implements Initializable {

    private static final String ACCESS_TOKEN = "Q1HPFqlwtYQAAAAAAAAAAd0WR4TfAgL8B1omCIYH2swUcSZfi066dsWf0Fn0JXE3";

    @FXML
    private ListView<Node> chatChoser;
    private ArrayList<Integer> chatIDs = new ArrayList<Integer>();

    @FXML
    private GridPane chatPane;


    @FXML
    private Button addButton;
    @FXML
    private ScrollPane chatScroll;
    @FXML
    private Button testButton;
    @FXML
    Button addChatButton;
    @FXML
    TextArea newMessageField;
    @FXML
    Button sendMessageButton;
    @FXML
    Button selectFileButton;
    @FXML
    Label selectedFileName;

    @FXML
    private ImageView btn_user, btn_message, btn_userBack;
    ;
    @FXML
    private AnchorPane h_user, h_message;
    @FXML
    private ImageView btn_user_in;

    @FXML
    private AnchorPane ListViewBlackList;
    @FXML
    private AnchorPane Stone;
    @FXML
    private Button Btn_BlackListOpen;
    @FXML
    private Button btn_close_blackList;
    @FXML
    ListView blackList_listView;
    @FXML
    private Button minButton;
    @FXML
    private Button maxButton;

    @FXML
    private Button closeButton;

    @FXML
    private AnchorPane myPane;
    Stage stage = null;
    @FXML
    private TextField searchMessageField;


    @FXML private Button Btn_question;
    @FXML private AnchorPane FAQ;
    @FXML private ImageView CloseFAQ;
    @FXML private Button chatInfoButton;



    @FXML
    private void handleMinButtonAction(ActionEvent event) {
        stage = (Stage) myPane.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void handleMaxButtonAction(ActionEvent event) {
        stage = (Stage) myPane.getScene().getWindow();
        if (stage.isMaximized())
            stage.setMaximized(false);
        else
            stage.setMaximized(true);
    }

    @FXML
    private void handleCloseButtonAction(ActionEvent event) {
        stage = (Stage) myPane.getScene().getWindow();
        stage.close();
    }
   /* @FXML private  void handleButtonAction(MouseEvent event){
        if(event.getTarget()==btn_user){
            h_user.setVisible(true);
            h_message.setVisible(false);
        }
        else if(event.getTarget()==btn_message){
            h_message.setVisible(true);
            h_user.setVisible(false);
        }
    }*/


    //@FXML Button startChatButton;
    //@FXML TextField usernameText;
    //@FXML Label addChatStatus;
    //@FXML DialogPane addChatPane;

    volatile ArrayList<ChatInfo> chats = new ArrayList<ChatInfo>();
    User user;
    ArrayList<TextFlow> textBubbles = new ArrayList<TextFlow>();
    public static volatile int selectedChatIndex = -3;
    public static volatile boolean isNeedToShowMessages = true;
    static DbxClientV2 client;
    File file = null;
    static public volatile ArrayList<Integer> indexesOfChatsWithoutKeys = new ArrayList<>();
    public static volatile ArrayList<String> usersIamInBlacklist = new ArrayList<>();

    private int numberOfRows = 0;


    @Override
    public void initialize (URL url, ResourceBundle resourceBundle) {


        chatInfoButton.setVisible(false);
        Image CloseImg;
        CloseImg = new Image(getClass().getClassLoader().getResourceAsStream("CloseWindow.png"), 20, 20, false, false);
        closeButton.setGraphic((new ImageView(CloseImg)));

        Image expendImg;
        expendImg = new Image(getClass().getClassLoader().getResourceAsStream("expend.png"), 20, 20, false, false);
        maxButton.setGraphic((new ImageView(expendImg)));

        Image MinimizeWindowImg;
        MinimizeWindowImg = new Image(getClass().getClassLoader().getResourceAsStream("MinimizeWindow.png"), 20, 20, false, false);
        minButton.setGraphic((new ImageView(MinimizeWindowImg)));

        Image ClipImg;
        ClipImg=new Image(getClass().getClassLoader().getResourceAsStream("Clip.png"),20,20,false,false);
        selectFileButton.setGraphic((new ImageView(ClipImg)));

        Image SendImg;
        SendImg=new Image(getClass().getClassLoader().getResourceAsStream("Send.png"),20,20,false,false);
        sendMessageButton.setGraphic((new ImageView(SendImg)));

        Image questionImg;
        questionImg=new Image(getClass().getClassLoader().getResourceAsStream("question.png"),25,25,false,false);
        Btn_question.setGraphic((new ImageView(questionImg)));

        Image chatInfoImg;
        chatInfoImg = new Image(getClass().getClassLoader().getResourceAsStream("info_icon.png"),25,25,false,false);
        chatInfoButton.setGraphic((new ImageView(chatInfoImg)));


        chatScroll.setFitToHeight(true);
        chatScroll.setFitToWidth(true);
        // Open menu user
        h_user.setTranslateX(-233);
        btn_user.setOnMouseClicked(event -> {
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.4));
            slide.setNode(h_user);

            slide.setToX(0);
            slide.play();

            h_user.setTranslateX(-233);
            slide.setOnFinished((ActionEvent e) -> {
                btn_user.setVisible(true);
                btn_userBack.setVisible(true);
            });
            TranslateTransition slide5 = new TranslateTransition();
            slide5.setDuration(Duration.seconds(0.4));
            slide5.setNode(FAQ);

            slide5.setToX(600);
            slide5.play();

            FAQ.setTranslateX(0);
            slide5.setOnFinished((ActionEvent e) -> {
                Btn_question.setVisible(true);
                CloseFAQ.setVisible(false);
            });
        });

        btn_userBack.setOnMouseClicked(event -> {
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.4));
            slide.setNode(h_user);

            slide.setToX(-600);
            slide.play();

            h_user.setTranslateX(0);
            slide.setOnFinished((ActionEvent e) -> {
                btn_user.setVisible(true);
                btn_userBack.setVisible(false);

            });
            TranslateTransition slide2 = new TranslateTransition();
            slide2.setDuration(Duration.seconds(0.4));
            slide2.setNode(ListViewBlackList);

            slide2.setToX(-600);
            slide2.play();

            ListViewBlackList.setTranslateX(0);
            slide2.setOnFinished((ActionEvent e) -> {
                Btn_BlackListOpen.setVisible(true);
                btn_close_blackList.setVisible(false);
            });
        });


        // Open BlackList
        ListViewBlackList.setTranslateX(-480);
        Btn_BlackListOpen.setOnMouseClicked(event -> {

            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.4));
            slide.setNode(ListViewBlackList);
            slide.setToX(0);
            slide.play();


            ListViewBlackList.setTranslateX(-240);
            slide.setOnFinished((ActionEvent e) -> {
                Btn_BlackListOpen.setVisible(true);
                btn_close_blackList.setVisible(true);

            });
        });
        btn_close_blackList.setOnMouseClicked(event -> {
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.4));
            slide.setNode(ListViewBlackList);

            slide.setToX(-600);
            slide.play();

            ListViewBlackList.setTranslateX(0);
            slide.setOnFinished((ActionEvent e) -> {
                Btn_BlackListOpen.setVisible(true);
                btn_close_blackList.setVisible(false);
            });
        });


        // Open FAQ user
        FAQ.setTranslateX(480);
        Btn_question.setOnMouseClicked(event -> {
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.4));
            slide.setNode(FAQ);
            slide.setToX(0);
            slide.play();


            FAQ.setTranslateX(240);
            slide.setOnFinished((ActionEvent e) -> {
                Btn_question.setVisible(true);
                CloseFAQ.setVisible(true);
            });
            TranslateTransition slide3 = new TranslateTransition();
            slide3.setDuration(Duration.seconds(0.4));
            slide3.setNode(h_user);

            slide3.setToX(-600);
            slide3.play();

            h_user.setTranslateX(0);
            slide3.setOnFinished((ActionEvent e) -> {
                btn_user.setVisible(true);
                btn_userBack.setVisible(false);

            });
            TranslateTransition slide4 = new TranslateTransition();
            slide4.setDuration(Duration.seconds(0.4));
            slide4.setNode(ListViewBlackList);

            slide4.setToX(-600);
            slide4.play();

            ListViewBlackList.setTranslateX(0);
            slide4.setOnFinished((ActionEvent e) -> {
                Btn_BlackListOpen.setVisible(true);
                btn_close_blackList.setVisible(false);
            });

        });
        CloseFAQ.setOnMouseClicked(event -> {
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.4));
            slide.setNode(FAQ);

            slide.setToX(600);
            slide.play();

            FAQ.setTranslateX(0);
            slide.setOnFinished((ActionEvent e) -> {
                Btn_question.setVisible(true);
                CloseFAQ.setVisible(false);
            });
        });


        newMessageField.setVisible(false);
        selectFileButton.setVisible(false);
        sendMessageButton.setVisible(false);

        try {
            client = connectToDropbox();
            FullAccount account = client.users().getCurrentAccount();
            System.out.println(account.getName().getDisplayName());
        } catch (DbxException e) {
            e.printStackTrace();
            return;
        }

        user = Main.user;

        if (!user.getPhotoName().isEmpty()) {
            System.out.println("in get photo name");
            Image image = null;
            File fileCheck = new File("C:\\MiniMessenger\\ChatProfilePhotos\\" + user.getPhotoName());
            if (fileCheck.exists())
            {
                try
                {
                    InputStream is = new FileInputStream("C:\\MiniMessenger\\ChatProfilePhotos\\" + user.getPhotoName());
                    image = new Image(is);
                } catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }
            }
            else
                image = getImageOfUserByUsername(user.getUsername());

            btn_user.setImage(image);
            btn_user_in.setImage(image);
            btn_user.setSmooth(true);
        }

        //GridPane gridPane = new GridPane()
        //System.out.println(chatPane.getColumnCount());
        //showMessages;
        //chatPane.getChildren().clear();
        //System.out.println(chatPane.getColumnCount());
        //if (true)
        //    return;
        System.out.println("in initialize");
        /*try
        {
            chats = JavaPostgreSql.getChats(user.getName());
        }
        catch (UserNotFoundException e)
        {
            e.printStackTrace();
            return;
        }

        for(int i=0; i < chats.size();i++)
        {
            chatChoser.getItems().add(chats.get(i).getUsername());
        }*/


        /*for (int i = 0; i < 20; i++)
        {
            if (i % 2 == 0)
                chats.add(new ChatInfo("V", i, 4));
            else
                chats.add(new ChatInfo("T", i, 4));
        }

        try
        {
            ChatSelectionController.initializeChats(chatChoser, chats);
        } catch (UserNotFoundException e)
        {
            e.printStackTrace();
        }*/

        try {
            chats = JavaPostgreSql.getChats(user.getUsername());
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }

        for (int i=0; i < chats.size();i++)
        {
            if (isGroupChat(chats.get(i)))
                chats.get(i).setGroupChat(true);
        }


        Encrypter.getKeysOfChats(chats);
        for (int i = 0; i < chats.size(); i++) {
            if (chats.get(i).getKey() == null)
                if (!JavaPostgreSql.getKeyForChat(chats.get(i))) {
                    JavaPostgreSql.getAllUsersInChat(chats.get(i));

                    chats.get(i).setMessages(new ArrayList<Message>());
                    JavaPostgreSql.requestKey(chats.get(i));
                    indexesOfChatsWithoutKeys.add(i);
                }
        }

        System.out.println("got keys");

        for (int i = 0; i < chats.size(); i++)
            setPhotoOfChat(chats.get(i));

        System.out.println("Amount of chats: " + chats.size());
        chats.sort(new CompareChats());

//        for (int i = 0; i < chats.size(); i++)
//        {
//            System.out.println(chats.get(i).getName() + " : " + chats.get(i).getTimeOfLastMessage());
//        };

        ChatSelectionController.initializeChats(chatChoser, chats);
        setBlackList();


        MessagesTracker.isProgrammRun = true;
        MessagesTracker.isConnectionFree = true;

        MessagesTracker messagesTracker = new MessagesTracker(chatChoser, chatPane, chats, chatScroll);
        messagesTracker.start();


        chatChoser.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Node>() {
            @Override
            public void changed(ObservableValue<? extends Node> observableValue, Node node, Node t1) {
                //Label selectedChat = (Label) chatChoser.getSelectionModel().getSelectedItem();
                if (selectedChatIndex != -1) {
                    selectedChatIndex = chatChoser.getSelectionModel().getSelectedIndex();
                    System.out.println("SELECTED INDEX IN CHAT CHOSER: " + selectedChatIndex);

                    if (chats.get(selectedChatIndex).getMessages().size() == 0
                            && chats.get(selectedChatIndex).getKey() != null) {
                        int chatIndex = chats.get(selectedChatIndex).getChatIndex();
                        if (chats.get(selectedChatIndex).getUsers().size() == 0)
                            JavaPostgreSql.getAllUsersInChat(chats.get(selectedChatIndex));

                        ArrayList<Message> userMessages = JavaPostgreSql.getMessages(chatIndex);
                        chats.get(selectedChatIndex).setMessages(userMessages);

                        for (int i = 0; i < chats.get(selectedChatIndex).getMessages().size(); i++) {
                            Encrypter.decryptMessage(chats.get(selectedChatIndex).getMessages().get(i),
                                    chats.get(selectedChatIndex).getKey());
                        }

//                for (int i = 0; i < userMessages.size(); i++)
//                {
//                    Message msg = userMessages.get(i);
//                    System.out.println(msg.getText()+" : " + msg.getSenderUsername());
//                }
                    }
                    System.out.println("Amount of messages: " + chats.get(selectedChatIndex).getMessages().size());
                    if (isNeedToShowMessages) {
                        showMessages(chats.get(selectedChatIndex).getMessages());
                        newMessageField.setVisible(true);
                        selectFileButton.setVisible(true);
                        sendMessageButton.setVisible(true);
                    }
                    if (chats.get(selectedChatIndex).getKey() == null
                            && !chats.get(selectedChatIndex).isIfNewChat()) {
                        newMessageField.setVisible(false);
                        selectFileButton.setVisible(false);
                        sendMessageButton.setVisible(false);
                    }

                    if (!chats.get(selectedChatIndex).isIfEnabledChat()) {
                        newMessageField.setVisible(false);
                        selectFileButton.setVisible(false);
                        sendMessageButton.setVisible(false);
                    }

                    if (chats.get(selectedChatIndex).isGroupChat())
                        chatInfoButton.setVisible(true);
                    else
                        chatInfoButton.setVisible(false);
                } else
                    System.out.println("-1");
            }
        });
    }



    /*
    @FXML
    private void logOut(ActionEvent event) throws IOException {
        Main m = new Main();
        m.changeScene("hello-view.fxml");
    }
    */

    @FXML
    void addToGrid() throws IOException, InterruptedException {


    }

    @FXML
    void addToGrid1s() throws IOException, InterruptedException {
        addToGrid();
        /*Label[] test = new Label[20];
        for (int i=0; i < 20;i++)
        {
            test[i] = new Label("XU");
        }
        for (int i=1; i < 20;i++)
            chatPane.addRow(i, test[i-1]);
        chatScroll.setVvalue(1.0);*/

    }

    @FXML
    void test() {
        Label[] test = new Label[20];
        for (int i = 0; i < 20; i++) {
            test[i] = new Label("XU");
        }
        for (int i = 1; i < 20; i++)
            chatPane.addRow(i, test[i - 1]);
        chatScroll.setVvalue(1);
    }


    @FXML
    void addChat() throws IOException {
        Stage dialogWindow = new Stage();
        dialogWindow.setTitle("Adding chat");
        dialogWindow.initModality(Modality.APPLICATION_MODAL);
        dialogWindow.setMinHeight(200);
        dialogWindow.setMinWidth(200);
        dialogWindow.setMaxHeight(500);
        dialogWindow.setMaxWidth(350);

        GridPane gridPane = new GridPane();

        VBox dialog = new VBox(10);
        ListView<String> addedUsers = new ListView<>();
        addedUsers.setPrefHeight(200);
        addedUsers.setMaxHeight(300);
        addedUsers.setMaxWidth(250);
        addedUsers.setDisable(true);
        ArrayList<User> usersToAdd = new ArrayList<>();
        usersToAdd.add(user);

        TextField chatName = new TextField();// = new TextField("Enter chat name");
        chatName.setPromptText("Enter chat name");
        chatName.setDisable(true);

        TextField usernameField = new TextField();
        Label enter = new Label("Enter user' usernames to start chat with");
        Label addChatStatus = new Label("");
        addChatStatus.setTextFill(Color.RED);
        Button startNewChat = new Button("Start new chat");
        startNewChat.setDisable(true);

        ImageView chatImage = new ImageView();
        chatImage.setImage(new Image("group_chat_nt.png"));
        chatImage.setDisable(true);
        chatImage.setVisible(false);
        chatImage.setFitHeight(50);
        chatImage.setFitWidth(50);

        final File[] file1 = new File[1];
        file1[0] = null;

        Label nameOfPhoto = new Label("");

        startNewChat.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                addChatStatus.setText("");
                if (usersToAdd.size() > 2 && chatName.getText().isBlank()) {
                    addChatStatus.setText("Enter chat name");
                    return;
                }

                if (usersToAdd.size() == 2) {
                    for (int i = 0; i < chats.size(); i++) {
                        if (chats.get(i).isGroupChat())
                            continue;

                        boolean[] areUsersInChat = {false, false};

                        if (chats.get(i).getUsers() == null)
                            JavaPostgreSql.getAllUsersInChat(chats.get(i));

                        for (int j = 0; j < chats.get(i).getUsers().size(); j++) {
                            if (usersToAdd.get(j).getUsername()
                                    .equals(chats.get(i).getUsers().get(j).getUsername()))
                                areUsersInChat[j] = true;
                        }
                        if (areUsersInChat[0] && areUsersInChat[1]) {
                            addChatStatus.setText("Chat already exists");
                            return;
                        }
                    }

                    for (int i = 0; i < user.getUserBlackList().size(); i++)
                        if (usersToAdd.get(1).getUsername().equals(user.getUserBlackList().get(i).getUsername())) {
                            addChatStatus.setText("User in black list");
                            return;
                        }

                    for (int i = 0; i < usersIamInBlacklist.size(); i++) {
                        if (usersToAdd.get(1).getUsername().equals(usersIamInBlacklist.get(i))) {
                            addChatStatus.setText("You' re in " + usersToAdd.get(1).getUsername() + "'s black list");
                            return;
                        }
                    }


                }

                Date date = new Date();
                String nameOfChat = null;
                if (usersToAdd.size() > 2)
                    nameOfChat = chatName.getText();
                else
                    nameOfChat = usersToAdd.get(1).getUsername() + "\n" + user.getUsername();

                ChatInfo chatToAdd = new ChatInfo(nameOfChat, date.getTime(), usersToAdd.size(), 0);
                if (usersToAdd.size() > 2)
                    chatToAdd.setGroupChat(true);
                chatToAdd.setUsers(usersToAdd);

                chatToAdd.setIfNewChat(true);
                if (file1[0] != null) {
                    ImageView iv = new ImageView(chatImage.getImage());
                    chatToAdd.setChatImage(iv);
                    chatToAdd.setChatPhotoName(file1[0].getName());
                    chatToAdd.setChatPhotoPath(file1[0].getAbsolutePath());
                } else
                    setPhotoOfChat(chatToAdd);

                ChatSelectionController.addNewChat(chatChoser, chatToAdd);
                selectedChatIndex++;
                chats.add(chatToAdd);
                chats.sort(new CompareChats());
                dialogWindow.close();
            }
        });


        Button addUserButton = new Button("Add user to chat");
        addUserButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                addChatStatus.setText("");
                String usernameText = usernameField.getText();
                if (usernameText.isEmpty()) {
                    addChatStatus.setText("Enter username");
                    return;
                }


                if (!JavaPostgreSql.isUserExists(usernameText)) {
                    addChatStatus.setText("User wasn't found");
                    return;
                }

                for (int i = 0; i < usersToAdd.size(); i++)
                    if (usernameText.equals(usersToAdd.get(i).getUsername())) {
                        addChatStatus.setText("User already in list");
                        return;
                    }

                addedUsers.getItems().add(usernameText);
                usersToAdd.add(JavaPostgreSql.getUserByUsername(usernameText));

                if (usersToAdd.size() == 2)
                    startNewChat.setDisable(false);

                if (usersToAdd.size() == 3) {
                    chatName.setDisable(false);
                    chatImage.setVisible(true);
                    chatImage.setDisable(false);
                }
                usernameField.clear();
            }
        });

        chatImage.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                nameOfPhoto.setTextFill(Color.BLACK);
                nameOfPhoto.setText("");
                FileChooser fileChooser = new FileChooser();
                file1[0] = fileChooser.showOpenDialog(Main.getStage());

                if (file1[0] == null)
                    return;

                InputStream is = null;

                if (!(getFileExtensionByFilename(file1[0].getName()).equals("png") ||
                        getFileExtensionByFilename(file1[0].getName()).equals("jpg")
                        || getFileExtensionByFilename(file1[0].getName()).equals("jpeg"))) {
                    file1[0] = null;
                    nameOfPhoto.setTextFill(Color.RED);
                    nameOfPhoto.setText("Choose picture");
                    return;
                }
                try {
                    is = new FileInputStream(file1[0].getAbsolutePath());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                chatImage.setImage(new Image(is));
            }
        });
        gridPane.addRow(0, usernameField);
        gridPane.addColumn(1, addUserButton);

        System.out.println(gridPane.getRowCount() + " ::::: " + gridPane.getColumnCount());
        //startNewChat.
        System.out.println("herer");
        dialog.setAlignment(Pos.CENTER);
        dialog.getChildren().addAll(enter, gridPane, addChatStatus, addedUsers, chatName, chatImage, nameOfPhoto, startNewChat);

        Scene scene = new Scene(dialog);
        dialogWindow.setScene(scene);
        dialogWindow.showAndWait();
    }

    void showMessages(ArrayList<Message> messages) {
        System.out.println("In showMessages()");
        chatPane.setVgap(10);
        chatPane.getChildren().clear();


        TextFlow[] textFlows = new TextFlow[messages.size()];
        for (int i = 0; i < messages.size(); i++) {
            textFlows[i] = new TextFlow();
            textFlows[i].setStyle("-fx-background-color:DODGERBLUE;-fx-background-radius:10");
            textFlows[i].setTextAlignment(TextAlignment.JUSTIFY);
        }

        for (int i = 0; i < messages.size(); i++) {
            Text text;
            String nameText = "";
            if(!messages.get(i).getSenderUsername().equals(user.getUsername()))
                nameText = messages.get(i).getSenderUsername() + "\n\n";

            if (messages.get(i).getAttachments().length() == 0)
                text = new Text(nameText+ messages.get(i).getText());
            else
                text = new Text(nameText+messages.get(i).getText() + '\n');

            text.setFill(Color.WHITE);
            text.setFont(Font.font("System", 12));
            textFlows[i].getChildren().add(text);
            String attachment = messages.get(i).getAttachments();
            int finalI = i;
            if (messages.get(i).getSenderUsername().equals(user.getUsername())) {
                textFlows[i].setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (mouseEvent.getButton() == MouseButton.SECONDARY)
                            setOnActionTextFlow(text, messages, finalI, chatPane, textFlows[finalI], chats, chatChoser);
                    /*{

                        Stage dialogWindow = new Stage();
                        dialogWindow.initModality(Modality.APPLICATION_MODAL);
                        dialogWindow.setMinHeight(200);
                        dialogWindow.setMinWidth(200);
                        dialogWindow.setMaxHeight(200);
                        dialogWindow.setMaxWidth(300);

                        GridPane gridPane = new GridPane();

                        Label label = new Label("New text of message:");
                        TextArea textArea = new TextArea(text.getText());
                        Label warningLabel = new Label("");
                        warningLabel.setTextFill(Color.RED);
                        Button deleteMessageButton = new Button("Delete message");
                        Button editMessageButton = new Button("Save");


                        deleteMessageButton.setOnAction(new EventHandler<ActionEvent>()
                        {
                            @Override
                            public void handle(ActionEvent actionEvent)
                            {
                                if (JavaPostgreSql.deleteMessage(messages.get(finalI)))
                                {
                                    messages.remove(finalI);
                                    chatPane.getChildren().remove(textFlows[finalI]);
                                    dialogWindow.close();
                                }
                            }
                        });

                        editMessageButton.setOnAction(new EventHandler<ActionEvent>()
                        {
                            @Override
                            public void handle(ActionEvent actionEvent)
                            {
                                String textMessage = textArea.getText();
                                if (textMessage.isBlank())
                                {
                                    warningLabel.setText("Enter text");
                                    return;
                                }

                                if (textMessage.equals(text.getText()))
                                    return;


                                messages.get(finalI).setText(textArea.getText());
                                Encrypter.encryptMessage(messages.get(finalI),
                                        chats.get(selectedChatIndex).getKey());

                                if (JavaPostgreSql.editMessage(messages.get(finalI)))
                                {
                                    messages.get(finalI).setText(textMessage);
                                    Text text1 = new Text(textMessage);
                                    text1.setFill(Color.WHITE);
                                    textFlows[finalI].getChildren().clear();
                                    textFlows[finalI].getChildren().add(text1);

                                }
                                dialogWindow.close();
                            }
                        });

                        gridPane.addRow(0, deleteMessageButton);
                        gridPane.addColumn(1, editMessageButton);

                        VBox dialog = new VBox(10);


                        dialog.setAlignment(Pos.CENTER);
                        dialog.getChildren().addAll(label, textArea, warningLabel, gridPane);

                        Scene scene = new Scene(dialog);
                        dialogWindow.setScene(scene);
                        dialogWindow.showAndWait();
                    }*/
                    }
                });
            }

            if (attachment.length() != 0) {

                if (getFileExtensionByFilename(attachment).equals("png") ||
                        getFileExtensionByFilename(attachment).equals("jpg")
                        || getFileExtensionByFilename(attachment).equals("jpeg")) {
                    try {
                        ImageView image = null;
                        if (messages.get(i).getImage() == null)
                        {
                            image = (ImageView) getPhotoOfMessage(messages.get(i));
                            //messages.get(i).setImage(new ImageView(image.getImage()));
                        }
                        else
                        {
                            ImageView image1 = messages.get(i).getImage();
                            image = new ImageView(image1.getImage());
                        }

                        image.setFitWidth(100);
                        image.setFitHeight(200);
                        image.setSmooth(true);

                        ImageView finalImage = image;
                        image.setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent mouseEvent) {
                                ImageView imageCopy = new ImageView(finalImage.getImage());
                                //System.out.println("Clicked");
                                showFullSizeImage(imageCopy);
                            }
                        });
                        chats.get(selectedChatIndex).getMessages().get(i).setImage(image);
                        //textFlows[i].setStyle("-fx-alignment: CENTER");
                        textFlows[i].setTextAlignment(TextAlignment.CENTER);
                        textFlows[i].setMinHeight(255);
                        textFlows[i].getChildren().add(image);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (DbxException e) {
                        e.printStackTrace();
                    }
                } else {
                    GridPane gridPane = new GridPane();
                    gridPane.getChildren().clear();
                    gridPane.addRow(1);
                    gridPane.addColumn(0);
                    System.out.println("In sendMessage(): rows - " + gridPane.getRowCount() +
                            " : columns - " + gridPane.getColumnCount());

                    ImageView imageView = new ImageView(new Image("fileImage.jpg"));
                    imageView.setFitHeight(30);
                    imageView.setFitWidth(30);
                    Message message = messages.get(i);
                    imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            //Runtime.getRuntime().exec("explorer.exe / select," + path);
                            //message.setFileAttachment(new File("a"));
                            //message.
                            try {
                                message.setFileAttachment((File) getPhotoOfMessage(message));
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (DbxException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    gridPane.add(imageView, 0, 1);
                    gridPane.add(new Label(messages.get(i).getAttachments()), 1, 1);
                    textFlows[i].getChildren().add(gridPane);
                }
            }


            chatPane.addRow(i);
            if (messages.get(i).getSenderUsername().equals(user.getUsername())) {
                chatPane.add(textFlows[i], 2, i);
            } else {
                chatPane.add(textFlows[i], 0, i);
            }

        }
        chatPane.layout();
        chatScroll.layout();
        chatScroll.setVvalue(1);
        System.out.println("rows in chatPane (showMessages): " + chatPane.getRowCount());
    }

    @FXML
    void sendMessage() {
        String messageText = newMessageField.getText();
        Date date = new Date();
        Message messageToSend = new Message(messageText, "", user.getUsername(),
                user.getName(), date.getTime(), chats.get(selectedChatIndex).getChatIndex());
        if (file != null) {
            System.out.println("file is not null");
            if (getFileExtension().equals("png") || getFileExtension().equals("jpg")
                    || getFileExtension().equals("jpeg")) {
                try {
                    InputStream is = new FileInputStream(file.getAbsolutePath());
                    messageToSend.setImage(new ImageView(new Image(is)));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            String fileName = correctFileName(chats.get(selectedChatIndex).getMessages(), file.getName());
            messageToSend.setAttachments(fileName);
        }


        if (chats.get(selectedChatIndex).isIfNewChat()) {
            /*String chatName = user.getUsername() + '\n' + chats.get(selectedChatIndex).getName();
            chats.get(selectedChatIndex).setChatName(chatName);*/

            if (JavaPostgreSql.addNewChat(chats.get(selectedChatIndex))) {
                Encrypter.createKeyForNewChat(chats.get(selectedChatIndex));
                MessagesTracker.chatWithhighestID = chats.get(selectedChatIndex).getChatIndex();
                chats.get(selectedChatIndex).setIfNewChat(false);

                uploadChatPhotoToDropbox(chats.get(selectedChatIndex));

                if (JavaPostgreSql.addUsersToChat(chats.get(selectedChatIndex).getUsers(),
                        chats.get(selectedChatIndex))) {
                    messageToSend.setMessage_chat_id(chats.get(selectedChatIndex).getChatIndex());
                    for (int i = 0; i < chats.get(selectedChatIndex).getUsers().size(); i++) {
                        if (!chats.get(selectedChatIndex).getUsers().get(i).getUsername()
                                .equals(Main.user.getUsername()))
                            JavaPostgreSql.transferKey(chats.get(selectedChatIndex),
                                    chats.get(selectedChatIndex).getUsers().get(i).getUsername());
                    }
                }
            } else {
                System.out.println("Can't send a message to new chat");
                return;
            }

        }

        if (messageToSend.getAttachments().length() != 0) {
            if (!uploadToCloudStorage(messageToSend)) {
                return;
            }
        }
        Encrypter.encryptMessage(messageToSend, chats.get(selectedChatIndex).getKey());
        if (JavaPostgreSql.sendMessage(messageToSend)) {
            chats.get(selectedChatIndex).setLastMessage(messageToSend.getTime());
            //if (selectedChatIndex != 0)
            // {
            int selectedChatIndexLocal = selectedChatIndex;
            selectedChatIndex = -1;

            ChatInfo chatToRelocate = chats.get(selectedChatIndexLocal);
            chatChoser.getItems().remove(selectedChatIndexLocal);
            ChatSelectionController.addNewChat(chatChoser, chatToRelocate);
            //selectedChatIndex = 0;
            chats.sort(new CompareChats());
            chatChoser.getSelectionModel().selectFirst();
            selectedChatIndex = 0;
            //}

            messageToSend.setText(newMessageField.getText());
            chats.get(selectedChatIndex).getMessages().add(messageToSend);
            TextFlow messageBubble = new TextFlow();
            messageBubble.setStyle("-fx-background-color:DODGERBLUE;-fx-background-radius:10");
            messageBubble.setTextAlignment(TextAlignment.JUSTIFY);

            Text text = new Text(newMessageField.getText());
            text.setFill(Color.WHITE);
            text.setFont(Font.font("System", 12));
            messageBubble.getChildren().add(text);

            messageBubble.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (mouseEvent.getButton() == MouseButton.SECONDARY)
                        setOnActionTextFlow(text, chats.get(selectedChatIndex).getMessages(),
                                chats.get(selectedChatIndex).getMessages().size() - 1, chatPane,
                                messageBubble, chats, chatChoser);
                }
            });


            if (messageToSend.getAttachments().length() != 0 && (getFileExtension().equals("png")
                    || getFileExtension().equals("jpg")
                    || getFileExtension().equals("jpeg"))) {


                ImageView image = messageToSend.getImage();

                image.setFitWidth(100);
                image.setFitHeight(200);
                image.setSmooth(true);

                image.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        ImageView imageCopy = new ImageView(image.getImage());
                        showFullSizeImage(imageCopy);
                    }
                });

                messageBubble.setTextAlignment(TextAlignment.CENTER);
                messageBubble.setMinHeight(220);
                messageBubble.getChildren().add(image);
            } else if (messageToSend.getAttachments().length() != 0) {
                GridPane gridPane = new GridPane();
                gridPane.getChildren().clear();
                gridPane.addRow(1);
                gridPane.addColumn(0);
                System.out.println("In sendMessage(): rows - " + gridPane.getRowCount() +
                        " : columns - " + gridPane.getColumnCount());

                ImageView imageView = new ImageView(new Image("fileImage.jpg"));
                imageView.setFitHeight(30);
                imageView.setFitWidth(30);
                String path = file.getParent();
                imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        try {
                            //Runtime.getRuntime().exec("explorer.exe / select," + path);
                            Runtime.getRuntime().exec("explorer " + path);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                gridPane.add(imageView, 0, 1);
                gridPane.add(new Label(file.getName()), 1, 1);
                messageBubble.getChildren().add(gridPane);
                messageToSend.setFileAttachment(file);
            }

            System.out.println("rows:" + chatPane.getRowCount());
            chatPane.addRow(chatPane.getRowCount());
            chatPane.add(messageBubble, 2, chatPane.getRowCount());
            chatPane.layout();
            chatScroll.layout();
            chatScroll.setVvalue(1);
            file = null;
            selectedFileName.setText("");
            newMessageField.clear();
        } else
            System.out.println("Not sent");
    }

    DbxClientV2 connectToDropbox() {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("User").build();
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
        return client;
    }

    public static Object getPhotoOfMessage(Message message) throws IOException, DbxException {
        String chatIndex = Integer.toString(message.getMessage_chat_id());
        String attachmentName = message.getAttachments();
        File file;
        String folderName;
        if (getFileExtensionByFilename(attachmentName).equals("png")
                || getFileExtensionByFilename(attachmentName).equals("jpg")
                || getFileExtensionByFilename(attachmentName).equals("jpeg")) {
            File file1 = new File("C:\\MiniMessenger\\ChatPhotos\\"+attachmentName);

            if(file1.exists())
            {
                InputStream is = new FileInputStream("C:\\MiniMessenger\\ChatPhotos\\"+attachmentName);
                return new ImageView(new Image(is));
            }

            file = new File("C:\\MiniMessenger\\ChatPhotos");
            folderName = "ChatPhotos";
        } else {
            file = new File("C:\\MiniMessenger\\ChatFiles");
            folderName = "ChatFiles";
        }
        file.mkdirs();
        OutputStream downloadFile = new FileOutputStream("C:\\MiniMessenger\\" + folderName
                + "\\" + attachmentName);
        FileMetadata metadata = client.files().downloadBuilder("/Apps/MiniMessenger/" + chatIndex + "/"
                + attachmentName).download(downloadFile);
        System.out.println(metadata.getName());
        downloadFile.close();


        InputStream is = new FileInputStream("C:\\MiniMessenger\\" + folderName
                + "\\" + attachmentName);
        if (folderName.equals("ChatPhotos")) {
            Image image = new Image(is);
            Image image1 = new Image(is);
            ImageView imageView = new ImageView(image);
            return imageView;
        } else {
            return new File("C:\\MiniMessenger\\" + folderName
                    + "\\" + attachmentName);
        }
    }


    public static void showFullSizeImage(ImageView image) {
        image.setDisable(true);
        Stage imageViewer = new Stage();

        imageViewer.initModality(Modality.APPLICATION_MODAL);
        ScrollPane fullSizeImagePane = new ScrollPane();
        fullSizeImagePane.setContent(image);
        fullSizeImagePane.setMaxHeight(700);
        fullSizeImagePane.setMaxWidth(700);

        Scene scene = new Scene(fullSizeImagePane);

        imageViewer.setScene(scene);
        imageViewer.showAndWait();

        image.setDisable(false);
    }

    @FXML
    void selectFile() {
        FileChooser fileChooser = new FileChooser();
        file = fileChooser.showOpenDialog(Main.getStage());
        if (file == null)
            return;
        selectedFileName.setText(file.getName());
    }

    String getFileExtension() {
        return FilenameUtils.getExtension(file.getAbsolutePath());
    }

    public static String getFileExtensionByFilename(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i >= 0) {
            extension = fileName.substring(i + 1);
        }
        System.out.println("Extension = " + extension);
        return extension;
    }

    boolean uploadToCloudStorage(Message message) {
        String path = file.getAbsolutePath();
        System.out.println(path);
        InputStream in = null;
        try {
            in = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        try {
            FileMetadata metadata = client.files().uploadBuilder("/Apps/MiniMessenger/"
                            + message.getMessage_chat_id() + "/" + message.getAttachments())
                    .uploadAndFinish(in);
            in.close();
            return true;
        } catch (DbxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    String correctFileName(ArrayList<Message> messages, String name) {
        for (int i = 0; i < messages.size(); i++)
            if (messages.get(i).getAttachments().equals(name)) {
                name = '_' + name;
                name = correctFileName(messages, name);
            }
        return name;
    }

    public static void setPhotoOfChat(ChatInfo chat) {
        try {
            String photoName = JavaPostgreSql.getPhotoNameOfChat(chat);
            chat.setChatPhotoName(photoName);
            File file = new File("C:\\MiniMessenger\\ChatProfilePhotos\\" + chat.getChatIndex());
            if (!file.exists())
                file.mkdirs();

            File checkNameFile = new File("C:\\MiniMessenger\\ChatProfilePhotos\\" +chat.getChatIndex()+ "\\"+ photoName);
            if (checkNameFile.exists() && photoName.length() != 0)
            {
                InputStream is = new FileInputStream("C:\\MiniMessenger\\ChatProfilePhotos\\" + chat.getChatIndex()+ "\\" + photoName);
                chat.setChatImage(new ImageView(new Image(is)));
                return;
            }
            OutputStream downloadFile = null;// = new FileOutputStream("C:\\MiniMessenger\\ChatProfilePhotos"
            //+ "\\" + photoName);

            if (photoName.equals("error")) {
                System.out.println("error in getting user photo");
                if (chat.isGroupChat())
                    chat.setChatImage(new ImageView(new Image("group_chat.png")));
                else
                    chat.setChatImage(new ImageView(new Image("user.png")));
                return;
            } else if (photoName.isEmpty()) {
                if (!chat.isGroupChat()) {
                    if (!chat.isIfNewChat())
                        JavaPostgreSql.getAllUsersInChat(chat);
                    User userInChat = null;
                    for (int i = 0; i < chat.getUsers().size(); i++)
                        if (!chat.getUsers().get(i).getUsername().equals(Main.user.getUsername()))
                            userInChat = chat.getUsers().get(i);

                    if (userInChat != null && !userInChat.getPhotoName().isEmpty()) {
                        photoName = userInChat.getPhotoName();
                        downloadFile = new FileOutputStream("C:\\MiniMessenger\\ChatProfilePhotos\\" +
                                chat.getChatIndex() + "\\" + photoName);
                        FileMetadata metadata = client.files().downloadBuilder("/Apps/MiniMessenger/UserPhotos/" +
                                userInChat.getUsername() + "/" + photoName).download(downloadFile);
                        InputStream is = new FileInputStream("C:\\MiniMessenger\\ChatProfilePhotos\\" +
                                chat.getChatIndex() + "\\" + photoName);
                        ImageView imageView = new ImageView(new Image(is));
                        chat.setChatImage(imageView);
                        downloadFile.close();
                    } else {
                        chat.setChatImage(new ImageView(new Image("user.png")));
                    }
                } else
                    chat.setChatImage(new ImageView(new Image("group_chat.png")));
            } else {
                downloadFile = new FileOutputStream("C:\\MiniMessenger\\ChatProfilePhotos\\" +
                        chat.getChatIndex() + "\\" + photoName);
                FileMetadata metadata = client.files().downloadBuilder("/Apps/MiniMessenger/" + chat.getChatIndex() +
                        "/ChatPhoto/" + photoName).download(downloadFile);
                InputStream is = new FileInputStream("C:\\MiniMessenger\\ChatProfilePhotos\\" +
                        chat.getChatIndex() + "\\" + photoName);
                ImageView imageView = new ImageView(new Image(is));
                chat.setChatImage(imageView);
                downloadFile.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (DbxException e) {
            e.printStackTrace();
        }
    }

    Image getImageOfUserByUsername(String username) {
        Image image = null;
        try {
            String photoName = JavaPostgreSql.getUserPhotoName(username);

            File file = new File("C:\\MiniMessenger\\ChatProfilePhotos");
            if (!file.exists())
                file.mkdirs();

            if (!photoName.isEmpty() && !photoName.equals("error")) {
                OutputStream downloadFile = new FileOutputStream("C:\\MiniMessenger\\ChatProfilePhotos\\" +
                        photoName);
                FileMetadata metadata = client.files().downloadBuilder("/Apps/MiniMessenger/UserPhotos/" +
                        username + "/" + photoName).download(downloadFile);
                System.out.println("metadata: " + metadata.getName());
                InputStream is = new FileInputStream("C:\\MiniMessenger\\ChatProfilePhotos\\" +
                        photoName);
                image = new Image(is);
                downloadFile.close();
            } else {
                if (photoName.equals("error"))
                    System.out.println("error in getting user photo");

                //InputStream is = getClass().getResourceAsStream("/user.png")//new FileInputStream("/user.png");
                image = new Image("user.png");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DbxException e) {
            e.printStackTrace();
        }
        return image;
    }

    @FXML
    void changeAccountPhoto() {
        File selectedPhoto;
        FileChooser fileChooser = new FileChooser();
        selectedPhoto = fileChooser.showOpenDialog(Main.getStage());

        if (selectedPhoto == null)
            return;

        if (getFileExtensionByFilename(selectedPhoto.getName()).equals("png")
                || getFileExtensionByFilename(selectedPhoto.getName()).equals("jpg")
                || getFileExtensionByFilename(selectedPhoto.getName()).equals("jpeg")) {
            try {
                user.setPhotoName(selectedPhoto.getName());
                JavaPostgreSql.updateProfilePhoto(user);
                InputStream in = new FileInputStream(selectedPhoto.getAbsolutePath());
                FileMetadata metadata = client.files().uploadBuilder("/Apps/MiniMessenger/"
                                + "UserPhotos/" + user.getUsername() + "/" + selectedPhoto.getName())
                        .uploadAndFinish(in);


                InputStream inputStream = new FileInputStream(selectedPhoto.getAbsolutePath());
                Image image = new Image(inputStream);
                btn_user.setImage(image);
                btn_user_in.setImage(image);
                in.close();
                inputStream.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UploadErrorException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DbxException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean uploadChatPhotoToDropbox(ChatInfo chat) {
        if (chat.getChatPhotoPath().isBlank())
            return false;

        String path = chat.getChatPhotoPath();
        System.out.println(path);
        InputStream in = null;
        try {
            in = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        try {
            FileMetadata metadata = client.files().uploadBuilder("/Apps/MiniMessenger/" + chat.getChatIndex() +
                            "/ChatPhoto/" + chat.getChatPhotoName())
                    .uploadAndFinish(in);
            in.close();
            return true;
        } catch (DbxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @FXML
    void addUserToBlacklist() {
        Stage dialogWindow = new Stage();
        dialogWindow.setTitle("Add user to black list");
        dialogWindow.initModality(Modality.APPLICATION_MODAL);
        dialogWindow.setMinHeight(200);
        dialogWindow.setMinWidth(200);
        dialogWindow.setMaxHeight(200);
        dialogWindow.setMaxWidth(200);

        VBox dialog = new VBox(10);

        Label label = new Label("Enter username to add to black list");
        TextField usernameField = new TextField();
        Label warningLabel = new Label("");
        warningLabel.setTextFill(Color.RED);
        Button addToBlackListButton = new Button("Add to black list");

        addToBlackListButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                warningLabel.setText("");
                if (usernameField.getText().isBlank()) {
                    warningLabel.setText("Enter username");
                    return;
                }

                if (usernameField.getText().equals(user.getUsername())) {
                    warningLabel.setText("You can't add yourself to black list");
                    return;
                }

                if (!JavaPostgreSql.isUserExists(usernameField.getText())) {
                    warningLabel.setText("User doesn't exist");
                    return;
                }

                if (!JavaPostgreSql.addUserToBlacklist(usernameField.getText()))
                    return;

                //Image imageOfBlockedUser = getImageOfUserByUsername(usernameField.getText());
                User userToBlackList = JavaPostgreSql.getUserByUsername(usernameField.getText());
                Label nameOfUserLabel = new Label(userToBlackList.getName()
                        + " (@" + userToBlackList.getUsername() + ")");

//                ImageView imageViewOfBlockedUser = new ImageView(imageOfBlockedUser);
//                imageViewOfBlockedUser.setFitHeight(40);
//                imageViewOfBlockedUser.setFitWidth(40);


                //nameOfUserLabel.setGraphic(imageViewOfBlockedUser);
                user.getUserBlackList().add(userToBlackList);
                blackList_listView.getItems().add(nameOfUserLabel);

                for (int i = 0; i < chats.size(); i++) {
                    if (chats.get(i).isGroupChat())
                        continue;

                    for (int j = 0; j < 2; j++) {
                        if ((chats.get(i).getUsers().size() == 0) && !chats.get(i).isIfNewChat())
                            JavaPostgreSql.getAllUsersInChat(chats.get(i));

                        if (userToBlackList.getUsername()
                                .equals(chats.get(i).getUsers().get(j).getUsername())) {
                            chats.get(i).setIfEnabledChat(false);

                            if (i == selectedChatIndex) {
                                newMessageField.setVisible(false);
                                sendMessageButton.setVisible(false);
                                selectFileButton.setVisible(false);
                            }
                        }
                    }
                }
                dialogWindow.close();
            }
        });

        dialog.setAlignment(Pos.CENTER);
        dialog.getChildren().addAll(label, usernameField, warningLabel, addToBlackListButton);

        Scene scene = new Scene(dialog);
        dialogWindow.setScene(scene);
        dialogWindow.showAndWait();
    }

    @FXML
    void removeUserFromBlacklist() {
        int indexToRemove = blackList_listView.getSelectionModel().getSelectedIndex();

        if (indexToRemove == -1)
            return;

        String usernameToRemove = user.getUserBlackList().get(indexToRemove).getUsername();

        if (JavaPostgreSql.removeUserFromBlacklist(usernameToRemove)) {
            for (int i = 0; i < chats.size(); i++) {
                if (chats.get(i).isGroupChat())
                    continue;

                for (int j = 0; j < 2; j++) {
                    if ((chats.get(i).getUsers().size() == 0) && !chats.get(i).isIfNewChat())
                        JavaPostgreSql.getAllUsersInChat(chats.get(i));

                    if (usernameToRemove.equals(chats.get(i).getUsers().get(j).getUsername())) {
                        chats.get(i).setIfEnabledChat(true);

                        if (i == selectedChatIndex) {
                            newMessageField.setVisible(true);
                            sendMessageButton.setVisible(true);
                            selectFileButton.setVisible(true);
                        }
                    }
                }
            }
            user.getUserBlackList().remove(indexToRemove);
            blackList_listView.getItems().remove(indexToRemove);
        }
    }

    void setBlackList() {
        usersIamInBlacklist = JavaPostgreSql.getUsersWhereIamInBlacklist(user.getUsername());
        ArrayList<String> usernamesOfBlacklist = JavaPostgreSql.getBlackList(user.getUsername());
        ArrayList<User> usersOfBlacklist = new ArrayList<>();
        for (int i = 0; i < usernamesOfBlacklist.size(); i++) {
            usersOfBlacklist.add( JavaPostgreSql.getUserByUsername(usernamesOfBlacklist.get(i)));

            //Image imageOfBlockedUser = getImageOfUserByUsername(usernamesOfBlacklist.get(i));
            User userToBlackList = JavaPostgreSql.getUserByUsername(usernamesOfBlacklist.get(i));
            Label nameOfUserLabel = new Label(userToBlackList.getName()
                    + " (@" + userToBlackList.getUsername() + ")");

//            ImageView imageViewOfBlockedUser = new ImageView(imageOfBlockedUser);
//            imageViewOfBlockedUser.setFitHeight(40);
//            imageViewOfBlockedUser.setFitWidth(40);
//
//            nameOfUserLabel.setGraphic(imageViewOfBlockedUser);
            blackList_listView.getItems().add(nameOfUserLabel);
        }

        user.setUserBlackList(usersOfBlacklist);


        for (int k = 0; k < usersOfBlacklist.size(); k++) {
            User userToBlackList = usersOfBlacklist.get(k);
            for (int i = 0; i < chats.size(); i++) {
                if (chats.get(i).isGroupChat())
                    continue;

                boolean isFound = false;
                for (int j = 0; j < 2; j++) {
                    if ((chats.get(i).getUsers().size() == 0) && !chats.get(i).isIfNewChat())
                        JavaPostgreSql.getAllUsersInChat(chats.get(i));

                    if (userToBlackList.getUsername()
                            .equals(chats.get(i).getUsers().get(j).getUsername())) {
                        isFound = true;
                        chats.get(i).setIfEnabledChat(false);
                    }
                }
                if (isFound)
                    break;
            }
        }

        for (int i = 0; i < usersIamInBlacklist.size(); i++) {
            for (int j = 0; j < chats.size(); j++) {
                if (chats.get(j).isGroupChat())
                    continue;

                boolean isFound = false;
                for (int k = 0; k < 2; k++) {
                    if ((chats.get(i).getUsers().size() == 0) && !chats.get(i).isIfNewChat())
                        JavaPostgreSql.getAllUsersInChat(chats.get(i));

                    if (usersIamInBlacklist.get(i).equals(chats.get(j).getUsers().get(k).getUsername())) {
                        chats.get(j).setIfEnabledChat(false);
                        isFound = true;
                    }
                }
                if (isFound)
                    break;
            }
        }
    }

    public static void setOnActionTextFlow(Text text, ArrayList<Message> messages,
                                           int finalI, GridPane chatPane, TextFlow textFlow, ArrayList<ChatInfo> chats, ListView<Node> chat_Choser) {
        Stage dialogWindow = new Stage();
        dialogWindow.initModality(Modality.APPLICATION_MODAL);
        dialogWindow.setMinHeight(200);
        dialogWindow.setMinWidth(200);
        dialogWindow.setMaxHeight(200);
        dialogWindow.setMaxWidth(300);

        GridPane gridPane = new GridPane();

        Label label = new Label("New text of message:");
        TextArea textArea = new TextArea(messages.get(finalI).getText());
        Label warningLabel = new Label("");
        warningLabel.setTextFill(Color.RED);
        Button deleteMessageButton = new Button("Delete message");
        Button editMessageButton = new Button("Save");


        deleteMessageButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (JavaPostgreSql.deleteMessage(messages.get(finalI))) {
                    messages.remove(finalI);
                    chatPane.getChildren().remove(textFlow);
                    dialogWindow.close();

                    if ((finalI == messages.size()) && messages.size() != 0) {
                        chats.get(selectedChatIndex).setLastMessage(messages.get(messages.size() - 1).getTime());
                        int chatID = chats.get(selectedChatIndex).getChatIndex();

                        chats.sort(new CompareChats());
                        selectedChatIndex = -1;
                        ChatSelectionController.initializeChats(chat_Choser, chats);
                        int i;
                        for (i = 0; i < chats.size(); i++)
                            if (chats.get(i).getChatIndex() == chatID)
                                chat_Choser.getSelectionModel().select(i);
                        selectedChatIndex = i;
                    }
                }
                dialogWindow.close();
            }
        });

        editMessageButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String textMessage = textArea.getText();
                if (textMessage.isBlank()) {
                    warningLabel.setText("Enter text");
                    return;
                }

                if (textMessage.equals(text.getText()))
                    return;


                messages.get(finalI).setText(textArea.getText());
                Encrypter.encryptMessage(messages.get(finalI),
                        chats.get(selectedChatIndex).getKey());

                if (JavaPostgreSql.editMessage(messages.get(finalI))) {
                    messages.get(finalI).setText(textMessage);
                    Text text1 = new Text(textMessage);
                    text1.setFill(Color.WHITE);
                    textFlow.getChildren().clear();
                    textFlow.getChildren().add(text1);

                }
                dialogWindow.close();
            }
        });

        gridPane.addRow(0, deleteMessageButton);
        gridPane.addColumn(1, editMessageButton);

        VBox dialog = new VBox(10);


        dialog.setAlignment(Pos.CENTER);
        dialog.getChildren().addAll(label, textArea, warningLabel, gridPane);

        Scene scene = new Scene(dialog);
        dialogWindow.setScene(scene);
        dialogWindow.showAndWait();
    }

    @FXML
    void findMessages() {
        if (searchMessageField.getText().isBlank())
            return;

        while (!MessagesDownloader.isReady) ;

        String textToFind = searchMessageField.getText();

        ListView<String> listOfFoundMessages = new ListView<>();

        for (int i = 0; i < chats.size(); i++) {
            if (chats.get(i).getKey() == null)
                continue;

            for (int j = 0; j < chats.get(i).getMessages().size(); j++) {
                String textMessage = chats.get(i).getMessages().get(j).getText();

                if (textToFind.length() == 1) {
                    String groupChatName = "";
                    if (chats.get(i).isGroupChat())
                        groupChatName = " in " + chats.get(i).getName();

                    if (textMessage.contains(" " + textToFind)
                            || textMessage.contains(" " + textToFind + " ")
                            || textMessage.contains(textToFind + " "))
                        listOfFoundMessages.getItems()
                                .add(chats.get(i).getMessages().get(j).getSenderName() + "(" +
                                        chats.get(i).getMessages().get(j).getSenderUsername() + ")" + groupChatName + "\n\n" +
                                        textMessage + "\n\n" + new Date(chats.get(i).getMessages().get(j).getTime()));
                } else {
                    String groupChatName = "";
                    if (chats.get(i).isGroupChat())
                        groupChatName = " in " + chats.get(i).getName();

                    if (textMessage.contains(textToFind))
                        listOfFoundMessages.getItems()
                                .add(chats.get(i).getMessages().get(j).getSenderName() + "(" +
                                        chats.get(i).getMessages().get(j).getSenderUsername() + ")" + groupChatName + "\n\n" +
                                        textMessage + "\n\n" + new Date(chats.get(i).getMessages().get(j).getTime()));
                }
            }
        }


        Stage dialogWindow = new Stage();
        dialogWindow.initModality(Modality.APPLICATION_MODAL);
        dialogWindow.setMinHeight(200);
        dialogWindow.setMinWidth(200);
        dialogWindow.setMaxHeight(500);
        dialogWindow.setMaxWidth(350);

        listOfFoundMessages.setPrefHeight(350);

        VBox dialog = new VBox(10);


//        findMessageButton.setOnAction(new EventHandler<ActionEvent>()
//        {
//            @Override
//            public void handle(ActionEvent actionEvent)
//            {
//                if (messageSubstringField.getText().isBlank())
//                {
//                    warningLabel.setText("Enter text to find");
//                    return;
//                }
//
//                listOfFoundMessages.getItems().clear();
//                String textToFind = messageSubstringField.getText();
//
//
//
//                for (int i=0; i < chats.size();i++)
//                {
//                    if (chats.get(i).getKey() == null)
//                    continue;
//
//                    for (int j=0; j < chats.get(i).getMessages().size();j++)
//                    {
//                        String textMessage = chats.get(i).getMessages().get(j).getText();
//
//                        if (textToFind.length() == 1)
//                        {
//                            if (textMessage.contains(" " + textToFind)
//                                    || textMessage.contains(" " + textToFind + " ")
//                                    || textMessage.contains(textToFind + " "))
//                                listOfFoundMessages.getItems()
//                                        .add(chats.get(i).getMessages().get(j).getSenderName() + "("+
//                                                chats.get(i).getMessages().get(j).getSenderUsername() +")\n\n"+
//                                                textMessage);
//                        }
//                        else
//                        {
//                            if (textMessage.contains(textToFind))
//                                listOfFoundMessages.getItems()
//                                        .add(chats.get(i).getMessages().get(j).getSenderName() + "("+
//                                                chats.get(i).getMessages().get(j).getSenderUsername() +")\n\n"+
//                                                textMessage);
//                        }
//                    }
//                }
//            }
//        });

        dialogWindow.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                System.out.println("CLOSED SEARCH");
            }
        });

        dialog.setAlignment(Pos.CENTER);
        dialog.getChildren().addAll(listOfFoundMessages);
        Scene scene = new Scene(dialog);
        dialogWindow.setScene(scene);
        dialogWindow.showAndWait();
    }

    @FXML
    void textChanged() {
        if (MessagesDownloader.isReady)
            return;

        MessagesDownloader messagesDownloader = new MessagesDownloader(chats, chatChoser);
        messagesDownloader.start();
    }

    @FXML
    void deleteAccount() {
        Stage dialogWindow = new Stage();
        dialogWindow.initModality(Modality.APPLICATION_MODAL);
        dialogWindow.setMinHeight(200);
        dialogWindow.setMinWidth(300);
        dialogWindow.setMaxHeight(200);
        dialogWindow.setMaxWidth(300);

        Label label = new Label("Enter password to delete account:");
        PasswordField passwordField = new PasswordField();
        Label warningLabel = new Label("");
        warningLabel.setTextFill(Color.RED);
        Button deleteAccountButton = new Button("Delete account");

        deleteAccountButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                warningLabel.setText("");
                if (passwordField.getText().isBlank()) {
                    warningLabel.setText("Enter password");
                    return;
                }

                String encryptedPassword = JavaPostgreSql.getPasswordByUsername(user.getUsername());
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                if (passwordEncoder.matches(passwordField.getText(), encryptedPassword)) {
                    JavaPostgreSql.deactivateAccount(user.getUsername());
                    logOut();
                    System.out.println("Logged out");
                } else
                    warningLabel.setText("Wrong password");
                dialogWindow.close();
            }
        });


        VBox dialog = new VBox(10);


        dialog.setAlignment(Pos.CENTER);
        dialog.getChildren().addAll(label, passwordField, warningLabel, deleteAccountButton);

        Scene scene = new Scene(dialog);
        dialogWindow.setScene(scene);
        dialogWindow.showAndWait();
    }

    @FXML
    void logOut() {
        MessagesTracker.isProgrammRun = false;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        selectedChatIndex = -3;
        isNeedToShowMessages = true;
        indexesOfChatsWithoutKeys = new ArrayList<>();
        usersIamInBlacklist = new ArrayList<>();
        Main m = new Main();
        try {
            m.changeScene("hello-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Changed screen to LoginScreen");
    }

    public static boolean isPhotoDownloaded(Message message)
    {
        if (message == null || message.getAttachments().length() == 0)
            return false;

        return false;
        //File checkFile = new File("")
    }

    @FXML
    void chatInfoDialog()
    {
        System.out.println("amount of users: " + (chats.get(selectedChatIndex).getUsers().size()));

        chats.get(selectedChatIndex).
                setModeratorUsernames(JavaPostgreSql.
                        getCreatorAndModeratorsOfChat(chats.get(selectedChatIndex).getChatIndex()));

        String role = "USER";

        for (int i=0; i < chats.get(selectedChatIndex).getModeratorUsernames().size();i++)
            if (user.getUsername().equals(chats.get(selectedChatIndex).getModeratorUsernames().get(i)))
        {
            if (i == 0)
                role = "CREATOR";
            else
                role = "MODER";
        }

        System.out.println("Moders in chat:");
        for (int i=0; i < chats.get(selectedChatIndex).getModeratorUsernames().size();i++)
            System.out.println(chats.get(selectedChatIndex).getModeratorUsernames().get(i));

        System.out.println("Users in chat:");
        for (int i=0; i < chats.get(selectedChatIndex).getUsers().size();i++)
            System.out.println(chats.get(selectedChatIndex).getUsers().get(i).getUsername());

        Stage dialogWindow = new Stage();
        dialogWindow.setTitle("Chat info");
        dialogWindow.initModality(Modality.APPLICATION_MODAL);
        dialogWindow.setMinHeight(200);
        dialogWindow.setMinWidth(200);
        dialogWindow.setMaxHeight(500);
        dialogWindow.setMaxWidth(350);

        VBox dialog = new VBox(10);

        TextField chatName = new TextField(chats.get(selectedChatIndex).getName());
        Button setNewChatNameButton = new Button("Change name");
        Button deleteUserButton = new Button("Delete user");
        deleteUserButton.setDisable(true);

        Button leaveChatButton = new Button("Leave chat");


        Button setModerButton = new Button("Select user...");
        setModerButton.setDisable(true);

        Button addUserButton = new Button("Add user");


        ListView<String> users = new ListView<>();
        users.setPrefHeight(150);

        ImageView chatImage = new ImageView(chats.get(selectedChatIndex).getChatImage().getImage());
        chatImage.setFitWidth(50);
        chatImage.setFitHeight(50);
        Label warningLabel = new Label("");
        warningLabel.setTextFill(Color.RED);

        if(role.equals("USER"))
        {
            chatName.setDisable(true);
            setNewChatNameButton.setDisable(true);
            addUserButton.setDisable(true);
            chatImage.setDisable(true);
            setModerButton.setDisable(true);
            deleteUserButton.setDisable(true);
        }

        leaveChatButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

                alert.setHeaderText("Leaving chat");
                alert.setContentText("Are you sure want to delete leave from " +
                        chats.get(selectedChatIndex).getName());
                Optional<ButtonType> result = alert.showAndWait();

                if (result.isPresent() && result.get() == ButtonType.OK)
                {
                    JavaPostgreSql.deleteUserFromChat(user.getUsername(), chats.get(selectedChatIndex).getChatIndex());
                    try
                    {
                        Thread.sleep(700);
                        dialogWindow.close();
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });

        setNewChatNameButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {
                if (chatName.getText().equals(chats.get(selectedChatIndex).getName()))
                {
                    return;
                }

                if (chatName.getText().isBlank())
                {
                    warningLabel.setText("Enter new name of chat");
                    return;
                }

                String newName = chatName.getText();
                if(JavaPostgreSql.changeNameOfChat(chats.get(selectedChatIndex).getChatIndex(),newName))
                {
                    chats.get(selectedChatIndex).setChatName(newName);
                    int oldSelectedChatIndex = selectedChatIndex;
                    selectedChatIndex =-1;
                    ChatSelectionController.initializeChats(chatChoser, chats);
                    chatChoser.getSelectionModel().select(oldSelectedChatIndex);
                    selectedChatIndex = oldSelectedChatIndex;
                }

            }
        });

        chatImage.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent mouseEvent)
            {
                FileChooser fileChooser = new FileChooser();
                File selectedImage = fileChooser.showOpenDialog(Main.getStage());
                if (selectedImage == null)
                    return;
                if (!(getFileExtensionByFilename(selectedImage.getName()).equals("png")
                ||getFileExtensionByFilename(selectedImage.getName()).equals("jpg")
                || getFileExtensionByFilename(selectedImage.getName()).equals("jpeg")))
                {
                    warningLabel.setText("Choose .png or .jpg/.jpeg");
                    return;
                }

                InputStream is = null;
                try
                {
                    is = new FileInputStream(selectedImage.getAbsolutePath());
                } catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }

                if (JavaPostgreSql.updateChatImageInDB(selectedImage.getName(), chats.get(selectedChatIndex).getChatIndex()))
                {
                    InputStream inputStream =null;
                    chatImage.setImage(new Image(is));
                    try
                    {
                        inputStream = new FileInputStream(selectedImage.getAbsolutePath());
                    } catch (FileNotFoundException e)
                    {
                        e.printStackTrace();
                    }
                    chats.get(selectedChatIndex).setChatImage(new ImageView(new Image(inputStream)));
                    chats.get(selectedChatIndex).setChatPhotoName(selectedImage.getName());
                    chats.get(selectedChatIndex).setChatPhotoPath(selectedImage.getAbsolutePath());
                    uploadChatPhotoToDropbox(chats.get(selectedChatIndex));
                    int oldSelectedChatIndex = selectedChatIndex;
                    selectedChatIndex = -1;
                    ChatSelectionController.initializeChats(chatChoser, chats);
                    selectedChatIndex = oldSelectedChatIndex;
                }
            }
        });

        deleteUserButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {
                if (users.getSelectionModel().getSelectedIndex() == -1)
                {
                    return;
                }

                int indexOfUserToDelete = users.getSelectionModel().getSelectedIndex();

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

                alert.setHeaderText("Deleting user from chat");
                alert.setContentText("Are you sure want to delete @" +
                        chats.get(selectedChatIndex).getUsers().get(indexOfUserToDelete).getUsername() +
                        " from current chat?");
                Optional<ButtonType> result = alert.showAndWait();

                if (result.isPresent() && result.get() == ButtonType.OK)
                {
                    JavaPostgreSql.deleteUserFromChat(chats.get(selectedChatIndex)
                                    .getUsers().get(indexOfUserToDelete).getUsername(),
                            chats.get(selectedChatIndex).getChatIndex());

                    for (int i=0; i < chats.get(selectedChatIndex).getModeratorUsernames().size();i++)
                        if (chats.get(selectedChatIndex).getUsers().get(indexOfUserToDelete).getUsername()
                                .equals(chats.get(selectedChatIndex).getModeratorUsernames().get(i)))
                        {
                            chats.get(selectedChatIndex).getModeratorUsernames().remove(i);
                            continue;
                        }

                    users.getItems().remove(indexOfUserToDelete);
                    chats.get(selectedChatIndex).getUsers().remove(indexOfUserToDelete);
                }


            }
        });

        setModerButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {
                int index = users.getSelectionModel().getSelectedIndex();
                if (index== -1 || users.getSelectionModel().getSelectedItem().contains("CREATOR"))
                {
                    return;
                }

                if (users.getSelectionModel().getSelectedItem().contains("MODER"))
                {
                    if (JavaPostgreSql.changeRoleOfUserInChat(chats.get(selectedChatIndex)
                            .getUsers().get(index).getUsername(),
                            chats.get(selectedChatIndex).getChatIndex(), 0))
                    {
                        System.out.println("Amount of moders before: " + chats.get(selectedChatIndex).getModeratorUsernames().size());
                        chats.get(selectedChatIndex).getModeratorUsernames().remove(chats.get(selectedChatIndex)
                                .getUsers().get(index).getUsername());
                        users.getItems().remove(index);
                        users.getItems().add(index,chats.get(selectedChatIndex).getUsers().get(index).getName()
                                + "(@" + chats.get(selectedChatIndex).getUsers().get(index).getUsername()
                                +") ");
                        System.out.println("Amount of moders before: " + chats.get(selectedChatIndex).getModeratorUsernames().size());
                    }
                }
                else
                {
                    if (JavaPostgreSql.changeRoleOfUserInChat(chats.get(selectedChatIndex)
                                    .getUsers().get(index).getUsername(),
                            chats.get(selectedChatIndex).getChatIndex(), 1))
                    {
                        System.out.println("Amount of moders before: " + chats.get(selectedChatIndex).getModeratorUsernames().size());
                        chats.get(selectedChatIndex).getModeratorUsernames().add(chats.get(selectedChatIndex)
                                .getUsers().get(index).getUsername());
                        users.getItems().remove(index);
                        users.getItems().add(index,chats.get(selectedChatIndex).getUsers().get(index).getName()
                                + "(@" + chats.get(selectedChatIndex).getUsers().get(index).getUsername()
                                +") [MODER]");
                        System.out.println("Amount of moders before: " + chats.get(selectedChatIndex).getModeratorUsernames().size());
                    }
                }

            }
        });

        final int[] myIndex = {0};
        for (int i=0; i < chats.get(selectedChatIndex).getUsers().size();i++)
        {
            if (chats.get(selectedChatIndex).getUsers().get(i).getUsername().equals(user.getUsername()))
                myIndex[0] = i;
        }

        String finalRole = role;
        users.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1)
            {
                deleteUserButton.setDisable(true);
                if (finalRole.equals("CREATOR"))
                    deleteUserButton.setDisable(false);

                int index = users.getSelectionModel().getSelectedIndex();
                if (finalRole.equals("USER"))
                    return;

                if (myIndex[0] == index)
                {
                    setModerButton.setDisable(true);
                    deleteUserButton.setDisable(true);
                    return;
                }

                if (finalRole.equals("CREATOR"))
                    setModerButton.setDisable(false);

                if(!chats.get(selectedChatIndex).getModeratorUsernames().get(0)
                        .equals(chats.get(selectedChatIndex).getUsers().get(index).getUsername()) && !chats.get(selectedChatIndex).getModeratorUsernames()
                        .contains(chats.get(selectedChatIndex).getUsers()
                                .get(index).getUsername()))
                    deleteUserButton.setDisable(false);

                if (chats.get(selectedChatIndex).getModeratorUsernames()
                        .contains(chats.get(selectedChatIndex).getUsers()
                                .get(index).getUsername()) && finalRole.equals("CREATOR")){
                    setModerButton.setText("Delete moder");
                }
                else if (!chats.get(selectedChatIndex).getModeratorUsernames()
                        .contains(chats.get(selectedChatIndex).getUsers()
                                .get(index).getUsername()) && finalRole.equals("CREATOR"))
                    setModerButton.setText("Make moder");
//                else
//                {
//                    setModerButton.setText("Select user...");
//                    setModerButton.setDisable(true);
//                }

            }
        });

        addUserButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {
                Stage dialogWindow = new Stage();
                dialogWindow.initModality(Modality.APPLICATION_MODAL);
                dialogWindow.setMinHeight(200);
                dialogWindow.setMinWidth(200);
                dialogWindow.setMaxHeight(200);
                dialogWindow.setMaxWidth(200);

                TextField nameText = new TextField();
                nameText.setPromptText("Enter username of user to add");
                Button addUserToChatButton = new Button("Add user");
                Label warningLabelAddUser = new Label("");
                warningLabelAddUser.setTextFill(Color.RED);

                VBox dialog = new VBox(10);

                addUserToChatButton.setOnAction(new EventHandler<ActionEvent>()
                {
                    @Override
                    public void handle(ActionEvent actionEvent)
                    {
                        warningLabelAddUser.setText("");

                        if (nameText.getText().isBlank())
                        {
                            warningLabelAddUser.setText("Enter username");
                            return;
                        }

                        String usernameText = nameText.getText();

                        if (!JavaPostgreSql.isUserExists(usernameText))
                        {
                            warningLabelAddUser.setText("User doesn't exists");
                            return;
                        }

                        for (int i = 0; i < chats.get(selectedChatIndex).getUsers().size(); i++)
                            if (usernameText.equals(chats.get(selectedChatIndex)
                                    .getUsers().get(i).getUsername()))
                            {
                                warningLabelAddUser.setText("User already in chat");
                                return;
                            }

                        ArrayList<User> addedUserArray = new ArrayList<>();

                        User userToAdd = JavaPostgreSql.getUserByUsername(usernameText);
                        addedUserArray.add(userToAdd);

                        if (JavaPostgreSql.addUsersToChat(addedUserArray, chats.get(selectedChatIndex)))
                        {
                            JavaPostgreSql.transferKey(chats.get(selectedChatIndex), usernameText);
                            chats.get(selectedChatIndex).getUsers().add(userToAdd);
                            users.getItems().add(userToAdd.getName() + "(@" + userToAdd.getUsername() + ")");
                        }
                        dialogWindow.close();
                    }
                });


                dialog.setAlignment(Pos.CENTER);
                //if (role.equals("CREATOR"))
                dialog.getChildren().addAll(nameText, warningLabelAddUser, addUserToChatButton);

                Scene scene = new Scene(dialog);
                dialogWindow.setScene(scene);
                dialogWindow.showAndWait();
            }
        });


        for (int i=0; i < chats.get(selectedChatIndex).getUsers().size();i++)
        {
            String nameOfUser = chats.get(selectedChatIndex).getUsers().get(i).getName()
                    + "(@" + chats.get(selectedChatIndex).getUsers().get(i).getUsername()
                    +") ";

            if (chats.get(selectedChatIndex).getUsers().get(i).getUsername()
                    .equals(chats.get(selectedChatIndex).getModeratorUsernames().get(0)))
            {
                nameOfUser+="[CREATOR]";
                users.getItems().add(nameOfUser);
                continue;
            }

            for (int j=1; j < chats.get(selectedChatIndex).getModeratorUsernames().size();j++)
            {
                if (chats.get(selectedChatIndex).getModeratorUsernames().get(j)
                        .equals(chats.get(selectedChatIndex).getUsers().get(i).getUsername()))
                    nameOfUser+="[MODERATOR]";
            }

            System.out.println("Name of users:");
            System.out.println(nameOfUser);
            users.getItems().add(nameOfUser);
        }

        GridPane gridPane = new GridPane();
        gridPane.addRow(0, setModerButton);
        gridPane.addColumn(1, deleteUserButton);
        gridPane.addColumn(2, addUserButton);
        gridPane.addColumn(3, leaveChatButton);

        dialog.setAlignment(Pos.CENTER);
        //if (role.equals("CREATOR"))
            dialog.getChildren().addAll(chatName, setNewChatNameButton,
                    chatImage, warningLabel, users,gridPane);

        Scene scene = new Scene(dialog);
        dialogWindow.setScene(scene);
        dialogWindow.showAndWait();
    }

    public static boolean isGroupChat(ChatInfo chat)
    {
        String chatName = chat.getName();
        Scanner scanner = new Scanner(chatName);
        int rowCount = 0;
        while(scanner.hasNextLine())
        {
            scanner.nextLine();
            rowCount++;
        }

        return rowCount == 1;
    }

}
