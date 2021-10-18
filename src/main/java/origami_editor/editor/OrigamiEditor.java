package origami_editor.editor;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.io.File;

public class OrigamiEditor {
    public static void main(String[] argv) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        SwingUtilities.invokeLater(() -> {
            FlatLaf.registerCustomDefaultsSource( "origami_editor.editor.themes" );
            FlatLightLaf.setup();

            App app = new App();//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<Rewrite location
            app.start();

            if (argv.length == 1) {
                // We got a file
                app.openFile(new File(argv[0]));
            }
        });
    }
}
