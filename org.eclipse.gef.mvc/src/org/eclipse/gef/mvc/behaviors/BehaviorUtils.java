/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.behaviors;

import java.util.Collection;
import java.util.List;

import org.eclipse.gef.mvc.parts.IFeedbackPart;
import org.eclipse.gef.mvc.parts.IHandlePart;
import org.eclipse.gef.mvc.parts.IRootPart;
import org.eclipse.gef.mvc.parts.IVisualPart;

/**
 * The {@link BehaviorUtils} class provides utility methods for the
 * implementation of {@link IBehavior}s, such as the creation of
 * {@link IFeedbackPart}s and {@link IHandlePart}s, or the
 * establishment/unestablishment of anchor relations.
 */
// TODO: Transfer this into a utility class that can be injected (and thus
// replaced) in the parts/policies where its needed, providing non-static
// functions.
public class BehaviorUtils {

	/**
	 * Adds the given list of anchoreds as children to the given
	 * {@link IRootPart}. Additionally, all given anchorages will be attached to
	 * the given anchorages.
	 *
	 * @param root
	 *            The {@link IRootPart}, the anchored {@link IVisualPart}s are
	 *            to be added to as children
	 * @param anchorages
	 *            the {@link IVisualPart}s which are to be added to the given
	 *            anchoreds as anchorages.
	 * @param anchoreds
	 *            the {@link IVisualPart}s to which the given anchorages are to
	 *            be added.
	 * @param <VR>
	 *            The visual root node of the UI toolkit this
	 *            {@link IVisualPart} is used in, e.g. javafx.scene.Node in case
	 *            of JavaFX.
	 * @see #removeAnchoreds(IRootPart, Collection, List)
	 */
	public static <VR> void addAnchoreds(IRootPart<VR, ? extends VR> root,
			Collection<? extends IVisualPart<VR, ? extends VR>> anchorages,
			List<? extends IVisualPart<VR, ? extends VR>> anchoreds) {
		if (anchoreds != null && !anchoreds.isEmpty()) {
			root.addChildren(anchoreds);
			for (IVisualPart<VR, ? extends VR> anchored : anchoreds) {
				for (IVisualPart<VR, ? extends VR> anchorage : anchorages) {
					anchored.attachToAnchorage(anchorage);
				}
			}
		}
	}

	/**
	 * Detaches the given anchoreds from the given anchorages and removes them
	 * as children from the given {@link IRootPart}.
	 *
	 * @param root
	 *            The {@link IRootPart} from which the anchoreds are to be
	 *            removed as children.
	 * @param anchorages
	 *            The anchorages to be removed from the given anchoreds.
	 * @param anchoreds
	 *            The anchoreds from which to remove the given anchorages.
	 * @param <VR>
	 *            The visual root node of the UI toolkit this
	 *            {@link IVisualPart} is used in, e.g. javafx.scene.Node in case
	 *            of JavaFX.
	 * @see #addAnchoreds(IRootPart, Collection, List)
	 */
	public static <VR> void removeAnchoreds(IRootPart<VR, ? extends VR> root,
			Collection<? extends IVisualPart<VR, ? extends VR>> anchorages,
			List<? extends IVisualPart<VR, ? extends VR>> anchoreds) {
		if (anchoreds != null && !anchoreds.isEmpty()) {
			for (IVisualPart<VR, ? extends VR> anchored : anchoreds) {
				for (IVisualPart<VR, ? extends VR> anchorage : anchorages) {
					anchored.detachFromAnchorage(anchorage);
				}
			}
			root.removeChildren(anchoreds);
		}
	}

}
