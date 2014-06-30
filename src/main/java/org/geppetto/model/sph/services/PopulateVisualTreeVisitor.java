/*******************************************************************************
 * The MIT License (MIT)
 * 
 * Copyright (c) 2011, 2013 OpenWorm.
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
package org.geppetto.model.sph.services;

import org.geppetto.core.model.state.ACompositeStateNode;
import org.geppetto.core.model.state.AspectNode;
import org.geppetto.core.model.state.EntityNode;
import org.geppetto.core.model.state.StateVariableNode;
import org.geppetto.core.model.state.visitors.DefaultStateVisitor;
import org.geppetto.core.model.values.FloatValue;
import org.geppetto.core.visualisation.model.Particle;
import org.geppetto.core.visualisation.model.Point;
import org.geppetto.core.visualisation.model.VisualModel;
import org.geppetto.model.sph.common.SPHConstants;

/**
 * @author matteocantarelli
 *
 */
public class PopulateVisualTreeVisitor extends DefaultStateVisitor
{

	private VisualModel _liquidModel = new VisualModel();
	private VisualModel _boundaryModel = new VisualModel();
	private VisualModel _elasticModel = new VisualModel();
	private Float _particleKind;
	private Particle _newParticle;
	private Point _newPoint;
	private AspectNode _aspectNode;

	/**
	 * @param EntityNode
	 * @param modelId
	 */
	public PopulateVisualTreeVisitor(AspectNode aspectNode, String modelId)
	{
		super();
		this._aspectNode = aspectNode;
		this._aspectNode.getVisualModel().add(_liquidModel);
		this._aspectNode.getVisualModel().add(_boundaryModel);
		this._aspectNode.getVisualModel().add(_elasticModel);

		_liquidModel.setId("LIQUID_" + modelId);
		_boundaryModel.setId("BOUNDARY_" + modelId);
		_elasticModel.setId("ELASTIC_" + modelId);
	}
	
	/* (non-Javadoc)
	 * @see org.geppetto.core.model.state.visitors.DefaultStateVisitor#inCompositeStateNode(org.geppetto.core.model.state.CompositeStateNode)
	 */
	@Override
	public boolean inCompositeStateNode(ACompositeStateNode node)
	{
		if(node.getName().startsWith("p["))
		{
			_newParticle=new Particle();
			_newParticle.setId(node.getName());
			_newPoint=new Point();
			_newParticle.setPosition(_newPoint);
		}
		return super.inCompositeStateNode(node);
	}

	/* (non-Javadoc)
	 * @see org.geppetto.core.model.state.visitors.DefaultStateVisitor#outCompositeStateNode(org.geppetto.core.model.state.CompositeStateNode)
	 */
	@Override
	public boolean outCompositeStateNode(ACompositeStateNode node)
	{
		if(node.getName().startsWith("p["))
		{
			if(_particleKind.equals(SPHConstants.LIQUID_TYPE))
			{
				_liquidModel.getObjects().add(_newParticle);
			}
			else if(_particleKind.equals(SPHConstants.ELASTIC_TYPE))
			{
				_elasticModel.getObjects().add(_newParticle);
			}
			else if(_particleKind.equals(SPHConstants.BOUNDARY_TYPE))
			{
				_boundaryModel.getObjects().add(_newParticle);
			}
			_newParticle=null;
			_newPoint=null;
		}
		return super.outCompositeStateNode(node);
	}

	/* (non-Javadoc)
	 * @see org.geppetto.core.model.state.visitors.DefaultStateVisitor#visitSimpleStateNode(org.geppetto.core.model.state.SimpleStateNode)
	 */
	@Override
	public boolean visitStateVariableNode(StateVariableNode node)
	{
		if(node.getName()=="x")
		{
			_newPoint.setX(((FloatValue)node.consumeFirstValue()).getAsDouble());
		}
		else if(node.getName()=="y")
		{
			_newPoint.setY(((FloatValue)node.consumeFirstValue()).getAsDouble());
		}
		else if(node.getName()=="z")
		{
			_newPoint.setZ(((FloatValue)node.consumeFirstValue()).getAsDouble());
		}
		else if(node.getName()=="p")
		{
			_particleKind=((FloatValue)node.consumeFirstValue()).getAsFloat();
		}
		return super.visitStateVariableNode(node);
	}


}
