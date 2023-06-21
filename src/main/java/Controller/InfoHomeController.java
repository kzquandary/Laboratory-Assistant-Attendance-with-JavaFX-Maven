package Controller;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import org.json.JSONArray;
import Project.Action;
import Project.ApiRoute;
import Project.StringVariable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;

public class InfoHomeController implements Initializable {
    public Text jmlmahasiswa;
    public Text jmlpertemuan;
    public Text jmllaporan;
    public boolean isAlertShown = false;

    public void initTextFields(FXMLLoader loader) {
        jmlmahasiswa = (Text) loader.getNamespace().get("jmlmahasiswa");
        jmlpertemuan = (Text) loader.getNamespace().get("jmlpertemuan");
        jmllaporan = (Text) loader.getNamespace().get("jmllaporan");
        setMahassiwa();
        setPeretmuan();
        setLaporan();
    }

    public void setMahassiwa() {
        try {
            URL url = new URL(ApiRoute.GetMahasiswa);
            getApi(url, jmlmahasiswa);
        } catch (Exception e) {
            showAlert(StringVariable.ExceptionE(String.valueOf(e)));
        }
    }

    public void getApi(URL url, Text text) {
        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(StringVariable.GET);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            JSONArray mahasiswas = new JSONArray(response.toString());
            int totalMahasiswa = mahasiswas.length();
            text.setText(String.valueOf(totalMahasiswa));
        } catch (IOException e) {
            showAlert(StringVariable.ApiError);
        }
    }

    public void setPeretmuan() {
        try {
            URL url = new URL(ApiRoute.GetPertemuan);
            getApi(url, jmlpertemuan);
        } catch (Exception e) {
            showAlert(StringVariable.ExceptionE(String.valueOf(e)));
        }
    }

    public void setLaporan() {
        try {
            URL url = new URL(ApiRoute.GetLaporan);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(StringVariable.GET);
            con.setRequestProperty(StringVariable.ContentType, "application/json");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONArray jsonArray = new JSONArray(response.toString());
            int count = 0;
            for (int i = 0; i < jsonArray.length(); i++) {
                if (jsonArray.getJSONObject(i).getString("status").equals("Mengumpulkan")) {
                    count++;
                }
            }
            jmllaporan.setText(Integer.toString(count));
        } catch (ConnectException e) {
            showAlert(StringVariable.ApiError);
        } catch (Exception e) {
            showAlert(StringVariable.ExceptionE(String.valueOf(e)));
        }
    }

    public void showAlert(String content) {
        if (!isAlertShown) {
            isAlertShown = true;
            Action.toasterror(content);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setMahassiwa();
        setPeretmuan();
        setLaporan();
    }
}
