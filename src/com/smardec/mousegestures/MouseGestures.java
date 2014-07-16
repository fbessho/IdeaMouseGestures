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

package com.smardec.mousegestures;

import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Main class for mouse gestures processing.
 * <br>Sample usage:
 * <code><pre>
 *      MouseGestures mouseGestures = new MouseGestures();
 *      mouseGestures.addMouseGesturesListener(myMouseGesturesListener);
 *      mouseGestures.start();</pre>
 * </code>
 */
public class MouseGestures {
	private AWTEventListener mouseGesturesEventListener;
	private MouseGesturesRecognizer mouseGesturesRecognizer;
	private List<MouseGesturesListener> listeners;
	private Movements movements;
	private int mouseButton;
	private int gridSize;
	private boolean diagonalEnabled;
	private boolean wheelEnabled;
	private boolean mouseTrailEnabled;
	private int mouseTrailSize;
	private Color mouseTrailColor;

	public MouseGestures() {
		this(Movements.DEFAULT);
	}

	public MouseGestures(Movements movements) {
		this.movements = movements;
		mouseGesturesEventListener = null;
		mouseGesturesRecognizer = new MouseGesturesRecognizer(this);
		listeners = Collections.synchronizedList(new LinkedList<MouseGesturesListener>());
		mouseButton = MouseEvent.BUTTON3_MASK;
		diagonalEnabled = false;
		wheelEnabled = false;
		gridSize = 50;
		mouseTrailEnabled = false;
		mouseTrailSize = 1;
		mouseTrailColor = Color.BLUE;
	}

	public void addMouseGesturesListener(MouseGesturesListener mouseGesturesListener) {
		if (mouseGesturesListener == null) return;
		listeners.add(mouseGesturesListener);
	}

	public void removeMouseGesturesListener(MouseGesturesListener mouseGesturesListener) {
		if (mouseGesturesListener == null) return;
		listeners.remove(mouseGesturesListener);
	}

	public Movements getMovements() {
		return movements;
	}

	public int getMouseButton() {
		return mouseButton;
	}

	public void setMouseButton(int mouseButton) {
		this.mouseButton = mouseButton;
	}

	public int getGridSize() {
		return gridSize;
	}

	public void setGridSize(int gridSize) {
		this.gridSize = gridSize;
	}

	public boolean isDiagonalEnabled() {
		return diagonalEnabled;
	}

	public void setDiagonalEnabled(boolean diagonalEnabled) {
		this.diagonalEnabled = diagonalEnabled;
	}

	public boolean isWheelEnabled() {
		return wheelEnabled;
	}

	public void setWheelEnabled(boolean wheelEnabled) {
		this.wheelEnabled = wheelEnabled;
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

	public void start() {
		if (mouseGesturesEventListener == null)
			mouseGesturesEventListener = new AWTEventListener() {
				public void eventDispatched(AWTEvent anEvent) {
					if (anEvent instanceof MouseEvent) {
						MouseEvent mouseEvent = (MouseEvent)anEvent;
						if ((mouseEvent.getModifiers() & mouseButton) == mouseButton) {
							mouseGesturesRecognizer.processMouseEvent(mouseEvent);
						}
						if ((mouseEvent.getID() == MouseEvent.MOUSE_RELEASED
							 || mouseEvent.getID() == MouseEvent.MOUSE_CLICKED)
							&& (mouseEvent.getModifiers() & mouseButton) == mouseButton)
							if (mouseGesturesRecognizer.isGestureRecognized()) {
								// prevents displaying popup menu and so on
								mouseEvent.consume();
								String gesture = mouseGesturesRecognizer.getGesture();
								// clear temporary information
								mouseGesturesRecognizer.clearTemporaryInfo();
								// execute action
								fireProcessMouseGesture(gesture);
							} else {
								// clear temporary information
								mouseGesturesRecognizer.clearTemporaryInfo();
							}
					}
				}
			};
		Toolkit.getDefaultToolkit().addAWTEventListener(mouseGesturesEventListener, 48L);
	}

	public void stop() {
		if (mouseGesturesEventListener != null) {
			Toolkit.getDefaultToolkit().removeAWTEventListener(mouseGesturesEventListener);
		}
	}

	void fireGestureMovementRecognized(String gesture) {
		for (MouseGesturesListener listener : listeners) {
			listener.gestureMovementRecognized(gesture);
		}
	}

	private void fireProcessMouseGesture(String aGesture) {
		for (MouseGesturesListener listener : listeners) {
			listener.processGesture(aGesture);
		}
	}
}
