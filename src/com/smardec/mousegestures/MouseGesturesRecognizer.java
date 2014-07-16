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

import com.smardec.helper.IdeaHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class MouseGesturesRecognizer {
	/**
	 * Tangent 22.5. For diagonal gestures handling.
	 */
	private static final float tg22dot5 = (float)0.41421357; // tg(22.5)

	/**
	 * Tangent 67.5. For diagonal gestures handling.
	 */
	private static final float tg67dot5 = (float)2.4142137; // tg(67.5)

	private MouseGestures mouseGestures;
	private Point startPoint;
	private StringBuffer gesture;
	private JFrame ideaFrame;
	private Point mouseTrailLastPoint;
	private Graphics2D mouseTrailGraphics;

	public MouseGesturesRecognizer(MouseGestures mouseGestures) {
		this.mouseGestures = mouseGestures;
		startPoint = null;
		gesture = new StringBuffer();
	}

	public void clearTemporaryInfo() {
		startPoint = null;
		gesture.delete(0, gesture.length());
		if (mouseTrailGraphics != null) {
			mouseTrailLastPoint = null;
			ideaFrame.setIgnoreRepaint(false);
			ideaFrame.repaint();
			ideaFrame = null;
			mouseTrailGraphics = null;
		}
	}

	public String getGesture() {
		return gesture.toString();
	}

	public boolean isGestureRecognized() {
		return gesture.length() > 0;
	}

	public void processMouseEvent(MouseEvent mouseEvent) {
		if (!(mouseEvent.getSource() instanceof Component)) return;
		Movements movements = mouseGestures.getMovements();
		try {
			if (mouseGestures.isWheelEnabled() && mouseEvent instanceof MouseWheelEvent) {
				MouseWheelEvent mouseWheelEvent = (MouseWheelEvent) mouseEvent;
				int wheelRotation = mouseWheelEvent.getWheelRotation();
				if (wheelRotation < 0) {
					saveMove(movements.getWheelUpMove());
				} else if (wheelRotation > 0) {
					saveMove(movements.getWheelDownMove());
				}
			}
		} catch (NoClassDefFoundError e) {
			//
		}

		Point mouseEventPoint = mouseEvent.getPoint();

		SwingUtilities.convertPointToScreen(mouseEventPoint, (Component)mouseEvent.getSource());

		processMouseTrail(mouseEventPoint);
		if (startPoint == null) {
			startPoint = mouseEventPoint;
			return;
		}

		int deltaX = mouseEventPoint.x - startPoint.x;
		int deltaY = mouseEventPoint.y - startPoint.y;
		int absDeltaX = Math.abs(deltaX);
		int absDeltaY = Math.abs(deltaY);
		if (absDeltaX < mouseGestures.getGridSize() && absDeltaY < mouseGestures.getGridSize()) return;
		float absTangent = (float)absDeltaX / (float)absDeltaY;
		if (!mouseGestures.isDiagonalEnabled()) {
			if (absTangent < 1) {
				if (deltaY < 0) {
					saveMove(movements.getUpMove());
				} else {
					saveMove(movements.getDownMove());
				}
			} else {
				if (deltaX < 0) {
					saveMove(movements.getLeftMove());
				} else {
					saveMove(movements.getRightMove());
				}
			}
		} else {
			if (absTangent < tg22dot5) {
				if (deltaY < 0) {
					saveMove(movements.getUpMove());
				} else {
					saveMove(movements.getDownMove());
				}
			} else if (absTangent < tg67dot5) {
				if (deltaY < 0) { // up
					if (deltaX < 0) {
						// left
						saveMove(movements.getUpLeftMove());
					} else {
						// right
						saveMove(movements.getUpRightMove());
					}
				} else { // down
					if (deltaX < 0) {
						// left
						saveMove(movements.getDownLeftMove());
					} else {
						// right
						saveMove(movements.getDownRightMove());
					}
				}
			} else { // absTangent >= tg67dot5
				if (deltaX < 0) {
					saveMove(movements.getLeftMove());
				} else {
					saveMove(movements.getRightMove());
				}
			}
		}
		startPoint = mouseEventPoint;
	}

	private void saveMove(char move) {
		// should not store two equal moves in succession, allows multiple wheel moves
		Movements movements = mouseGestures.getMovements();
		if (move != movements.getWheelUpMove() &&
			move != movements.getWheelDownMove() &&
			gesture.length() > 0 &&
			gesture.charAt(gesture.length() - 1) == move) return;
		gesture.append(move);
		mouseGestures.fireGestureMovementRecognized(getGesture());
	}

	private void processMouseTrail(Point mouseEventPoint) {
		if (mouseGestures.isMouseTrailEnabled()) {
			if (mouseTrailGraphics == null) {
				if (ideaFrame == null) {
					ideaFrame = IdeaHelper.getCurrentJFrame();
				}
				mouseTrailGraphics = (Graphics2D)ideaFrame.getGraphics();
				ideaFrame.setIgnoreRepaint(true);
			}
			Point ideaLocation = ideaFrame.getLocationOnScreen();
			mouseEventPoint.x -= ideaLocation.x;
			mouseEventPoint.y -= ideaLocation.y;
			if (mouseTrailLastPoint != null) {
				mouseTrailGraphics.setColor(mouseGestures.getMouseTrailColor());
				BasicStroke stroke = new BasicStroke(mouseGestures.getMouseTrailSize());
				mouseTrailGraphics.setStroke(stroke);
				mouseTrailGraphics.drawLine(mouseTrailLastPoint.x, mouseTrailLastPoint.y,
											mouseEventPoint.x, mouseEventPoint.y);
			}
			mouseTrailLastPoint = mouseEventPoint;
		}
	}
}
