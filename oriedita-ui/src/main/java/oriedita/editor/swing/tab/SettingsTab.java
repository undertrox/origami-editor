package oriedita.editor.swing.tab;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.action.ActionType;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.BackgroundModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.GridModel;
import oriedita.editor.service.ButtonService;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

@ApplicationScoped
public class SettingsTab {
    private JPanel root;
    private JButton toggleDiagonalButton;
    private JTextField gridAngleTextField;
    private JButton resetButton;
    private JButton moveVerticallyButton;
    private JButton moveHorizontallyButton;
    private JTextField gridXATextField;
    private JLabel ratioLabel1;
    private JTextField gridXBTextField;
    private JLabel ratioLabel2;
    private JTextField gridXCTextField;
    private JTextField gridYATextField;
    private JLabel ratioLabel3;
    private JTextField gridYBTextField;
    private JLabel ratioLabel4;
    private JTextField gridYCTextField;
    private JTextField lineOffsetTextField;
    private JCheckBox showCheckBox;
    private JButton selectButton;
    private JButton transparentButton;
    private JCheckBox lockCheckBox;
    private JButton moveButton;
    private JButton trimButton;
    private JButton twoColorButton;
    private JCheckBox persistCheckBox;
    private JButton frameButton;
    private JCheckBox selectAnd3ClickCheckbox;
    private JSlider lineThicknessSlider;
    private JSlider errorOpacitySlider;

    private final ButtonService buttonService;
    private final GridModel gridModel;
    private final BackgroundModel backgroundModel;
    private final ApplicationModel applicationModel;
    private final CanvasModel canvasModel;

    @Inject
    public SettingsTab(ButtonService buttonService,
                       GridModel gridModel,
                       BackgroundModel backgroundModel,
                       ApplicationModel applicationModel, CanvasModel canvasModel) {
        this.buttonService = buttonService;
        this.gridModel = gridModel;
        this.backgroundModel = backgroundModel;
        this.applicationModel = applicationModel;
        this.canvasModel = canvasModel;
    }

    public void init() {
        buttonService.addDefaultListener($$$getRootComponent$$$());

        backgroundModel.addPropertyChangeListener(e -> setData(backgroundModel));
        applicationModel.addPropertyChangeListener(e -> setData(applicationModel));
        canvasModel.addPropertyChangeListener(e -> setData(canvasModel));

        setData(applicationModel);

        buttonService.setIcon(ratioLabel1, "labelPlus");
        buttonService.setIcon(ratioLabel2, "labelSqrt");
        buttonService.setIcon(ratioLabel3, "labelPlus");
        buttonService.setIcon(ratioLabel4, "labelSqrt");

        gridModel.bind(gridAngleTextField, "gridAngle");
        gridModel.bind(lineOffsetTextField, "intervalGridSize");
        gridModel.bind(gridXATextField, "gridXA");
        gridModel.bind(gridXBTextField, "gridXB");
        gridModel.bind(gridXCTextField, "gridXC");
        gridModel.bind(gridYATextField, "gridYA");
        gridModel.bind(gridYBTextField, "gridYB");
        gridModel.bind(gridYCTextField, "gridYC");

        buttonService.registerTextField(gridAngleTextField, ActionType.setGridAngleAction.action());
        buttonService.registerTextField(lineOffsetTextField, ActionType.setIntervalGridSizeAction.action());
        buttonService.registerTextField(gridXATextField, ActionType.setGridXAction.action());
        buttonService.registerTextField(gridXBTextField, ActionType.setGridXAction.action());
        buttonService.registerTextField(gridXCTextField, ActionType.setGridXAction.action());
        buttonService.registerTextField(gridYATextField, ActionType.setGridYAction.action());
        buttonService.registerTextField(gridYBTextField, ActionType.setGridYAction.action());
        buttonService.registerTextField(gridYCTextField, ActionType.setGridYAction.action());

        errorOpacitySlider.addChangeListener(e -> applicationModel.setCheck4ColorTransparency(errorOpacitySlider.getValue()));
        lineThicknessSlider.addChangeListener(e -> applicationModel.setLineWidth(lineThicknessSlider.getValue()));
    }

    private void setData(CanvasModel canvasModel) {
        selectAnd3ClickCheckbox.setSelected(canvasModel.isCkbox_add_frame_SelectAnd3click_isSelected());
    }

    private void setData(ApplicationModel applicationModel) {
        persistCheckBox.setSelected(applicationModel.getSelectPersistent());
        errorOpacitySlider.setValue(applicationModel.getCheck4ColorTransparency());
        lineThicknessSlider.setValue(applicationModel.getLineWidth());
    }

    private void setData(BackgroundModel backgroundModel) {
        lockCheckBox.setSelected(backgroundModel.isLockBackground());
        showCheckBox.setSelected(backgroundModel.isDisplayBackground());
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
        root = new JPanel();
        root.setLayout(new GridLayoutManager(13, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("Canvas");
        root.add(label1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        root.add(spacer1, new GridConstraints(12, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        root.add(spacer2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 7), null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Grid");
        root.add(label2, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        root.add(spacer3, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 7), null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        root.add(panel1, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        toggleDiagonalButton = new JButton();
        toggleDiagonalButton.setActionCommand("drawDiagonalGridlinesAction");
        toggleDiagonalButton.setText("toggleDiagonal");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(toggleDiagonalButton, gbc);
        gridAngleTextField = new JTextField();
        gridAngleTextField.setMinimumSize(new Dimension(40, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(gridAngleTextField, gbc);
        resetButton = new JButton();
        resetButton.setActionCommand("gridConfigureResetAction");
        resetButton.setText("Reset");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(resetButton, gbc);
        moveVerticallyButton = new JButton();
        moveVerticallyButton.setActionCommand("moveIntervalGridVerticalAction");
        moveVerticallyButton.setText("moveVertically");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(moveVerticallyButton, gbc);
        lineOffsetTextField = new JTextField();
        lineOffsetTextField.setMinimumSize(new Dimension(40, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(lineOffsetTextField, gbc);
        moveHorizontallyButton = new JButton();
        moveHorizontallyButton.setActionCommand("moveIntervalGridHorizontalAction");
        moveHorizontallyButton.setText("moveHorizontally");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(moveHorizontallyButton, gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel2, gbc);
        gridXATextField = new JTextField();
        gridXATextField.setMinimumSize(new Dimension(40, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(gridXATextField, gbc);
        ratioLabel1 = new JLabel();
        ratioLabel1.setText("+");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel2.add(ratioLabel1, gbc);
        gridXBTextField = new JTextField();
        gridXBTextField.setMinimumSize(new Dimension(40, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(gridXBTextField, gbc);
        ratioLabel2 = new JLabel();
        ratioLabel2.setText("sqrt");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel2.add(ratioLabel2, gbc);
        gridXCTextField = new JTextField();
        gridXCTextField.setMinimumSize(new Dimension(40, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(gridXCTextField, gbc);
        gridYATextField = new JTextField();
        gridYATextField.setMinimumSize(new Dimension(40, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(gridYATextField, gbc);
        ratioLabel3 = new JLabel();
        ratioLabel3.setText("+");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel2.add(ratioLabel3, gbc);
        gridYBTextField = new JTextField();
        gridYBTextField.setMinimumSize(new Dimension(40, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(gridYBTextField, gbc);
        ratioLabel4 = new JLabel();
        ratioLabel4.setText("sqrt");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel2.add(ratioLabel4, gbc);
        gridYCTextField = new JTextField();
        gridYCTextField.setMinimumSize(new Dimension(40, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(gridYCTextField, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("Background");
        root.add(label3, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        root.add(spacer4, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 7), null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        root.add(panel3, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        selectButton = new JButton();
        selectButton.setActionCommand("readBackgroundAction");
        selectButton.setText("Select");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(selectButton, gbc);
        transparentButton = new JButton();
        transparentButton.setActionCommand("transparentAction");
        transparentButton.setText("Transparent");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(transparentButton, gbc);
        moveButton = new JButton();
        moveButton.setActionCommand("backgroundSetPositionAction");
        moveButton.setText("Move");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(moveButton, gbc);
        trimButton = new JButton();
        trimButton.setActionCommand("backgroundTrimAction");
        trimButton.setText("Trim");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(trimButton, gbc);
        showCheckBox = new JCheckBox();
        showCheckBox.setActionCommand("backgroundToggleAction");
        showCheckBox.setHorizontalAlignment(0);
        showCheckBox.setText("Show");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 10;
        panel3.add(showCheckBox, gbc);
        lockCheckBox = new JCheckBox();
        lockCheckBox.setActionCommand("backgroundLockAction");
        lockCheckBox.setHorizontalAlignment(0);
        lockCheckBox.setText("Lock");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 10;
        panel3.add(lockCheckBox, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("Misc");
        root.add(label4, new GridConstraints(10, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        root.add(spacer5, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 7), null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        root.add(panel4, new GridConstraints(11, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        twoColorButton = new JButton();
        twoColorButton.setActionCommand("drawTwoColoredCpAction");
        twoColorButton.setText("twoColor");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(twoColorButton, gbc);
        persistCheckBox = new JCheckBox();
        persistCheckBox.setActionCommand("selectPersistentAction");
        persistCheckBox.setHorizontalAlignment(0);
        persistCheckBox.setText("Persist");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(persistCheckBox, gbc);
        frameButton = new JButton();
        frameButton.setActionCommand("operationFrameSelectAction");
        frameButton.setText("frame");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(frameButton, gbc);
        selectAnd3ClickCheckbox = new JCheckBox();
        selectAnd3ClickCheckbox.setActionCommand("selectAnd3ClickAction");
        selectAnd3ClickCheckbox.setHorizontalAlignment(0);
        selectAnd3ClickCheckbox.setText("sel mcm");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(selectAnd3ClickCheckbox, gbc);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        root.add(panel5, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setHorizontalAlignment(0);
        label5.setText("Line Thickness: ");
        panel5.add(label5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lineThicknessSlider = new JSlider();
        lineThicknessSlider.setMaximum(10);
        lineThicknessSlider.setPaintTicks(false);
        lineThicknessSlider.setSnapToTicks(true);
        lineThicknessSlider.setValue(1);
        panel5.add(lineThicknessSlider, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, 1, 1, null, null, null, 0, false));
        errorOpacitySlider = new JSlider();
        errorOpacitySlider.setMaximum(100);
        errorOpacitySlider.setSnapToTicks(true);
        errorOpacitySlider.setValue(1);
        panel5.add(errorOpacitySlider, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, 1, 1, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setHorizontalAlignment(0);
        label6.setHorizontalTextPosition(11);
        label6.setText("Error Opacity:");
        panel5.add(label6, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
    }

}
