/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.operations;

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.mvc.parts.IContentPart;

import com.google.common.collect.ImmutableList;

/**
 * The {@link AddContentChildOperation} uses the {@link IContentPart} API to
 * remove a content object from an {@link IContentPart}.
 *
 * @author mwienand
 *
 * @param <VR>
 *            The visual root node of the UI toolkit, e.g. javafx.scene.Node in
 *            case of JavaFX.
 */
public class AddContentChildOperation<VR> extends AbstractOperation
		implements ITransactionalOperation {

	private final IContentPart<VR, ? extends VR> parent;
	private final Object contentChild;
	private int index;

	// capture initial content children (for no-op test)
	private List<Object> initialContentChildren;

	/**
	 * Creates a new {@link AddContentChildOperation} for adding the given
	 * <i>contentChild</i> {@link Object} to the content children of the given
	 * <i>parent</i> {@link IContentPart}.
	 *
	 * @param parent
	 *            The {@link IContentPart} to which a content child is to be
	 *            added.
	 * @param contentChild
	 *            The content {@link Object} which is to be added to the content
	 *            children of the <i>parent</i>.
	 * @param index
	 *            The index of the <i>contentChild</i> within the <i>parent</i>
	 *            's list of content children.
	 */
	public AddContentChildOperation(IContentPart<VR, ? extends VR> parent,
			Object contentChild, int index) {
		super("Add Content Child");
		this.parent = parent;
		this.contentChild = contentChild;
		this.index = index;
		this.initialContentChildren = ImmutableList
				.copyOf(parent.getContentChildrenUnmodifiable());
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		// System.out.println("EXEC add content " + contentChild + " to " +
		// parent
		// + ".");
		if (parent.getContent() != null && !parent
				.getContentChildrenUnmodifiable().contains(contentChild)) {
			parent.addContentChild(contentChild, index);
		}
		// TODO: re-order in case the index does not match??
		return Status.OK_STATUS;
	}

	@Override
	public boolean isContentRelevant() {
		return true;
	}

	@Override
	public boolean isNoOp() {
		return initialContentChildren.contains(contentChild);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		// System.out.println("UNDO add content " + contentChild + " to " +
		// parent
		// + ".");
		if (parent.getContent() != null && parent
				.getContentChildrenUnmodifiable().contains(contentChild)) {
			parent.removeContentChild(contentChild);
		}
		return Status.OK_STATUS;
	}

}