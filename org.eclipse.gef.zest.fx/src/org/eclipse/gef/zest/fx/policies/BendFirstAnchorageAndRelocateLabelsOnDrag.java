/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.zest.fx.policies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.mvc.fx.policies.FXBendFirstAnchorageOnSegmentHandleDragPolicy;
import org.eclipse.gef.mvc.parts.PartUtils;
import org.eclipse.gef.zest.fx.ZestProperties;
import org.eclipse.gef.zest.fx.parts.AbstractLabelPart;
import org.eclipse.gef.zest.fx.parts.EdgeLabelPart;
import org.eclipse.gef.zest.fx.parts.EdgePart;

import javafx.scene.input.MouseEvent;

/**
 * An {@link FXBendFirstAnchorageOnSegmentHandleDragPolicy} that also takes care
 * of relocating related {@link EdgeLabelPart}s.
 *
 * @author anyssen
 *
 */
public class BendFirstAnchorageAndRelocateLabelsOnDrag extends FXBendFirstAnchorageOnSegmentHandleDragPolicy {

	@Override
	public void drag(MouseEvent e, Dimension delta) {
		super.drag(e, delta);
		for (AbstractLabelPart lp : getLabelParts()) {
			lp.getAdapter(TransformLabelPolicy.class).preserveLabelOffset();
		}
	}

	@Override
	public void dragAborted() {
		for (AbstractLabelPart lp : getLabelParts()) {
			rollback(lp.getAdapter(TransformLabelPolicy.class));
			restoreRefreshVisuals(lp);
		}
		super.dragAborted();
	}

	private List<AbstractLabelPart> getEdgeLabelParts(EdgePart edgePart) {
		List<AbstractLabelPart> linked = new ArrayList<>();
		linked.addAll(new ArrayList<>(PartUtils
				.filterParts(PartUtils.getAnchoreds(edgePart, ZestProperties.LABEL__NE), AbstractLabelPart.class)));
		linked.addAll(new ArrayList<>(PartUtils.filterParts(
				PartUtils.getAnchoreds(edgePart, ZestProperties.EXTERNAL_LABEL__NE), AbstractLabelPart.class)));
		linked.addAll(new ArrayList<>(PartUtils.filterParts(
				PartUtils.getAnchoreds(edgePart, ZestProperties.SOURCE_LABEL__E), AbstractLabelPart.class)));
		linked.addAll(new ArrayList<>(PartUtils.filterParts(
				PartUtils.getAnchoreds(edgePart, ZestProperties.TARGET_LABEL__E), AbstractLabelPart.class)));
		return linked;
	}

	private List<AbstractLabelPart> getLabelParts() {
		Set<AbstractLabelPart> labelParts = Collections
				.newSetFromMap(new IdentityHashMap<AbstractLabelPart, Boolean>());
		// ensure that linked parts are moved with us during dragging
		labelParts
				.addAll(getEdgeLabelParts((EdgePart) getHost().getAnchoragesUnmodifiable().keySet().iterator().next()));
		for (Iterator<AbstractLabelPart> iterator = labelParts.iterator(); iterator.hasNext();) {
			// filter out those that do not have a stored position
			if (iterator.next().getStoredLabelPosition() == null) {
				iterator.remove();
			}
		}
		return new ArrayList<>(labelParts);
	}

	@Override
	public void press(MouseEvent e) {
		super.press(e);
		// init label transform policies
		for (AbstractLabelPart lp : getLabelParts()) {
			storeAndDisableRefreshVisuals(lp);
			init(lp.getAdapter(TransformLabelPolicy.class));
		}
	}

	@Override
	public void release(MouseEvent e, Dimension delta) {
		for (AbstractLabelPart lp : getLabelParts()) {
			commit(lp.getAdapter(TransformLabelPolicy.class));
			restoreRefreshVisuals(lp);
		}
		super.release(e, delta);
	}

}
