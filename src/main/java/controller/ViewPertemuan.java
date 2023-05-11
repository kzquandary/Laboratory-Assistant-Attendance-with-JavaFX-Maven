//package controller;
//
//import com.aslabapp.aslabapp.Main;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.fxml.Initializable;
//import javafx.scene.Node;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.control.TableCell;
//import javafx.scene.control.TableColumn;
//import javafx.scene.control.TableView;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.scene.image.Image;
//import javafx.scene.input.MouseEvent;
//import javafx.scene.layout.AnchorPane;
//import javafx.scene.layout.HBox;
//import javafx.stage.Stage;
//import javafx.util.Callback;
//import model.Pertemuan;
//import org.json.JSONArray;
//import org.json.JSONObject;
//import project.Route;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.Objects;
//import java.util.ResourceBundle;
//public class ViewPertemuan implements Initializable {
//    public AnchorPane root;
//    public Button mhsview;
//    public Button back;
//    public Button tambah;
//
//    public Button minimize;
//    @FXML
//    private TableView<model.Pertemuan> tbpertemuan;
//    @FXML
//    private TableColumn<model.Pertemuan, String> p1;
//    @FXML
//    private TableColumn<model.Pertemuan, String> p2;
//    @FXML
//    private TableColumn<model.Pertemuan, Pertemuan> action;
//    private double xOffset = 0;
//    private double yOffset = 0;
//    @Override
//    public void initialize(URL location, ResourceBundle resources){
//        root.setOnMousePressed(this::handleMousePressed);
//        root.setOnMouseDragged(this::handleMouseDragged);
////        System.out.println("Mahasiswa Di Klik");
//        getTabel();
//    }
//    public void getTabel() {
//        try {
//            URL url = new URL(Route.URL + "pertemuan");
//            HttpURLConnection con = (HttpURLConnection) url.openConnection();
//            con.setRequestMethod("GET");
//            int status = con.getResponseCode();
//            if (status == 200) {
//                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//                String inputLine;
//                StringBuilder content = new StringBuilder();
//                while ((inputLine = in.readLine()) != null) {
//                    content.append(inputLine);
//                }
//                in.close();
//                JSONArray jsonArray = new JSONArray(content.toString());
//                ObservableList<Pertemuan> dataPert = FXCollections.observableArrayList();
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//                    String kode_pertemuan = jsonObject.getString("kode_pertemuan");
//                    String tanggal_pertemuan = jsonObject.getString("tanggal_pertemuan");
//                    dataPert.add(new Pertemuan(kode_pertemuan, tanggal_pertemuan));
//                }
//                p1.setCellValueFactory(new PropertyValueFactory<>("kode_pertemuan"));
//                p2.setCellValueFactory(new PropertyValueFactory<>("tanggal_pertemuan"));
//                action.setCellFactory(new Callback<>() {
//                    @Override
//                    public TableCell<Pertemuan, Pertemuan> call(TableColumn<Pertemuan, Pertemuan> param) {
//                        return new TableCell<>() {
//                            private final Button updateButton = new Button("Update");
//                            private final Button deleteButton = new Button("Hapus");
//
//                            {
//                                updateButton.setOnAction(event -> {
//                                    Pertemuan item = getTableView().getItems().get(getIndex());
//
//                                    FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(""));
//                                    Parent parent;
//                                    try {
//                                        parent = fxmlLoader.load();
//                                    } catch (IOException e) {
//                                        throw new RuntimeException(e);
//                                    }
//                                    Updatemhs updatemhs = fxmlLoader.getController();
//
//                                    // Isi TextField dengan data dari tabel
//                                    updatemhs.nim.setText(item.getKode_pertemuan());
//                                    updatemhs.nama.setText(item.getTanggal_pertemuan());
//
//                                    // Tampilkan popup
//                                    Stage stage = new Stage();
//                                    stage.setTitle("Update Mahasiswa");
//                                    stage.setScene(new Scene(parent));
//                                    stage.setResizable(false);
//                                    stage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("logo.png"))));
//                                    stage.show();
//                                });
//                                updateButton.setStyle("-fx-background-color:  #0a9efa; -fx-font-family: 'Comic Sans MS'");
//
//                                deleteButton.setOnAction(event -> {
//                                    Pertemuan item = getTableView().getItems().get(getIndex());
////                                    System.out.println("Menghapus mahasiswa dengan NIM " + item.getKode_pertemuan());
//                                    try {
//                                        URL url = new URL(Route.URL + "pertemuan/" + item.getKode_pertemuan());
//                                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                                        conn.setRequestMethod("POST");
//                                        conn.connect();
//                                        int responseCode = conn.getResponseCode();
//                                        if (responseCode == 201) {
////                                            System.out.println("Data mahasiswa berhasil dihapus");
//                                            tbpertemuan.getItems().remove(item);
//                                        } else {
////                                            System.out.println("Data mahasiswa gagal dihapus");
//                                        }
//
//                                        conn.disconnect();
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
//                                });
//                                deleteButton.setStyle("-fx-background-color:  #e74c3c; -fx-font-family: 'Comic Sans MS'");
//                            }
//
//                            @Override
//                            public void updateItem(Pertemuan item, boolean empty) {
//                                super.updateItem(item, empty);
//                                if (empty) {
//                                    setGraphic(null);
//                                } else {
//                                    HBox buttonsContainer = new HBox(10, updateButton, deleteButton);
//                                    setGraphic(buttonsContainer);
//                                    buttonsContainer.setVisible(true); // menampilkan tombol
//                                }
//                            }
//                        };
//                    }
//                });
//
//                tbpertemuan.setItems(dataPert);
////                System.out.println("Hit API dengan Res : 200");
//            }
//            con.disconnect();
//        } catch (Exception e) {
////            System.out.println("Error: " + e.getMessage());
//        }
//    }
//    public void refresh() {
//        getTabel();
//    }
//
//    @FXML
//    private void handleMinimize(ActionEvent event) {
//        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//        stage.setIconified(true);
//    }
//    @FXML
//    private void handleExit(ActionEvent event) {
//        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//        stage.close();
//    }
//    @FXML
//    private void handleMousePressed(MouseEvent event) {
//        xOffset = event.getSceneX();
//        yOffset = event.getSceneY();
//    }
//
//    @FXML
//    private void handleMouseDragged(MouseEvent event) {
//        Node node = (Node) event.getSource();
//        node.getScene().getWindow().setX(event.getScreenX() - xOffset);
//        node.getScene().getWindow().setY(event.getScreenY() - yOffset);
//    }
//    @FXML
//    public void tambahpertemuan()  throws IOException {
//        Stage stage = (Stage) tambah.getScene().getWindow();
//        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(""));
//        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
//        stage.setTitle("Aslab App");
//        stage.setScene(scene);
//        stage.setResizable(false);
//        stage.show();
//    }
//    @FXML
//    void back() throws IOException {
//        TambahPertemuan.back(back);
//    }
//}
