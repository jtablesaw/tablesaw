package tech.tablesaw.plotly;

import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.display.Browser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Displays plots in a development setting, by exporting a file containing the HTML and Javascript, and then opening
 * the file in the default browser on the developer's machine.
 */
public class Plot {

    private static final String DEFAULT_DIV_NAME = "target";
    private static final String DEFAULT_OUTPUT_FILE = "output.html";
    private static final String DEFAULT_OUTPUT_FOLDER = "testoutput";

    public static void show(Figure figure, String divName, File outputFile) {
        String output = figure.asJavascript(divName);

        try {
            try (FileWriter fileWriter = new FileWriter(outputFile)) {
                fileWriter.write(output);
            }
            new Browser().browse(outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void show(Figure figure, String divName) {
        show(figure, divName, defaultFile());
    }

    public static void show(Figure figure) {
        show(figure, defaultFile());
    }

    public static void show(Figure figure, File outputFile) {
        show(figure, DEFAULT_DIV_NAME, outputFile);
    }

    public static File defaultFile() {
        Path path = Paths.get(DEFAULT_OUTPUT_FOLDER, DEFAULT_OUTPUT_FILE);
        try {
            Files.createDirectories(path);
        } catch (IOException e){
            e.printStackTrace();
        }
        return path.toFile();
    }
}
