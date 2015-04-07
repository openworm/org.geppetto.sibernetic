package org.geppetto.model.sph.services;

import org.geppetto.core.common.GeppettoInitializationException;
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
import org.geppetto.model.sph.x.SPHModelX;
import org.geppetto.model.sph.x.SPHParticleX;

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
	
	
	public void populateVisualTree(IModel model, AspectSubTreeNode visualTree){
		CompositeNode _liquidModel = new CompositeNode("LIQUID_"
				+ model.getId());
		CompositeNode _boundaryModel = new CompositeNode("BOUNDARY_"
				+ model.getId());
		CompositeNode _elasticModel = new CompositeNode("ELASTIC_"
				+ model.getId());

		int i = 0;
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
