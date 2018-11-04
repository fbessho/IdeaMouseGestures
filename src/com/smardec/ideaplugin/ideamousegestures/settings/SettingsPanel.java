/*
MouseGestures - pure Java library for recognition and processing mouse gestures.
Copyright (C) 2003-2007 Smardec

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package com.smardec.ideaplugin.ideamousegestures.settings;

import com.intellij.openapi.options.ConfigurationException;
import com.smardec.ideaplugin.ideamousegestures.MouseGesturesPlugin;
import com.smardec.ideaplugin.ideamousegestures.lang.LangUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SettingsPanel {
	private MouseGesturesPlugin mouseGesturesPlugin;
	private JPanel mainPanel;
	private JSpinner gridSizeSpinner;
	private JCheckBox blockRightClickOnEditorCheckBox;
	private JCheckBox diagonalEnabledCheckBox;
	private JCheckBox mouseTrailEnabledCheckBox;
	private JSpinner mouseTrailSizeSpinner;
	private JButton mouseTrailColorButton;
	private GestureActionPanel gestureActionPanel;
	private MouseTrailColorPanel mouseTrailColorChooser;

	public SettingsPanel(MouseGesturesPlugin mouseGesturesPlugin) {
		this.mouseGesturesPlugin = mouseGesturesPlugin;
		mainPanel = new JPanel(new BorderLayout(5, 5));
		gestureActionPanel = new GestureActionPanel(this.mouseGesturesPlugin, this);
		mainPanel.setBorder(BorderFactory.createTitledBorder(LangUtils.get(LangUtils.SETTINGS_TITLE)));
		mainPanel.add(gestureActionPanel.getPanel(), BorderLayout.CENTER);
		JPanel bottomPanel = new JPanel(new BorderLayout());
		JPanel boxPanel = new JPanel();
		boxPanel.setLayout(new BoxLayout(boxPanel, BoxLayout.X_AXIS));
		boxPanel.add(getAdvancedPanel());
		boxPanel.add(Box.createHorizontalStrut(5));
		boxPanel.add(getMouseTrailPanel());
		bottomPanel.add(boxPanel, BorderLayout.WEST);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);
	}

	public void apply() throws ConfigurationException {
		Settings settings = mouseGesturesPlugin.getSettings();
		settings.setGestureActions(gestureActionPanel.getActions());
		settings.setGridSize(((Number)gridSizeSpinner.getValue()).intValue());
		settings.setBlockRightClickOnEditor(blockRightClickOnEditorCheckBox.isSelected());
		settings.setDiagonalEnabled(diagonalEnabledCheckBox.isSelected());
		settings.setMouseTrailEnabled(mouseTrailEnabledCheckBox.isSelected());
		settings.setMouseTrailSize(((Number)mouseTrailSizeSpinner.getValue()).intValue());
		settings.setMouseTrailColor(mouseTrailColorButton.getBackground());
		mouseGesturesPlugin.setSettings(settings);
	}

	public void dispose() {
		gestureActionPanel.dispose();
		mainPanel = null;
	}

	public JPanel getPanel() {
		return mainPanel;
	}

	public boolean isModified() {
		if (gestureActionPanel.isModified()) return true;
		Settings settings = mouseGesturesPlugin.getSettings();
		if (((Number)gridSizeSpinner.getValue()).intValue() != settings.getGridSize()) return true;
		if (blockRightClickOnEditorCheckBox.isSelected() != settings.isBlockRightClickOnEditor()) return true;
		if (diagonalEnabledCheckBox.isSelected() != settings.isDiagonalEnabled()) return true;
		if (mouseTrailEnabledCheckBox.isSelected() != settings.isMouseTrailEnabled()) return true;
		if (((Number)mouseTrailSizeSpinner.getValue()).intValue() != settings.getMouseTrailSize()) return true;
		return !mouseTrailColorButton.getBackground().equals(settings.getMouseTrailColor());
	}

	public void reset() {
		gestureActionPanel.reset();
		Settings settings = mouseGesturesPlugin.getSettings();
		gridSizeSpinner.setValue(settings.getGridSize());
		blockRightClickOnEditorCheckBox.setSelected(settings.isBlockRightClickOnEditor());
		diagonalEnabledCheckBox.setSelected(settings.isDiagonalEnabled());
		mouseTrailEnabledCheckBox.setSelected(settings.isMouseTrailEnabled());
		mouseTrailSizeSpinner.setValue(settings.getMouseTrailSize());
		mouseTrailColorButton.setBackground(settings.getMouseTrailColor());
	}

	private JPanel getAdvancedPanel() {
		JPanel advancedPanel = new JPanel(new GridBagLayout());
		advancedPanel.setBorder(BorderFactory.createTitledBorder(LangUtils.get(LangUtils.SETTINGS_ADVANCED_TITLE)));

		GridBagConstraints left = new GridBagConstraints();
		left.gridx = 0;
		left.gridy = 0;
		left.fill = GridBagConstraints.NONE;
		left.weightx = 0.0;
		left.weighty = 0.0;
		left.anchor = GridBagConstraints.WEST;
		left.insets = new Insets(2, 2, 2, 2);

		GridBagConstraints right = new GridBagConstraints();
		right.gridx = 1;
		right.gridy = 0;
		right.fill = GridBagConstraints.NONE;
		right.weightx = 1.0;
		right.weighty = 0.0;
		right.anchor = GridBagConstraints.WEST;
		right.insets = new Insets(2, 2, 2, 2);

		Settings settings = mouseGesturesPlugin.getSettings();

		JLabel gridSizeLabel = new JLabel(LangUtils.get(LangUtils.SETTINGS_ADVANCED_MINMOVEMENT_TEXT));
		gridSizeSpinner = new JSpinner(new SpinnerNumberModel(settings.getGridSize(), 10, 1000, 1));
		gridSizeLabel.setLabelFor(gridSizeSpinner);
		String gridSizeToolTip = LangUtils.get(LangUtils.SETTINGS_ADVANCED_MINMOVEMENT_TIP);
		gridSizeSpinner.setToolTipText(gridSizeToolTip);
		gridSizeLabel.setToolTipText(gridSizeToolTip);
		advancedPanel.add(gridSizeLabel, left);
		advancedPanel.add(gridSizeSpinner, right);

		left.gridy++;
		right.gridy++;

		JLabel blockRightClickOnEditorLabel = new JLabel(LangUtils.get(LangUtils.SETTINGS_ADVANCED_BLOCK));
		blockRightClickOnEditorCheckBox = new JCheckBox();
		blockRightClickOnEditorLabel.setLabelFor(blockRightClickOnEditorCheckBox);
		String blockRightClickOnEditorToolTip = LangUtils.get(LangUtils.SETTINGS_ADVANCED_BLOCK_TIP);
		blockRightClickOnEditorCheckBox.setToolTipText(blockRightClickOnEditorToolTip);
		blockRightClickOnEditorLabel.setToolTipText(blockRightClickOnEditorToolTip);
		blockRightClickOnEditorCheckBox.setSelected(settings.isBlockRightClickOnEditor());
		advancedPanel.add(blockRightClickOnEditorLabel, left);
		advancedPanel.add(blockRightClickOnEditorCheckBox, right);

		left.gridy++;
		right.gridy++;

		JLabel diagonalEnabledLabel = new JLabel(LangUtils.get(LangUtils.SETTINGS_ADVANCED_DIAGONAL));
		diagonalEnabledCheckBox = new JCheckBox();
		diagonalEnabledLabel.setLabelFor(diagonalEnabledCheckBox);
		String diagonalEnabledToolTip = LangUtils.get(LangUtils.SETTINGS_ADVANCED_DIAGONAL_TIP);
		diagonalEnabledCheckBox.setToolTipText(diagonalEnabledToolTip);
		diagonalEnabledLabel.setToolTipText(diagonalEnabledToolTip);
		diagonalEnabledCheckBox.setSelected(settings.isDiagonalEnabled());
		advancedPanel.add(diagonalEnabledLabel, left);
		advancedPanel.add(diagonalEnabledCheckBox, right);
		return advancedPanel;
	}

	private JPanel getMouseTrailPanel() {
		JPanel mouseTrailPanel = new JPanel(new GridBagLayout());
		mouseTrailPanel.setBorder(BorderFactory.createTitledBorder(LangUtils.get(LangUtils.SETTINGS_MOUSETRAIL_TITLE)));

		GridBagConstraints left = new GridBagConstraints();
		left.gridx = 0;
		left.gridy = 0;
		left.fill = GridBagConstraints.NONE;
		left.weightx = 0.0;
		left.weighty = 0.0;
		left.anchor = GridBagConstraints.WEST;
		left.insets = new Insets(2, 2, 2, 2);

		GridBagConstraints right = new GridBagConstraints();
		right.gridx = 1;
		right.gridy = 0;
		right.fill = GridBagConstraints.NONE;
		right.weightx = 1.0;
		right.weighty = 0.0;
		right.anchor = GridBagConstraints.WEST;
		right.insets = new Insets(2, 2, 2, 2);

		Settings settings = mouseGesturesPlugin.getSettings();

		mouseTrailSizeSpinner = new JSpinner(new SpinnerNumberModel(settings.getMouseTrailSize(), 1, 50, 1));
		mouseTrailColorButton = new JButton();

		JLabel mouseTrailEnabledLabel = new JLabel(LangUtils.get(LangUtils.SETTINGS_MOUSETRAIL_TEXT));
		mouseTrailEnabledCheckBox = new JCheckBox();
		mouseTrailEnabledLabel.setLabelFor(mouseTrailEnabledCheckBox);
		String mouseTrailEnabledToolTip = LangUtils.get(LangUtils.SETTINGS_MOUSETRAIL_TIP);
		mouseTrailEnabledCheckBox.setToolTipText(mouseTrailEnabledToolTip);
		mouseTrailEnabledLabel.setToolTipText(mouseTrailEnabledToolTip);
		mouseTrailEnabledCheckBox.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent changeEvent) {
				boolean mouseTrailEnabled = mouseTrailEnabledCheckBox.isSelected();
				mouseTrailColorButton.setEnabled(mouseTrailEnabled);
				mouseTrailSizeSpinner.setEnabled(mouseTrailEnabled);
			}
		});
		mouseTrailEnabledCheckBox.setSelected(settings.isMouseTrailEnabled());
		mouseTrailPanel.add(mouseTrailEnabledLabel, left);
		mouseTrailPanel.add(mouseTrailEnabledCheckBox, right);

		left.gridy++;
		right.gridy++;

		JLabel mouseTrailSizeLabel = new JLabel(LangUtils.get(LangUtils.SETTINGS_MOUSETRAIL_SIZE));
		mouseTrailSizeLabel.setLabelFor(mouseTrailSizeSpinner);
		mouseTrailPanel.add(mouseTrailSizeLabel, left);
		mouseTrailPanel.add(mouseTrailSizeSpinner, right);

		left.gridy++;
		right.gridy++;

		JLabel mouseTrailColorLabel = new JLabel(LangUtils.get(LangUtils.SETTINGS_MOUSETRAIL_COLOR));
		mouseTrailColorLabel.setLabelFor(mouseTrailColorButton);
		mouseTrailColorButton.setBackground(settings.getMouseTrailColor());
		mouseTrailColorButton.setOpaque(true);
		mouseTrailColorButton.setSize(20, 20);
		mouseTrailColorButton.setPreferredSize(mouseTrailColorButton.getSize());
		mouseTrailColorButton.setMinimumSize(mouseTrailColorButton.getSize());
		mouseTrailColorButton.setMaximumSize(mouseTrailColorButton.getSize());
		mouseTrailColorButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseEvent) {
				if (mouseEvent.getButton() == MouseEvent.BUTTON1 && mouseTrailEnabledCheckBox.isSelected()) {
					prepareMouseTrailColorChooser();
					Color newColor = mouseTrailColorChooser.getColor(mouseTrailColorButton.getBackground());
					if (newColor == null) return;
					mouseTrailColorButton.setBackground(newColor);
				}
			}
		});
		mouseTrailPanel.add(mouseTrailColorLabel, left);
		mouseTrailPanel.add(mouseTrailColorButton, right);

		return mouseTrailPanel;

	}

	private void prepareMouseTrailColorChooser() {
		if (mouseTrailColorChooser == null) {
			mouseTrailColorChooser = new MouseTrailColorPanel(mainPanel);
		}
	}

	public boolean isDiagnoalEnabled() {
		return diagonalEnabledCheckBox.isSelected();
	}
}
