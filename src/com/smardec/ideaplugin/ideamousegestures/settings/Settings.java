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

import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import com.smardec.ideaplugin.ideamousegestures.ActionHelper;
import com.smardec.ideaplugin.ideamousegestures.GestureAction;
import org.jdom.Element;
import org.jdom.Text;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Settings implements JDOMExternalizable {
	private static final String TAG_GESTURE = "gesture";
	private static final String TAG_GRID_SIZE = "grid-size";
	private static final String TAG_BLOCK_RIGHT_CLICK = "block-right-click";
	private static final String TAG_DIAGONAL_ENABLED = "diagonal-enabled";
	private static final String TAG_MOUSE_TRAIL_ENABLED = "mouse-trail-enabled";
	private static final String TAG_MOUSE_TRAIL_SIZE = "mouse-trail-size";
	private static final String TAG_MOUSE_TRAIL_COLOR = "mouse-trail-color";
	private static final String TAG_GESTURE_ACTIONS = "gesture-actions";
	private static final String TAG_GESTURE_ACTION = "gesture-action";
	private static final String TAG_ACTION_PATH = "action-path";
	private static final String TAG_ACTION_PATH_ITEM = "action-path-item";

	private static final int     DEFAULT_GRID_SIZE = 30;
	private static final boolean DEFAULT_BLOCK_RIGHT_CLICK_ON_EDITOR = true;
	private static final boolean DEFAULT_DIAGONAL_ENABLED = false;
	private static final boolean DEFAULT_MOUSE_TRAIL_ENABLED = true;
	private static final int     DEFAULT_MOUSE_TRAIL_SIZE = 5;
	private static final Color   DEFAULT_MOUSE_TRAIL_COLOR = Color.RED;

	private int gridSize;
	private boolean blockRightClickOnEditor;
	private boolean diagonalEnabled;
	private boolean mouseTrailEnabled;
	private int mouseTrailSize;
	private Color mouseTrailColor;
	private SortedSet<GestureAction> gestureActions;

	public Settings() {
		reset();
	}

	private void reset() {
		gridSize = DEFAULT_GRID_SIZE;
		blockRightClickOnEditor = DEFAULT_BLOCK_RIGHT_CLICK_ON_EDITOR;
		gestureActions = new TreeSet<GestureAction>();
		diagonalEnabled = DEFAULT_DIAGONAL_ENABLED;
		mouseTrailEnabled = DEFAULT_MOUSE_TRAIL_ENABLED;
		mouseTrailSize = DEFAULT_MOUSE_TRAIL_SIZE;
		mouseTrailColor = DEFAULT_MOUSE_TRAIL_COLOR;
	}

	public void readExternal(Element anElement) throws InvalidDataException {
		reset();
		try {
			String gridSizeText = anElement.getChildTextTrim(TAG_GRID_SIZE);
			if (gridSizeText != null) gridSize = Integer.parseInt(gridSizeText);

			String blockRightClickText = anElement.getChildTextTrim(TAG_BLOCK_RIGHT_CLICK);
			if (blockRightClickText != null) blockRightClickOnEditor = Boolean.parseBoolean(blockRightClickText);

			String diagonalEnabledText = anElement.getChildTextTrim(TAG_DIAGONAL_ENABLED);
			if (diagonalEnabledText != null) diagonalEnabled = Boolean.parseBoolean(diagonalEnabledText);

			String mouseTrailEnabledText = anElement.getChildTextTrim(TAG_MOUSE_TRAIL_ENABLED);
			if (mouseTrailEnabledText != null) mouseTrailEnabled = Boolean.parseBoolean(mouseTrailEnabledText);

			if (mouseTrailEnabled) {
				String mouseTrailSizeText = anElement.getChildTextTrim(TAG_MOUSE_TRAIL_SIZE);
				if (mouseTrailSizeText != null) mouseTrailSize = Integer.parseInt(mouseTrailSizeText);

				String mouseTrailColorText = anElement.getChildTextTrim(TAG_MOUSE_TRAIL_COLOR);
				if (mouseTrailColorText != null) mouseTrailColor = new Color(Integer.parseInt(mouseTrailColorText));
			}

			Element gestureActionsElement = anElement.getChild(TAG_GESTURE_ACTIONS);
			if (gestureActionsElement != null) {
				List<GestureAction> gestureActionList = new LinkedList<GestureAction>();
				for (Object o : gestureActionsElement.getChildren(TAG_GESTURE_ACTION)) {
					Element gestureActionElement = (Element)o;
					String gesture = gestureActionElement.getChildTextTrim(TAG_GESTURE);
					List<String> pathItemList = new LinkedList<String>();
					Element actionPathElement = gestureActionElement.getChild(TAG_ACTION_PATH);
					if (actionPathElement != null) {
						for (Object oo : actionPathElement.getChildren(TAG_ACTION_PATH_ITEM)) {
							String pathItem = ((Element)oo).getTextTrim();
							if (pathItem != null) pathItemList.add(pathItem);
						}
					}
					String[] path = pathItemList.toArray(new String[pathItemList.size()]);
					if (ActionHelper.getInstance().isValidAction(path)) {
						gestureActionList.add(new GestureAction(gesture, path));
					}
				}
				setGestureActions(gestureActionList.toArray(new GestureAction[gestureActionList.size()]));
			}
		} catch (Exception e) {
			throw new InvalidDataException(e.toString());
		}
	}

	public void writeExternal(Element anElement) throws WriteExternalException {
		try {
			Element gridSizeElement = new Element(TAG_GRID_SIZE);
			gridSizeElement.setContent(new Text(Integer.toString(gridSize)));
			anElement.addContent(gridSizeElement);

			Element blockRightClickElement = new Element(TAG_BLOCK_RIGHT_CLICK);
			blockRightClickElement.setContent(new Text(Boolean.toString(blockRightClickOnEditor)));
			anElement.addContent(blockRightClickElement);

			Element diagonalEnabledElement = new Element(TAG_DIAGONAL_ENABLED);
			diagonalEnabledElement.setContent(new Text(Boolean.toString(diagonalEnabled)));
			anElement.addContent(diagonalEnabledElement);

			Element mouseTrailEnabledElement = new Element(TAG_MOUSE_TRAIL_ENABLED);
			mouseTrailEnabledElement.setContent(new Text(Boolean.toString(mouseTrailEnabled)));
			anElement.addContent(mouseTrailEnabledElement);

			if (mouseTrailEnabled) {
				Element mouseTrailSizeElement = new Element(TAG_MOUSE_TRAIL_SIZE);
				mouseTrailSizeElement.setContent(new Text(Integer.toString(mouseTrailSize)));
				anElement.addContent(mouseTrailSizeElement);

				Element mouseTrailColorElement = new Element(TAG_MOUSE_TRAIL_COLOR);
				mouseTrailColorElement.setContent(new Text(Integer.toString(mouseTrailColor.getRGB())));
				anElement.addContent(mouseTrailColorElement);
			}

			Element gestureActionsElement = new Element(TAG_GESTURE_ACTIONS);
			for (GestureAction gestureAction : gestureActions) {
				Element gestureActionElement = new Element(TAG_GESTURE_ACTION);

				Element gestureElement = new Element(TAG_GESTURE);
				gestureElement.setContent(new Text(gestureAction.getGesture()));
				gestureActionElement.addContent(gestureElement);

				String[] path = gestureAction.getActionPath();
				Element pathElement = new Element(TAG_ACTION_PATH);
				for (String pathItem : path) {
					Element pathItemElement = new Element(TAG_ACTION_PATH_ITEM);
					pathItemElement.setContent(new Text(pathItem));
					pathElement.addContent(pathItemElement);
				}
				gestureActionElement.addContent(pathElement);
				gestureActionsElement.addContent(gestureActionElement);
			}
			anElement.addContent(gestureActionsElement);
		} catch (Exception e) {
			throw new WriteExternalException(e.toString());
		}
	}

	public GestureAction getAction(String aGesture) {
		if (aGesture == null) return null;
		for (GestureAction gestureAction : gestureActions) {
			if (gestureAction.getGesture().equals(aGesture)) return gestureAction;
		}
		return null;
	}

	public Collection<GestureAction> getGestureActions() {
		return Collections.unmodifiableCollection(gestureActions);
	}

	public boolean isModified(Set<GestureAction> someGestureActions) {
		return !gestureActions.equals(someGestureActions);
	}

	public void setGestureActions(GestureAction[] someGestureActions) {
		gestureActions.clear();
		gestureActions.addAll(Arrays.asList(someGestureActions));
	}

	public int getGridSize() {
		return gridSize;
	}

	public void setGridSize(int aGridSize) {
		gridSize = aGridSize;
	}

	public boolean isBlockRightClickOnEditor() {
		return blockRightClickOnEditor;
	}

	public void setBlockRightClickOnEditor(boolean blockRightClickOnEditor) {
		this.blockRightClickOnEditor = blockRightClickOnEditor;
	}

	public boolean isDiagonalEnabled() {
		return diagonalEnabled;
	}

	public void setDiagonalEnabled(boolean diagonalEnabled) {
		this.diagonalEnabled = diagonalEnabled;
	}

	public boolean isMouseTrailEnabled() {
		return mouseTrailEnabled;
	}

	public void setMouseTrailEnabled(boolean mouseTrailEnabled) {
		this.mouseTrailEnabled = mouseTrailEnabled;
	}

	public int getMouseTrailSize() {
		return mouseTrailSize;
	}

	public void setMouseTrailSize(int mouseTrailSize) {
		this.mouseTrailSize = mouseTrailSize;
	}

	public Color getMouseTrailColor() {
		return mouseTrailColor;
	}

	public void setMouseTrailColor(Color mouseTrailColor) {
		this.mouseTrailColor = mouseTrailColor;
	}
}
