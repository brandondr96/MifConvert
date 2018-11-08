import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ImageConverter extends Application {
	private static final Paint bg = Color.AZURE;
	private static final Paint fn = Color.YELLOW;
	private Stage mstage;
	
	/**
	 * Sets the stage for the converter
	 */
	public void start (Stage stage) {
		mstage = stage;
		Scene converterScene = makeScene();
		stage.setScene(converterScene);
		stage.setTitle("Mif Convert");
		stage.show();
	}
	
	/**
	 * Sets the scene and button for the converter
	 * @return The completed scene
	 */
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
	
	/**
	 * Checks to ensure that the file chosen is valid
	 */
	private void initiate() {
		File toConvert = getFile();
		if(!(toConvert == null)) {
			mstage.setTitle("Loading...");
			BufferedImage image = getImage(toConvert);
			convert(image, 1);
		}
	}
	
	/**
	 * Opens the file chooser for the user
	 * @return Desired user file
	 */
	private File getFile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose Game File");
		Stage tempStage = new Stage();
		File file = fileChooser.showOpenDialog(tempStage);
		return file;
	}
	
	/**
	 * Converts the file into the image for processing
	 * @param file The input file
	 * @return The image
	 */
	private BufferedImage getImage(File file) {
		BufferedImage img = null;
		try {
		    img = ImageIO.read(file);
		} catch (IOException e) {
		}
		return img;
	}
	
	/**
	 * Converts the image into the data and index arrays for color values
	 * @param toConvert The input image
	 * @param n The initial conversion factor for the colors
	 */
	private void convert(BufferedImage toConvert, int n) {
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
                
                if(red%n<n/2) {
                	red = red-red%n;
                }
                else {
                	red = red+(n-red%n);
                }
                if(green%n<n/2) {
                	green = green-green%n;
                }
                else {
                	green = green+(n-green%n);
                }
                if(blue%n<n/2) {
                	blue = blue-blue%n;
                }
                else {
                	blue = blue+(n-blue%n);
                }
                iso = (blue << 16)+(green << 8)+red;
                
                //END ROUNDING
                String ncolor = iso.toString();
                if(!colors.contains(ncolor)) {
                	colors.add(ncolor);
                }
                Integer loc = colors.indexOf(ncolor);
                pixels.add(loc.toString());
            }
        }
		if(colors.size()>256) {
			convert(toConvert, n+1);
		}
		else {
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
	}
	
	/**
	 * Adds the correct formatting for the .mif file
	 * @param list The current array of data to be printed
	 */
	private void setFormat(ArrayList<String> list) {
		list.add("WIDTH=24;");
		list.add("DEPTH=307200;");
		list.add("ADDRESS_RADIX=UNS;");
		list.add("DATA_RADIX=DEC;");
		list.add("CONTENT BEGIN");
	}
	
	/**
	 * Write the desired data into the .mif files
	 * @param toData The image data array
	 * @param toIndex The image index array
	 */
	private void writeFile(ArrayList<String> toData, ArrayList<String> toIndex) {
		// Write first file
		Path file1 = Paths.get("imgdata.mif");
		try {
			Files.write(file1, toData, Charset.forName("UTF-8"));
		} catch (IOException e) {
			//Handle exception
		}
		
		// Write second file
		Path file2 = Paths.get("imgindex.mif");
		try {
			Files.write(file2, toIndex, Charset.forName("UTF-8"));
		} catch (IOException e) {
			//Handle exception
		}
		finale();
	}
	
	/**
	 * Notify the user that the file has been successfully converted
	 */
	private void finale() {
		mstage.setTitle("Mif Convert");
		Stage nstage = new Stage();
		Button text = new Button("DONE :)");
		Scene tscene = new Scene(text,200,100,fn);
		nstage.setScene(tscene);
		nstage.show();
	}
	
	/**
	 * Main function for the program
	 * @param args
	 */
	public static void main (String[] args) {
        launch(args);
    }
}
