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
package org.eclipse.gef.mvc.policies;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.geometry.euclidean.Angle;
import org.eclipse.gef.geometry.planar.AffineTransform;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.mvc.models.GridModel;
import org.eclipse.gef.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef.mvc.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.operations.TransformContentOperation;
import org.eclipse.gef.mvc.parts.ITransformableContentPart;
import org.eclipse.gef.mvc.viewer.IViewer;

/**
 * The {@link AbstractTransformPolicy} is a {@link AbstractTransactionPolicy}
 * that handles the transformation of its {@link #getHost() host}.
 * <p>
 * When working with transformations, the order in which the individual
 * transformations are concatenated is important. The transformation that is
 * concatenated last will be applied first. For example, the rotation around a
 * pivot point consists of 3 steps:
 * <ol>
 * <li>Translate the coordinate system, so that the pivot point is in the origin
 * <code>(-px, -py)</code>.
 * <li>Rotate the coordinate system.
 * <li>Translate back to the original position <code>(px, py)</code>.
 * </ol>
 * But the corresponding transformations have to be concatenated in reverse
 * order, i.e. translate back first, rotate then, translate pivot to origin
 * last. This is easy to confuse, that's why this policy manages a list of
 * pre-transforms and a list of post-transforms. These transformations (as well
 * as the initial node transformation) are concatenated as follows to yield the
 * new node transformation for the host:
 *
 * <pre>
 *            --&gt; --&gt; --&gt;  direction of concatenation --&gt; --&gt; --&gt;
 *
 *            postTransforms  initialTransform  preTransforms
 *            |------------|                        |-----------|
 * postIndex: n, n-1, ...  0              preIndex: 0, 1,  ...  m
 *
 *            &lt;-- &lt;-- &lt;-- &lt;-- direction of effect &lt;-- &lt;-- &lt;-- &lt;--
 * </pre>
 * <p>
 * As you can see, the last pre-transform is concatenated last, and therefore,
 * will affect the host first. Generally, a post-transform manipulates the
 * transformed node, while a pre-transform manipulates the coordinate system
 * before the node is transformed.
 * <p>
 * You can use the {@link #createPreTransform()} and
 * {@link #createPostTransform()} methods to create a pre- or a post-transform
 * and append it to the respective list. Therefore, the most recently created
 * pre-transform will be applied first, and the most recently created
 * post-transform will be applied last. When creating a pre- or post-transform,
 * the index of that transform within the respective list will be returned. This
 * index can later be used to manipulate the transform.
 * <p>
 * The {@link #setPostRotate(int, Angle)},
 * {@link #setPostScale(int, double, double)},
 * {@link #setPostTransform(int, AffineTransform)},
 * {@link #setPostTranslate(int, double, double)},
 * {@link #setPreRotate(int, Angle)}, {@link #setPreScale(int, double, double)},
 * {@link #setPreTransform(int, AffineTransform)}, and
 * {@link #setPreTranslate(int, double, double)} methods can be used to change a
 * previously created pre- or post-transform.
 * <p>
 * Subclasses need to override the following methods to implement
 * transformations for their host:
 * <ul>
 * <li>{@link #createOperation()} - Creates an {@link ITransactionalOperation}
 * that can be used to change the host's transformation.
 * <li>{@link #getCurrentTransform()} - Extracts the host's transformation and
 * converts it to an {@link AffineTransform}.
 * <li>{@link #updateTransformOperation(AffineTransform)} - Updates the
 * operation that was created within {@link #createOperation()} so that the
 * host's transformation is changed to match the given {@link AffineTransform}.
 * </ul>
 *
 * @author mwienand
 * @param <VR>
 *            The visual root node of the UI toolkit used, e.g.
 *            javafx.scene.Node in case of JavaFX.
 *
 */
public abstract class AbstractTransformPolicy<VR>
		extends AbstractTransactionPolicy<VR> {

	/**
	 * Computes the offset which needs to be added to the given local
	 * coordinates in order to stay on the grid/snap to the grid.
	 *
	 * @param gridModel
	 *            The {@link GridModel} of the host's {@link IViewer}.
	 * @param localX
	 *            The x-coordinate in host coordinates.
	 * @param localY
	 *            The y-coordinate in host coordinates.
	 * @param gridCellWidthFraction
	 *            The granularity of the horizontal grid steps.
	 * @param gridCellHeightFraction
	 *            The granularity of the vertical grid steps.
	 * @return A {@link Dimension} representing the offset that needs to be
	 *         added to the local coordinates so that they snap to the grid.
	 */
	// TODO: move to utils
	public static Dimension getSnapToGridOffset(GridModel gridModel,
			final double localX, final double localY,
			final double gridCellWidthFraction,
			final double gridCellHeightFraction) {
		// TODO: pass in scene coordinates so that the snap can be computed
		// correctly even though transformations are used
		double snapOffsetX = 0, snapOffsetY = 0;
		if ((gridModel != null) && gridModel.isSnapToGrid()) {
			// determine snap width
			final double snapWidth = gridModel.getGridCellWidth()
					* gridCellWidthFraction;
			final double snapHeight = gridModel.getGridCellHeight()
					* gridCellHeightFraction;

			snapOffsetX = localX % snapWidth;
			if (snapOffsetX > (snapWidth / 2)) {
				snapOffsetX = snapWidth - snapOffsetX;
				snapOffsetX *= -1;
			}

			snapOffsetY = localY % snapHeight;
			if (snapOffsetY > (snapHeight / 2)) {
				snapOffsetY = snapHeight - snapOffsetY;
				snapOffsetY *= -1;
			}
		}
		return new Dimension(snapOffsetX, snapOffsetY);
	}

	/**
	 * The initial node transformation of the manipulated part.
	 */
	private AffineTransform initialTransform;

	/**
	 * The {@link List} of transformations that are applied before the old
	 * transformation.
	 */
	private List<AffineTransform> preTransforms = new ArrayList<>();

	/**
	 * The {@link List} of transformations that are applied after the old
	 * transformation.
	 */
	private List<AffineTransform> postTransforms = new ArrayList<>();

	/**
	 * Applies the given {@link AffineTransform} as the new transformation
	 * matrix to the {@link #getHost() host}. All transformation changes are
	 * applied via this method. Therefore, subclasses can override this method
	 * to perform adjustments that are necessary for its {@link #getHost() host}
	 * .
	 *
	 * @param finalTransform
	 *            The new transformation matrix for the {@link #getHost() host}.
	 */
	protected void applyTransform(AffineTransform finalTransform) {
		updateTransformOperation(finalTransform);
		// locally execute operation
		locallyExecuteOperation();
	}

	@Override
	public ITransactionalOperation commit() {
		ITransactionalOperation commitOperation = super.commit();
		if (commitOperation != null && !commitOperation.isNoOp()
				&& isContentTransformable()) {
			// chain content changes
			ForwardUndoCompositeOperation composite = new ForwardUndoCompositeOperation(
					"Transform Content");
			composite.add(commitOperation);
			// compute delta between new and initial transform and apply it
			composite.add(createTransformContentOperation());
			commitOperation = composite;
		}

		preTransforms.clear();
		postTransforms.clear();
		initialTransform = null;

		return commitOperation;
	}

	/**
	 * Creates a new {@link AffineTransform} and appends it to the
	 * postTransforms list. Therefore, the new {@link AffineTransform} will
	 * affect the host after all other transforms, as shown below:
	 *
	 * <pre>
	 *            --&gt; --&gt; --&gt;  direction of concatenation --&gt; --&gt; --&gt;
	 *
	 *            postTransforms  initialTransform  preTransforms
	 *            |------------|                        |-----------|
	 * postIndex: n, n-1, ...  0              preIndex: 0, 1,  ...  m
	 *
	 *            &lt;-- &lt;-- &lt;-- &lt;-- direction of effect &lt;-- &lt;-- &lt;-- &lt;--
	 * </pre>
	 *
	 * A post-transform manipulates the transformed node, while a pre-transform
	 * manipulates the coordinate system before the node is transformed.
	 *
	 * @return A new {@link AffineTransform} that is appended to the
	 *         postTransforms list.
	 */
	public int createPostTransform() {
		checkInitialized();
		postTransforms.add(new AffineTransform());
		return postTransforms.size() - 1;
	}

	/**
	 * Creates a new {@link AffineTransform} and appends it to the preTransforms
	 * list. Therefore, the new {@link AffineTransform} will affect the host
	 * before all other transforms, as shown below:
	 *
	 * <pre>
	 *            --&gt; --&gt; --&gt;  direction of concatenation --&gt; --&gt; --&gt;
	 *
	 *            postTransforms  initialTransform  preTransforms
	 *            |------------|                        |-----------|
	 * postIndex: n, n-1, ...  0              preIndex: 0, 1,  ...  m
	 *
	 *            &lt;-- &lt;-- &lt;-- &lt;-- direction of effect &lt;-- &lt;-- &lt;-- &lt;--
	 * </pre>
	 *
	 * A post-transform manipulates the transformed node, while a pre-transform
	 * manipulates the coordinate system before the node is transformed.
	 *
	 * @return A new {@link AffineTransform} that is appended to the
	 *         preTransforms list.
	 */
	public int createPreTransform() {
		checkInitialized();
		preTransforms.add(new AffineTransform());
		return preTransforms.size() - 1;
	}

	/**
	 * Returns an operation to transform the content.
	 *
	 * @return The ITransactionalOperation to transform the content.
	 */
	protected ITransactionalOperation createTransformContentOperation() {
		AffineTransform delta = getInitialTransform().getInverse()
				.preConcatenate(getCurrentTransform());
		ITransactionalOperation transformContentOperation = new TransformContentOperation<>(
				(ITransformableContentPart<VR, ? extends VR>) getHost(), delta);
		return transformContentOperation;
	}

	/**
	 * Returns the {@link AffineTransform} that matches the node transformation
	 * of the {@link #getHost() host}.
	 *
	 * @return The host's {@link AffineTransform}.
	 */
	public abstract AffineTransform getCurrentTransform();

	/**
	 * Returns a copy of the initial node transformation of the host (obtained
	 * via {@link #getCurrentTransform()}).
	 *
	 * @return A copy of the initial node transformation of the host (obtained
	 *         via {@link #getCurrentTransform()}).
	 */
	public AffineTransform getInitialTransform() {
		return initialTransform;
	}

	@Override
	public void init() {
		preTransforms.clear();
		postTransforms.clear();
		initialTransform = getCurrentTransform();
		super.init();
	}

	/**
	 * Returns whether the content can be transformed.
	 *
	 * @return <code>true</code> if the content can be transformed,
	 *         <code>false</code> otherwise.
	 */
	protected boolean isContentTransformable() {
		return getHost() instanceof ITransformableContentPart;
	}

	/**
	 * Sets the specified post-transform to a rotation by the given angle.
	 *
	 * @param index
	 *            The index of the post-transform to manipulate.
	 * @param rotation
	 *            The counter clock-wise rotation {@link Angle}.
	 */
	public void setPostRotate(int index, Angle rotation) {
		checkInitialized();
		postTransforms.get(index).setToRotation(rotation.rad());
		updateTransform();
	}

	/**
	 * Sets the specified post-transform to a scaling by the given factors.
	 *
	 * @param index
	 *            The index of the post-transform to manipulate.
	 * @param sx
	 *            The horizontal scale factor.
	 * @param sy
	 *            The vertical scale factor.
	 */
	public void setPostScale(int index, double sx, double sy) {
		checkInitialized();
		postTransforms.get(index).setToScale(sx, sy);
		updateTransform();
	}

	/**
	 * Sets the specified post-transform to the given {@link AffineTransform}.
	 *
	 * @param postTransformIndex
	 *            The index of the post-transform to manipulate.
	 * @param transform
	 *            The {@link AffineTransform} that replaces the specified
	 *            post-transform.
	 */
	public void setPostTransform(int postTransformIndex,
			AffineTransform transform) {
		checkInitialized();
		postTransforms.get(postTransformIndex).setTransform(transform);
		updateTransform();
	}

	/**
	 * Sets the specified post-transform to a translation by the given offsets.
	 *
	 * @param index
	 *            The index of the post-transform to manipulate.
	 * @param tx
	 *            The horizontal translation offset (in local coordinates).
	 * @param ty
	 *            The vertical translation offset (in local coordinates).
	 */
	public void setPostTranslate(int index, double tx, double ty) {
		checkInitialized();
		// TODO: snap to grid
		postTransforms.get(index).setToTranslation(tx, ty);
		updateTransform();
	}

	/**
	 * Sets the specified pre-transform to a rotation by the given angle.
	 *
	 * @param index
	 *            The index of the pre-transform to manipulate.
	 * @param rotation
	 *            The counter clock-wise rotation {@link Angle}.
	 */
	public void setPreRotate(int index, Angle rotation) {
		checkInitialized();
		preTransforms.get(index).setToRotation(rotation.rad());
		updateTransform();
	}

	/**
	 * Sets the specified pre-transform to a scaling by the given factors.
	 *
	 * @param index
	 *            The index of the pre-transform to manipulate.
	 * @param sx
	 *            The horizontal scale factor.
	 * @param sy
	 *            The vertical scale factor.
	 */
	public void setPreScale(int index, double sx, double sy) {
		checkInitialized();
		preTransforms.get(index).setToScale(sx, sy);
		updateTransform();
	}

	/**
	 * Sets the specified pre-transform to the given {@link AffineTransform}.
	 *
	 * @param preTransformIndex
	 *            The index of the pre-transform to manipulate.
	 * @param transform
	 *            The {@link AffineTransform} that replaces the specified
	 *            pre-transform.
	 */
	public void setPreTransform(int preTransformIndex,
			AffineTransform transform) {
		checkInitialized();
		preTransforms.get(preTransformIndex).setTransform(transform);
		updateTransform();
	}

	/**
	 * Sets the specified pre-transform to a translation by the given offsets.
	 *
	 * @param index
	 *            The index of the pre-transform to manipulate.
	 * @param tx
	 *            The horizontal translation offset (in parent coordinates).
	 * @param ty
	 *            The vertical translation offset (in parent coordinates).
	 */
	public void setPreTranslate(int index, double tx, double ty) {
		checkInitialized();
		// TODO: snap to grid
		preTransforms.get(index).setToTranslation(tx, ty);
		updateTransform();
	}

	/**
	 * Changes the {@link #getHost() host's} transformation to the given
	 * {@link AffineTransform}. Clears the pre- and post-transforms lists.
	 *
	 * @param finalTransform
	 *            The new {@link AffineTransform} for the {@link #getHost()
	 *            host}.
	 */
	public void setTransform(AffineTransform finalTransform) {
		checkInitialized();
		// clear pre- and post-transforms lists
		preTransforms.clear();
		postTransforms.clear();
		// apply new transform to host (and update the operation)
		applyTransform(finalTransform);
	}

	/**
	 * Composes the pre- and post-transforms lists and the initial node
	 * transform to one composite transformation. This composite transformation
	 * is then applied to the host using
	 * {@link #applyTransform(AffineTransform)}.
	 *
	 * <pre>
	 *            --&gt; --&gt; --&gt;  direction of concatenation --&gt; --&gt; --&gt;
	 *
	 *            postTransforms  initialTransform  preTransforms
	 *            |------------|                        |-----------|
	 * postIndex: n, n-1, ...  0              preIndex: 0, 1,  ...  m
	 *
	 *            &lt;-- &lt;-- &lt;-- &lt;-- direction of effect &lt;-- &lt;-- &lt;-- &lt;--
	 * </pre>
	 */
	protected void updateTransform() {
		// compose transformations to one composite transformation
		AffineTransform composite = new AffineTransform();
		// concatenate pre transforms (in reverse order as the last pre
		// transform should be applied first)
		for (int i = postTransforms.size() - 1; i >= 0; i--) {
			composite.concatenate(postTransforms.get(i));
		}
		// concatenate old transform
		composite.concatenate(initialTransform);
		// concatenate post transforms
		for (AffineTransform pre : preTransforms) {
			composite.concatenate(pre);
		}
		// apply composite transform to host
		applyTransform(composite);
	}

	/**
	 * Updates the operation that was created within {@link #createOperation()}
	 * so that it will set the {@link #getHost() host's} transformation to match
	 * the given {@link AffineTransform} upon execution.
	 *
	 * @param finalTransform
	 *            The new transformation for the host.
	 */
	protected abstract void updateTransformOperation(
			AffineTransform finalTransform);

}
