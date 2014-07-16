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

package com.smardec.helper;

import com.intellij.openapi.wm.WindowManager;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;

import javax.swing.*;

public class IdeaHelper {
	public static JFrame getCurrentJFrame() {
		return WindowManager.getInstance().getFrame(getCurrentProjet());
	}

	public static StatusBar getCurrentStatusBar() {
		return WindowManager.getInstance().getStatusBar(getCurrentProjet());
	}

	private static Project getCurrentProjet() {
		Project projects[] = ProjectManager.getInstance().getOpenProjects();
		if (projects == null || projects.length == 0) return null;
		if (projects.length == 1) return projects[0];
		for (Project project : projects) {
			if (WindowManager.getInstance().getFrame(project).isActive()) return project;
		}
		return projects[0];
	}
}
