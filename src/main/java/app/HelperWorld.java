package app;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;

import com.interactivemesh.jfx.importer.stl.StlMeshImporter;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HelperWorld {

	final XformWorld world = new XformWorld();	
	final FileChooser fileChooser = new FileChooser();
//	private Vector<Pair<Shape3D, String>> saveVector = new Vector<Pair<Shape3D,String>>();
	private Map<Integer,String> mapPath = new HashMap();
	private Integer countObiect = 0;
	private Window stage;

	public HelperWorld() {
	}
	public void setStage(Window stage) {
		this.stage = stage;
	}

	public XformWorld getWorld() {
		return world;
	}


	public void loadScen()
	{
		fileChooser.getExtensionFilters().clear();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
		fileChooser.getExtensionFilters().add(extFilter);

		File file = fileChooser.showOpenDialog(stage);
		if (file != null) {
			//tu ładowanie pliku z jsona
		}	

	}

	public void SaveScen()
	{
		//Ustawienie jakiego typu pliki mają być otwierane;
		fileChooser.getExtensionFilters().clear();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
		fileChooser.getExtensionFilters().add(extFilter);
		//Pokazanie Dlialogu zapisu;
		File file = fileChooser.showSaveDialog(stage);

		if(file != null){
			String pom = convertToJson();
			SaveFile(pom, file);
		}	
		

	}
	private String convertToJson()
	{
		JSONArray objArr = new JSONArray();

		ListIterator<Node> it =world.getChildren().listIterator();
		// petla przechodzenia po Liscie Obiektów;
		while(it.hasNext())
		{
			Node pom = it.next();
			//parsowanie pojedynczego obiektu;
			JSONObject obj = convertToJsonObj(pom);
			objArr.put(obj);
		}

		return objArr.toString();
	}
	
	private JSONObject convertToJsonObj( Node node)
	{
		 JSONObject obj = new JSONObject();
		 String name = node.getClass().getSimpleName();
		 try {
			obj.put("name",name);
			obj.put("x", node.getTranslateX());
			obj.put("y", node.getTranslateY());
			obj.put("z", node.getTranslateZ());
			//Sprawdzenie czy obiekt jest typu Importowanego; 
			if(name.compareTo("MeshView")==0)
			{
				countObiect -=1;
				//odczytujemy ścieżkę skąd został załadowany obiekt
				String pom = mapPath.get(countObiect);
			    obj.put("file", pom);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 return obj;
	}
	
	private void SaveFile(String content, File file){
		try {
			FileWriter fileWriter = null;
			fileWriter = new FileWriter(file);
			fileWriter.write(content);
			fileWriter.close();
		} catch (IOException ex) {
		}

	}	

	public void loadCustomObiect()
	{
		fileChooser.getExtensionFilters().clear();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("STL files (*.stl)", "*.stl");
		fileChooser.getExtensionFilters().add(extFilter);

		File file = fileChooser.showOpenDialog(stage);
		if (file != null) {
			openFile(file);
		}	
	}

	private void openFile(File file) {

		StlMeshImporter importer = new StlMeshImporter();
		importer.read(file);
		Mesh mesh = importer.getImport();
		MeshView[] meshViews = new MeshView[] { new MeshView(mesh) };
		System.out.println(file.getPath());

		mapPath.put(countObiect,file.getPath());
		countObiect+=1;
		world.getChildren().addAll(meshViews);
	}

	void onAddBox() {
		Box box = new Box(100, 100, 100);
		box.setTranslateX(100);
		box.setTranslateY(0);
		box.setTranslateZ(0);
		world.getChildren().add(box);
	}

	void onAddCylinder() {
		Cylinder cylinder = new Cylinder(30,100);
		cylinder.setTranslateX(300);
		cylinder.setTranslateY(0);
		cylinder.setTranslateZ(0);

		world.getChildren().add(cylinder);
	}

	void onAddSphere() {
		Sphere sphere = new Sphere(90);
		sphere.setTranslateX(200);
		sphere.setTranslateY(0);
		sphere.setTranslateZ(0);

		world.getChildren().add(sphere);
	}
}
