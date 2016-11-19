package app;
import java.io.File;
import java.util.ListIterator;

import com.interactivemesh.jfx.importer.stl.StlMeshImporter;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point3D;
import javafx.scene.SubScene;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Spinner;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.Node;
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
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

public class MainPanelController {
	@FXML
	private SubScene _subscen;
	@FXML
	private GridPane cameraGrid;

	private final Rotate boxRotateX = new Rotate(0, Rotate.X_AXIS);
	private final Rotate boxRotateY = new Rotate(0, Rotate.Y_AXIS);
	private final Rotate boxRotateZ = new Rotate(0, Rotate.Z_AXIS);
	private final Rotate floorRotateX = new Rotate(0, Rotate.X_AXIS);
	private final Rotate floorRotateY = new Rotate(0, Rotate.Y_AXIS);
	private final Rotate floorRotateZ = new Rotate(0, Rotate.Z_AXIS);

	private final Group group = new Group();
	final Group root = new Group();
	private XformWorld world;
	final PerspectiveCamera camera = new PerspectiveCamera(true);
	final XformCamera cameraXform = new XformCamera();
	final HelperWorld helperWorld = new HelperWorld();
	private static final double CAMERA_INITIAL_DISTANCE = -1000;
	private static final double CAMERA_NEAR_CLIP = 0.1;
	private static final double CAMERA_FAR_CLIP = 10000.0;
	private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
	private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
	private final Rotate rotateZ = new Rotate(0, Rotate.Z_AXIS);
//	private Translate translate = new Translate(0, -50, -1200);
//	private final double sceneWidth = 800;
//	private final double sceneHeight = 600;
	private Sphere sphere = new Sphere(90);
	private Cylinder cylinder = new Cylinder(30,100);
	private Box box = new Box(100, 100, 100);
	private Box floor = new Box(1000, 10, 1000);

	private double mouseOldX = 0;
	private double mouseOldY = 0;
	private double mousePosX = 0 ;
	private double mousePosY = 0 ;
	private double mouseDeltaX = 0 ;
	private double mouseDeltaY = 0 ;
	 //------------------------------------
    private static final String MESH_FILENAME = "/home/ariel/workspace/leapMotion/bin/skorupa7.stl";
	  //---------------------------------------------

	@FXML
	void onClose() {
		Platform.exit();
	} 

	@FXML
	public void initialize()
	{
		world = helperWorld.getWorld();
		
		System.out.println("MainPanelControllr initialize");
		root.getChildren().add(world);
        root.setDepthTest(DepthTest.ENABLE);
        buildCamera();
//        buildBodySystem();
		// 3D objekty aplikacji: kula, sześcian, podłoga
		sphere.setTranslateX(0);
		sphere.setMaterial(new PhongMaterial(Color.AQUA));
		cylinder.getTransforms().addAll(new Translate(200,0,0));
		box.getTransforms().addAll(boxRotateX, boxRotateY, boxRotateZ, new Translate(100, 0, 0));
		box.setMaterial(new PhongMaterial(Color.RED));
		
		floor.getTransforms().addAll(floorRotateX, floorRotateY, floorRotateZ, new Translate(0,100, 0));
		floor.setMaterial(new PhongMaterial(Color.CORNSILK));
//
//		// dodanie obiektów do głównej grupy obiektów aplikacji
//		world.getChildren().add(floor);
		root.getChildren().addAll(floor);
//		world.getChildren().add(box);
//		world.getChildren().add(sphere);
//		world.getChildren().add(cylinder);
//		creatobiektSTL();


		// tworzenie sceny wraz z obiektami
		_subscen.setRoot(root);
		_subscen.setFill(Color.GREY);
		 handleMouse(_subscen);
		 
//		 Shape3D shepe = box;
//		System.out.println("class name" + shepe.getClass().getName());
		
//		ListIterator<Node> it =world.getChildren().listIterator();
//
//		while(it.hasNext())
//		{
//			Node pom = it.next();
//			System.out.println(pom.getClass().getSimpleName());
//			
//		}



		_subscen.setCamera(camera);

//		helperWorld.setStage(_subscen.getScene().getWindow());

		//--------------------------------
		//			primaryStage.setScene(scene);
		//			primaryStage.show();
	}    
	private void handleMouse(SubScene scene) {
		scene.setOnMousePressed((MouseEvent me) -> {
			mousePosX = me.getSceneX();
			mousePosY = me.getSceneY();
			mouseOldX = me.getSceneX();
			mouseOldY = me.getSceneY();
		});
		scene.setOnMouseDragged((MouseEvent me) -> {
			mouseOldX = mousePosX;
			mouseOldY = mousePosY;
			mousePosX = me.getSceneX();
			mousePosY = me.getSceneY();
			mouseDeltaX = (mousePosX - mouseOldX);
			mouseDeltaY = (mousePosY - mouseOldY);
			if (me.isPrimaryButtonDown()) {
				cameraXform.ry(mouseDeltaX * 180.0 / scene.getWidth());
				cameraXform.rx(-mouseDeltaY * 180.0 / scene.getHeight());
			} else if (me.isSecondaryButtonDown()) {
				camera.setTranslateZ(camera.getTranslateZ() + mouseDeltaY);
			}
			
		});
	}
	private void buildCamera() {
		root.getChildren().add(cameraXform);
		cameraXform.getChildren().add(camera);
		camera.setNearClip(CAMERA_NEAR_CLIP);
		camera.setFarClip(CAMERA_FAR_CLIP);
		camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
	}
	private void buildBodySystem() {
		PhongMaterial whiteMaterial = new PhongMaterial();
		whiteMaterial.setDiffuseColor(Color.WHITE);
		whiteMaterial.setSpecularColor(Color.LIGHTBLUE);
		Box box = new Box(400, 200, 100);
		box.setMaterial(whiteMaterial);
		PhongMaterial redMaterial = new PhongMaterial();
		redMaterial.setDiffuseColor(Color.DARKRED);
		redMaterial.setSpecularColor(Color.RED);
		Sphere sphere = new Sphere(5);
		sphere.setMaterial(redMaterial);
		sphere.setTranslateX(200.0);
		sphere.setTranslateY(-100.0);
		sphere.setTranslateZ(-50.0);
		world.getChildren().addAll(box);
		world.getChildren().addAll(sphere);
	}
	
	
	private void creatobiektSTL()
    {

		File file = new File(MESH_FILENAME);
		StlMeshImporter importer = new StlMeshImporter();
		importer.read(file);
		Mesh mesh = importer.getImport();
		MeshView[] meshViews = new MeshView[] { new MeshView(mesh) };
		world.getChildren().addAll(meshViews);
    }

	@FXML
	void onAddBox() {
		helperWorld.onAddBox();
	}

	@FXML
	void onAddCylinder() {
		helperWorld.onAddCylinder();
	}

	@FXML
	void onAddSphere() {
		helperWorld.onAddSphere();
	}
	
	@FXML
	void onAddCustom()
	{
		helperWorld.loadCustomObiect();
	}
	
	@FXML
	void onSaveAss() {
		helperWorld.SaveScen();
	}	

	
}
