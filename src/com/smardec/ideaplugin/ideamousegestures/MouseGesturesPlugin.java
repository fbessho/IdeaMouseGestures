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

package com.smardec.ideaplugin.ideamousegestures;

import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.wm.StatusBar;
import com.smardec.helper.IdeaHelper;
import com.smardec.ideaplugin.ideamousegestures.lang.LangUtils;
import com.smardec.ideaplugin.ideamousegestures.settings.Settings;
import com.smardec.ideaplugin.ideamousegestures.settings.SettingsPanel;
import com.smardec.mousegestures.MouseGestures;
import com.smardec.mousegestures.MouseGesturesListener;
import com.smardec.mousegestures.Movements;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;

public class MouseGesturesPlugin implements ApplicationComponent, JDOMExternalizable, Configurable {
	private static final String PLUGIN_IMAGE = "com/smardec/ideaplugin/ideamousegestures/img/mouse.png";
	private Settings theSettings;
	private SettingsPanel theSettingsPanel;
	private MouseGestures theMouseGestures;
	private MouseGesturesListener theMouseGesturesListener;
	private AWTEventListener thAwtEventListener;
	private boolean isEditingGestureInSettings;

	public MouseGesturesPlugin() {
		theSettings = new Settings();
		theSettingsPanel = null;
		theMouseGestures = createMouseGestures();
		theMouseGesturesListener = new MouseGesturesListener() {
			public void gestureMovementRecognized(String currentGesture) {
				if (isEditingGestureInSettings) return;
				StatusBar statusBar = IdeaHelper.getCurrentStatusBar();
				if (statusBar != null) {
					GestureAction gestureAction = theSettings.getAction(currentGesture);
					String info;
					if (gestureAction != null) {
						info = gestureAction.getDisplayString();
					} else {
						info = GestureAction.formatDisplayGesture(currentGesture) + " " +
							   LangUtils.get(LangUtils.UNKNOW_GESTURE);
					}
					statusBar.setInfo(info);
				}
			}

			public void processGesture(String gesture) {
				if (isEditingGestureInSettings) return;
				try {
					GestureAction gestureAction = theSettings.getAction(gesture);
					if (gestureAction != null) {
						ActionHelper.getInstance().invoke(gestureAction.getActionPath());
					}
					StatusBar statusBar = IdeaHelper.getCurrentStatusBar();
					if (statusBar != null) {
						statusBar.setInfo("");
					}
				} catch (Exception e) {
					//
				}
			}
		};
		isEditingGestureInSettings = false;
	}

	@NotNull
	public String getComponentName() {
		return "com.smardec.ideaplugin.ideamousegestures.MouseGesturesPlugin";
	}

	public void initComponent() {
		initMouseGestures();
		syncMouseGesturesWithSettings();
		theMouseGestures.start();
		theMouseGestures.addMouseGesturesListener(theMouseGesturesListener);
	}

	public void disposeComponent() {
		theMouseGestures.removeMouseGesturesListener(theMouseGesturesListener);
		Toolkit.getDefaultToolkit().removeAWTEventListener(thAwtEventListener);
		theMouseGestures.stop();
	}

	public String getDisplayName() {
		return LangUtils.get(LangUtils.PLUGIN_DISPLAY_NAME);
	}

	public Icon getIcon() {
		return new ImageIcon(getClass().getClassLoader().getResource(PLUGIN_IMAGE));
	}

	public String getHelpTopic() {
		return null;
	}

	public void readExternal(Element element) throws InvalidDataException {
		theSettings.readExternal(element);
	}

	public void writeExternal(Element element) throws WriteExternalException {
		theSettings.writeExternal(element);
	}

	public JComponent createComponent() {
		if (theSettingsPanel == null) {
			theSettingsPanel = new SettingsPanel(this);
		}
		return theSettingsPanel.getPanel();
	}

	public boolean isModified() {
		if (theSettingsPanel == null) return false;
		return theSettingsPanel.isModified();
	}

	public void apply() throws ConfigurationException {
		if (theSettingsPanel == null) return;
		theSettingsPanel.apply();
		syncMouseGesturesWithSettings();
	}

	public void reset() {
		if (theSettingsPanel == null) return;
		theSettingsPanel.reset();
	}

	public void disposeUIResources() {
		if (theSettingsPanel == null) return;
		theSettingsPanel.dispose();
		theSettingsPanel = null;
	}

	public MouseGestures getMouseGestures() {
		return theMouseGestures;
	}

	public Settings getSettings() {
		return theSettings;
	}

	public boolean isEditingGestureInSettings() {
		return isEditingGestureInSettings;
	}

	public void setEditingGestureInSettings(boolean editingGestureInSettings) {
		this.isEditingGestureInSettings = editingGestureInSettings;
	}

	private void initMouseGestures() {
		thAwtEventListener = new AWTEventListener() {
			public void eventDispatched(AWTEvent event) {
				if (event instanceof MouseEvent) {
					MouseEvent mouseEvent = (MouseEvent)event;
					if (theSettings.isBlockRightClickOnEditor() &&
						(mouseEvent.getSource() instanceof DataProvider) &&
						mouseEvent.getID() == 501 && (mouseEvent.getModifiers() & 4) == 4)
						mouseEvent.consume();
				}
			}
		};
		Toolkit.getDefaultToolkit().addAWTEventListener(thAwtEventListener, 48L);
	}

	private void syncMouseGesturesWithSettings() {
		theMouseGestures.setGridSize(theSettings.getGridSize());
		theMouseGestures.setDiagonalEnabled(theSettings.isDiagonalEnabled());
		theMouseGestures.setMouseTrailEnabled(theSettings.isMouseTrailEnabled());
		theMouseGestures.setMouseTrailSize(theSettings.getMouseTrailSize());
		theMouseGestures.setMouseTrailColor(theSettings.getMouseTrailColor());
	}

	private static MouseGestures createMouseGestures() {
		try {
			return new MouseGestures(new Movements(LangUtils.get("plugin.movements")));
		} catch (Exception e) {
			e.printStackTrace();
			return new MouseGestures();
		}
	}
}
