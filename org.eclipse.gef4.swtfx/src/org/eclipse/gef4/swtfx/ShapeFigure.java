/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 * 
 *******************************************************************************/
package org.eclipse.gef4.swtfx;

import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Path;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swtfx.gc.GraphicsContext;
import org.eclipse.gef4.swtfx.gc.LineCap;
import org.eclipse.gef4.swtfx.gc.LineJoin;

public class ShapeFigure<T extends IShape> extends AbstractFigure {

	private T shape;
	private StrokeType strokeType = StrokeType.CENTER;

	public ShapeFigure(T shape) {
		this.shape = shape;
	}

	// TODO
	// public Paint getFillPaint();
	// public void setFillPaint(...);
	// public Paint getStrokePaint();
	// public void setStrokePaint(...);
	// public double[] getDashes();
	// public double getDashOffset();
	// public void setDashes(double....);
	// public void setDashes(double[]);
	// ... (line width, miter limit, anti-aliasing)

	@Override
	public boolean contains(double localX, double localY) {
		return geomContains(localX, localY);
	}

	@Override
	public void doPaint(GraphicsContext g) {
		g.save();
		Point loc = shape.getBounds().getLocation().getNegated();
		g.translate(loc.x, loc.y);

		g.fillPath(shape.toPath());
		switch (getStrokeType()) {
		case CENTER:
			g.strokePath(shape.toPath());
			break;
		case OUTSIDE:
			throw new IllegalStateException(
					"The StrokeType.OUTSIDE is not yet implemented.");
		case INSIDE:
			throw new IllegalStateException(
					"The StrokeType.INSIDE is not yet implemented.");
		default:
			throw new IllegalStateException("Unknown StrokeType: "
					+ getStrokeType());
		}

		g.restore();
	}

	/**
	 * Returns <code>true</code> if the given point is contained by the
	 * underlying {@link IShape}.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean geomContains(double x, double y) {
		return shape.contains(shape.getBounds().getLocation().getNegated()
				.getTranslated(x, y));
	}

	@Override
	public Rectangle getBoundsInLocal() {
		Rectangle boundsInLocal = getLayoutBounds();

		Path clip = getPaintStateByReference().getClipPathByReference();
		if (clip != null) {
			// advance/reduce boundsInLocal by clip
			// FIXME: Atm, the clip is a Path which is not transformed, or
			// anything. In JavaFX, the clip can be set to any Node.
			boundsInLocal = Path.intersect(clip, boundsInLocal.toPath())
					.getBounds();
		}

		return boundsInLocal;
	}

	/**
	 * Returns the geometric bounds of the underlying {@link IShape}. For some
	 * shapes, the bounds have to be translated to the local coordinate system
	 * of the {@link INode}.
	 * 
	 * @return
	 */
	private Rectangle getGeomBounds() {
		Rectangle bounds = shape.getBounds();
		return bounds.getTranslated(bounds.getLocation().getNegated());
	}

	@Override
	public Rectangle getLayoutBounds() {
		Rectangle layoutBounds = getGeomBounds();

		if (strokeType != StrokeType.INSIDE) {
			// advance layoutBounds by strokeWidth
			double strokeWidth = getPaintStateByReference().getLineWidth();
			if (strokeType == StrokeType.CENTER) {
				strokeWidth /= 2;
			}
			layoutBounds.expand(strokeWidth, strokeWidth, strokeWidth,
					strokeWidth);
		}

		return layoutBounds;
	}

	@Override
	public LineCap getLineCap() {
		return getPaintStateByReference().getLineCap();
	}

	@Override
	public LineJoin getLineJoin() {
		return getPaintStateByReference().getLineJoin();
	}

	public T getShape() {
		return shape;
	}

	public StrokeType getStrokeType() {
		return strokeType;
	}

	@Override
	public void setLineCap(LineCap cap) {
		getPaintStateByReference().setLineCap(cap);
	}

	@Override
	public void setLineJoin(LineJoin join) {
		getPaintStateByReference().setLineJoin(join);
	}

	public void setStrokeType(StrokeType strokeType) {
		this.strokeType = strokeType;
	}

	@Override
	public String toString() {
		return "ShapeFigure(" + shape + ")";
	}

}
