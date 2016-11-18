package app;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;

public class FirstPanelController {
	
	
	private MainScenView mainScenView;
	
	public void setMainScenView(MainScenView mainScenView) {
		this.mainScenView = mainScenView;
	}

	@FXML
	void onExit() {

	}

	@FXML
	void onLoad() {

	}

	@FXML
	void onStart() {

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
