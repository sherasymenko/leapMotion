package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

@SuppressWarnings("restriction")
public class MainScenView extends Application {
	private Scene scen;
	Stage primaryStage;

	public static void main(String[] args) {
		launch(args);
		System.out.print("Main work");
	}

	@Override
	public void start(Stage ps) throws Exception {
		primaryStage = ps;
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(this.getClass().getResource("/FirstPanel.fxml"));
		StackPane stackPanel = loader.load();
		FirstPanelController firstController = loader.getController();
		firstController.setMainScenView(this);
		scen = new Scene(stackPanel);
		ps.setScene(scen);
		ps.setFullScreen(true);
		ps.show();
	}

	public void launchDefaultScene() throws Exception {
		StartDefaultScene defaultScene = new StartDefaultScene();
		defaultScene.start(primaryStage);
		stop();
	}

	public void launchSceneWithLoadObjects() throws Exception {
		LoadObjectsScene loadObjectsScene = new LoadObjectsScene();
		loadObjectsScene.start(primaryStage);
		stop();
	}

	public void setStackPanel(StackPane panel) {
		scen.setRoot(panel);
	}
}