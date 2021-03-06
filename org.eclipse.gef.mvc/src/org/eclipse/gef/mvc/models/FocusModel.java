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
package org.eclipse.gef.mvc.models;

import java.beans.PropertyChangeEvent;

import org.eclipse.gef.common.dispose.IDisposable;
import org.eclipse.gef.mvc.parts.IContentPart;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * The {@link FocusModel} stores the {@link IContentPart} which has keyboard
 * focus. Note that you are responsible for synchronizing keyboard focus with
 * the model.
 *
 * @author mwienand
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit used, e.g.
 *            javafx.scene.Node in case of JavaFX.
 *
 */
public class FocusModel<VR> implements IDisposable {

	/**
	 * The {@link FocusModel} fires {@link PropertyChangeEvent}s when the
	 * focused part changes. This is the name of the property that is delivered
	 * with the event.
	 *
	 * @see #setFocus(IContentPart)
	 */
	final public static String FOCUS_PROPERTY = "focus";

	private ObjectProperty<IContentPart<VR, ? extends VR>> focusedProperty = new SimpleObjectProperty<>(
			this, FOCUS_PROPERTY);

	/**
	 * Constructs a new {@link FocusModel}. The {@link #getFocus() focused}
	 * {@link IContentPart} is set to <code>null</code>.
	 */
	public FocusModel() {
	}

	/**
	 * @since 1.1
	 */
	@Override
	public void dispose() {
		focusedProperty.set(null);
	}

	/**
	 * Returns an object property providing the currently focused
	 * {@link IContentPart}.
	 *
	 * @return An object property named {@link #FOCUS_PROPERTY}.
	 */
	public ObjectProperty<IContentPart<VR, ? extends VR>> focusProperty() {
		return focusedProperty;
	}

	/**
	 * Returns the {@link IContentPart} which has keyboard focus, or
	 * <code>null</code> if no {@link IContentPart} currently has keyboard
	 * focus.
	 *
	 * @return the IContentPart which has keyboard focus, or <code>null</code>
	 */
	public IContentPart<VR, ? extends VR> getFocus() {
		return focusedProperty.get();
	}

	/**
	 * Selects the given IContentPart as the focus part. Note that setting the
	 * focus part does not assign keyboard focus to the part.
	 *
	 * @param focusPart
	 *            The {@link IContentPart} which should become the new focus
	 *            part.
	 */
	public void setFocus(IContentPart<VR, ? extends VR> focusPart) {
		focusedProperty.set(focusPart);
	}

}
