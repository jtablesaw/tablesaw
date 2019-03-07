package tech.tablesaw.plotly.display;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;

public class Browser {

    public static void main(String[] args) throws Exception {

        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(new URI("http://www.example.com"));
        }
    }

    public void browse(File file) throws IOException {
        if (Desktop.isDesktopSupported()) {

            Desktop.getDesktop().browse(file.toURI());
        }
        else {
            throw new UnsupportedOperationException("Browser not supported.");
        }
    }
}
