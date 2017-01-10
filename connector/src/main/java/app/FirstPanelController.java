package app;

import javafx.fxml.FXML;

@SuppressWarnings("restriction")
public class FirstPanelController {
	private MainScenView mainScenView;

	public void setMainScenView(MainScenView mainScenView) {
		this.mainScenView = mainScenView;
	}

	@FXML
	void onExit() {
		System.exit(0);
	}

	// włącznie sceny z wgranymi objektami
	@FXML
	void onLoad() throws Exception {
		mainScenView.launchSceneWithLoadObjects();
	}

	// włączenie domyślnej sceny
	@FXML
	void onStart() throws Exception {
		mainScenView.launchDefaultScene();
	}
}