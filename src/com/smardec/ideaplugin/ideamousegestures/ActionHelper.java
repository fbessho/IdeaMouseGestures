/*
MouseGestures - pure Java library for recognition and processing mouse gestures.
Copyright (C) 2003-2005 Smardec

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

package com.smardec.ideaplugin.ideamousegestures;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.GeneralPath;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ActionHelper {
	private static final Node[] EMPTY_NODE_ARRAY = new Node[0];
	private static final Icon GROUP_ICON = UIManager.getIcon("Tree.openIcon");
	private static final String ROOT_NODE_TEXT = "root";
	private static final Icon UNSELECTED_ARROW_ICON = new ArrowIcon(false);
	private static final Icon SELECTED_ARROW_ICON = new ArrowIcon(true);

	private static ActionHelper instance;

	private Node rootNode;
	private List<Node> actionNodes;

	public static ActionHelper getInstance() {
		if (instance == null) {
			instance = new ActionHelper();
		}
		return instance;
	}

	private static enum NodeType {GROUP, SIMPLE, SEPARATOR}

	private static class Node {
		private Node parent;
		private String text;
		private Icon icon;
		private NodeType nodeType;
		private AnAction action;
		private Node[] children;
		private String[] actionPath;

		private Node(NodeType nodeType, Node parent, String text, Icon icon, AnAction action) {
			this.nodeType = nodeType;
			this.parent = parent;
			this.text = text;
			this.icon = icon;
			this.action = action;
			this.children = null;
			if (this.nodeType == NodeType.SIMPLE) {
				List<String> strings = new LinkedList<String>();
				strings.add(text);
				while (parent != null && parent.parent != null) {
					strings.add(parent.text);
					parent = parent.parent;
				}
				Collections.reverse(strings);
				actionPath = strings.toArray(new String[strings.size()]);
			} else {
				actionPath = null;
			}
		}
	}

	private static class ArrowIcon implements Icon {
		private boolean selected;

		public ArrowIcon(boolean selected) {
			this.selected = selected;
		}

		public void paintIcon(Component c, Graphics g1D, int x, int y) {
			Graphics2D g = (Graphics2D)g1D;
			Object antialias = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
			Color color = g.getColor();

			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.translate(x, y);

			GeneralPath gp = new GeneralPath();
			gp.moveTo(3.5f, 3.5f);
			gp.lineTo(12.5f, 8);
			gp.lineTo(3.5f, 12.5f);
			gp.closePath();
			g.setColor(selected ? Color.LIGHT_GRAY : Color.DARK_GRAY);
			g.fill(gp);
			g.setColor(selected ? Color.WHITE : Color.BLACK);
			g.draw(gp);

			g.translate(-x, -y);
			g.setColor(color);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antialias);
		}

		public int getIconWidth() {
			return 16;
		}

		public int getIconHeight() {
			return 16;
		}
	}

	private ActionHelper() {
		actionNodes = new LinkedList<Node>();
		rootNode = new Node(NodeType.GROUP, null, ROOT_NODE_TEXT, GROUP_ICON, null);
		AnAction anAction = ActionManager.getInstance().getAction(IdeActions.GROUP_MAIN_MENU);
		if (anAction instanceof DefaultActionGroup) {
			AnAction[] mainMenus = ((DefaultActionGroup)anAction).getChildren(null);
			List<Node> childrenNodes = new LinkedList<Node>();
			for (AnAction mainMenu : mainMenus) {
				childrenNodes.addAll(Arrays.asList(convert(rootNode, mainMenu, actionNodes)));
			}
			rootNode.children = childrenNodes.toArray(new Node[childrenNodes.size()]);
		}
	}

	public boolean isValidAction(String[] actionPath) {
		if (actionPath == null || actionPath.length == 0) return false;
		for (Node actionNode : actionNodes) {
			if (Arrays.equals(actionNode.actionPath, actionPath)) return true;
		}
		return false;
	}

	public void invoke(String[] actionPath) {
		Node actionNode = findActionNode(actionPath);
		if (actionNode == null || actionNode.nodeType != NodeType.SIMPLE) return;
		actionNode.action.actionPerformed(new AnActionEvent(null,
												 DataManager.getInstance().getDataContext(),
												 ActionPlaces.UNKNOWN,
												 actionNode.action.getTemplatePresentation(),
												 com.intellij.openapi.actionSystem.ActionManager.getInstance(),
												 0));
	}

	public JPopupMenu createActionSelectionPopup(final IActionNodeSelectionListener selectionListener) {
		JPopupMenu actionSelectionPopupMenu = new JPopupMenu();
		Node root = rootNode;
		Node[] children = root.children;
		for (Node child : children) {
			if (child.nodeType == NodeType.SEPARATOR) {
				actionSelectionPopupMenu.addSeparator();
			} else {
				actionSelectionPopupMenu.add(createMenuItem(child, selectionListener));
			}
		}
		return actionSelectionPopupMenu;
	}

	public void constructView(JPanel panel, String[] actionPath, Color foreGround, boolean selected) {
		panel.removeAll();
		Node actionNode = findActionNode(actionPath);
		if (actionNode == null) {
			JLabel emptyLabel = new JLabel(" ");
			emptyLabel.setFont(emptyLabel.getFont().deriveFont(12f));
			panel.add(emptyLabel);
			panel.revalidate();
			return;
		}
		List<JLabel> labels = new LinkedList<JLabel>();
		Node parentNode = actionNode.parent;
		labels.add(getLabel(actionNode, foreGround));
		while (parentNode != null && parentNode.parent != null) {
			labels.add(getLabel(parentNode, foreGround));
			parentNode = parentNode.parent;
		}
		for (int i = labels.size() - 1; i >= 0; i--) {
			JLabel label = labels.get(i);
			panel.add(label);
			panel.add(Box.createHorizontalStrut(3));
			if (i > 0) {
				panel.add(new JLabel(selected ? SELECTED_ARROW_ICON : UNSELECTED_ARROW_ICON));
				panel.add(Box.createHorizontalStrut(3));
			}
		}
	}

	private Node findActionNode(String[] actionPath) {
		if (actionPath == null || actionPath.length == 0) return null;
		for (Node actionNode : actionNodes) {
			if (Arrays.equals(actionNode.actionPath, actionPath)) return actionNode;
		}
		return null;
	}

	private static JMenuItem createMenuItem(final Node actionNode,
											final IActionNodeSelectionListener selectionListener) {
		switch (actionNode.nodeType) {
			case SIMPLE: {
				return new JMenuItem(new AbstractAction(actionNode.text, actionNode.icon) {
					public void actionPerformed(ActionEvent e) {
						selectionListener.onSelect(actionNode.actionPath);
					}
				});
			}
			case GROUP: {
				JMenu menu = new JMenu(new AbstractAction(actionNode.text, actionNode.icon) {
					public void actionPerformed(ActionEvent e) {}
				});
				Node[] children = actionNode.children;
				for (Node child : children) {
					if (child.nodeType == NodeType.SEPARATOR) {
						menu.addSeparator();
					} else {
						menu.add(createMenuItem(child, selectionListener));
					}
				}
				return menu;
			}
			default:
				throw new IllegalArgumentException("Unexpected node type");
		}
	}

	private static Node[] convert(Node parent, AnAction action, List<Node> actionNodes) {
		if (action instanceof Separator) return new Node[]{new Node(NodeType.SEPARATOR, parent, null, null, null)};
		if (action instanceof DefaultActionGroup) {
			DefaultActionGroup defaultActionGroup = (DefaultActionGroup)action;
			Presentation presentation = defaultActionGroup.getTemplatePresentation();
			String text = presentation.getText();
			if (text == null) {
				List<Node> childrenNodes = new LinkedList<Node>();
				AnAction[] childrenActions = defaultActionGroup.getChildren(null);
				for (AnAction childAction : childrenActions) {
					childrenNodes.addAll(Arrays.asList(convert(parent, childAction, actionNodes)));
				}
				return childrenNodes.toArray(new Node[childrenNodes.size()]);
			} else {
				Node groupActionNode = new Node(NodeType.GROUP, parent, text, GROUP_ICON, null);
				List<Node> childrenNodes = new LinkedList<Node>();
				AnAction[] childrenActions = defaultActionGroup.getChildren(null);
				for (AnAction childAction : childrenActions) {
					childrenNodes.addAll(Arrays.asList(convert(groupActionNode, childAction, actionNodes)));
				}
				groupActionNode.children = childrenNodes.toArray(new Node[childrenNodes.size()]);
				return new Node[]{groupActionNode};
			}
		} else {
			if (action == null) return EMPTY_NODE_ARRAY;
			Presentation presentation = action.getTemplatePresentation();
			if (presentation == null) return EMPTY_NODE_ARRAY;
			String text = presentation.getText();
			if (text == null) return EMPTY_NODE_ARRAY;
			Node actionNode = new Node(NodeType.SIMPLE, parent, text, presentation.getIcon(), action);
			actionNodes.add(actionNode);
			return new Node[]{actionNode};
		}
	}

	private static JLabel getLabel(Node node, Color foreGround) {
		JLabel label = new JLabel(node.text, node.icon, JLabel.LEADING);
		label.setFont(label.getFont().deriveFont(12f));
		label.setForeground(foreGround);
		return label;
	}
}
