
import view.swing.SwingGameView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SwingGameView().show());
    }
}