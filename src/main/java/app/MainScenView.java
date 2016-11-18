package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class MainScenView  extends Application{
	
	private Scene scen;
	
	public static void main(String[] args) {
		launch(args);
		System.out.print("Main work");

	}

@Override
public void start(Stage primaryStage) throws Exception {

			// synchronizacja z LeapMotion
	FXMLLoader loader = new FXMLLoader();
	loader.setLocation(this.getClass().getResource("/FirstPanel.fxml"));
	StackPane stackPanel = loader.load();
	FirstPanelController firstController = loader.getController();
	firstController.setMainScenView(this);
	scen = new Scene(stackPanel);
	primaryStage.setScene(scen);
//	primaryStage.setFullScreen(true);
	primaryStage.show();
}

public void setStackPanel(StackPane panel) {

	scen.setRoot(panel);
	
}
}
