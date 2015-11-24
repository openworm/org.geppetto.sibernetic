/*******************************************************************************
 * The MIT License (MIT)
 * 
 * Copyright (c) 2011 - 2015 OpenWorm.
 * http://openworm.org
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT License
 * which accompanies this distribution, and is available at
 * http://opensource.org/licenses/MIT
 *
 * Contributors:
 *     	OpenWorm - http://openworm.org/people.html
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights 
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell 
 * copies of the Software, and to permit persons to whom the Software is 
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR 
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE 
 * USE OR OTHER DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package org.geppetto.model.sph.features;
import java.util.List;
import java.util.StringTokenizer;

import org.geppetto.core.data.model.IAspectConfiguration;
import org.geppetto.core.model.ModelInterpreterException;
import org.geppetto.core.services.GeppettoFeature;
import org.geppetto.model.sph.SPHParticle;
import org.geppetto.model.sph.x.SPHModelX;
import org.geppetto.model.values.ValuesFactory;

/**
 * Variable watch feature for SPH simulator
 * @author Jesus Martinez (jesus@metacell.us)
 *
 */
public class SPHSimulationTreeFeature implements IWatchableVariableListFeature{

	private AspectSubTreeNode simulationTree;
	
	private GeppettoFeature type = GeppettoFeature.WATCHABLE_VARIABLE_LIST_FEATURE;
	
	@Override
	public GeppettoFeature getType()
	{
		return type;
	}

	@Override
	public boolean listWatchableVariables(AspectNode aspectNode, IAspectConfiguration configuration) throws ModelInterpreterException
	{
		boolean modified = true;
		
		simulationTree = (AspectSubTreeNode) aspectNode.getSubTree(AspectTreeType.SIMULATION_TREE);
		simulationTree.setId(AspectTreeType.SIMULATION_TREE.toString());
		simulationTree.setModified(modified);

		SPHModelX _sphModelX = (SPHModelX) aspectNode.getModel();
		String name = "particle.position";
		int index =0;
		// check which watchable variables are being watched
		for (SPHParticle p : _sphModelX.getParticles()) {
			if(index<2){
				String particleID = String.valueOf(index);

				// tokenize variable path in watch list via dot
				// separator (handle array brackets)
				StringTokenizer tokenizer = new StringTokenizer(name, ".");
				ACompositeValue node = simulationTree;
				while (tokenizer.hasMoreElements()) {
					// loop through tokens and build tree
					String current = tokenizer.nextToken();
					boolean found = false;
					for (ANode child : node.getChildren()) {
						if (child.getId().equals(current)) {
							if (child instanceof ACompositeValue) {
								node = (ACompositeValue) child;
							}
							found = true;
							break;
						}
					}
					if (found) {
						continue;
					} else {
						if (tokenizer.hasMoreElements()) {
							// not a leaf, create a composite statenode
							String nodeName = current;
							if (current.equals("particle")) {
								nodeName = current + "[" + particleID
										+ "]";
							}

							CompositeValue newNode = new CompositeValue(
									nodeName);
							newNode.setId(nodeName);

							boolean addNewNode = containsNode(node,
									newNode.getId());

							if (addNewNode) {
								node.addChild(newNode);
								node = newNode;
							} else {
								node = getNode(node, newNode.getId());
							}
						}else{
							CompositeValue newNode = new CompositeValue(
									current);
							newNode.setId(current);

							boolean addNewNode = containsNode(node,
									newNode.getId());

							if (addNewNode) {
								node.addChild(newNode);
								node = newNode;
								
								VariableValue newNodeX = new VariableValue("x");
								newNodeX.setId("x");
								FloatValue valX = ValuesFactory.getFloatValue(p.getPositionVector().getX());;
								QuantityValue qX = new QuantityValue();
								qX.setValue(valX);
								newNodeX.addQuantity(qX);

								VariableValue newNodeY = new VariableValue("y");
								newNodeY.setId("y");
								FloatValue valY = ValuesFactory.getFloatValue(p.getPositionVector().getY());;
								QuantityValue qY = new QuantityValue();
								qY.setValue(valY);
								newNodeY.addQuantity(qY);

								VariableValue newNodeZ = new VariableValue("z");
								newNodeZ.setId("z");
								FloatValue valZ = ValuesFactory.getFloatValue(p.getPositionVector().getZ());;
								QuantityValue qZ = new QuantityValue();
								qZ.setValue(valZ);
								newNodeZ.addQuantity(qZ);

								node.addChild(newNodeX);
								node.addChild(newNodeY);
								node.addChild(newNodeZ);
							}
						}
					}
				}
			}
			index++;
		}

		simulationTree.setModified(true);

		return modified;
	}
	
	private boolean containsNode(ACompositeValue node, String name) {
		List<ANode> children = node.getChildren();

		boolean addNewNode = true;
		for (ANode child : children) {
			if (child.getId().equals(name)) {
				addNewNode = false;
				return addNewNode;
			}
			if (child instanceof ACompositeValue) {
				if (((ACompositeValue) child).getChildren() != null) {
					addNewNode = containsNode((ACompositeValue) child, name);
				}
			}

		}

		return addNewNode;
	}

	private ACompositeValue getNode(ACompositeValue node, String name) {
		ACompositeValue newNode = null;

		List<ANode> children = node.getChildren();

		boolean addNewNode = true;
		for (ANode child : children) {
			if (child.getId().equals(name)) {
				newNode = (ACompositeValue) child;
				return newNode;
			}
			if (child instanceof ACompositeValue) {
				if (((ACompositeValue) child).getChildren() != null) {
					newNode = getNode((ACompositeValue) child, name);
				}
			}

		}

		return newNode;
	}
}
