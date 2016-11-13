package app;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.SubScene;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Spinner;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

public class MainPanelController {
	@FXML
	private SubScene _subscen;
	  @FXML
	    private GridPane cameraGrid;
	
	private Spinner<Integer> spinX;
	private Spinner<Integer> spinY;
	private Spinner<Integer> spinZ; 
	private Spinner<Integer> transX; 
	private Spinner<Integer> transY; 
	private Spinner<Integer> transZ; 

	private final Rotate boxRotateX = new Rotate(0, Rotate.X_AXIS);
	private final Rotate boxRotateY = new Rotate(0, Rotate.Y_AXIS);
	private final Rotate boxRotateZ = new Rotate(0, Rotate.Z_AXIS);
	private final Rotate floorRotateX = new Rotate(0, Rotate.X_AXIS);
	private final Rotate floorRotateY = new Rotate(0, Rotate.Y_AXIS);
	private final Rotate floorRotateZ = new Rotate(0, Rotate.Z_AXIS);

	private final Group group = new Group();
	private PerspectiveCamera camera;
	private Scene scene;
	private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
	private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
	private final Rotate rotateZ = new Rotate(0, Rotate.Z_AXIS);
	private Translate translate = new Translate(0, -50, -1200);
	private final double sceneWidth = 800;
	private final double sceneHeight = 600;
	private Sphere sphere = new Sphere(90);
	private Cylinder cylinder = new Cylinder(30,100);
	private Box box = new Box(100, 100, 100);
	private Box floor = new Box(1000, 10, 1000);
	
	@FXML
    void onClose() {
		Platform.exit();
    } 

	 @FXML
	 public void initialize()
	 {
		 System.out.println("MainPanelControllr initialize");
		 initialSpinBox();
	// 3D objekty aplikacji: kula, sześcian, podłoga
		sphere.setTranslateX(300);
//		sphere.setTranslateY(0);
//		sphere.setTranslateZ(0);
		
			sphere.setMaterial(new PhongMaterial(Color.AQUA));
			
			cylinder.getTransforms().addAll(new Translate(200,0,0));
			
			
			box.getTransforms().addAll(boxRotateX, boxRotateY, boxRotateZ, new Translate(100, 0, 0));
			box.setMaterial(new PhongMaterial(Color.RED));
			
			floor.getTransforms().addAll(floorRotateX, floorRotateY, floorRotateZ, new Translate(0, 0, 0));
			floor.setMaterial(new PhongMaterial(Color.CORNSILK));

			// dodanie obiektów do głównej grupy obiektów aplikacji
			group.getChildren().add(box);
			group.getChildren().add(sphere);
			group.getChildren().add(cylinder);
			group.getChildren().add(floor);

			group.setDepthTest(DepthTest.ENABLE);
//			group.setTranslateX(0);
//			group.setTranslateY(0);
//			group.setTranslateZ(0);

			// tworzenie sceny wraz z obiektami
			_subscen.setRoot(group);

			

			// ustawienia kamery, kąta widzenia, itd.
			camera = new PerspectiveCamera(true);
			camera.setVerticalFieldOfView(false);
			camera.setNearClip(0.1);
			camera.setFarClip(10000.0);
//			camera.getTransforms().addAll(rotateX, rotateY, new Translate(0, 0, -1050));
//			camera.getTransforms().addAll(new Translate(0, 0, -1050));
//			camera.setTranslateX(200);
//			camera.setTranslateY(200);
//			camera.setTranslateZ(200); 
			camera.setFieldOfView(40);
			camera.getTransforms().addAll(
	                rotateY ,
	                rotateX ,
	                rotateZ ,
	                translate 
	        );
			// ustawienie głownej kameery, wybór kolor tła sceny, ustawienie głownej
			// sceny
			_subscen.setCamera(camera);
			_subscen.setHeight(1000);
			_subscen.setWidth(1000);
			_subscen.setFill(Color.BLANCHEDALMOND);
//			primaryStage.setScene(scene);
//			primaryStage.show();
	 }

	 private void initialSpinBox()
	 {

		 spinX = new Spinner<Integer>(-180, 180, 0);
		 spinX.setEditable(true);
		 spinX.valueProperty().addListener((s, ov, nv) -> {
//			Rotate rx = new Rotate(nv-ov, Rotate.X_AXIS);
//			camera.getTransforms().add(rx); 
			 rotateX.setAngle(rotateX.getAngle()+(nv-ov));
		 } );
		 
		 spinY = new Spinner<Integer>(-180, 180, 0);
		 spinY.setEditable(true);
		 spinY.valueProperty().addListener((s, ov, nv) -> {
//			Rotate rx = new Rotate(nv-ov, Rotate.Y_AXIS);
//			camera.getTransforms().add(rx); 
			 rotateY.setAngle(rotateY.getAngle()+(nv-ov));
		 } );
		 
		 spinZ = new Spinner<Integer>(-180, 180, 0);
		 spinZ.setEditable(true);
		 spinZ.valueProperty().addListener((s, ov, nv) -> {
//			Rotate rx = new Rotate(nv-ov, Rotate.Z_AXIS);
//			camera.getTransforms().add(rx); 
			 rotateZ.setAngle(rotateZ.getAngle()+(nv-ov));
		 } );
		 
		 transX = new Spinner<Integer>(-10000,10000,0);
		 transX.setEditable(true);
		 transY = new Spinner<Integer>(-10000,10000,0);
		 transY.setEditable(true);
		 transZ = new Spinner<Integer>(-10000,10000,0);
		 transZ.setEditable(true);

		 transX.valueProperty().addListener((s, ov, nv) -> {
			Integer rx = nv-ov;
			Double oldX = camera.getTranslateX();
			camera.setTranslateX(oldX+rx);; 
		 } );
		 
		 transY.valueProperty().addListener((s, ov, nv) -> {
			Integer rx = nv-ov;
			Double oldX = camera.getTranslateY();
			camera.setTranslateY(oldX+rx);; 
		 } );
		 
		 
		 transZ.valueProperty().addListener((s, ov, nv) -> {
			Integer rx = nv-ov;
			Double oldX = camera.getTranslateZ();
			camera.setTranslateZ(oldX+rx);; 
		 } );
		 cameraGrid.add(spinX,1,0);
		 cameraGrid.add(spinY,1,1);
		 cameraGrid.add(spinZ,1,2);
		 
		 cameraGrid.add(transX,1,3);
		 cameraGrid.add(transY,1,4);
		 cameraGrid.add(transZ,1,5);
		 
	 }
	 
	 @FXML
	    void onAddBox() {

		 System.out.println("MainPanelControllr onAddBox");
	          Box box = new Box(100, 100, 100);
	          box.setTranslateX(100);
	          box.setTranslateY(0);
	          box.setTranslateZ(0);
	          group.getChildren().add(box);
	          
	      
	    }
	 
	 @FXML
	 void onAddCylinder() {
		 System.out.println("MainPanelControllr Cylinder");
		 Cylinder cylinder = new Cylinder(30,100);
		 cylinder.setTranslateX(300);
		 cylinder.setTranslateY(0);
		 cylinder.setTranslateZ(0);
		 group.getChildren().add(cylinder);
	 }

	    @FXML
	    void onAddSphere() {
	    	System.out.println("MainPanelControllr Sphere");
	    	Sphere sphere = new Sphere(90);
	    	sphere.setTranslateX(200);
	    	sphere.setTranslateY(0);
	    	sphere.setTranslateZ(0);
	    	group.getChildren().add(sphere);

	    }

}
