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

import com.intellij.util.containers.HashSet;

import java.util.Set;

/**
 * Date: 29 juil. 2007
 * @author: Pierre Le Lannic
 */
public class Movements {
	public static final Movements DEFAULT = new Movements();

	/**
	 * String representation of left movement.
	 */
	private char leftMove;
	/**
	 * String representation of right movement.
	 */
	private char rightMove;
	/**
	 * String representation of up movement.
	 */
	private char upMove;
	/**
	 * String representation of down movement.
	 */
	private char downMove;
	/**
	 * String representation of up-left movement.
	 */
	private char upLeftMove;
	/**
	 * String representation of up-right movement.
	 */
	private char upRightMove;
	/**
	 * String representation of down-left movement.
	 */
	private char downLeftMove;
	/**
	 * String representation of down-right movement.
	 */
	private char downRightMove;
	/**
	 * String representation of wheel up movement.
	 */
	private char wheelUpMove;
	/**
	 * String representation of wheel down movement.
	 */
	private char wheelDownMove;

	public Movements() {
		setDefaults();
	}

	public Movements(String movements) {
		try {
			assertMovements(movements);
			leftMove = movements.charAt(0);
			rightMove = movements.charAt(1);
			upMove = movements.charAt(2);
			downMove = movements.charAt(3);
			downLeftMove = movements.charAt(4);
			downRightMove = movements.charAt(5);
			upLeftMove = movements.charAt(6);
			upRightMove = movements.charAt(7);
			wheelUpMove = movements.charAt(8);
			wheelDownMove = movements.charAt(9);
		} catch (Exception e) {
			e.printStackTrace();
			setDefaults();
		}
	}

	public char getLeftMove() {
		return leftMove;
	}

	public char getRightMove() {
		return rightMove;
	}

	public char getUpMove() {
		return upMove;
	}

	public char getDownMove() {
		return downMove;
	}

	public char getUpLeftMove() {
		return upLeftMove;
	}

	public char getUpRightMove() {
		return upRightMove;
	}

	public char getDownLeftMove() {
		return downLeftMove;
	}

	public char getDownRightMove() {
		return downRightMove;
	}

	public char getWheelUpMove() {
		return wheelUpMove;
	}

	public char getWheelDownMove() {
		return wheelDownMove;
	}
	
	public char[] getMovements() {
		return new char[]{leftMove, rightMove, upMove, downMove, upLeftMove, upRightMove, downLeftMove, downRightMove};
	}

	private void setDefaults() {
		leftMove = 'L';
		rightMove = 'R';
		upMove = 'U';
		downMove = 'D';
		upLeftMove = '7';
		upRightMove = '9';
		downLeftMove = '1';
		downRightMove = '3';
		wheelUpMove = '+';
		wheelDownMove = '-';
	}

	private void assertMovements(String movements) {
		if (movements == null || movements.length() != 10) {
			throw new IllegalArgumentException("Movements " + movements + " should contain 10 chars");
		}
		Set<Character> used = new HashSet<Character>();
		for (int i = 0; i < movements.length(); i++) {
			char c = movements.charAt(i);
			if (used.contains(c)) {
				throw new IllegalArgumentException("Mouvements " + movements + " contains twice the character " + c);
			}
			used.add(c);
		}
	}
}
