package tech.tablesaw.api.plotjs;

import tech.tablesaw.columns.Column;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class JsPlot {

    static File outputFile() {
        String folder = "testoutput";
        String fileName = "output.html";
        Path path = Paths.get(folder);
        try {
            Files.createDirectories(path);
        } catch (IOException e){
            e.printStackTrace();
        }

        return Paths.get(folder + "/" + fileName).toFile();
    }

    static String[] columnToStringArray(Column numberColumn) {
        String[] x = new String[numberColumn.size()];
        for (int i = 0; i < numberColumn.size(); i++) {
            x[i] = numberColumn.getString(i);
        }
        return x;
    }

}
