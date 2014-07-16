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

package com.smardec.ideaplugin.ideamousegestures.lang;

import java.util.ResourceBundle;

public class LangUtils {
	public static final String PLUGIN_DISPLAY_NAME = "plugin.display.name";
	public static final String SETTINGS_ADVANCED_BLOCK = "settings.advanced.block";
	public static final String SETTINGS_ADVANCED_BLOCK_TIP = "settings.advanced.block.tip";
	public static final String SETTINGS_ADVANCED_DIAGONAL = "settings.advanced.diagonal";
	public static final String SETTINGS_ADVANCED_DIAGONAL_TIP = "settings.advanced.diagonal.tip";
	public static final String SETTINGS_ADVANCED_MINMOVEMENT_TEXT = "settings.advanced.minmovement.text";
	public static final String SETTINGS_ADVANCED_MINMOVEMENT_TIP = "settings.advanced.minmovement.tip";
	public static final String SETTINGS_ADVANCED_TITLE = "settings.advanced.title";
	public static final String SETTINGS_GESTURES_ADD = "settings.gestures.add";
	public static final String SETTINGS_GESTURES_ADD_EXPLAIN = "settings.gestures.add.explain";
	public static final String SETTINGS_GESTURES_EDIT = "settings.gestures.edit";
	public static final String SETTINGS_GESTURES_EDITOR_ACCEPT = "settings.gestures.editor.accept";
	public static final String SETTINGS_GESTURES_EDITOR_ACTION_TEXT = "settings.gestures.editor.action.text";
	public static final String SETTINGS_GESTURES_EDITOR_ADD_TITLE = "settings.gestures.editor.add.title";
	public static final String SETTINGS_GESTURES_EDITOR_CANCEL = "settings.gestures.editor.cancel";
	public static final String SETTINGS_GESTURES_EDITOR_EDIT_TITLE = "settings.gestures.editor.edit.title";
	public static final String SETTINGS_GESTURES_EDITOR_HELP = "settings.gestures.editor.help";
	public static final String SETTINGS_GESTURES_EDITOR_HELP_DIALOG_TITLE = "settings.gestures.editor.help.dialog.title";
	public static final String SETTINGS_GESTURES_EDITOR_HELP_DIALOG_MESSAGE = "settings.gestures.editor.help.dialog.message";
	public static final String SETTINGS_GESTURES_EDITOR_GESTURE_TEXT = "settings.gestures.editor.gesture.text";
	public static final String SETTINGS_GESTURES_EDIT_EXPLAIN = "settings.gestures.edit.explain";
	public static final String SETTINGS_GESTURES_REMOVE = "settings.gestures.remove";
	public static final String SETTINGS_GESTURES_REMOVE_EXPLAIN = "settings.gestures.remove.explain";
	public static final String SETTINGS_GESTURES_TITLE = "settings.gestures.title";
	public static final String SETTINGS_MOUSETRAIL_COLOR = "settings.mousetrail.color";
	public static final String SETTINGS_MOUSETRAIL_COLORSETTINGS_ACCEPT = "settings.mousetrail.colorsettings.accept";
	public static final String SETTINGS_MOUSETRAIL_COLORSETTINGS_CANCEL = "settings.mousetrail.colorsettings.cancel";
	public static final String SETTINGS_MOUSETRAIL_COLORSETTINGS_TITLE = "settings.mousetrail.colorsettings.title";
	public static final String SETTINGS_MOUSETRAIL_SIZE = "settings.mousetrail.size";
	public static final String SETTINGS_MOUSETRAIL_TEXT = "settings.mousetrail.text";
	public static final String SETTINGS_MOUSETRAIL_TIP = "settings.mousetrail.tip";
	public static final String SETTINGS_MOUSETRAIL_TITLE = "settings.mousetrail.title";
	public static final String SETTINGS_TITLE = "settings.title";
	public static final String UNKNOW_GESTURE = "unknow.gesture";

	private static ResourceBundle bundle;

	public static String get(String key) {
		if (bundle == null) {
			bundle = ResourceBundle.getBundle("com.smardec.ideaplugin.ideamousegestures.lang.mousegestures");
		}
		return bundle.getString(key);
	}
}
