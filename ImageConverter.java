import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ImageConverter extends Application {
	private static final Paint bg = Color.AZURE;
	
	public void start (Stage stage) {
		Scene converterScene = makeScene();
		stage.setScene(converterScene);
		stage.setTitle("Mif Convert");
		stage.show();
	}
	
	private Scene makeScene() {
		Button fileLoad = new Button("Choose Image");
		Scene toReturn = new Scene(fileLoad, 400, 40, bg);
		fileLoad.setPrefWidth(400);
		fileLoad.setPrefHeight(40);
		fileLoad.setLayoutX(0);
		fileLoad.setLayoutY(0);
		fileLoad.setOnAction(click->{
			initiate();
			});
		return toReturn;
	}
	
	private void initiate() {
		File toConvert = getFile();
		if(!(toConvert == null)) {
			BufferedImage image = getImage(toConvert);
			convert(image);
		}
	}
	
	private File getFile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose Game File");
		Stage tempStage = new Stage();
		File file = fileChooser.showOpenDialog(tempStage);
		return file;
	}
	
	private BufferedImage getImage(File file) {
		BufferedImage img = null;
		try {
		    img = ImageIO.read(file);
		} catch (IOException e) {
		}
		return img;
	}
	
	private void convert(BufferedImage toConvert) {
		ArrayList<String> colors = new ArrayList<String>();
		ArrayList<String> pixels = new ArrayList<String>();
		for (int y=0; y<toConvert.getHeight(); y++) {
            for (int x=0; x<toConvert.getWidth(); x++) {
                Integer iso = toConvert.getRGB(x, y);
                Integer red = (iso & 0xff0000) >> 16;
                Integer green = (iso & 0x00ff00) >> 8;
                Integer blue = iso & 0x0000ff;
                //ROUNDING HERE
                //65536  //255
      
                //iso = iso-iso%65536;		//Rough functionality
                
//                red = red-red%41;
//                green = green-green%41;
//                blue = blue-blue%41;
                
                red = red-red%30;
                green = green-green%30;
                blue = blue-blue%30;
                iso = (red << 16)+(green << 8)+blue;
                
                //END ROUNDING
                String ncolor = iso.toString();
                if(!colors.contains(ncolor)) {
                	colors.add(ncolor);
                }
                Integer loc = colors.indexOf(ncolor);
                pixels.add(loc.toString());
            }
        }
		// Create imgdata array
		ArrayList<String> toData = new ArrayList<String>();
		setFormat(toData);
		int fin = 0;
		for (int i=0;i<pixels.size();i++) {
			toData.add(""+i+" : "+pixels.get(i)+";");
			fin = i;
		}
		fin++;
		if(fin<307199) {
			toData.add("["+fin+"..307199] : 0;");
		}
		toData.add("END;");
		
		// Create imgindex array
		ArrayList<String> toIndex = new ArrayList<String>();
		setFormat(toIndex);
		fin = 0;
		for (int i=0;i<colors.size();i++) {
			toIndex.add(""+i+" : "+colors.get(i)+";");
			fin = i;
		}
		fin++;
		if(fin<307199) {
			toData.add("["+fin+"..307199] : 0;");
		}
		toIndex.add("END;");		
		writeFile(toData, toIndex);
	}
	
	private void setFormat(ArrayList<String> list) {
		list.add("WIDTH=24;");
		list.add("DEPTH=307200;");
		list.add("ADDRESS_RADIX=UNS;");
		list.add("DATA_RADIX=DEC;");
		list.add("CONTENT BEGIN");
	}
	
	private void writeFile(ArrayList<String> toData, ArrayList<String> toIndex) {
		// Write first file
		Path file1 = Paths.get("imgdata.mif");
		try {
			Files.write(file1, toData, Charset.forName("UTF-8"));
		} catch (IOException e) {
		}
		
		// Write second file
		Path file2 = Paths.get("imgindex.mif");
		try {
			Files.write(file2, toIndex, Charset.forName("UTF-8"));
		} catch (IOException e) {
		}
		finale();
	}
	
	private void finale() {
		Stage nstage = new Stage();
		Button text = new Button("DONE :)");
		Scene tscene = new Scene(text,200,200,bg);
		nstage.setScene(tscene);
		nstage.show();
	}
	
	public static void main (String[] args) {
        launch(args);
    }
}
