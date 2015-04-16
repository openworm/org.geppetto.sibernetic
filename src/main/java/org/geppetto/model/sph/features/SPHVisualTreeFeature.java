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

import org.geppetto.core.features.IVisualTreeFeature;
import org.geppetto.core.model.IModel;
import org.geppetto.core.model.ModelInterpreterException;
import org.geppetto.core.model.runtime.AspectNode;
import org.geppetto.core.model.runtime.AspectSubTreeNode;
import org.geppetto.core.model.runtime.CompositeNode;
import org.geppetto.core.model.runtime.EntityNode;
import org.geppetto.core.model.runtime.ParticleNode;
import org.geppetto.core.model.runtime.AspectSubTreeNode.AspectTreeType;
import org.geppetto.core.model.values.FloatValue;
import org.geppetto.core.model.values.ValuesFactory;
import org.geppetto.core.services.GeppettoFeature;
import org.geppetto.core.visualisation.model.Point;
import org.geppetto.model.sph.SPHParticle;
import org.geppetto.model.sph.common.SPHConstants;
import org.geppetto.model.sph.services.SPHModelInterpreterService;
import org.geppetto.model.sph.x.SPHModelX;
import org.geppetto.model.sph.x.SPHParticleX;

/**
 * Visual tree feature for populating visualization tree
 * @author Jesus Martinez (jesus@metacell.us)
 *
 */
public class SPHVisualTreeFeature implements IVisualTreeFeature{
	private GeppettoFeature type = GeppettoFeature.VISUAL_TREE_FEATURE;
	private SPHModelX _sphModelX;

	public SPHVisualTreeFeature(SPHModelX sphModelX) {
		this._sphModelX = sphModelX;
	}

	@Override
	public GeppettoFeature getType() {
		return type ;
	}
	
	@Override
	public boolean populateVisualTree(AspectNode aspectNode)
			throws ModelInterpreterException {
		AspectSubTreeNode visualizationTree = (AspectSubTreeNode) aspectNode
				.getSubTree(AspectTreeType.VISUALIZATION_TREE);

		this.populateVisualTree(aspectNode.getModel(),visualizationTree);
		visualizationTree.setModified(true);
		aspectNode.setModified(true);
		((EntityNode) aspectNode.getParentEntity())
				.updateParentEntitiesFlags(true);
		return true;
	}
	
	
	/**
	 * Aid method for populating visual tree
	 * 
	 * @param model
	 * @param visualTree
	 */
	public void populateVisualTree(IModel model, AspectSubTreeNode visualTree){
		CompositeNode _liquidModel = new CompositeNode("LIQUID_"
				+ model.getId());
		CompositeNode _boundaryModel = new CompositeNode("BOUNDARY_"
				+ model.getId());
		CompositeNode _elasticModel = new CompositeNode("ELASTIC_"
				+ model.getId());

		int i = -1;
		for(SPHParticle p : _sphModelX.getParticles())
		{
			((SPHParticleX) p).setId(_sphModelX.getId() + i++);
			
			String particleId = SPHModelInterpreterService.getParticleId(i);
			FloatValue xV = ValuesFactory
					.getFloatValue(p.getPositionVector().getX());
			FloatValue yV = ValuesFactory.getFloatValue(p.getPositionVector().getY());
			FloatValue zV = ValuesFactory.getFloatValue(p.getPositionVector().getZ());
			FloatValue pV = ValuesFactory.getFloatValue(p.getPositionVector().getP());

			if (pV.getAsFloat() != SPHConstants.BOUNDARY_TYPE) {
				// don't need to create a state for the boundary particles,
				// they don't move.
				ParticleNode particle = new ParticleNode(particleId);
				Point pos = new Point();
				pos.setX(xV.getAsDouble());
				pos.setY(yV.getAsDouble());
				pos.setZ(zV.getAsDouble());
				particle.setPosition(pos);
				particle.setParticleKind(pV.getAsFloat());
				particle.setId(particleId);

				if (pV.getAsFloat() == (SPHConstants.LIQUID_TYPE)) {
					_liquidModel.addChild(particle);
				} else if (pV.getAsFloat() == (SPHConstants.ELASTIC_TYPE)) {
					_elasticModel.addChild(particle);
				} else if (pV.getAsFloat() == (SPHConstants.BOUNDARY_TYPE)) {
					_boundaryModel.addChild(particle);
				}
			}
		}

		visualTree.addChild(_liquidModel);
		visualTree.addChild(_elasticModel);
		visualTree.addChild(_boundaryModel);
	}


}
