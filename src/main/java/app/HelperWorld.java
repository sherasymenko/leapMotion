package app;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ListIterator;

import com.interactivemesh.jfx.importer.stl.StlMeshImporter;

import javafx.scene.Node;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

public class HelperWorld {
	
	final XformWorld world = new XformWorld();	
	final FileChooser fileChooser = new FileChooser();
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
		//Set extension filter
        fileChooser.getExtensionFilters().clear();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);
        //Show save file dialog
        File file = fileChooser.showSaveDialog(stage);
        
        if(file != null){
            SaveFile("ala ma kota", file);
        }	

		
		
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
		    world.getChildren().addAll(meshViews);
    }
	

}
