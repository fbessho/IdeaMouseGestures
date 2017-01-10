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

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.smardec.ideaplugin.ideamousegestures.lang.LangUtils;
import com.smardec.ideaplugin.ideamousegestures.settings.Settings;
import com.smardec.ideaplugin.ideamousegestures.settings.SettingsPanel;

import javax.swing.*;

public class MouseGesturesConfiguable implements Configurable {
	private static final String PLUGIN_IMAGE = "com/smardec/ideaplugin/ideamousegestures/img/mouse.png";
	private Settings theSettings;
	private SettingsPanel theSettingsPanel;
	private MouseGesturesPlugin app;

	public MouseGesturesConfiguable(MouseGesturesPlugin app) {
		this.app = app;
		theSettings = new Settings();
		theSettingsPanel = null;
	}

	public String getDisplayName() {
		return LangUtils.get(LangUtils.PLUGIN_DISPLAY_NAME);
	}

	public String getHelpTopic() {
		return null;
	}

	public JComponent createComponent() {
		if (theSettingsPanel == null) {
			theSettingsPanel = new SettingsPanel(app);
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
		app.syncMouseGesturesWithSettings();
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

	public Settings getSettings() {
		return theSettings;
	}

}
