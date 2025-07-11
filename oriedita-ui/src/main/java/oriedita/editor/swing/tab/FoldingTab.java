package oriedita.editor.swing.tab;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.common.converter.DoubleConverter;
import oriedita.common.converter.IntConverter;
import oriedita.editor.action.ActionType;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.FoldedFigureModel;
import oriedita.editor.databinding.FoldedFiguresList;
import oriedita.editor.drawing.FoldedFigure_Drawer;
import oriedita.editor.handler.PopupMenuAdapter;
import oriedita.editor.service.BindingService;
import oriedita.editor.service.ButtonService;
import oriedita.editor.swing.component.ColorIcon;
import oriedita.editor.swing.component.combobox.CustomTextComboBoxRenderer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.PopupMenuEvent;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

@ApplicationScoped
public class FoldingTab {
    private final FoldedFigureModel foldedFigureModel;
    private JPanel root;
    private JCheckBox overlapCheckBox;
    private JButton fixOverlapButton;
    private JButton polygonFoldabilityButton;
    private JButton CP_rcgButton;
    private JButton s_faceButton;
    private JComboBox<FoldedFigure_Drawer> foldedFiguresBox;
    private JButton goButton;
    private JButton AS100Button;
    private JTextField foldedFiguresTextField;
    private JButton constraintButton;
    private JButton editWireframeButton;
    private JButton editFoldedModelButton;
    private JButton undoButton;
    private JButton redoButton;
    private JButton flipButton;
    private JButton duplicateButton;
    private JButton moveButton;
    private JButton rotateBtn;
    private JButton rotate2Btn;
    private JTextField rotationTextField;
    private JTextField scaleTextField;
    private JButton scaleDecrease;
    private JButton scaleIncrease;
    private JButton frontColorButton;
    private JButton backColorButton;
    private JButton lineColorButton;
    private JButton mvWireframeButton;
    private JButton wireframeButton;
    private JButton decreaseOpacityButton;
    private JButton increaseOpacityButton;
    private JCheckBox coloredCheckBox;
    private JButton foldButton;
    private JButton stopButton;
    private JButton trashButton;
    private JButton a_sButton;

    private final ButtonService buttonService;
    private final ApplicationModel applicationModel;
    private final FoldedFiguresList foldedFiguresList;
    private final BindingService bindingService;

    @Inject
    public FoldingTab(ButtonService buttonService,
                      ApplicationModel applicationModel,
                      FoldedFiguresList foldedFiguresList, FoldedFigureModel foldedFigureModel, BindingService bindingService) {
        this.buttonService = buttonService;
        this.applicationModel = applicationModel;
        this.foldedFiguresList = foldedFiguresList;
        this.foldedFigureModel = foldedFigureModel;
        this.bindingService = bindingService;
    }

    public void init() {
        buttonService.addDefaultListener($$$getRootComponent$$$());

        applicationModel.addPropertyChangeListener(event -> getData(applicationModel));
        foldedFigureModel.addPropertyChangeListener(event -> getData(foldedFigureModel));

        foldedFiguresBox.setModel(foldedFiguresList);
        foldedFiguresBox.setRenderer(new CustomTextComboBoxRenderer<>((d, i) -> {
            if (i == -1) {
                return -1 == foldedFiguresBox.getSelectedIndex() ?
                        ""
                        : Integer.toString(foldedFiguresBox.getSelectedIndex() + 1);
            } else {
                return Integer.toString(i + 1);
            }
        }));
        foldedFiguresBox.addPopupMenuListener(new PopupMenuAdapter() {
            private boolean numbersTemporary = false;

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                if (!applicationModel.getDisplayNumbers()) {
                    applicationModel.setDisplayNumbers(true);
                    numbersTemporary = true;
                } else {
                    numbersTemporary = false;
                }
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                if (applicationModel.getDisplayNumbers() && numbersTemporary) {
                    applicationModel.setDisplayNumbers(false);
                }
            }
        });

        bindingService.addBinding(foldedFigureModel, "foldedCases", foldedFiguresTextField, new IntConverter() {
            @Override
            public boolean canConvertBack(String s) {
                return super.canConvertBack(s) && super.convertBack(s) > 0;
            }
        });
        bindingService.addBinding(foldedFigureModel, "scale", scaleTextField, new DoubleConverter("0.0####"));
        bindingService.addBinding(foldedFigureModel, "rotation", rotationTextField, new DoubleConverter("0.0####"));
        buttonService.registerTextField(foldedFiguresTextField, ActionType.goToFoldedFigureAction.action());
        buttonService.registerTextField(scaleTextField, ActionType.foldedFigureSizeSetAction.action());
        buttonService.registerTextField(rotationTextField, ActionType.foldedFigureRotateSetAction.action());

        foldButton.setText("Fold");
    }

    private void getData(ApplicationModel applicationModel) {
        overlapCheckBox.setSelected(applicationModel.getCkOEnabled());
    }

    private void getData(FoldedFigureModel foldedFigureModel) {
        coloredCheckBox.setSelected(foldedFigureModel.isTransparencyColor());

        frontColorButton.setIcon(new ColorIcon(foldedFigureModel.getFrontColor()));
        backColorButton.setIcon(new ColorIcon(foldedFigureModel.getBackColor()));
        lineColorButton.setIcon(new ColorIcon(foldedFigureModel.getLineColor()));
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
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        root.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Validate");
        panel1.add(label1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        panel1.add(panel2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        overlapCheckBox = new JCheckBox();
        overlapCheckBox.setActionCommand("ckOAction");
        overlapCheckBox.setHorizontalAlignment(0);
        overlapCheckBox.setText("Overlap");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(overlapCheckBox, gbc);
        fixOverlapButton = new JButton();
        fixOverlapButton.setActionCommand("fxOAction");
        fixOverlapButton.setText("fix Overlap");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 5;
        panel2.add(fixOverlapButton, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(panel3, gbc);
        polygonFoldabilityButton = new JButton();
        polygonFoldabilityButton.setActionCommand("o_F_checkAction");
        polygonFoldabilityButton.setText("Polygon Foldability");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 5;
        panel3.add(polygonFoldabilityButton, gbc);
        CP_rcgButton = new JButton();
        CP_rcgButton.setActionCommand("suitei_01Action");
        CP_rcgButton.setText("CP_rcg");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(CP_rcgButton, gbc);
        s_faceButton = new JButton();
        s_faceButton.setActionCommand("koteimen_siteiAction");
        s_faceButton.setText("S_face");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipady = 5;
        panel3.add(s_faceButton, gbc);
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 7), null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        root.add(spacer2, new GridConstraints(12, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        root.add(panel4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel4.add(spacer3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 7), null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Transform Fold");
        root.add(label2, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        root.add(panel5, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        flipButton = new JButton();
        flipButton.setActionCommand("foldedFigureFlipAction");
        flipButton.setText("flip");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(flipButton, gbc);
        duplicateButton = new JButton();
        duplicateButton.setActionCommand("duplicateFoldedModelAction");
        duplicateButton.setText("duplicate");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(duplicateButton, gbc);
        moveButton = new JButton();
        moveButton.setActionCommand("foldedFigureMoveAction");
        moveButton.setText("move");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(moveButton, gbc);
        rotationTextField = new JTextField();
        rotationTextField.setMinimumSize(new Dimension(40, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(rotationTextField, gbc);
        rotateBtn = new JButton();
        rotateBtn.setActionCommand("foldedFigureRotateAntiClockwiseAction");
        rotateBtn.setText("rotateCounterclockwise");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(rotateBtn, gbc);
        rotate2Btn = new JButton();
        rotate2Btn.setActionCommand("foldedFigureRotateClockwiseAction");
        rotate2Btn.setText("rotateClockwise");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(rotate2Btn, gbc);
        scaleTextField = new JTextField();
        scaleTextField.setMinimumSize(new Dimension(40, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(scaleTextField, gbc);
        scaleDecrease = new JButton();
        scaleDecrease.setActionCommand("foldedFigureSizeDecreaseAction");
        scaleDecrease.setText("scaleDecrease");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(scaleDecrease, gbc);
        scaleIncrease = new JButton();
        scaleIncrease.setActionCommand("foldedFigureSizeIncreaseAction");
        scaleIncrease.setText("scaleIncrease");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(scaleIncrease, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel5.add(spacer4, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("Edit Fold");
        root.add(label3, new GridConstraints(10, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridBagLayout());
        root.add(panel6, new GridConstraints(11, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        frontColorButton = new JButton();
        frontColorButton.setActionCommand("frontColorAction");
        frontColorButton.setText("FC");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipady = 5;
        panel6.add(frontColorButton, gbc);
        backColorButton = new JButton();
        backColorButton.setActionCommand("backColorAction");
        backColorButton.setText("BC");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(backColorButton, gbc);
        lineColorButton = new JButton();
        lineColorButton.setActionCommand("lineColorAction");
        lineColorButton.setText("LC");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipady = 5;
        panel6.add(lineColorButton, gbc);
        mvWireframeButton = new JButton();
        mvWireframeButton.setActionCommand("suitei_02Action");
        mvWireframeButton.setText("mvWireframe");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(mvWireframeButton, gbc);
        wireframeButton = new JButton();
        wireframeButton.setActionCommand("suitei_03Action");
        wireframeButton.setText("wireframe");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(wireframeButton, gbc);
        decreaseOpacityButton = new JButton();
        decreaseOpacityButton.setActionCommand("coloredXRayDecreaseAction");
        decreaseOpacityButton.setText("decreaseOpacity");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(decreaseOpacityButton, gbc);
        increaseOpacityButton = new JButton();
        increaseOpacityButton.setActionCommand("coloredXRayIncreaseAction");
        increaseOpacityButton.setText("increaseOpacity");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(increaseOpacityButton, gbc);
        coloredCheckBox = new JCheckBox();
        coloredCheckBox.setActionCommand("coloredXRayAction");
        coloredCheckBox.setMargin(new Insets(2, 10, 2, 2));
        coloredCheckBox.setText("Colored");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(coloredCheckBox, gbc);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridBagLayout());
        root.add(panel7, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel7.add(panel8, gbc);
        undoButton = new JButton();
        undoButton.setActionCommand("foldedFigureUndoAction");
        undoButton.setText("undo");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel8.add(undoButton, gbc);
        redoButton = new JButton();
        redoButton.setActionCommand("foldedFigureRedoAction");
        redoButton.setText("redo");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel8.add(redoButton, gbc);
        final Spacer spacer5 = new Spacer();
        root.add(spacer5, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 7), null, null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridBagLayout());
        root.add(panel9, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        constraintButton = new JButton();
        constraintButton.setActionCommand("addColorConstraintAction");
        constraintButton.setText("C");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel9.add(constraintButton, gbc);
        editWireframeButton = new JButton();
        editWireframeButton.setActionCommand("oriagari_sousaAction");
        editWireframeButton.setText("editWireframe");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel9.add(editWireframeButton, gbc);
        editFoldedModelButton = new JButton();
        editFoldedModelButton.setActionCommand("oriagari_sousa_2Action");
        editFoldedModelButton.setText("editFoldedModel");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel9.add(editFoldedModelButton, gbc);
        foldedFiguresBox = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel9.add(foldedFiguresBox, gbc);
        goButton = new JButton();
        goButton.setActionCommand("goToFoldedFigureAction");
        goButton.setText("Go");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipady = 5;
        panel9.add(goButton, gbc);
        foldButton = new JButton();
        foldButton.setActionCommand("foldAction");
        foldButton.setText("Fold");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel9.add(foldButton, gbc);
        stopButton = new JButton();
        stopButton.setActionCommand("haltAction");
        stopButton.setText("Stop");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(stopButton, gbc);
        trashButton = new JButton();
        trashButton.setActionCommand("foldedFigureTrashAction");
        trashButton.setText("Trash");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(trashButton, gbc);
        AS100Button = new JButton();
        AS100Button.setActionCommand("as100Action");
        AS100Button.setText("AS100");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel9.add(AS100Button, gbc);
        foldedFiguresTextField = new JTextField();
        foldedFiguresTextField.setMinimumSize(new Dimension(20, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel9.add(foldedFiguresTextField, gbc);
        a_sButton = new JButton();
        a_sButton.setActionCommand("anotherSolutionAction");
        a_sButton.setText("a_s");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipady = 5;
        panel9.add(a_sButton, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("Calculate Fold");
        root.add(label4, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer6 = new Spacer();
        root.add(spacer6, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 7), null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
    }

}
