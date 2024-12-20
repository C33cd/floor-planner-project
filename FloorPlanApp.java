import javax.swing.*;
import java.awt.*;

public class FloorPlanApp extends JFrame {
    private CanvasPanel canvasPanel;
    private ControlPanel controlPanel;

    public FloorPlanApp() {
        setTitle("2D Floor Plan Designer");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        

        canvasPanel = new CanvasPanel();
        controlPanel = new ControlPanel(canvasPanel);
        canvasPanel.setControlPanel(controlPanel);

        add(canvasPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FloorPlanApp app = new FloorPlanApp();
            app.setExtendedState(JFrame.MAXIMIZED_BOTH);
            app.setVisible(true);
        });
    }
}
