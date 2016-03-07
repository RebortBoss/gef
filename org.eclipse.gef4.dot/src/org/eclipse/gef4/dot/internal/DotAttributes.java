/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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
package org.eclipse.gef4.dot.internal;

import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.gef4.dot.internal.parser.DotAttributesStandaloneSetup;
import org.eclipse.gef4.dot.internal.parser.dotAttributes.Point;
import org.eclipse.gef4.dot.internal.parser.dotAttributes.SplineType;
import org.eclipse.gef4.dot.internal.parser.parser.antlr.DotAttributesParser;
import org.eclipse.gef4.dot.internal.parser.services.DotAttributesGrammarAccess;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.eclipse.xtext.ParserRule;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.SyntaxErrorMessage;
import org.eclipse.xtext.parser.IParseResult;

import com.google.inject.Injector;

/**
 * The {@link DotAttributes} class contains all properties which are supported
 * by {@link DotImport} and {@link DotExport}, i.e. they are set on the
 * resulting {@link Graph}.
 * 
 * @author mwienand
 * @author anyssen
 *
 */
public class DotAttributes {

	// TODO: if platform is running, don't use standalone setup here
	private static final Injector injector = new DotAttributesStandaloneSetup()
			.createInjectorAndDoEMFRegistration();

	private static final DotAttributesParser dotAttributesParser = injector
			.getInstance(DotAttributesParser.class);

	private static final DotAttributesGrammarAccess dotAttributesGrammarAccess = injector
			.getInstance(DotAttributesGrammarAccess.class);

	/**
	 * Specifies the identifier of a node.
	 */
	public static final String NODE_ID = "id";

	/**
	 * Specifies the label of a node.
	 */
	public static final String NODE_LABEL = "label";

	/**
	 * Specified the 'pos' attribute of a node
	 */
	public static final String NODE_POS = "pos";

	/**
	 * Specified the 'height' attribute of a node
	 */
	public static final String NODE_HEIGHT = "height";

	/**
	 * Specified the 'width' attribute of a node
	 */
	public static final String NODE_WIDTH = "width";

	/**
	 * Specifies the 'id' attribuge of an edge.
	 */
	public static final String EDGE_ID = "id";

	/**
	 * Specifies the 'pos' attribute of an edge.
	 */
	public static final String EDGE_POS = "pos";

	/**
	 * Specifies the label of an edge.
	 */
	public static final String EDGE_LABEL = "label";

	/**
	 * Specifies the tail label of an edge (taillabel).
	 */
	public static final String EDGE_TAILLABEL = "taillabel";

	/**
	 * Specifies the tail label of an edge (headlabel).
	 */
	public static final String EDGE_HEADLABEL = "headlabel";

	/**
	 * Specifies the external label of an edge.
	 */
	public static final String EDGE_XLABEL = "xlabel";

	/**
	 * Specifies the external label of an node.
	 */
	public static final String NODE_XLABEL = "xlabel";

	/**
	 * Specifies the position of a node's external label.
	 */
	public static final String NODE_XLP = "xlp";

	/**
	 * Specifies the position of an edge's external label.
	 */
	public static final String EDGE_XLP = "xlp";

	/**
	 * Specifies the position of an edge's label (lp).
	 */
	public static final String EDGE_LP = "lp";

	/**
	 * Specifies the position of an edge's head label (head_lp).
	 */
	public static final String EDGE_HEAD_LP = "head_lp";

	/**
	 * Specifies the position of an edge's tail label (tail_lp).
	 */
	public static final String EDGE_TAIL_LP = "tail_lp";

	/**
	 * Specifies the rendering style of an edge, i.e. if it is solid, dashed,
	 * dotted, etc. Possible values are defined by {@link #EDGE_STYLE_VALUES}.
	 * The default value is defined by {@link #EDGE_STYLE_DEFAULT}.
	 */
	public static final String EDGE_STYLE = "style";

	/**
	 * This {@link #EDGE_STYLE} value specifies that the edge is rendered with
	 * the void, which means the the original Dot default value is used.
	 */
	public static final String EDGE_STYLE_VOID = "";

	/**
	 * This {@link #EDGE_STYLE} value specifies that the edge is rendered
	 * dashed.
	 */
	public static final String EDGE_STYLE_DASHED = "dashed";

	/**
	 * This {@link #EDGE_STYLE} value specifies that the edge is rendered
	 * dotted.
	 */
	public static final String EDGE_STYLE_DOTTED = "dotted";

	/**
	 * This {@link #EDGE_STYLE} value specifies that the edge is rendered solid.
	 */
	public static final String EDGE_STYLE_SOLID = "solid";

	/**
	 * This {@link #EDGE_STYLE} value specifies that the edge is rendered
	 * invisible.
	 */
	public static final String EDGE_STYLE_INVIS = "invis";

	/**
	 * This {@link #EDGE_STYLE} value specifies that the edge is rendered bold.
	 */
	public static final String EDGE_STYLE_BOLD = "bold";

	/**
	 * This {@link #EDGE_STYLE} value specifies that the edge is rendered
	 * tapered.
	 */
	public static final String EDGE_STYLE_TAPERED = "tapered";

	/**
	 * Defines all possible values for the {@link #EDGE_STYLE} property.
	 */
	public static final Set<String> EDGE_STYLE_VALUES = new HashSet<>(
			Arrays.asList(EDGE_STYLE_DASHED, EDGE_STYLE_DOTTED,
					EDGE_STYLE_SOLID, EDGE_STYLE_INVIS, EDGE_STYLE_BOLD,
					EDGE_STYLE_TAPERED, EDGE_STYLE_VOID));

	/**
	 * Defines the default value for the {@link #EDGE_STYLE} property, which is
	 * {@link #EDGE_STYLE_SOLID}.
	 */
	public static final String EDGE_STYLE_DEFAULT = EDGE_STYLE_SOLID;

	/**
	 * Specifies the graph type. Possible values are defined by
	 * {@link #GRAPH_TYPE_VALUES}. The default value is defined by
	 * {@link #GRAPH_TYPE_DEFAULT}.
	 */
	public static final String GRAPH_TYPE = "type";

	/**
	 * This {@link #GRAPH_TYPE} value specifies that the edges within the graph
	 * are directed.
	 */
	public static final String GRAPH_TYPE_DIRECTED = "directed";

	/**
	 * This {@link #GRAPH_TYPE} value specifies that the edges within the graph
	 * are undirected.
	 */
	public static final String GRAPH_TYPE_UNDIRECTED = "undirected";

	/**
	 * Defines all possible values for the {@link #GRAPH_TYPE} property.
	 */
	public static final Set<String> GRAPH_TYPE_VALUES = new HashSet<>(
			Arrays.asList(GRAPH_TYPE_DIRECTED, GRAPH_TYPE_UNDIRECTED));

	/**
	 * Defines the default value for {@link #GRAPH_TYPE}, which is
	 * {@link #GRAPH_TYPE_UNDIRECTED}.
	 */
	public static final String GRAPH_TYPE_DEFAULT = GRAPH_TYPE_UNDIRECTED;

	/**
	 * Specifies the layout algorithm which shall be used to layout the graph.
	 * Possible values are defined by {@link #GRAPH_LAYOUT_VALUES}. The default
	 * value is defined by {@link #GRAPH_LAYOUT_DEFAULT}.
	 */
	public static final String GRAPH_LAYOUT = "layout";

	/**
	 * This {@link #GRAPH_LAYOUT} value specifies that the "dot" layout
	 * algorithm is to be used for laying out the graph.
	 */
	public static final String GRAPH_LAYOUT_DOT = "dot";

	/**
	 * This {@link #GRAPH_LAYOUT} value specifies that the "osage" layout
	 * algorithm is to be used for laying out the graph.
	 */
	public static final String GRAPH_LAYOUT_OSAGE = "osage";

	/**
	 * This {@link #GRAPH_LAYOUT} value specifies that the "grid" layout
	 * algorithm is to be used for laying out the graph.
	 */
	public static final String GRAPH_LAYOUT_GRID = "grid";

	/**
	 * This {@link #GRAPH_LAYOUT} value specifies that the "twopi" layout
	 * algorithm is to be used for laying out the graph.
	 */
	public static final String GRAPH_LAYOUT_TWOPI = "twopi";

	/**
	 * This {@link #GRAPH_LAYOUT} value specifies that the "circo" layout
	 * algorithm is to be used for laying out the graph.
	 */
	public static final String GRAPH_LAYOUT_CIRCO = "circo";

	/**
	 * This {@link #GRAPH_LAYOUT} value specifies that the "neato" layout
	 * algorithm is to be used for laying out the graph.
	 */
	public static final String GRAPH_LAYOUT_NEATO = "neato";

	/**
	 * This {@link #GRAPH_LAYOUT} value specifies that the "fdp" layout
	 * algorithm is to be used for laying out the graph.
	 */
	public static final String GRAPH_LAYOUT_FDP = "fdp";

	/**
	 * This {@link #GRAPH_LAYOUT} value specifies that the "sfdp" layout
	 * algorithm is to be used for laying out the graph.
	 */
	public static final String GRAPH_LAYOUT_SFDP = "sfdp";

	/**
	 * Defines all possible values for the {@link #GRAPH_LAYOUT} property.
	 */
	public static final Set<String> GRAPH_LAYOUT_VALUES = new HashSet<>(
			Arrays.asList(GRAPH_LAYOUT_DOT, GRAPH_LAYOUT_OSAGE,
					GRAPH_LAYOUT_GRID, GRAPH_LAYOUT_TWOPI, GRAPH_LAYOUT_CIRCO,
					GRAPH_LAYOUT_NEATO, GRAPH_LAYOUT_FDP, GRAPH_LAYOUT_SFDP));

	/**
	 * Defines the default value for the {@link #GRAPH_LAYOUT} property, which
	 * is {@link #GRAPH_LAYOUT_DOT}.
	 */
	public static final String GRAPH_LAYOUT_DEFAULT = GRAPH_LAYOUT_DOT;

	/**
	 * Specifies the rankdir property which is passed to the layout algorithm
	 * which is used for laying out the graph. Possible values are defined by
	 * {@link #GRAPH_RANKDIR_VALUES}. The default value is defined by
	 * {@link #GRAPH_RANKDIR_DEFAULT}.
	 */
	public static final String GRAPH_RANKDIR = "rankdir";

	/**
	 * This {@link #GRAPH_RANKDIR} value specifies that the graph is to be laid
	 * out horizontally from left to right.
	 */
	public static final String GRAPH_RANKDIR_LR = "lr";

	/**
	 * This {@link #GRAPH_RANKDIR} value specifies that the graph is to be laid
	 * out vertically from top to bottom.
	 */
	public static final String GRAPH_RANKDIR_TD = "td";

	/**
	 * Defines all possible values for the {@link #GRAPH_RANKDIR} property.
	 */
	public static final Set<String> GRAPH_RANKDIR_VALUES = new HashSet<>(
			Arrays.asList(GRAPH_RANKDIR_LR, GRAPH_RANKDIR_TD));

	/**
	 * Defines the default value for the {@link #GRAPH_RANKDIR} property.
	 */
	public static final String GRAPH_RANKDIR_DEFAULT = GRAPH_RANKDIR_TD;

	/**
	 * Returns the value of the {@link #GRAPH_LAYOUT} property of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #GRAPH_LAYOUT} property.
	 * @return The value of the {@link #GRAPH_LAYOUT} property of the given
	 *         {@link Graph}.
	 */
	public static String getLayout(Graph graph) {
		return (String) graph.attributesProperty().get(GRAPH_LAYOUT);
	}

	/**
	 * Sets the {@link #GRAPH_LAYOUT} property of the given {@link Graph} to the
	 * given <i>layout</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #GRAPH_LAYOUT} property.
	 * @param layout
	 *            The new value for the {@link #GRAPH_LAYOUT} property.
	 * @throws IllegalArgumentException
	 *             when the given <i>layout</i> value is not supported, i.e. not
	 *             contained within {@link #GRAPH_LAYOUT_VALUES}.
	 */
	public static void setLayout(Graph graph, String layout) {
		if (!GRAPH_LAYOUT_VALUES.contains(layout)) {
			throw new IllegalArgumentException(
					"Cannot set graph attribute \"layout\" to \"" + layout
							+ "\"; supported values: " + GRAPH_LAYOUT_VALUES);
		}
		graph.attributesProperty().put(GRAPH_LAYOUT, layout);
	}

	/**
	 * Returns the value of the {@link #GRAPH_TYPE} property of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #GRAPH_TYPE} property.
	 * @return The value of the {@link #GRAPH_TYPE} property of the given
	 *         {@link Graph}.
	 */
	public static String getType(Graph graph) {
		return (String) graph.attributesProperty().get(GRAPH_TYPE);
	}

	/**
	 * Sets the {@link #GRAPH_TYPE} property of the given {@link Graph} to the
	 * given <i>type</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #GRAPH_TYPE} property.
	 * @param type
	 *            The new value for the {@link #GRAPH_TYPE} property.
	 * @throws IllegalArgumentException
	 *             when the given <i>type</i> value is not supported, i.e. not
	 *             contained within {@link #GRAPH_TYPE_VALUES}.
	 */
	public static void setType(Graph graph, String type) {
		if (!GRAPH_TYPE_VALUES.contains(type)) {
			throw new IllegalArgumentException(
					"Cannot set graph attribute \"type\" to \"" + type
							+ "\"; supported values: " + GRAPH_TYPE_VALUES);
		}
		graph.attributesProperty().put(GRAPH_TYPE, type);
	}

	/**
	 * Returns the value of the {@link #GRAPH_RANKDIR} property of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #GRAPH_RANKDIR} property.
	 * @return The value of the {@link #GRAPH_RANKDIR} property of the given
	 *         {@link Graph}.
	 */
	public static String getRankdir(Graph graph) {
		return (String) graph.attributesProperty().get(GRAPH_RANKDIR);
	}

	/**
	 * Sets the {@link #GRAPH_RANKDIR} property of the given {@link Graph} to
	 * the given <i>rankdir</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #GRAPH_RANKDIR} property.
	 * @param rankdir
	 *            The new value for the {@link #GRAPH_RANKDIR} property.
	 * @throws IllegalArgumentException
	 *             when the given <i>rankdir</i> value is not supported, i.e.
	 *             not contained within {@link #GRAPH_RANKDIR_VALUES}.
	 */
	public static void setRankdir(Graph graph, String rankdir) {
		if (!GRAPH_RANKDIR_VALUES.contains(rankdir)) {
			throw new IllegalArgumentException(
					"Cannot set graph attribute \"rankdir\" to \"" + rankdir
							+ "\"; supported values: " + GRAPH_RANKDIR_VALUES);
		}
		graph.attributesProperty().put(GRAPH_RANKDIR, rankdir);
	}

	/**
	 * Returns the value of the {@link #NODE_LABEL} property of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #NODE_LABEL} property.
	 * @return The value of the {@link #NODE_LABEL} property of the given
	 *         {@link Node}.
	 */
	public static String getLabel(Node node) {
		return (String) node.attributesProperty().get(NODE_LABEL);
	}

	/**
	 * Returns the value of the {@link #NODE_XLABEL} property of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #NODE_XLABEL} property.
	 * @return The value of the {@link #NODE_XLABEL} property of the given
	 *         {@link Node}.
	 */
	public static String getXLabel(Node node) {
		return (String) node.attributesProperty().get(NODE_XLABEL);
	}

	/**
	 * Returns the value of the {@link #EDGE_XLABEL} property of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #EDGE_XLABEL} property.
	 * @return The value of the {@link #EDGE_XLABEL} property of the given
	 *         {@link Edge}.
	 */
	public static String getXLabel(Edge edge) {
		return (String) edge.attributesProperty().get(EDGE_XLABEL);
	}

	/**
	 * Returns the value of the {@link #NODE_XLP} property of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #NODE_XLP} property.
	 * @return The value of the {@link #NODE_XLP} property of the given
	 *         {@link Node}.
	 */
	public static String getXlp(Node node) {
		return (String) node.attributesProperty().get(NODE_XLP);
	}

	/**
	 * Returns the value of the {@link #EDGE_XLP} property of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #EDGE_XLP} property.
	 * @return The value of the {@link #EDGE_XLP} property of the given
	 *         {@link Edge}.
	 */
	public static String getXlp(Edge edge) {
		return (String) edge.attributesProperty().get(EDGE_XLP);
	}

	/**
	 * Sets the {@link #NODE_LABEL} property of the given {@link Node} to the
	 * given <i>label</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #NODE_LABEL} property.
	 * @param label
	 *            The new value for the {@link #NODE_LABEL} property.
	 */
	public static void setLabel(Node node, String label) {
		node.attributesProperty().put(NODE_LABEL, label);
	}

	/**
	 * Sets the {@link #EDGE_XLABEL} property of the given {@link Edge} to the
	 * given <i>xLabel</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #EDGE_XLABEL} property.
	 * @param xLabel
	 *            The new value for the {@link #EDGE_XLABEL} property.
	 */
	public static void setXLabel(Edge edge, String xLabel) {
		edge.attributesProperty().put(EDGE_XLABEL, xLabel);
	}

	/**
	 * Sets the {@link #EDGE_XLP} property of the given {@link Edge} to the
	 * given <i>xlp</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #EDGE_XLP} property.
	 * @param xlp
	 *            The new value for the {@link #EDGE_XLP} property.
	 */
	public static void setXlp(Edge edge, String xlp) {
		edge.attributesProperty().put(EDGE_XLP, xlp);
	}

	/**
	 * Sets the {@link #NODE_XLP} property of the given {@link Node} to the
	 * given <i>xlp</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #NODE_XLP} property.
	 * @param xlp
	 *            The new value for the {@link #NODE_XLP} property.
	 */
	public static void setXlp(Node node, String xlp) {
		node.attributesProperty().put(NODE_XLP, xlp);
	}

	/**
	 * Sets the {@link #EDGE_LP} property of the given {@link Edge} to the given
	 * <i>lp</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #EDGE_LP} property.
	 * @param lp
	 *            The new value for the {@link #EDGE_LP} property.
	 */
	public static void setLp(Edge edge, String lp) {
		edge.attributesProperty().put(EDGE_LP, lp);
	}

	/**
	 * Sets the {@link #EDGE_HEAD_LP} property of the given {@link Edge} to the
	 * given <i>headLp</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #EDGE_HEAD_LP} property.
	 * @param headLp
	 *            The new value for the {@link #EDGE_HEAD_LP} property.
	 */
	public static void setHeadLp(Edge edge, String headLp) {
		edge.attributesProperty().put(EDGE_LP, headLp);
	}

	/**
	 * Sets the {@link #EDGE_TAIL_LP} property of the given {@link Edge} to the
	 * given <i>tailLp</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #EDGE_TAIL_LP} property.
	 * @param tailLp
	 *            The new value for the {@link #EDGE_TAIL_LP} property.
	 */
	public static void setTailLp(Edge edge, String tailLp) {
		edge.attributesProperty().put(EDGE_TAIL_LP, tailLp);
	}

	/**
	 * Sets the {@link #NODE_XLABEL} property of the given {@link Node} to the
	 * given <i>xLabel</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #NODE_XLABEL} property.
	 * @param xLabel
	 *            The new value for the {@link #NODE_XLABEL} property.
	 */
	public static void setXLabel(Node node, String xLabel) {
		node.attributesProperty().put(NODE_XLABEL, xLabel);
	}

	/**
	 * Returns the value of the {@link #NODE_ID} property of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #NODE_ID} property.
	 * @return The value of the {@link #NODE_ID} property of the given
	 *         {@link Node}.
	 */
	public static String getId(Node node) {
		return (String) node.attributesProperty().get(NODE_ID);
	}

	/**
	 * Returns the value of the {@link #NODE_POS} property of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #NODE_POS} property.
	 * @return The value of the {@link #NODE_POS} property of the given
	 *         {@link Node}.
	 */
	public static String getPos(Node node) {
		return (String) node.attributesProperty().get(NODE_POS);
	}

	/**
	 * Returns the value of the {@link #EDGE_POS} property of the given
	 * {@link Node}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #EDGE_POS} property.
	 * @return The value of the {@link #EDGE_POS} property of the given
	 *         {@link Edge}.
	 */
	public static String getPos(Edge edge) {
		return (String) edge.attributesProperty().get(EDGE_POS);
	}

	/**
	 * Returns the value of the {@link #EDGE_LP} property of the given
	 * {@link Node}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #EDGE_LP} property.
	 * @return The value of the {@link #EDGE_LP} property of the given
	 *         {@link Edge}.
	 */
	public static String getLp(Edge edge) {
		return (String) edge.attributesProperty().get(EDGE_LP);
	}

	/**
	 * Returns the value of the {@link #EDGE_HEAD_LP} property of the given
	 * {@link Node}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #EDGE_HEAD_LP} property.
	 * @return The value of the {@link #EDGE_HEAD_LP} property of the given
	 *         {@link Edge}.
	 */
	public static String getHeadLp(Edge edge) {
		return (String) edge.attributesProperty().get(EDGE_HEAD_LP);
	}

	/**
	 * Returns the value of the {@link #EDGE_TAIL_LP} property of the given
	 * {@link Node}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #EDGE_TAIL_LP} property.
	 * @return The value of the {@link #EDGE_TAIL_LP} property of the given
	 *         {@link Edge}.
	 */
	public static String getTailLp(Edge edge) {
		return (String) edge.attributesProperty().get(EDGE_TAIL_LP);
	}

	/**
	 * Returns the (parsed) value of the {@link #EDGE_POS} property of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #EDGE_POS} property, parsed as a {@link SplineType}.
	 * @return The value of the {@link #EDGE_POS} property of the given
	 *         {@link Edge}.
	 */
	public static SplineType getPosParsed(Edge edge) {
		IParseResult parsedPropertyValue = parsePropertyValue(
				dotAttributesGrammarAccess.getSplineTypeRule(), getPos(edge));
		SplineType splineType = (SplineType) parsedPropertyValue
				.getRootASTElement();
		return splineType;
	}

	/**
	 * Returns the (parsed) value of the {@link #NODE_POS} property of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #NODE_POS} property, parsed as a {@link Point}.
	 * @return The value of the {@link #NODE_POS} property of the given
	 *         {@link Node}.
	 */
	public static Point getPosParsed(Node node) {
		IParseResult parsedPropertyValue = parsePropertyValue(
				dotAttributesGrammarAccess.getPointRule(), getPos(node));
		Point point = (Point) parsedPropertyValue.getRootASTElement();
		return point;
	}

	/**
	 * Returns the value of the {@link #NODE_HEIGHT} property of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #NODE_HEIGHT} property.
	 * @return The value of the {@link #NODE_HEIGHT} property of the given
	 *         {@link Node}.
	 */
	public static String getHeight(Node node) {
		return (String) node.attributesProperty().get(NODE_HEIGHT);
	}

	/**
	 * Returns the value of the {@link #NODE_WIDTH} property of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #NODE_WIDTH} property.
	 * @return The value of the {@link #NODE_WIDTH} property of the given
	 *         {@link Node}.
	 */
	public static String getWidth(Node node) {
		return (String) node.attributesProperty().get(NODE_WIDTH);
	}

	/**
	 * Sets the {@link #NODE_POS} property of the given {@link Node} to the
	 * given value.
	 * 
	 * @param node
	 *            The {@link Node} whose property value to set.
	 * @param pos
	 *            The new value of the {@link #NODE_POS} property.
	 */
	public static void setPos(Node node, String pos) {
		IParseResult parseResult = parsePropertyValue(
				dotAttributesGrammarAccess.getPointRule(), pos);
		if (parseResult.hasSyntaxErrors()) {
			throw new IllegalArgumentException(
					"Cannot set node attribute '" + NODE_POS + "' to '" + pos
							+ "': " + getSyntaxErrorMessages(parseResult));
		}
		node.getAttributes().put(NODE_POS, pos);
	}

	/**
	 * Sets the {@link #NODE_HEIGHT} property of the given {@link Node} to the
	 * given value.
	 * 
	 * @param node
	 *            The {@link Node} whose property value to set.
	 * @param height
	 *            The new value of the {@link #NODE_HEIGHT} property.
	 */
	public static void setHeight(Node node, String height) {
		try {
			Double.parseDouble(height);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
					"Cannot set node attribute '" + NODE_HEIGHT + "' to '"
							+ height + "': parsing as double failed.");
		}
		node.getAttributes().put(NODE_HEIGHT, height);
	}

	/**
	 * Sets the {@link #NODE_WIDTH} property of the given {@link Node} to the
	 * given value.
	 * 
	 * @param node
	 *            The {@link Node} whose property value to set.
	 * @param width
	 *            The new value of the {@link #NODE_WIDTH} property.
	 */
	public static void setWidth(Node node, String width) {
		try {
			Double.parseDouble(width);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
					"Cannot set node attribute '" + NODE_WIDTH + "' to '"
							+ width + "': parsing as double failed.");
		}
		node.getAttributes().put(NODE_WIDTH, width);
	}

	/**
	 * Sets the {@link #EDGE_POS} property of the given {@link Edge} to the
	 * given value.
	 * 
	 * @param edge
	 *            The {@link Edge} whose property value to set.
	 * @param pos
	 *            The new value of the {@link #EDGE_POS} property.
	 */
	public static void setPos(Edge edge, String pos) {
		IParseResult parseResult = parsePropertyValue(
				dotAttributesGrammarAccess.getSplineTypeRule(), pos);
		if (parseResult.hasSyntaxErrors()) {
			throw new IllegalArgumentException(
					"Cannot set edge attribute '" + EDGE_POS + "' to '" + pos
							+ "': " + getSyntaxErrorMessages(parseResult));
		}
		edge.getAttributes().put(EDGE_POS, pos);
	}

	/**
	 * Sets the {@link #NODE_ID} property of the given {@link Node} to the given
	 * <i>id</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #NODE_ID} property.
	 * @param id
	 *            The new value for the {@link #NODE_ID} property.
	 */
	public static void setId(Node node, String id) {
		node.attributesProperty().put(NODE_ID, id);
	}

	/**
	 * Returns the value of the {@link #EDGE_LABEL} property of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #EDGE_LABEL} property.
	 * @return The value of the {@link #EDGE_LABEL} property of the given
	 *         {@link Edge}.
	 */
	public static String getLabel(Edge edge) {
		return (String) edge.attributesProperty().get(EDGE_LABEL);
	}

	/**
	 * Returns the value of the {@link #EDGE_HEADLABEL} property of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #EDGE_HEADLABEL} property.
	 * @return The value of the {@link #EDGE_HEADLABEL} property of the given
	 *         {@link Edge}.
	 */
	public static String getHeadLabel(Edge edge) {
		return (String) edge.attributesProperty().get(EDGE_HEADLABEL);
	}

	/**
	 * Returns the value of the {@link #EDGE_TAILLABEL} property of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #EDGE_TAILLABEL} property.
	 * @return The value of the {@link #EDGE_TAILLABEL} property of the given
	 *         {@link Edge}.
	 */
	public static String getTailLabel(Edge edge) {
		return (String) edge.attributesProperty().get(EDGE_TAILLABEL);
	}

	/**
	 * Sets the {@link #EDGE_LABEL} property of the given {@link Edge} to the
	 * given <i>label</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #EDGE_LABEL} property.
	 * @param label
	 *            The new value for the {@link #EDGE_LABEL} property.
	 */
	public static void setLabel(Edge edge, String label) {
		edge.attributesProperty().put(EDGE_LABEL, label);
	}

	/**
	 * Sets the {@link #EDGE_HEADLABEL} property of the given {@link Edge} to
	 * the given <i>label</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #EDGE_HEADLABEL} property.
	 * @param label
	 *            The new value for the {@link #EDGE_HEADLABEL} property.
	 */
	public static void setHeadLabel(Edge edge, String label) {
		edge.attributesProperty().put(EDGE_HEADLABEL, label);
	}

	/**
	 * Sets the {@link #EDGE_TAILLABEL} property of the given {@link Edge} to
	 * the given <i>label</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #EDGE_TAILLABEL} property.
	 * @param label
	 *            The new value for the {@link #EDGE_TAILLABEL} property.
	 */
	public static void setTailLabel(Edge edge, String label) {
		edge.attributesProperty().put(EDGE_TAILLABEL, label);
	}

	/**
	 * Returns the value of the {@link #EDGE_STYLE} property of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #EDGE_STYLE} property.
	 * @return The value of the {@link #EDGE_STYLE} property of the given
	 *         {@link Edge}.
	 */
	public static String getStyle(Edge edge) {
		return (String) edge.attributesProperty().get(EDGE_STYLE);
	}

	/**
	 * Sets the {@link #EDGE_STYLE} property of the given {@link Edge} to the
	 * given <i>style</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #EDGE_STYLE} property.
	 * @param style
	 *            The new value for the {@link #EDGE_STYLE} property.
	 * @throws IllegalArgumentException
	 *             when the given <i>style</i> value is not supported, i.e. not
	 *             contained within {@link #EDGE_STYLE_VALUES}.
	 */
	public static void setStyle(Edge edge, String style) {
		if (!EDGE_STYLE_VALUES.contains(style)) {
			throw new IllegalArgumentException(
					"Cannot set edge attribute \"style\" to \"" + style
							+ "\"; supported values: " + EDGE_STYLE_VALUES);
		}
		edge.attributesProperty().put(EDGE_STYLE, style);
	}

	/**
	 * Returns the value of the {@link #EDGE_ID} property of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #EDGE_ID} property.
	 * @return The value of the {@link #EDGE_ID} property of the given
	 *         {@link Edge}.
	 */
	public static String getId(Edge edge) {
		return (String) edge.attributesProperty().get(EDGE_ID);
	}

	/**
	 * Sets the {@link #EDGE_ID} property of the given {@link Edge} to the given
	 * <i>id</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #EDGE_ID} property.
	 * @param id
	 *            The new value for the {@link #EDGE_ID} property.
	 */
	public static void setId(Edge edge, String id) {
		edge.attributesProperty().put(EDGE_ID, id);
	}

	private static IParseResult parsePropertyValue(ParserRule rule,
			String propertyValue) {
		IParseResult parseResult = dotAttributesParser.parse(rule,
				new StringReader(propertyValue));
		return parseResult;
	}

	private static String getSyntaxErrorMessages(IParseResult parseResult) {
		StringBuilder sb = new StringBuilder();
		for (INode error : parseResult.getSyntaxErrors()) {
			SyntaxErrorMessage syntaxErrorMessage = error
					.getSyntaxErrorMessage();
			sb.append(syntaxErrorMessage.getMessage());
		}
		return sb.toString();
	}

	/**
	 * Returns the (parsed) value of the {@link #NODE_XLP} property of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #NODE_XLP} property, parsed as a {@link Point}.
	 * @return The value of the {@link #NODE_XLP} property of the given
	 *         {@link Node}.
	 */
	public static Point getXlpParsed(Node node) {
		IParseResult parsedPropertyValue = parsePropertyValue(
				dotAttributesGrammarAccess.getPointRule(), getXlp(node));
		Point point = (Point) parsedPropertyValue.getRootASTElement();
		return point;
	}

	/**
	 * Returns the (parsed) value of the {@link #EDGE_XLP} property of the given
	 * {@link Node}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #EDGE_XLP} property, parsed as a {@link Point}.
	 * @return The value of the {@link #EDGE_XLP} property of the given
	 *         {@link Edge}.
	 */
	public static Point getXlpParsed(Edge edge) {
		IParseResult parsedPropertyValue = parsePropertyValue(
				dotAttributesGrammarAccess.getPointRule(), getXlp(edge));
		Point point = (Point) parsedPropertyValue.getRootASTElement();
		return point;
	}

	/**
	 * Returns the (parsed) value of the {@link #EDGE_LP} property of the given
	 * {@link Node}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #EDGE_LP} property, parsed as a {@link Point}.
	 * @return The value of the {@link #EDGE_LP} property of the given
	 *         {@link Edge}.
	 */
	public static Point getLpParsed(Edge edge) {
		IParseResult parsedPropertyValue = parsePropertyValue(
				dotAttributesGrammarAccess.getPointRule(), getLp(edge));
		Point point = (Point) parsedPropertyValue.getRootASTElement();
		return point;
	}

	/**
	 * Returns the (parsed) value of the {@link #EDGE_HEAD_LP} property of the
	 * given {@link Node}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #EDGE_HEAD_LP} property, parsed as a {@link Point}.
	 * @return The value of the {@link #EDGE_HEAD_LP} property of the given
	 *         {@link Edge}.
	 */
	public static Point getHeadLpParsed(Edge edge) {
		IParseResult parsedPropertyValue = parsePropertyValue(
				dotAttributesGrammarAccess.getPointRule(), getHeadLp(edge));
		Point point = (Point) parsedPropertyValue.getRootASTElement();
		return point;
	}

	/**
	 * Returns the (parsed) value of the {@link #EDGE_TAIL_LP} property of the
	 * given {@link Node}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #EDGE_TAIL_LP} property, parsed as a {@link Point}.
	 * @return The value of the {@link #EDGE_TAIL_LP} property of the given
	 *         {@link Edge}.
	 */
	public static Point getTailLpParsed(Edge edge) {
		IParseResult parsedPropertyValue = parsePropertyValue(
				dotAttributesGrammarAccess.getPointRule(), getTailLp(edge));
		Point point = (Point) parsedPropertyValue.getRootASTElement();
		return point;
	}
}