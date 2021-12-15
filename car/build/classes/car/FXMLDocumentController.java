package car;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import panel.Panel;

/**
 *
 * @author bassa Controller Class :)
 */
public class FXMLDocumentController implements Initializable {

    //Creating a new
    DB db = new DB();
    //Instance

    @FXML
    private TableView<Cost> tblCosts;

    @FXML
    private TableColumn<Cost, String> colSpentOn;

    @FXML
    private TableColumn<Cost, Integer> colPrice;

    @FXML
    private TableColumn<Cost, String> colDate;

    @FXML
    private TableColumn<Cost, Integer> colKm;

    @FXML
    private TableColumn<Cost, String> colComment;

    @FXML
    private Label lblAlltogether;

    //declare select method, will be implemented in modify method
    private void select(int id) {
        for (int i = 0; i < tblCosts.getItems().size(); i++) {
            Cost c = tblCosts.getItems().get(i);
            if (c.getId() == id) {
                tblCosts.getSelectionModel().select(i);
                return;
            }
        }
    }

    //I hope everybody understand this part :)
    @FXML
    void key(KeyEvent event) throws Exception {
        KeyCode code = event.getCode();
        if (code == KeyCode.INSERT) {
            newCost();
        } else if (code == KeyCode.DELETE) {
            delete();
        } else if (code == KeyCode.ENTER) {
            newCost();
        }
    }

    @FXML
    void delete() {
        int i = tblCosts.getSelectionModel().getSelectedIndex();
        if (i > -1) {
            if (!Panel.yesno("Delete", "Are you sure, you want to delete this cost?")) {
                //implement Panel.yesno method
                return;
            }
            int id = tblCosts.getItems().get(i).getId();//setting id
            db.delete(id);//implement DB.delete method on id-th element
            db.load(tblCosts.getItems());//reload costs
            int last = tblCosts.getItems().size() - 1;
            if (i <= last) {
                tblCosts.getSelectionModel().select(i);//selecting the same row after delete
            } else if (i > 0) {
                tblCosts.getSelectionModel().select(i - 1);//selecting the same row after delete
            }
            count();
        }
        tblCosts.requestFocus();
    }

    @FXML
    void modify() throws Exception {
        int i = tblCosts.getSelectionModel().getSelectedIndex();
        if (i > -1) {
            int id = tblCosts.getItems().get(i).getId();//set id's value
            window("Modify", i);
            db.load(tblCosts.getItems());
            //after modify ends, select the correct row
            select(id);
            count();
        }
        tblCosts.requestFocus();
    }

    @FXML
    void newCost() throws Exception {
        int i = tblCosts.getSelectionModel().getSelectedIndex();//before opening the new cost window we store the selected row
        int length = tblCosts.getItems().size();//we let length be tblCosts size
        window("New cost", -1);//opening the window
        db.load(tblCosts.getItems());//...
        if (tblCosts.getItems().size() > length) {
            int max = 0;
            for (Cost c : tblCosts.getItems()) {//go through the data
                if (c.getId() > max) {
                    max = c.getId();
                    select(max);//when we get maximum value we select it
                    count();
                } else {
                    tblCosts.getSelectionModel().select(i);//else we selected the stored row before we started to set a new cost
                }
                tblCosts.requestFocus();
            }
        }
    }

    private void count() {
        int amount = 0;
        for (Cost c : tblCosts.getItems()) {
            amount += c.getPrice();
        }
        lblAlltogether.setText("All together:\n" + amount + " Ft");
    }

    private void window(String title, int i) throws Exception {
        //Loading FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("window.fxml"));
        Parent root = loader.load();

        //Giving data to the new window
        WindowController wc = loader.getController();
        if (i > -1) {//Modify
            wc.setCost(tblCosts.getItems().get(i));
        } else {    //New
            wc.setCost(new Cost(0, "refueling", 0,
                    LocalDate.now().toString(), 0, ""));//Setting basic values for the window
        }
        wc.setDB(db);

        //opening the new window
        Scene scene = new Scene(root);
        Stage window = new Stage();
        window.setResizable(false);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setScene(scene);
        window.setTitle(title);
        window.getIcons().add(new Image("car.png"));
        window.showAndWait();

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // I tell the program here, which column contains which field of the DB
        colSpentOn.setCellValueFactory(new PropertyValueFactory<>("spenton"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colKm.setCellValueFactory(new PropertyValueFactory<>("km"));
        colComment.setCellValueFactory(new PropertyValueFactory<>("comment"));

        db.load(tblCosts.getItems());
        count();
    }

}
