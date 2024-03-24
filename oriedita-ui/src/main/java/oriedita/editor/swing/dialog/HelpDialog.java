package oriedita.editor.swing.dialog;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.tinylog.Logger;
import oriedita.editor.CanvasUI;
import oriedita.editor.FrameProvider;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.BackgroundModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.FoldedFigureModel;
import oriedita.editor.databinding.FoldedFiguresList;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.service.AnimationService;
import oriedita.editor.service.TaskExecutorService;
import oriedita.editor.swing.component.BulletinBoard;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class HelpDialog {
    private ResourceBundle helpBundle;
    private final Point point = new Point();
    private final FrameProvider frameProvider;
    private final ApplicationModel applicationModel;
    private JPanel contentPane;
    private JTextPane helpLabel;
    private HelpDialogUI helpDialogUI;
    private CanvasUI canvasUI;

    public void setVisible(boolean helpVisible) {
        assert helpDialogUI != null;

        helpDialogUI.setVisible(helpVisible);
    }

    private static class HelpDialogUI extends JDialog {
        public HelpDialogUI(Frame owner, JPanel contentPane, ApplicationModel applicationModel) {
            super(owner, "Help");

            setContentPane(contentPane);


            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    applicationModel.setHelpVisible(false);
                }
            });

            applicationModel.addPropertyChangeListener(e -> {
                if (e.getPropertyName() == null || e.getPropertyName().equals("helpVisible")) {
                    setVisible(applicationModel.getHelpVisible());
                }
                owner.requestFocus();
            });

            setUndecorated(true);

            setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

            // call onCancel() when cross is clicked
            setDefaultCloseOperation(HIDE_ON_CLOSE);
        }
    }

    @Inject
    public HelpDialog(FrameProvider frameProvider, ApplicationModel applicationModel,
                      @Named("creasePatternCamera") Camera creasePatternCamera,
                      @Named("foldingExecutor") TaskExecutorService foldingExecutor,
                      BackgroundModel backgroundModel,
                      CanvasModel canvasModel,
                      @Named("mainCreasePattern_Worker") CreasePattern_Worker mainCreasePatternWorker,
                      AnimationService animationService,
                      BulletinBoard bulletinBoard,
                      FoldedFigureModel foldedFigureModel,
                      FoldedFiguresList foldedFiguresList) {
        this.frameProvider = frameProvider;
        this.applicationModel = applicationModel;
        this.canvasUI = new CanvasUI(creasePatternCamera, foldingExecutor, backgroundModel, canvasModel,
                mainCreasePatternWorker, animationService, applicationModel, bulletinBoard, foldedFigureModel,
                foldedFiguresList);
    }

    public void start(Point canvasLocation, Dimension canvasSize) {
        $$$setupUI$$$();

        canvasUI.setLayout(null);
        canvasUI.setDim(new Dimension(300, 300));
        canvasUI.setMinimumSize(new Dimension(300, 300));
        canvasUI.setPreferredSize(new Dimension(300, 300));
        canvasUI.setData(applicationModel);
        JPanel p = new JPanel();
        p.setMinimumSize(new Dimension(300, 300));
        p.setPreferredSize(new Dimension(300, 300));
        p.add(canvasUI);
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 2.0;
        gbc.weighty = 2.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPane.add(p, gbc);
        helpDialogUI = new HelpDialogUI(frameProvider.get(), contentPane, applicationModel);

        JPopupMenu popup = new JPopupMenu();
        JMenuItem dismissMenuItem = new JMenuItem("Dismiss");
        dismissMenuItem.addActionListener(e -> applicationModel.setHelpVisible(false));

        popup.add(dismissMenuItem);

        // Code to move the dialog by dragging the label.
        helpLabel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                point.x = e.getX();
                point.y = e.getY();

                maybeShowPopup(e);

                frameProvider.get().requestFocus();
            }

            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popup.show(e.getComponent(),
                            e.getX(), e.getY());
                }
            }
        });
        helpLabel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point p = helpDialogUI.getLocation();
                helpDialogUI.setLocation(p.x + e.getX() - point.x,
                        p.y + e.getY() - point.y);
                canvasUI.repaint();
            }
        });


        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> applicationModel.setHelpVisible(false), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        helpBundle = ResourceBundle.getBundle("help");

        helpLabel.setBorder(new EmptyBorder(10, 10, 10, 10));

        helpDialogUI.pack();

        helpDialogUI.setLocation(canvasLocation.x + canvasSize.width - helpDialogUI.getSize().width - 10, canvasLocation.y + 10);

        helpLabel.setText(helpBundle.getString("a__hajimeni"));
    }

    public void setExplanation(String key) {
        helpLabel.setText(processPaths(helpBundle.getString(key)));
    }

    private String processPaths(String helpText) {
        Pattern p = Pattern.compile("src\\s*=\\s*[\"']([^\"']*)[\"']");
        Matcher matcher = p.matcher(helpText);
        return matcher.replaceAll(result -> {
            String path = result.group(1);
            URL resource = HelpDialog.class.getClassLoader().getResource(path);
            if (resource != null) {
                return "src='" + resource + "'";
            }
            Logger.warn("Failed to find resource " + path);
            return "src='' /> Failed to load " + path + "<br";
        });
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridBagLayout());
        contentPane.setAutoscrolls(false);
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setHorizontalScrollBarPolicy(31);
        scrollPane1.setMinimumSize(new Dimension(300, 400));
        scrollPane1.setPreferredSize(new Dimension(300, 400));
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPane.add(scrollPane1, gbc);
        helpLabel = new JTextPane();
        helpLabel.setContentType("text/html");
        helpLabel.setFocusable(false);
        helpLabel.setOpaque(true);
        scrollPane1.setViewportView(helpLabel);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
