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
import org.geppetto.core.features.IWatchableVariableListFeature;
import org.geppetto.core.model.ModelInterpreterException;
import org.geppetto.core.model.runtime.AspectNode;
import org.geppetto.core.model.runtime.AspectSubTreeNode;
import org.geppetto.core.model.runtime.AspectSubTreeNode.AspectTreeType;
import org.geppetto.core.services.GeppettoFeature;

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
	public boolean listWatchableVariables(AspectNode aspectNode) throws ModelInterpreterException
	{
		boolean modified = true;
		
		simulationTree = (AspectSubTreeNode) aspectNode.getSubTree(AspectTreeType.SIMULATION_TREE);
		simulationTree.setId(AspectTreeType.SIMULATION_TREE.toString());
		simulationTree.setModified(modified);
		
		//@tarelli fill this method!!
		// I have copy&paste the following code from the sphsolverservice. It may be helpful:
//		@Override
//		public void populateSimulationTree(AspectSubTreeNode watchTree) {
//			// map watchable buffers that are not already mapped
//			// NOTE: position is mapped for scene generation - improving performance
//			// by not mapping it again
//			_velocityPtr = _velocity.map(_queue, CLMem.MapFlags.Read);
//
//			// check which watchable variables are being watched
//			for (AVariable var : getWatchableVariables().getVariables()) {
//				String varName = var.getName();
//				// get watchable variables path
//				varName = varName
//						.replace(watchTree.getInstancePath() + ".", "");
//				// remove array bracket arguments from variable paths
//				String varNameNoBrackets = varName;
//				String particleID = null;
//				if (varName.indexOf("[") != -1) {
//					varNameNoBrackets = varName.substring(0,
//							varName.indexOf("["))
//							+ varName.substring(varName.indexOf("]") + 1,
//									varName.length());
//					particleID = varName.substring(varName.indexOf("[") + 1,
//							varName.indexOf("]"));
//				}
//
//				Integer ID = null;
//				if (particleID != null) {
//					// check that paticleID is valid
//					ID = Integer.parseInt(particleID);
//					if (!(ID < _particleCount)) {
//						throw new IllegalArgumentException(
//								"SPHSolverService:updateStateTreeForWatch - particle index is out of boundaries");
//					}
//				}
//
//				// tokenize variable path in watch list via dot
//				// separator (handle array brackets)
//				StringTokenizer tokenizer = new StringTokenizer(varName, ".");
//				ACompositeNode node = watchTree;
//				while (tokenizer.hasMoreElements()) {
//					// loop through tokens and build tree
//					String current = tokenizer.nextToken();
//					boolean found = false;
//					for (ANode child : node.getChildren()) {
//						if (child.getId().equals(current)) {
//							if (child instanceof ACompositeNode) {
//								node = (ACompositeNode) child;
//							}
//							found = true;
//							break;
//						}
//					}
//					if (found) {
//						continue;
//					} else {
//						if (tokenizer.hasMoreElements()) {
//							// not a leaf, create a composite statenode
//							String nodeName = current;
//							if (current.equals("particle")) {
//								nodeName = current + "[" + particleID
//										+ "]";
//							}
//
//							CompositeNode newNode = new CompositeNode(
//									nodeName);
//							newNode.setId(nodeName);
//
//							boolean addNewNode = containsNode(node,
//									newNode.getId());
//
//							if (addNewNode) {
//								node.addChild(newNode);
//								node = newNode;
//							} else {
//								node = getNode(node, newNode.getId());
//							}
//						} else {
//							// it's a leaf node
//							VariableNode newNode = new VariableNode(
//									current);
//							newNode.setId(current);
//
//							FloatValue val = null;
//
//							// get value
//							switch (current) {
//							case "x":
//								val = ValuesFactory
//								.getFloatValue(_positionPtr
//										.get(ID));
//								break;
//							case "y":
//								val = ValuesFactory
//								.getFloatValue(_positionPtr
//										.get(ID + 1));
//								break;
//							case "z":
//								val = ValuesFactory
//								.getFloatValue(_positionPtr
//										.get(ID + 2));
//								break;
//							}
//
//							PhysicalQuantity q = new PhysicalQuantity();
//							q.setValue(val);
//							newNode.addPhysicalQuantity(q);
//
//							node.addChild(newNode);
//						}
//					}
//				}
//			}
//
//			watchTree.setModified(true);
//			// unmap watchable buffers
//			_velocity.unmap(_queue, _positionPtr);
//		}
		
		return modified;
	}
}
