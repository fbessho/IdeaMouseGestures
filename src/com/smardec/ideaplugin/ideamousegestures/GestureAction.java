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

import java.util.Arrays;

public class GestureAction implements Comparable<GestureAction> {
	private static final String SEPARATOR = " -> ";
	private String gesture;
	private String[] actionPath;

	public static String formatDisplayGesture(String gesture) {
		String displayString = "";
		for (int i = 0; i < gesture.length(); i++) {
			displayString += gesture.charAt(i);
			if (i != gesture.length() - 1) {
				displayString += ',';
			}
		}
		return displayString;
	}

	public GestureAction(String gesture, String[] actionPath) {
		this.gesture = gesture;
		this.actionPath = actionPath;
	}

	public int compareTo(GestureAction that) {
		int gestureCompare = this.gesture.compareTo(that.gesture);
		if (gestureCompare < 0) return -1;
		if (gestureCompare > 0) return 1;
		for (int i = 0; i < this.actionPath.length && i < that.actionPath.length; i++) {
			int pathItemCompare = this.actionPath[i].compareTo(that.actionPath[i]);
			if (pathItemCompare < 0) return -1;
			if (pathItemCompare > 0) return 1;
		}
		if (this.actionPath.length < that.actionPath.length) return -1;
		if (this.actionPath.length > that.actionPath.length) return 1;
		return 0;
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GestureAction that = (GestureAction)o;
		if (!Arrays.equals(actionPath, that.actionPath)) return false;
		if (!gesture.equals(that.gesture)) return false;
		return true;
	}

	public int hashCode() {
		int result;
		result = gesture.hashCode();
		result = 31 * result + Arrays.hashCode(actionPath);
		return result;
	}

	public String getGesture() {
		return gesture;
	}

	public String[] getActionPath() {
		return actionPath;
	}

	public String getDisplayString() {
		String result = formatDisplayGesture(gesture);
		result += " (";
		for (int i = 0; i < actionPath.length; i++) {
			result += actionPath[i];
			if (i < actionPath.length - 1) result += SEPARATOR;
		}
		result += ")";
		return result;
	}
}
