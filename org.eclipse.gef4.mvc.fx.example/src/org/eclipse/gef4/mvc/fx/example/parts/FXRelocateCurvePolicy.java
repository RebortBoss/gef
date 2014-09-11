/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.example.parts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.fx.anchors.FXStaticAnchor;
import org.eclipse.gef4.fx.nodes.FXConnection;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.operations.FXBendOperation;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocatePolicy;

public class FXRelocateCurvePolicy extends FXResizeRelocatePolicy {

	private FXBendOperation op;
	private Point[] initialPositions;

	@Override
	public IUndoableOperation commit() {
		return op;
	}

	/**
	 * Returns the indices of all movable anchors. Only those anchors are
	 * relocated by this policy.
	 *
	 * @return {@link List} of {@link Integer}s specifying the anchors to
	 *         relocate.
	 */
	protected List<Integer> getIndicesOfMovableAnchors() {
		List<Integer> indices = new ArrayList<Integer>();
		if (!op.getConnection().isStartConnected()) {
			indices.add(0);
		}
		for (int i = 0; i < op.getNewAnchors().size() - 2; i++) {
			if (!op.getConnection().isWayConnected(i)) {
				indices.add(i + 1);
			}
		}
		if (!op.getConnection().isEndConnected()) {
			indices.add(op.getNewAnchors().size() - 1);
		}
		return indices;
	}

	@Override
	public void init() {
		op = new FXBendOperation((FXConnection) getHost().getVisual());
		initialPositions = op.getConnection().getPoints();
	}

	/**
	 * Executes the current operation without pushing it onto the operation
	 * history.
	 */
	protected void locallyExecuteOperation() {
		try {
			op.execute(null, null);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void performResizeRelocate(double dx, double dy, double dw, double dh) {
		for (int i : getIndicesOfMovableAnchors()) {
			Point p = initialPositions[i];
			op.getNewAnchors().set(
					i,
					new FXStaticAnchor(JavaFX2Geometry.toPoint(getHost()
							.getVisual().localToScene(p.x + dx, p.y + dy))));
		}
		locallyExecuteOperation();
	}

}