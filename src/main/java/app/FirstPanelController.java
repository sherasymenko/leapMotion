package app;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class FirstPanelController {
	
	
	private MainScenView mainScenView;
	
	public void setMainScenView(MainScenView mainScenView) {
		this.mainScenView = mainScenView;
	}

	@FXML
	void onExit() {

		System.exit(0);
	}

	@FXML
	void onLoad() throws Exception {
		
		Process proc = Runtime.getRuntime().exec("java -jar D:\\zpp_workspace\\plik_uruchamiający\\LMApplication-0.0.1-SNAPSHOT.jar");
		System.exit(0);
	}

	@FXML
	void onStart() throws IOException {
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(this.getClass().getResource("/MainPanel.fxml"));
		StackPane stackPanel = null;
		try {
			stackPanel = loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mainScenView.setStackPanel(stackPanel);

	}

}
