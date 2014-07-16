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

import com.smardec.ideaplugin.ideamousegestures.lang.LangUtils;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;

public class MouseTrailColorPanel {
	private JComponent parentComponent;
	private JPanel mainPanel;
	private JColorChooser colorChooser;
	private JButton previewButton;
	private JDialog dialog;
	private Color selectedColor;
	private JButton okButton;

	public MouseTrailColorPanel(JComponent parentComponent) {
		this.parentComponent = parentComponent;
		mainPanel = new JPanel(new BorderLayout(5, 5));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		colorChooser = new JColorChooser();
		previewButton = new JButton();
		Dimension size = new Dimension(100, 25);
		previewButton.setSize(size);
		previewButton.setPreferredSize(size);
		previewButton.setMinimumSize(size);
		previewButton.setMaximumSize(size);
		previewButton.setEnabled(true);
		previewButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		previewButton.setOpaque(true);
		previewButton.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
				if ("foreground".equals(propertyChangeEvent.getPropertyName()))
					previewButton.setBackground((Color)propertyChangeEvent.getNewValue());
			}
		});
		colorChooser.setPreviewPanel(previewButton);
		mainPanel.add(colorChooser, BorderLayout.CENTER);
		mainPanel.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		}, KeyStroke.getKeyStroke(27, 0), 1);

		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING, 0, 0));
		okButton = new JButton(new AbstractAction(LangUtils.get(LangUtils.SETTINGS_MOUSETRAIL_COLORSETTINGS_ACCEPT)) {
			public void actionPerformed(ActionEvent e) {
				onOK();
			}
		});
		bottomPanel.add(okButton);
		bottomPanel.add(Box.createHorizontalStrut(5));
		bottomPanel.add(new JButton(new AbstractAction(LangUtils.get(LangUtils.SETTINGS_MOUSETRAIL_COLORSETTINGS_CANCEL)) {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		}));
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);
	}

	public Color getColor(Color startColor) {
		if (dialog == null) {
			String title = LangUtils.get(LangUtils.SETTINGS_MOUSETRAIL_COLORSETTINGS_TITLE);
			Window parentWindow = SwingUtilities.getWindowAncestor(parentComponent);
			if (parentWindow instanceof Frame) {
				dialog = new JDialog((Frame)parentWindow, title, true);
			} else if (parentWindow instanceof Dialog) {
				dialog = new JDialog((Dialog)parentWindow, title, true);
			} else {
				throw new IllegalStateException("Cannot show a modal dialog without an actual" +
												" Frame or Dialog parent window");
			}
			dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			dialog.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					onCancel();
				}
			});
			dialog.setContentPane(mainPanel);
			dialog.setResizable(false);
			dialog.getRootPane().setDefaultButton(okButton);
		}
		colorChooser.setColor(startColor);
		dialog.setSize(450, 360);
		dialog.setLocationRelativeTo(parentComponent);
		dialog.setVisible(true);
		return selectedColor;
	}

	private void onOK() {
		selectedColor = colorChooser.getColor();
		dialog.setVisible(false);
	}

	private void onCancel() {
		selectedColor = null;
		dialog.setVisible(false);
	}
}
