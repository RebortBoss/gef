/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.parts;

import java.util.Collections;
import java.util.List;

import org.eclipse.gef4.fx.listeners.VisualChangeListener;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import javafx.geometry.Bounds;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

/**
 * Abstract base class for external labels.
 *
 * @author anyssen
 *
 */
public abstract class AbstractLabelPart extends AbstractFXContentPart<Group> {

	/**
	 * The CSS class that is assigned to the visualization of the
	 * {@link EdgeLabelPart} of this {@link EdgeContentPart}.
	 */
	public static final String CSS_CLASS_LABEL = "label";
	private VisualChangeListener vcl = new VisualChangeListener() {
		@Override
		protected void boundsInLocalChanged(Bounds oldBounds, Bounds newBounds) {
			refreshVisual();
		}

		@Override
		protected void localToParentTransformChanged(Node observed, Transform oldTransform, Transform newTransform) {
			refreshVisual();
		}
	};
	private Translate translate;
	private Text text;

	@Override
	protected void attachToAnchorageVisual(IVisualPart<Node, ? extends Node> anchorage, String role) {
		vcl.register(anchorage.getVisual(), getVisual());
	}

	/**
	 * Creates the text visual.
	 *
	 * @return The created {@link Text}.
	 */
	protected Text createText() {
		text = new Text();
		text.setTextOrigin(VPos.TOP);
		text.setManaged(false);
		text.setPickOnBounds(true);
		// add translation transform to the Text
		translate = new Translate();
		text.getTransforms().add(translate);
		// add css class
		text.getStyleClass().add(CSS_CLASS_LABEL);
		return text;
	}

	@Override
	protected void detachFromAnchorageVisual(IVisualPart<Node, ? extends Node> anchorage, String role) {
		vcl.unregister();
	}

	@Override
	protected List<? extends Object> doGetContentChildren() {
		return Collections.emptyList();
	}

	/**
	 * Returns the offset that is currently applied to the label.
	 *
	 * @return The offset that is currently applied to the label.
	 */
	public Translate getOffset() {
		return translate;
	}

	/**
	 * Returns the text visual.
	 *
	 * @return The {@link Text} used as visual.
	 */
	protected Text getText() {
		return text;
	}

	/**
	 * Adjusts the label's position to fit the given {@link Point}.
	 *
	 * @param visual
	 *            This node's visual.
	 * @param position
	 *            This node's position.
	 */
	protected void refreshPosition(Node visual, Point position) {
		if (position != null) {
			// TODO: use transform policy, so positions are persisted properly
			getVisual().setTranslateX(position.x);
			getVisual().setTranslateY(position.y);

			// // translate
			// FXTransformPolicy transformPolicy =
			// getAdapter(FXTransformPolicy.class);
			// transformPolicy.init();
			// transformPolicy.setTransform(new AffineTransform(1, 0, 0, 1,
			// position.x, position.y));
			// try {
			// transformPolicy.commit().execute(null, null);
			// } catch (ExecutionException e) {
			// throw new IllegalStateException(e);
			// }
		}
	}

}