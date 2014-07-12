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

import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geppetto.core.model.IModel;
import org.geppetto.core.model.IModelInterpreter;
import org.geppetto.core.model.ModelInterpreterException;
import org.geppetto.core.model.runtime.AspectNode;
import org.geppetto.core.model.runtime.AspectSubTreeNode;
import org.geppetto.core.model.runtime.AspectSubTreeNode.ASPECTTREE;
import org.geppetto.model.sph.SPHModel;
import org.geppetto.model.sph.SPHParticle;
import org.geppetto.model.sph.x.SPHModelX;
import org.geppetto.model.sph.x.SPHParticleX;
import org.springframework.stereotype.Service;

/**
 * @author matteocantarelli
 * 
 */
@Service
public class SPHModelInterpreterService implements IModelInterpreter
{

	private static Log logger = LogFactory.getLog(SPHModelInterpreterService.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geppetto.core.model.IModelProvider#readModel(java .lang.String)
	 */
	public IModel readModel(URL url, List<URL> recordings, String instancePath) throws ModelInterpreterException
	{
		JAXBContext context;
		SPHModelX sphModelX = null;
		try
		{
			context = JAXBContext.newInstance(SPHModel.class);
			Unmarshaller um = context.createUnmarshaller();
			SPHModel sphModel = (SPHModel) um.unmarshal(url);
			sphModelX = new SPHModelX(sphModel);
			sphModelX.setInstancePath(instancePath);
			int i = 0;
			for(SPHParticle p : sphModelX.getParticles())
			{
				((SPHParticleX) p).setId(sphModelX.getId() + i++);
			}
		}
		catch(JAXBException e1)
		{
			logger.error("Unable to read model with url: " + url);
			throw new ModelInterpreterException(e1);
		}
		return sphModelX;
	}



	public static String getPropertyPath(int index, String vector, String property)
	{
		return getParticleId(index)+"." + vector + "." + property;
	}
	
	public static String getParticleId(int index)
	{
		return "p[" + index + "]";
	}

	@Override
	public boolean populateVisualTree(AspectNode aspectNode) throws ModelInterpreterException {
		AspectSubTreeNode visualizationTree = (AspectSubTreeNode) aspectNode.getSubTree(ASPECTTREE.VISUALIZATION_TREE);
		
		PopulateVisualTreeVisitor createSceneVisitor=new PopulateVisualTreeVisitor(visualizationTree,aspectNode.getModel().getId());
		visualizationTree.apply(createSceneVisitor);
		
		return true;
	}

	@Override
	public boolean populateModelTree(AspectNode aspectNode) throws ModelInterpreterException {
		AspectSubTreeNode modelTree = (AspectSubTreeNode) aspectNode.getSubTree(ASPECTTREE.MODEL_TREE);
		
		PopulateModelTreeVisitor createSceneVisitor=new PopulateModelTreeVisitor(aspectNode,aspectNode.getId());
		modelTree.apply(createSceneVisitor);
		
		return true;
	}


	@Override
	public boolean populateRuntimeTree(AspectNode aspectNode) {
		AspectSubTreeNode runTimeTree = new AspectSubTreeNode();
		
		AspectSubTreeNode modelTree = (AspectSubTreeNode) aspectNode.getSubTree(ASPECTTREE.MODEL_TREE);
		AspectSubTreeNode visualizationTree = (AspectSubTreeNode) aspectNode.getSubTree(ASPECTTREE.VISUALIZATION_TREE);
		AspectSubTreeNode simulationTree = (AspectSubTreeNode) aspectNode.getSubTree(ASPECTTREE.WATCH_TREE);
		
		return true;
	}
	
}
