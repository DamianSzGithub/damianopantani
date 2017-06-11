package pl.damian;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {

	private static final String ALL_JS = "all.js";
	private static final String TEST_MODE_SUFFIX = "TEST_MODE";
	
	public static void main(String[] args) throws IOException {
		
		mergeJsIn("js/");
		
	    File template = getHtmlTemplate();
	    TestModeOptions options = TestModeOptions.read();
	    String testMode = new TestMode().runTestMode(readFile(template), options);
	    String fileName = template.getName();
	    fileName = fileName.substring(0, fileName.lastIndexOf("."))+TEST_MODE_SUFFIX+".html";
		File file = new File(fileName);
		file.delete();
		file.createNewFile();
	    appendToFile(testMode, fileName);
	}

	public static String readFile(File file) throws IOException {
		  byte[] encoded = Files.readAllBytes(Paths.get(file.getCanonicalPath()));
		  return new String(encoded, StandardCharsets.UTF_8);
	}
	
	private static void mergeJsIn(String path) throws IOException {
		File merged = new File(path+ALL_JS);
		merged.delete();
		merged.createNewFile();
		File[] allFiles = new File(path).listFiles();
		if (allFiles == null){
			throw new IllegalArgumentException("Cant find any file in directory: "+new File(path));
		}
		
		List<File> fileList = sortScripts(allFiles);
		
		for (File js: fileList){
			appendToFile(readFile(js), merged.getAbsolutePath());
		}
	}

	private static List<File> sortScripts(File[] allFiles) {
		List<File> results = new ArrayList<>();
		for (File f: allFiles){
			if (f.getName().equals("jquery.min.js")){
				results.add(f);
			}
		}
		for (File f: allFiles){
			if (f.getName().equals("bootstrap.min.js")){
				results.add(f);
			}
		}
		for (File f: allFiles){
			if (!f.getName().equals("main.js") && !f.getName().equals(ALL_JS) && !results.contains(f)){
				results.add(f);
			}
		}
		for (File f: allFiles){
			if (f.getName().equals("main.js")){
				results.add(f);
			}
		}
		return results;
	}

	private static File getHtmlTemplate() throws IOException {
		List<File> htmls = getHtmlFilesOfCurrentDir();
		for(File html: htmls){
			if (!html.getName().contains(TEST_MODE_SUFFIX)){
				return html;
			}
		}
		throw new IllegalArgumentException("CAN'T FIND SUITABLE HTML TEMPLATE FILE");
		
	}

	private static void appendToFile(String string, String fileName) throws IOException {
		BufferedWriter writter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, true), StandardCharsets.UTF_8));
		writter.write(string);
		writter.close();
	}
	
	private static List<File> getHtmlFilesOfCurrentDir() throws IOException {
		List<File> files = new ArrayList<>();
		for (File file : new File(".").listFiles()) {
	        if (!file.isDirectory() && file.getAbsolutePath().endsWith("html")) {
	        	files.add(file);
	        }
	    }
		
		return files;
	}

}
