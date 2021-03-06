/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef.zest.fx;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.common.adapt.inject.AdaptableScopes;
import org.eclipse.gef.common.adapt.inject.AdapterMap;
import org.eclipse.gef.common.adapt.inject.AdapterMaps;
import org.eclipse.gef.layout.LayoutContext;
import org.eclipse.gef.mvc.behaviors.HoverBehavior;
import org.eclipse.gef.mvc.behaviors.SelectionBehavior;
import org.eclipse.gef.mvc.fx.MvcFxModule;
import org.eclipse.gef.mvc.fx.behaviors.FXConnectionClickableAreaBehavior;
import org.eclipse.gef.mvc.fx.domain.FXDomain;
import org.eclipse.gef.mvc.fx.parts.FXCircleSegmentHandlePart;
import org.eclipse.gef.mvc.fx.parts.FXDefaultFocusFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.FXDefaultHoverFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.FXDefaultHoverHandlePartFactory;
import org.eclipse.gef.mvc.fx.parts.FXDefaultSelectionFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.FXDefaultSelectionHandlePartFactory;
import org.eclipse.gef.mvc.fx.parts.FXSquareSegmentHandlePart;
import org.eclipse.gef.mvc.fx.policies.FXBendConnectionPolicy;
import org.eclipse.gef.mvc.fx.policies.FXFocusAndSelectOnClickPolicy;
import org.eclipse.gef.mvc.fx.policies.FXHoverOnHoverPolicy;
import org.eclipse.gef.mvc.fx.policies.FXNormalizeConnectedOnDragPolicy;
import org.eclipse.gef.mvc.fx.policies.FXResizePolicy;
import org.eclipse.gef.mvc.fx.policies.FXResizeTranslateFirstAnchorageOnHandleDragPolicy;
import org.eclipse.gef.mvc.fx.policies.FXRotateSelectedOnHandleDragPolicy;
import org.eclipse.gef.mvc.fx.policies.FXSelectFocusedOnTypePolicy;
import org.eclipse.gef.mvc.fx.policies.FXTransformConnectionPolicy;
import org.eclipse.gef.mvc.fx.policies.FXTransformPolicy;
import org.eclipse.gef.mvc.fx.policies.FXTranslateSelectedOnDragPolicy;
import org.eclipse.gef.mvc.fx.policies.FXTraverseFocusOnTypePolicy;
import org.eclipse.gef.mvc.fx.providers.GeometricOutlineProvider;
import org.eclipse.gef.mvc.fx.providers.ShapeBoundsProvider;
import org.eclipse.gef.mvc.fx.viewer.FXViewer;
import org.eclipse.gef.mvc.parts.IContentPartFactory;
import org.eclipse.gef.mvc.viewer.AbstractViewer;
import org.eclipse.gef.zest.fx.behaviors.EdgeHidingBehavior;
import org.eclipse.gef.zest.fx.behaviors.EdgeLabelHidingBehavior;
import org.eclipse.gef.zest.fx.behaviors.EdgeLayoutBehavior;
import org.eclipse.gef.zest.fx.behaviors.GraphLayoutBehavior;
import org.eclipse.gef.zest.fx.behaviors.NodeHidingBehavior;
import org.eclipse.gef.zest.fx.behaviors.NodeLayoutBehavior;
import org.eclipse.gef.zest.fx.models.HidingModel;
import org.eclipse.gef.zest.fx.models.NavigationModel;
import org.eclipse.gef.zest.fx.parts.EdgeLabelPart;
import org.eclipse.gef.zest.fx.parts.EdgePart;
import org.eclipse.gef.zest.fx.parts.GraphPart;
import org.eclipse.gef.zest.fx.parts.HideHoverHandlePart;
import org.eclipse.gef.zest.fx.parts.NodeLabelPart;
import org.eclipse.gef.zest.fx.parts.NodePart;
import org.eclipse.gef.zest.fx.parts.ShowHiddenNeighborsHoverHandlePart;
import org.eclipse.gef.zest.fx.parts.ZestFxContentPartFactory;
import org.eclipse.gef.zest.fx.parts.ZestFxHoverHandlePartFactory;
import org.eclipse.gef.zest.fx.parts.ZestFxRootPart;
import org.eclipse.gef.zest.fx.parts.ZestFxSelectionHandlePartFactory;
import org.eclipse.gef.zest.fx.policies.BendFirstAnchorageAndRelocateLabelsOnDrag;
import org.eclipse.gef.zest.fx.policies.HideFirstAnchorageOnClickPolicy;
import org.eclipse.gef.zest.fx.policies.HideOnTypePolicy;
import org.eclipse.gef.zest.fx.policies.HidePolicy;
import org.eclipse.gef.zest.fx.policies.OpenNestedGraphOnDoubleClickPolicy;
import org.eclipse.gef.zest.fx.policies.OpenParentGraphOnDoubleClickPolicy;
import org.eclipse.gef.zest.fx.policies.SemanticZoomPolicy;
import org.eclipse.gef.zest.fx.policies.ShowHiddenNeighborsOfFirstAnchorageOnClickPolicy;
import org.eclipse.gef.zest.fx.policies.ShowHiddenNeighborsOnTypePolicy;
import org.eclipse.gef.zest.fx.policies.ShowHiddenNeighborsPolicy;
import org.eclipse.gef.zest.fx.policies.TransformLabelPolicy;
import org.eclipse.gef.zest.fx.policies.TranslateSelectedAndRelocateLabelsOnDragPolicy;
import org.eclipse.gef.zest.fx.providers.NodePartAnchorProvider;

import com.google.inject.Binder;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;

import javafx.scene.Node;

/**
 * The {@link ZestFxModule} defines Zest.FX specific bindings additional to the
 * bindings defined within {@link MvcFxModule}.
 *
 * @author mwienand
 *
 */
public class ZestFxModule extends MvcFxModule {

	@Override
	protected void bindAbstractContentPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindAbstractContentPartAdapters(adapterMapBinder);
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXFocusAndSelectOnClickPolicy.class);
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXSelectFocusedOnTypePolicy.class);
		adapterMapBinder.addBinding(AdapterKey.role(FXDefaultHoverHandlePartFactory.HOVER_HANDLES_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);
	}

	@Override
	protected void bindContentViewerAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindContentViewerAdapters(adapterMapBinder);
		bindNavigationModelAsContentViewerAdapter(adapterMapBinder);
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(HidingModel.class);
	}

	@Override
	protected void bindContentViewerRootPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindContentViewerRootPartAdapters(adapterMapBinder);

		adapterMapBinder.addBinding(AdapterKey.role("open-parent-graph")).to(OpenParentGraphOnDoubleClickPolicy.class);

		// keyboard focus traversal
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXTraverseFocusOnTypePolicy.class);

		// select focused on type
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXSelectFocusedOnTypePolicy.class);
	}

	/**
	 * Adds (default) {@link AdapterMap} bindings for {@link EdgeLabelPart} and
	 * all sub-classes. May be overwritten by sub-classes to change the default
	 * bindings.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link EdgeLabelPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindEdgeLabelPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// selection link feedback provider
		adapterMapBinder
				.addBinding(AdapterKey
						.role(FXDefaultSelectionFeedbackPartFactory.SELECTION_LINK_FEEDBACK_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);

		// selection feedback provider
		adapterMapBinder
				.addBinding(AdapterKey.role(FXDefaultSelectionFeedbackPartFactory.SELECTION_FEEDBACK_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);

		// hover feedback provider
		adapterMapBinder.addBinding(AdapterKey.role(FXDefaultHoverFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);

		// transform policy
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(TransformLabelPolicy.class);

		// hiding behavior
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(EdgeLabelHidingBehavior.class);

		// hover on-hover policy
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXHoverOnHoverPolicy.class);

		// translate on drag
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXTranslateSelectedOnDragPolicy.class);
	}

	/**
	 * Adds (default) {@link AdapterMap} bindings for {@link EdgePart} and all
	 * sub-classes. May be overwritten by sub-classes to change the default
	 * bindings.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link EdgePart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindEdgePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// layout behavior
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(EdgeLayoutBehavior.class);
		// hiding behavior
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(EdgeHidingBehavior.class);

		// selection link feedback provider
		adapterMapBinder
				.addBinding(AdapterKey
						.role(FXDefaultSelectionFeedbackPartFactory.SELECTION_LINK_FEEDBACK_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);

		// selection feedback provider
		adapterMapBinder
				.addBinding(AdapterKey.role(FXDefaultSelectionFeedbackPartFactory.SELECTION_FEEDBACK_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);

		// selection handles
		adapterMapBinder
				.addBinding(AdapterKey.role(FXDefaultSelectionHandlePartFactory.SELECTION_HANDLES_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);

		// hover feedback provider
		adapterMapBinder.addBinding(AdapterKey.role(FXDefaultHoverFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);

		// geometry provider for focus feedback
		adapterMapBinder.addBinding(AdapterKey.role(FXDefaultFocusFeedbackPartFactory.FOCUS_FEEDBACK_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);

		// clickable area behavior
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXConnectionClickableAreaBehavior.class);

		// transform policy
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXTransformConnectionPolicy.class);

		// translate selected on-drag policy
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(TranslateSelectedAndRelocateLabelsOnDragPolicy.class);

		// hover on-hover policy
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXHoverOnHoverPolicy.class);

		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXBendConnectionPolicy.class);
	}

	@Override
	protected void bindFXChangeViewportPolicyAsFXRootPartAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// overwrite default zoom policy to perform semantic zooming (navigating
		// nested graphs on zoom level changes)
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(SemanticZoomPolicy.class);
	}

	/**
	 * Bind bend-on-drag policy to {@link FXCircleSegmentHandlePart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link FXCircleSegmentHandlePart} as a key.
	 */
	protected void bindFXCircleSegmentHandlePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		AdapterMaps.getAdapterMapBinder(binder(), FXCircleSegmentHandlePart.class).addBinding(AdapterKey.defaultRole())
				.to(BendFirstAnchorageAndRelocateLabelsOnDrag.class);
	}

	@Override
	protected void bindFXRootPartAsContentViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role(FXDomain.CONTENT_VIEWER_ROLE)).to(ZestFxRootPart.class)
				.in(AdaptableScopes.typed(FXViewer.class));
	}

	/**
	 * Bind resize and rotate behavior to {@link FXSquareSegmentHandlePart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link FXSquareSegmentHandlePart} as a key.
	 */
	protected void bindFXSquareSegmentHandlePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role("resize-relocate-first-anchorage"))
				.to(FXResizeTranslateFirstAnchorageOnHandleDragPolicy.class);
		adapterMapBinder.addBinding(AdapterKey.role("rotate")).to(FXRotateSelectedOnHandleDragPolicy.class);
	}

	/**
	 * Adds (default) {@link AdapterMap} bindings for {@link GraphPart} and all
	 * sub-classes. May be overwritten by sub-classes to change the default
	 * bindings.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link GraphPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindGraphPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(LayoutContext.class);
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(GraphLayoutBehavior.class);
	}

	/**
	 * Adds (default) {@link AdapterMap} bindings for
	 * {@link HideHoverHandlePart} and all sub-classes. May be overwritten by
	 * sub-classes to change the default bindings.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link HideHoverHandlePart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindHidingHoverHandlePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role("hide")).to(HideFirstAnchorageOnClickPolicy.class);
	}

	@Override
	protected void bindHoverHandlePartFactoryAsContentViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role(HoverBehavior.HOVER_HANDLE_PART_FACTORY))
				.to(ZestFxHoverHandlePartFactory.class);
	}

	/**
	 * Binds {@link IContentPartFactory} to {@link ZestFxContentPartFactory}.
	 */
	protected void bindIContentPartFactory() {
		binder().bind(new TypeLiteral<IContentPartFactory<Node>>() {
		}).to(ZestFxContentPartFactory.class).in(AdaptableScopes.typed(FXViewer.class));
	}

	/**
	 * Adds a binding for {@link NavigationModel} to the given adapter map
	 * binder that will insert the bindings into {@link AbstractViewer}s.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the binding is added.
	 */
	protected void bindNavigationModelAsContentViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(NavigationModel.class);
	}

	/**
	 * Adds (default) {@link AdapterMap} bindings for {@link NodeLabelPart} and
	 * all sub-classes. May be overwritten by sub-classes to change the default
	 * bindings.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link NodeLabelPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindNodeLabelPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// selection link feedback provider
		adapterMapBinder
				.addBinding(AdapterKey
						.role(FXDefaultSelectionFeedbackPartFactory.SELECTION_LINK_FEEDBACK_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);

		// selection feedback provider
		adapterMapBinder
				.addBinding(AdapterKey.role(FXDefaultSelectionFeedbackPartFactory.SELECTION_FEEDBACK_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);

		// hover feedback provider
		adapterMapBinder.addBinding(AdapterKey.role(FXDefaultHoverFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);

		// transform policy
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(TransformLabelPolicy.class);

		// hover on-hover policy
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXHoverOnHoverPolicy.class);

		// translate on drag
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXTranslateSelectedOnDragPolicy.class);

	}

	/**
	 * Adds (default) {@link AdapterMap} bindings for {@link NodePart} and all
	 * sub-classes. May be overwritten by sub-classes to change the default
	 * bindings.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link NodePart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindNodePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// layout
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(NodeLayoutBehavior.class);
		// pruning
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(HidePolicy.class);
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(ShowHiddenNeighborsPolicy.class);
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(NodeHidingBehavior.class);

		// translate on-drag
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(TranslateSelectedAndRelocateLabelsOnDragPolicy.class);

		// show hidden neighbors on-type
		adapterMapBinder.addBinding(AdapterKey.role("show-hidden-neighbors")).to(ShowHiddenNeighborsOnTypePolicy.class);

		// hide on-type
		adapterMapBinder.addBinding(AdapterKey.role("hide")).to(HideOnTypePolicy.class);

		adapterMapBinder.addBinding(AdapterKey.role("open-nested-graph")).to(OpenNestedGraphOnDoubleClickPolicy.class);

		// transform policy for relocation
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXTransformPolicy.class);

		// resize policy to resize nesting nodes
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXResizePolicy.class);

		// anchor provider
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(NodePartAnchorProvider.class);

		// feedback and handles
		adapterMapBinder
				.addBinding(AdapterKey.role(FXDefaultSelectionHandlePartFactory.SELECTION_HANDLES_GEOMETRY_PROVIDER))
				.toProvider(new Provider<ShapeBoundsProvider>() {
					@Override
					public ShapeBoundsProvider get() {
						return new ShapeBoundsProvider(1);
					}
				});

		// selection feedback provider
		adapterMapBinder
				.addBinding(AdapterKey.role(FXDefaultSelectionFeedbackPartFactory.SELECTION_FEEDBACK_GEOMETRY_PROVIDER))
				.toProvider(new Provider<ShapeBoundsProvider>() {
					@Override
					public ShapeBoundsProvider get() {
						return new ShapeBoundsProvider(0.5);
					}
				});

		// selection link feedback provider
		adapterMapBinder
				.addBinding(AdapterKey
						.role(FXDefaultSelectionFeedbackPartFactory.SELECTION_LINK_FEEDBACK_GEOMETRY_PROVIDER))
				.toProvider(new Provider<ShapeBoundsProvider>() {
					@Override
					public ShapeBoundsProvider get() {
						return new ShapeBoundsProvider(0.5);
					}
				});

		// hover feedback provider
		adapterMapBinder.addBinding(AdapterKey.role(FXDefaultHoverFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);

		// focus feedback provider
		adapterMapBinder.addBinding(AdapterKey.role(FXDefaultFocusFeedbackPartFactory.FOCUS_FEEDBACK_GEOMETRY_PROVIDER))
				.toProvider(new Provider<ShapeBoundsProvider>() {
					@Override
					public ShapeBoundsProvider get() {
						return new ShapeBoundsProvider(0.5);
					}
				});

		// hover on-hover
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXHoverOnHoverPolicy.class);

		// normalize on drag
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXNormalizeConnectedOnDragPolicy.class);
	}

	@Override
	protected void bindSelectionHandlePartFactoryAsContentViewerAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role(SelectionBehavior.SELECTION_HANDLE_PART_FACTORY))
				.to(ZestFxSelectionHandlePartFactory.class);
	}

	/**
	 * Adds (default) {@link AdapterMap} bindings for
	 * {@link ShowHiddenNeighborsHoverHandlePart} and all sub-classes. May be
	 * overwritten by sub-classes to change the default bindings.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link ShowHiddenNeighborsHoverHandlePart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindShowHiddenNeighborsHoverHandlePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		bindShowHiddenNeighborsOfFirstAnchorageOnClickPolicyAsShowHiddenNeighborsHoverHandlePartAdapter(
				adapterMapBinder);
	}

	/**
	 * Adds a binding for
	 * {@link ShowHiddenNeighborsOfFirstAnchorageOnClickPolicy} to the given
	 * adapter map binder that will insert the bindings into
	 * {@link ShowHiddenNeighborsHoverHandlePart}s.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the binding is added.
	 */
	protected void bindShowHiddenNeighborsOfFirstAnchorageOnClickPolicyAsShowHiddenNeighborsHoverHandlePartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role("showHiddenNeighbors"))
				.to(ShowHiddenNeighborsOfFirstAnchorageOnClickPolicy.class);
	}

	@Override
	protected void configure() {
		super.configure();

		bindIContentPartFactory();

		bindGraphPartAdapters(AdapterMaps.getAdapterMapBinder(binder(), GraphPart.class));
		bindNodePartAdapters(AdapterMaps.getAdapterMapBinder(binder(), NodePart.class));
		bindEdgePartAdapters(AdapterMaps.getAdapterMapBinder(binder(), EdgePart.class));
		bindEdgeLabelPartAdapters(AdapterMaps.getAdapterMapBinder(binder(), EdgeLabelPart.class));
		bindNodeLabelPartAdapters(AdapterMaps.getAdapterMapBinder(binder(), NodeLabelPart.class));

		bindFXSquareSegmentHandlePartAdapters(
				AdapterMaps.getAdapterMapBinder(binder(), FXSquareSegmentHandlePart.class));
		bindFXCircleSegmentHandlePartAdapters(
				AdapterMaps.getAdapterMapBinder(binder(), FXCircleSegmentHandlePart.class));

		bindHidingHoverHandlePartAdapters(AdapterMaps.getAdapterMapBinder(binder(), HideHoverHandlePart.class));
		bindShowHiddenNeighborsHoverHandlePartAdapters(
				AdapterMaps.getAdapterMapBinder(binder(), ShowHiddenNeighborsHoverHandlePart.class));
	}

}
