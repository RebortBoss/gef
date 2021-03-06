/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.policies;

import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.fx.nodes.OrthogonalRouter;
import org.eclipse.gef.fx.utils.NodeUtils;
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Line;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Polyline;
import org.eclipse.gef.mvc.behaviors.SelectionBehavior;
import org.eclipse.gef.mvc.models.GridModel;
import org.eclipse.gef.mvc.models.SelectionModel;
import org.eclipse.gef.mvc.parts.IContentPart;
import org.eclipse.gef.mvc.parts.IVisualPart;
import org.eclipse.gef.mvc.policies.AbstractTransformPolicy;

import com.google.common.reflect.TypeToken;

import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * Uses the {@link FXBendConnectionPolicy} of its host to move the dragged
 * connection segment.
 */
public class FXBendOnSegmentDragPolicy extends AbstractFXInteractionPolicy
		implements IFXOnDragPolicy {

	private CursorSupport cursorSupport = new CursorSupport(this);
	private Point initialMouseInScene;
	private boolean isInvalid = false;
	private boolean isPrepared;

	@Override
	public void drag(MouseEvent e, Dimension delta) {
		if (isInvalid) {
			return;
		}

		// prepare for manipulation upon first drag
		if (!isPrepared) {
			isPrepared = true;
			prepareBend(getBendPolicy());
		}

		Point2D endPointInParent = getHost().getVisual().getParent()
				.sceneToLocal(e.getSceneX(), e.getSceneY());
		Dimension snapToGridOffset = AbstractTransformPolicy
				.getSnapToGridOffset(
						getHost().getRoot().getViewer().<GridModel> getAdapter(
								GridModel.class),
						endPointInParent.getX(), endPointInParent.getY(),
						getSnapToGridGranularityX(),
						getSnapToGridGranularityY());
		endPointInParent = new Point2D(
				endPointInParent.getX() - snapToGridOffset.width,
				endPointInParent.getY() - snapToGridOffset.height);
		Point2D endPointInScene = getHost().getVisual().getParent()
				.localToScene(endPointInParent);
		getBendPolicy().move(initialMouseInScene,
				FX2Geometry.toPoint(endPointInScene));
		updateHandles();
	}

	@Override
	public void dragAborted() {
		if (isInvalid) {
			return;
		}

		rollback(getBendPolicy());
		restoreRefreshVisuals(getHost());

		updateHandles();
	}

	/**
	 * Returns the {@link FXBendConnectionPolicy} of the host.
	 *
	 * @return The {@link FXBendConnectionPolicy} of the host.
	 */
	protected FXBendConnectionPolicy getBendPolicy() {
		return getHost().getAdapter(FXBendConnectionPolicy.class);
	}

	/**
	 * Returns the {@link CursorSupport} of this policy.
	 *
	 * @return The {@link CursorSupport} of this policy.
	 */
	protected CursorSupport getCursorSupport() {
		return cursorSupport;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IVisualPart<Node, Connection> getHost() {
		return (IVisualPart<Node, Connection>) super.getHost();
	}

	/**
	 * Returns the horizontal granularity for "snap-to-grid" where
	 * <code>1</code> means it will snap to integer grid positions.
	 *
	 * @return The horizontal granularity for "snap-to-grid".
	 */
	protected double getSnapToGridGranularityX() {
		return 1;
	}

	/**
	 * Returns the vertical granularity for "snap-to-grid" where <code>1</code>
	 * means it will snap to integer grid positions.
	 *
	 * @return The vertical granularity for "snap-to-grid".
	 */
	protected double getSnapToGridGranularityY() {
		return 1;
	}

	@Override
	public void hideIndicationCursor() {
		cursorSupport.restoreCursor();
	}

	/**
	 * Returns <code>true</code> if the given {@link MouseEvent} should trigger
	 * bending. Otherwise returns <code>false</code>. Per default returns
	 * <code>true</code> if a single mouse click is performed.
	 *
	 * @param event
	 *            The {@link MouseEvent} in question.
	 * @return <code>true</code> if the given {@link MouseEvent} should trigger
	 *         focus and select, otherwise <code>false</code>.
	 */
	protected boolean isBend(MouseEvent event) {
		boolean isInvalid = false;
		if (!(getHost().getVisual().getRouter() instanceof OrthogonalRouter)) {
			// abort if non-orthogonal
			isInvalid = true;
		} else {
			IVisualPart<Node, ? extends Node> host = getHost();
			@SuppressWarnings("serial")
			ObservableList<IContentPart<Node, ? extends Node>> selection = host
					.getRoot().getViewer()
					.getAdapter(new TypeToken<SelectionModel<Node>>() {
					}).getSelectionUnmodifiable();
			if (selection.size() > 1 && selection.contains(host)) {
				// abort if part of multiple selection
				isInvalid = true;
			} else if (!getHost().getVisual().isStartConnected()
					&& !getHost().getVisual().isEndConnected()) {
				// abort if unconnected
				isInvalid = true;
			}
		}
		return !isInvalid;
	}

	/**
	 * Prepares the given {@link FXBendConnectionPolicy} for the manipulation of
	 * its host.
	 *
	 * @param bendPolicy
	 *            The {@link FXBendConnectionPolicy} that is prepared.
	 */
	private void prepareBend(FXBendConnectionPolicy bendPolicy) {
		// determine curve in scene coordinates
		Connection connection = bendPolicy.getConnection();

		// construct polyline for connection points
		Polyline polyline = new Polyline(
				connection.getPointsUnmodifiable().toArray(new Point[] {}));
		Polyline polylineInScene = (Polyline) NodeUtils.localToScene(connection,
				polyline);

		// determine pressed segment (nearest to mouse)
		Line[] segmentsInScene = polylineInScene.getCurves();
		double minDistance = -1;
		int segmentIndex = -1;
		for (int i = 0; i < segmentsInScene.length; i++) {
			Line segment = segmentsInScene[i];
			Point projection = segment.getProjection(initialMouseInScene);
			double distance = projection.getDistance(initialMouseInScene);
			if (minDistance < 0 || distance < minDistance) {
				minDistance = distance;
				segmentIndex = i;
			}
		}

		if (segmentIndex < 0) {
			// it is better to die than to return in failure
			throw new IllegalStateException("Cannot identify pressed segment.");
		}

		// select segment
		bendPolicy.selectSegment(segmentIndex);
	}

	@Override
	public void press(MouseEvent e) {
		isInvalid = !isBend(e);
		if (isInvalid) {
			return;
		}

		isPrepared = false;

		// save initial mouse position in scene coordinates
		initialMouseInScene = new Point(e.getSceneX(), e.getSceneY());

		// disable refresh visuals for the host
		storeAndDisableRefreshVisuals(getHost());

		// initialize bend policy
		FXBendConnectionPolicy bendPolicy = getBendPolicy();
		init(bendPolicy);
		updateHandles();
	}

	@Override
	public void release(MouseEvent e, Dimension delta) {
		if (isInvalid) {
			return;
		}

		commit(getBendPolicy());
		restoreRefreshVisuals(getHost());

		updateHandles();
	}

	@Override
	public boolean showIndicationCursor(KeyEvent event) {
		return false;
	}

	@Override
	public boolean showIndicationCursor(MouseEvent event) {
		// TODO: Show <|> or ^-v indication cursor for segment movement.
		// cursorSupport.storeAndReplaceCursor(verticalSegment ?
		// LEFT_RIGHT_CURSRO : TOP_DOWN_CURSOR);
		return false;
	}

	/**
	 * Updates the selection handles.
	 */
	@SuppressWarnings("unchecked")
	protected void updateHandles() {
		getHost().getRoot().getAdapter(SelectionBehavior.class)
				.updateHandles(getHost(), null, null);
	}

}
