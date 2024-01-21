package view.gui.frame;

import view.gui.panel.ControllersPanel;
import view.gui.panel.ImageDisplayPanel;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JPanel imageDisplayPanel;
    private JPanel controllersPanel;

    public MainFrame(JPanel imageDisplayPanel, JPanel controllersPanel) {
        this.imageDisplayPanel = imageDisplayPanel;
        this.controllersPanel = controllersPanel;
        this.setLayout(new GridBagLayout());
        //将imageDisplayPanel放在左边,controllersPanel放在右边
        this.add(this.imageDisplayPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 0), 0, 0));
        this.add(this.controllersPanel, new GridBagConstraints(1, 0, 1, 1, 0, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    public static void main(String[] args) {
        ImageDisplayPanel imageDisplayPanel = new ImageDisplayPanel();
        MainFrame mainFrame = new MainFrame(imageDisplayPanel, new ControllersPanel(imageDisplayPanel));
        mainFrame.setVisible(true);
    }
}
