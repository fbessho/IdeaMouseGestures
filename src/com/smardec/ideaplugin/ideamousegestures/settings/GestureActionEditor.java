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

import com.smardec.ideaplugin.ideamousegestures.*;
import com.smardec.ideaplugin.ideamousegestures.lang.LangUtils;
import com.smardec.ideaplugin.ideamousegestures.IActionNodeSelectionListener;
import com.smardec.mousegestures.MouseGesturesListener;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import static java.awt.GridBagConstraints.*;
import java.awt.event.*;
import java.util.Set;

public class GestureActionEditor implements IActionNodeSelectionListener {
	private JPanel mainPanel;
	private JTextField gestureTextField;
	private JPopupMenu actionPopupMenu;
	private Action okAction;
	private JPanel actionDescriptorPanel;
	private String[] selectedActionPath;
	private JComponent parentComponent;
	private JDialog dialog;
	private MouseGesturesListener editMouseGesturesListener;
	private MouseGesturesPlugin mouseGesturesPlugin;
	private Set<String> forbiddenGestureNames;
	private JButton changeActionDescriptorButton;
	private JDialog helpDialog;

	public GestureActionEditor(JComponent parentComponent, MouseGesturesPlugin mouseGesturesPlugin) {
		this.parentComponent = parentComponent;
		this.mouseGesturesPlugin = mouseGesturesPlugin;
		forbiddenGestureNames = null;
		mainPanel = new JPanel(new BorderLayout(5, 5));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JPanel centerPanel = new JPanel(new GridBagLayout());
		centerPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK),
																 BorderFactory.createEmptyBorder(4, 4, 4, 4)));
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = NONE;
		c.anchor = EAST;
		c.insets = new Insets(2, 2, 2, 2);
		centerPanel.add(new JLabel(LangUtils.get(LangUtils.SETTINGS_GESTURES_EDITOR_GESTURE_TEXT)), c);

		c.gridx = 1;
		c.gridwidth = 2;
		c.fill = HORIZONTAL;
		c.weightx = 1.0;
		c.anchor = WEST;
		editMouseGesturesListener = new MouseGesturesListener() {
			public void processGesture(String s1) {}

			public void gestureMovementRecognized(String currentGesture) {
				gestureTextField.setText(currentGesture);
			}
		};
		gestureTextField = new JTextField(30);
		gestureTextField.setFont(gestureTextField.getFont().deriveFont(Font.BOLD, 15f));
		gestureTextField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				GestureActionEditor.this.mouseGesturesPlugin.getMouseGestures().addMouseGesturesListener(editMouseGesturesListener);
			}

			public void focusLost(FocusEvent e) {
				GestureActionEditor.this.mouseGesturesPlugin.getMouseGestures().removeMouseGesturesListener(editMouseGesturesListener);
			}
		});
		gestureTextField.setDocument(new PlainDocument() {
			public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
				if (str == null || str.length() == 0) return;
				str = str.toUpperCase();
				char[] moves = GestureActionEditor.this.mouseGesturesPlugin.getMouseGestures().getMovements().getMovements();
				String excludeList = "[^";
				for (char move : moves) {
					excludeList += move;
				}
				excludeList += "]";
				str = str.replaceAll(excludeList, "");
				for (char move : moves) {
					str = str.replaceAll(move + "+", "" + move);
				}
				if (str.length() == 0) return;
				String text = getText(0, getLength());
				if (text.length() > 0 && text.charAt(text.length() - 1) == str.charAt(0)) str = str.substring(1);
				super.insertString(offs, str, a);
			}
		});
		gestureTextField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				revalidate();
			}

			public void removeUpdate(DocumentEvent e) {
				revalidate();
			}

			public void changedUpdate(DocumentEvent e) {
				revalidate();
			}
		});
		centerPanel.add(gestureTextField, c);

		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 1.0;
		c.gridwidth = 1;
		c.fill = NONE;
		c.anchor = EAST;
		c.weightx = 0.0;
		centerPanel.add(new JLabel(LangUtils.get(LangUtils.SETTINGS_GESTURES_EDITOR_ACTION_TEXT)), c);

		c.gridx = 1;
		c.anchor = CENTER;
		changeActionDescriptorButton = new JButton(new AbstractAction("...") {
			public void actionPerformed(ActionEvent e) {
				onChangeActionDescriptor();
			}
		});
		changeActionDescriptorButton.setMargin(new Insets(0, 0, 0, 0));
		centerPanel.add(changeActionDescriptorButton, c);

		c.gridx = 2;
		c.weightx = 1.0;
		c.fill = HORIZONTAL;
		c.anchor = WEST;
		actionDescriptorPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
		actionDescriptorPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK),
																		   BorderFactory.createEmptyBorder(2, 2, 2, 2)));
		actionDescriptorPanel.setOpaque(true);
		actionDescriptorPanel.setBackground(Color.WHITE);
		centerPanel.add(actionDescriptorPanel, c);

		mainPanel.add(centerPanel, BorderLayout.CENTER);

		okAction = new AbstractAction(LangUtils.get(LangUtils.SETTINGS_GESTURES_EDITOR_ACCEPT)) {
			public void actionPerformed(ActionEvent e) {
				onOk();
			}
		};
		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		bottomPanel.add(new JButton(okAction));
		bottomPanel.add(Box.createHorizontalStrut(5));
		bottomPanel.add(new JButton(new AbstractAction(LangUtils.get(LangUtils.SETTINGS_GESTURES_EDITOR_CANCEL)) {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		}));
		bottomPanel.add(Box.createHorizontalStrut(5));
		bottomPanel.add(new JButton(new AbstractAction(LangUtils.get(LangUtils.SETTINGS_GESTURES_EDITOR_HELP)) {
			public void actionPerformed(ActionEvent e) {
				onHelp();
			}
		}));
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);
	}

	public GestureAction edit(GestureAction startGestureAction, Set<String> forbiddenGestures, boolean diagonalEnabled) {
		if (dialog == null) {
			Window ancestor = SwingUtilities.getWindowAncestor(parentComponent);
			String title = " ";
			if (ancestor instanceof Frame) {
				dialog = new JDialog((Frame)ancestor, title, true);
			} else if (ancestor instanceof Dialog) {
				dialog = new JDialog((Dialog)ancestor, title, true);
			} else {
				throw new IllegalStateException("Cannot show a modal dialog without an actual" +
												" Frame or Dialog parent window");
			}
			dialog.setContentPane(mainPanel);
			dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			dialog.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					onCancel();
				}
			});
		}
		if (startGestureAction != null) {
			dialog.setTitle(LangUtils.get(LangUtils.SETTINGS_GESTURES_EDITOR_EDIT_TITLE));
			gestureTextField.setText(startGestureAction.getGesture());
			setActionNode(startGestureAction.getActionPath());
		} else {
			dialog.setTitle(LangUtils.get(LangUtils.SETTINGS_GESTURES_EDITOR_ADD_TITLE));
			gestureTextField.setText("");
			setActionNode(null);
		}
		forbiddenGestureNames = forbiddenGestures;
		dialog.pack();
		dialog.setLocationRelativeTo(parentComponent);
		boolean storedDiagonalEnabled = mouseGesturesPlugin.getMouseGestures().isDiagonalEnabled();
		mouseGesturesPlugin.getMouseGestures().setDiagonalEnabled(diagonalEnabled);
		dialog.setVisible(true);
		mouseGesturesPlugin.getMouseGestures().setDiagonalEnabled(storedDiagonalEnabled);
		gestureTextField.requestFocusInWindow();
		if (isValid()) {
			return new GestureAction(gestureTextField.getText().trim(), selectedActionPath);
		} else {
			return null;
		}
	}

	public void onSelect(String[] selectedActionPath) {
		setActionNode(selectedActionPath);
	}

	private boolean isValid() {
		String gesture = gestureTextField.getText();
		if (gesture == null || gesture.length() == 0) return false;
		if (forbiddenGestureNames != null && forbiddenGestureNames.contains(gesture)) return false;
		return ActionHelper.getInstance().isValidAction(selectedActionPath);
	}

	private void onCancel() {
		selectedActionPath = null;
		dialog.setVisible(false);
	}

	private void onChangeActionDescriptor() {
		preparePopupMenu();
		actionPopupMenu.show(changeActionDescriptorButton, 0, changeActionDescriptorButton.getHeight());
	}

	private void onOk() {
		dialog.setVisible(false);
	}

	private void onHelp() {
		if (helpDialog == null) {
			prepareHelpDialog();
		}
		helpDialog.pack();
		helpDialog.setLocationRelativeTo(dialog);
		helpDialog.setVisible(true);
	}

	private void preparePopupMenu() {
		if (actionPopupMenu != null) return;
		actionPopupMenu = ActionHelper.getInstance().createActionSelectionPopup(this);
	}

	private void revalidate() {
		okAction.setEnabled(isValid());
	}

	private void setActionNode(String[] actionPath) {
		selectedActionPath = actionPath;
		ActionHelper.getInstance().constructView(actionDescriptorPanel, actionPath, Color.BLACK, false);
		actionDescriptorPanel.revalidate();
		actionDescriptorPanel.repaint();
		revalidate();
	}

	private void prepareHelpDialog() {
		helpDialog = new JDialog(dialog, LangUtils.get(LangUtils.SETTINGS_GESTURES_EDITOR_HELP_DIALOG_TITLE), true);
		JPanel contentPane = new JPanel(new BorderLayout(5, 5));
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		JLabel label = new JLabel(LangUtils.get(LangUtils.SETTINGS_GESTURES_EDITOR_HELP_DIALOG_MESSAGE));
		label.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK),
														   BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		contentPane.add(label, BorderLayout.CENTER);
		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		new JButton("");
		bottomPanel.add(new JButton(new AbstractAction(LangUtils.get(LangUtils.SETTINGS_GESTURES_EDITOR_ACCEPT)) {
			public void actionPerformed(ActionEvent e) {
				if (helpDialog == null) {
					// shouldn't happen
					return;
				}
				helpDialog.setVisible(false);
			}
		}));
		contentPane.add(bottomPanel, BorderLayout.SOUTH);
		helpDialog.setContentPane(contentPane);
		helpDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}
}
