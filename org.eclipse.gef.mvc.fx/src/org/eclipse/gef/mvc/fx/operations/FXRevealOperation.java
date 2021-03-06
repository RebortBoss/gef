/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.mvc.fx.viewer.FXViewer;
import org.eclipse.gef.mvc.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.parts.IVisualPart;
import org.eclipse.gef.mvc.viewer.IViewer;

import javafx.scene.Node;

/**
 * The {@link FXRevealOperation} can be used to reveal an {@link IVisualPart}
 * within its {@link IViewer}, i.e. manipulates the viewport translation so that
 * the part is visible.
 *
 * @author mwienand
 *
 */
public class FXRevealOperation extends AbstractOperation
		implements ITransactionalOperation {

	private double tx = 0d;
	private double ty = 0d;

	private FXViewer viewer;
	private IVisualPart<Node, ? extends Node> part = null;

	/**
	 * Constructs a new {@link FXRevealOperation} that will reveal the given
	 * {@link IVisualPart} upon execution.
	 *
	 * @param part
	 *            The {@link IVisualPart} that will be revealed upon execution
	 *            of this operation.
	 */
	public FXRevealOperation(IVisualPart<Node, ? extends Node> part) {
		super("Reveal");
		this.part = part;
		viewer = (FXViewer) part.getRoot().getViewer();
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		// store the viewport translation
		tx = viewer.getCanvas().getHorizontalScrollOffset();
		ty = viewer.getCanvas().getVerticalScrollOffset();
		viewer.reveal(part);
		return Status.OK_STATUS;
	}

	@Override
	public boolean isContentRelevant() {
		return false;
	}

	@Override
	public boolean isNoOp() {
		return false;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	/**
	 * Sets the part that is to be revealed.
	 *
	 * @param part
	 *            The part to be revealed.
	 */
	public void setPart(IVisualPart<Node, ? extends Node> part) {
		this.part = part;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		// restore the viewport translation
		viewer.getCanvas().setHorizontalScrollOffset(tx);
		viewer.getCanvas().setVerticalScrollOffset(ty);
		return Status.OK_STATUS;
	}

}
