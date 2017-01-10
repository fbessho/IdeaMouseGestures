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

import com.smardec.ideaplugin.ideamousegestures.ActionHelper;
import com.smardec.ideaplugin.ideamousegestures.GestureAction;
import com.smardec.ideaplugin.ideamousegestures.MouseGesturesPlugin;
import com.smardec.ideaplugin.ideamousegestures.lang.LangUtils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class GestureActionPanel {
	private static final String ADD_GESTURE_ACTION_ID = "addGestureAction";
	private static final String REMOVE_GESTURE_ACTION_ID = "removeGestureAction";
	private static final String EDIT_GESTURE_ACTION_ID = "editGestureAction";

	private MouseGesturesPlugin mouseGesturesPlugin;
	private SettingsPanel settingsPanel;
	private SortedSet<GestureAction> gestureActions;
	private JPanel mainPanel;
	private Action removeAction;
	private Action editAction;
	private GestureActionListModel gestureActionListModel;
	private JList gestureActionList;
	private GestureActionEditor gestureActionEditor;

	private abstract class ToolTippedAction extends AbstractAction {
		protected ToolTippedAction(String name, String toolTip) {
			super(name);
			putValue(name, toolTip);
		}
	}

	private class GestureActionListModel extends AbstractListModel {
		public int getSize() {
			return gestureActions.size();
		}

		public Object getElementAt(int index) {
			return getGestureActionAt(index);
		}

		public void fireFilled(int size) {
			fireIntervalAdded(this, 0, size - 1);
		}

		public void fireEmptied(int size) {
			fireIntervalRemoved(this, 0, size - 1);
		}

		public void fireAdded(int index) {
			fireIntervalAdded(this, index, index);
		}

		public void fireRemoved(int index) {
			fireIntervalRemoved(this, index, index);
		}

		public void fireUpdated(int index) {
			fireContentsChanged(this, index, index);
		}
	}

	private static class GestureActionRenderer implements ListCellRenderer {
		private static final Border NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
		private JPanel panel;
		private JLabel gestureLabel;
		private JPanel actionPanel;

		public GestureActionRenderer() {
			panel = new JPanel(new GridLayout(2, 1, 5, 5));
			panel.setOpaque(true);
			gestureLabel = new JLabel(" ");
			gestureLabel.setFont(gestureLabel.getFont().deriveFont(Font.BOLD, 15f));
			panel.add(gestureLabel);
			actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
			actionPanel.setOpaque(false);
			panel.add(actionPanel);
		}

		public Component getListCellRendererComponent(JList list, Object value, int index,
													  boolean selected, boolean hasFocus) {
			GestureAction gestureAction = (GestureAction)value;
			gestureLabel.setText(gestureAction.getGesture());
			panel.setComponentOrientation(list.getComponentOrientation());
			Color textColor;
			if (selected) {
				panel.setBackground(list.getSelectionBackground());
				textColor = list.getSelectionForeground();
			} else {
				panel.setBackground(list.getBackground());
				textColor = list.getForeground();
			}
			gestureLabel.setForeground(textColor);
			ActionHelper.getInstance().constructView(actionPanel, gestureAction.getActionPath(), textColor, selected);

			panel.setEnabled(list.isEnabled());

			Border border = null;
			if (hasFocus) {
				if (selected) {
					border = UIManager.getBorder("List.focusSelectedCellHighlightBorder");
				}
				if (border == null) {
					border = UIManager.getBorder("List.focusCellHighlightBorder");
				}
			} else {
				border = NO_FOCUS_BORDER;
			}
			panel.setBorder(border);
			panel.revalidate();
			return panel;
		}
	}

	public GestureActionPanel(MouseGesturesPlugin mouseGesturesPlugin, SettingsPanel settingsPanel) {
		this.mouseGesturesPlugin = mouseGesturesPlugin;
		this.settingsPanel = settingsPanel;

		mainPanel = new JPanel(new BorderLayout(5, 5));
		mainPanel.setBorder(BorderFactory.createTitledBorder(LangUtils.get(LangUtils.SETTINGS_GESTURES_TITLE)));

		Action addAction = new ToolTippedAction(LangUtils.get(LangUtils.SETTINGS_GESTURES_ADD),
												LangUtils.get(LangUtils.SETTINGS_GESTURES_ADD_EXPLAIN)) {
			public void actionPerformed(ActionEvent aActionEvent) {
				onAdd();
			}
		};
		removeAction = new ToolTippedAction(LangUtils.get(LangUtils.SETTINGS_GESTURES_REMOVE),
											LangUtils.get(LangUtils.SETTINGS_GESTURES_REMOVE_EXPLAIN)) {
			public void actionPerformed(ActionEvent aActionEvent) {
				onRemove();
			}
		};
		editAction = new ToolTippedAction(LangUtils.get(LangUtils.SETTINGS_GESTURES_EDIT),
										  LangUtils.get(LangUtils.SETTINGS_GESTURES_EDIT_EXPLAIN)) {
			public void actionPerformed(ActionEvent aActionEvent) {
				onEdit();
			}
		};

		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.add(addAction);
		toolBar.add(removeAction);
		toolBar.add(editAction);
		mainPanel.add(toolBar, BorderLayout.NORTH);

		gestureActions = new TreeSet<GestureAction>();
		gestureActionListModel = new GestureActionListModel();
		gestureActionList = new JList(gestureActionListModel);
		gestureActionList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		gestureActionList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent aListSelectionEvent) {
				onSelection();
			}
		});
		gestureActionList.setCellRenderer(new GestureActionRenderer());
		gestureActionList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent aMouseEvent) {
				if (aMouseEvent.getClickCount() == 2) {
					onEdit();
				}
			}
		});
		ActionMap actionMap = gestureActionList.getActionMap();
		InputMap inputMap = gestureActionList.getInputMap();
		actionMap.put(ADD_GESTURE_ACTION_ID, addAction);
		actionMap.put(REMOVE_GESTURE_ACTION_ID, removeAction);
		actionMap.put(EDIT_GESTURE_ACTION_ID, editAction);
		inputMap.put(KeyStroke.getKeyStroke("INSERT"), ADD_GESTURE_ACTION_ID);
		inputMap.put(KeyStroke.getKeyStroke("PLUS"), ADD_GESTURE_ACTION_ID);
		inputMap.put(KeyStroke.getKeyStroke("DELETE"), REMOVE_GESTURE_ACTION_ID);
		inputMap.put(KeyStroke.getKeyStroke("MINUS"), REMOVE_GESTURE_ACTION_ID);
		inputMap.put(KeyStroke.getKeyStroke("F2"), EDIT_GESTURE_ACTION_ID);
		mainPanel.add(new JScrollPane(gestureActionList), BorderLayout.CENTER);

		onSelection();
	}

	public JPanel getPanel() {
		return mainPanel;
	}

	public void reset() {
		int size = gestureActions.size();
		if (size > 0) {
			gestureActions.clear();
			gestureActionListModel.fireEmptied(size);
		}
		gestureActions.addAll(mouseGesturesPlugin.getSettings().getGestureActions());
		size = gestureActions.size();
		if (size > 0) {
			gestureActionListModel.fireFilled(size);
		}
	}

	public GestureAction[] getActions() {
		GestureAction[] gestureActions = this.gestureActions.toArray(new GestureAction[this.gestureActions.size()]);
		return gestureActions;
	}

	public void dispose() {
		mainPanel = null;
	}

	public boolean isModified() {
		return mouseGesturesPlugin.getSettings().isModified(gestureActions);
	}

	private void onSelection() {
		boolean hasSelection = gestureActionList.getSelectedIndex() != -1;
		editAction.setEnabled(hasSelection);
		removeAction.setEnabled(hasSelection);
	}

	private void onAdd() {
		GestureAction newGestureAction = addOrEdit(null);
		if (newGestureAction == null) return;
		concreteAdd(newGestureAction);
	}

	private void onRemove() {
		int[] selectedIndices = gestureActionList.getSelectedIndices();
		if (selectedIndices == null || selectedIndices.length == 0) return;
		int startIndex = selectedIndices[0];
		for (int i = selectedIndices.length - 1; i >= 0; i--) {
			int selectedIndice = selectedIndices[i];
			concreteRemove(getGestureActionAt(selectedIndice));
		}
		startIndex = Math.min(startIndex, gestureActions.size() - 1);
		if (startIndex == -1) return;
		gestureActionList.setSelectedIndex(startIndex);
	}

	private void onEdit() {
		int selectedIndex = gestureActionList.getSelectedIndex();
		if (selectedIndex == -1) return;
		GestureAction gestureAction = getGestureActionAt(selectedIndex);
		GestureAction editedGestureAction = addOrEdit(gestureAction);
		if (editedGestureAction == null) return;
		concreteRemove(gestureAction);
		concreteAdd(editedGestureAction);
	}

	private GestureAction addOrEdit(GestureAction selectedGestureAction) {
		Set<String> forbiddenGestures = new TreeSet<String>();
		for (GestureAction gestureAction : gestureActions) {
			if (selectedGestureAction == null || !gestureAction.equals(selectedGestureAction)) {
				forbiddenGestures.add(gestureAction.getGesture());
			}
		}
		prepareGestureActionEditor();
		return gestureActionEditor.edit(selectedGestureAction, forbiddenGestures, settingsPanel.isDiagnoalEnabled());
	}

	private void concreteAdd(GestureAction gestureAction) {
		if (gestureAction == null) return;
		gestureActions.add(gestureAction);
		int index = indexOf(gestureAction);
		gestureActionListModel.fireAdded(index);
		gestureActionList.setSelectedIndex(index);
	}

	private void concreteRemove(GestureAction gestureAction) {
		if (gestureAction == null) return;
		int index = indexOf(gestureAction);
		if (index == -1) return;
		gestureActions.remove(gestureAction);
		gestureActionListModel.fireRemoved(index);
	}

	private int indexOf(GestureAction gestureAction) {
		if (gestureAction == null) return -1;
		int i = 0;
		for (Iterator<GestureAction> iterator = gestureActions.iterator(); iterator.hasNext(); i++) {
			if (iterator.next().equals(gestureAction)) return i;
		}
		return -1;
	}

	private GestureAction getGestureActionAt(int index) {
		int i = 0;
		for (Iterator<GestureAction> iterator = gestureActions.iterator(); iterator.hasNext(); i++) {
			GestureAction gestureAction = iterator.next();
			if (i == index) return gestureAction;
		}
		return null;
	}

	private void prepareGestureActionEditor() {
		if (gestureActionEditor == null) {
			gestureActionEditor = new GestureActionEditor(mainPanel, mouseGesturesPlugin);
		}
	}
}
