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
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.wm.StatusBar;
import com.smardec.helper.IdeaHelper;
import com.smardec.ideaplugin.ideamousegestures.lang.LangUtils;
import com.smardec.ideaplugin.ideamousegestures.settings.Settings;
import com.smardec.mousegestures.MouseGestures;
import com.smardec.mousegestures.MouseGesturesListener;
import com.smardec.mousegestures.Movements;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;

@State(name = "MouseGestures", storages = { @Storage(id = "MouseGestures", file = "$APP_CONFIG$/mouseGestures.xml") })
public class MouseGesturesPlugin implements ApplicationComponent
		, PersistentStateComponent<Settings>
{
	private Settings theSettings;
	private MouseGestures theMouseGestures;
	private MouseGesturesListener theMouseGesturesListener;
	private AWTEventListener thAwtEventListener;

	public MouseGesturesPlugin() {
		theSettings = new Settings();
		theMouseGestures = createMouseGestures();
		theMouseGesturesListener = new MouseGesturesListener() {
			public void gestureMovementRecognized(String currentGesture) {
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

	public MouseGestures getMouseGestures() {
		return theMouseGestures;
	}

	public Settings getSettings() {
		return theSettings;
	}

	public void setSettings(Settings settings) {
		this.theSettings = settings;
		syncMouseGesturesWithSettings();
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

	public void syncMouseGesturesWithSettings() {
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

	@Nullable @Override public Settings getState()
	{
		return theSettings;
	}

	public void loadState(Settings state) {
		theSettings = state;
	}
}
