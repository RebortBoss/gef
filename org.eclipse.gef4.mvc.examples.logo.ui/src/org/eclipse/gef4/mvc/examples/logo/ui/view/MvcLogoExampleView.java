/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.examples.logo.ui.view;

import java.util.Collections;
import java.util.List;

import org.eclipse.gef4.mvc.examples.logo.MvcLogoExampleModule;
import org.eclipse.gef4.mvc.examples.logo.model.FXGeometricModel;
import org.eclipse.gef4.mvc.examples.logo.ui.MvcLogoExampleUiModule;
import org.eclipse.gef4.mvc.fx.ui.parts.FXView;

import com.google.inject.Guice;
import com.google.inject.util.Modules;

public class MvcLogoExampleView extends FXView {

	// TODO: create FXView via an executable extension factory (obtaining the
	// injector via the bundle)
	public MvcLogoExampleView() {
		super(Guice.createInjector(Modules.override(new MvcLogoExampleModule())
				.with(new MvcLogoExampleUiModule())));
	}

	@Override
	protected List<Object> getContents() {
		return Collections.<Object> singletonList(new FXGeometricModel());
	}

}