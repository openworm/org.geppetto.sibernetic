/**
 * 
 */
package org.openworm.simulationengine.model.sph.services;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.openworm.simulationengine.core.model.IModel;
import org.openworm.simulationengine.core.model.IModelInterpreter;
import org.openworm.simulationengine.core.visualisation.model.AGeometry;
import org.openworm.simulationengine.core.visualisation.model.Entity;
import org.openworm.simulationengine.core.visualisation.model.Particle;
import org.openworm.simulationengine.core.visualisation.model.Point;
import org.openworm.simulationengine.core.visualisation.model.Scene;
import org.openworm.simulationengine.model.sph.SPHModel;
import org.openworm.simulationengine.model.sph.SPHParticle;
import org.openworm.simulationengine.model.sph.x.SPHFactory;
import org.openworm.simulationengine.model.sph.x.SPHModelX;
import org.openworm.simulationengine.model.sph.x.SPHParticleX;
import org.springframework.stereotype.Service;

/**
 * @author matteocantarelli
 * 
 */
@Service
public class SPHModelInterpreterService implements IModelInterpreter
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openworm.simulationengine.core.model.IModelProvider#readModel(java
	 * .lang.String)
	 */
	public List<IModel> readModel(URL url)
	{
		JAXBContext context;

		List<IModel> sphModels = new ArrayList<IModel>();
		try
		{
			context = JAXBContext.newInstance(SPHModel.class);
			Unmarshaller um = context.createUnmarshaller();
			SPHModel sphModel = (SPHModel) um.unmarshal(url);
			SPHModelX sphModelX=new SPHModelX(sphModel);
			sphModels.add(sphModelX);
			int i=0;
			for(SPHParticle p:sphModelX.getParticles())
			{
				((SPHParticleX)p).setId(sphModelX.getId()+i++);
			}
		}
		catch (JAXBException e1)
		{
			e1.printStackTrace();
		}
		return sphModels;
	}

	/* (non-Javadoc)
	 * @see org.openworm.simulationengine.core.model.IModelInterpreter#getSceneFromModel(java.util.List)
	 */
	public Scene getSceneFromModel(List<IModel> model)
	{
		Scene scene = new Scene();
		for (IModel m : model)
		{
			Entity e = new Entity();
			scene.getEntities().add(e);
			SPHModelX sphModel = (SPHModelX) m;
			e.setId(sphModel.getId());
			for (SPHParticle p : sphModel.getParticles())
			{
				e.getGeometries().add(getParticleGeometry(p));
			}
		}
		return scene;
	}

	/**
	 * @param sphp
	 * @return
	 */
	private AGeometry getParticleGeometry(SPHParticle sphp)
	{
		Particle p = new Particle();
		Point point=new Point();
		point.setX((double) sphp.getPositionVector().getX());
		point.setY((double) sphp.getPositionVector().getY());
		point.setZ((double) sphp.getPositionVector().getZ());
		p.setPosition(point);
		p.setId(((SPHParticleX)sphp).getId());
		return p;
	}

}
