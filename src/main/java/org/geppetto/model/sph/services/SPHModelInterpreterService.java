/**
 * 
 */
package org.geppetto.model.sph.services;

import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geppetto.core.model.IModel;
import org.geppetto.core.model.IModelInterpreter;
import org.geppetto.core.model.StateSet;
import org.geppetto.core.model.values.FloatValue;
import org.geppetto.core.visualisation.model.AGeometry;
import org.geppetto.core.visualisation.model.Entity;
import org.geppetto.core.visualisation.model.Particle;
import org.geppetto.core.visualisation.model.Point;
import org.geppetto.core.visualisation.model.Scene;
import org.geppetto.model.sph.SPHModel;
import org.geppetto.model.sph.SPHParticle;
import org.geppetto.model.sph.common.SPHConstants;
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
	public IModel readModel(URL url)
	{
		JAXBContext context;
		SPHModelX sphModelX = null;
		try
		{
			context = JAXBContext.newInstance(SPHModel.class);
			Unmarshaller um = context.createUnmarshaller();
			SPHModel sphModel = (SPHModel) um.unmarshal(url);
			sphModelX = new SPHModelX(sphModel);
			int i = 0;
			for(SPHParticle p : sphModelX.getParticles())
			{
				((SPHParticleX) p).setId(sphModelX.getId() + i++);
			}
		}
		catch(JAXBException e1)
		{
			e1.printStackTrace();
		}
		return sphModelX;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geppetto.core.model.IModelInterpreter#getSceneFromModel(java.util.List)
	 */
	public Scene getSceneFromModel(IModel model, StateSet stateSet)
	{
		long starttime = System.currentTimeMillis();
		Scene scene = new Scene();

		Entity liquidEntity = new Entity();
		Entity boundaryEntity = new Entity();
		Entity elasticEntity = new Entity();

		scene.getEntities().add(liquidEntity);
		scene.getEntities().add(boundaryEntity);
		scene.getEntities().add(elasticEntity);

		SPHModelX sphModel = (SPHModelX) model;
		liquidEntity.setId("LIQUID_" + sphModel.getId());
		boundaryEntity.setId("BOUNDARY_" + sphModel.getId());
		elasticEntity.setId("ELASTIC_" + sphModel.getId());

		for(int i=0;i<sphModel.getParticles().size();i++)
		{
			Float type = ((FloatValue)stateSet.getLastValueFor(getPropertyPath(i,"pos","p"))).getAsFloat();
			if(type.equals(SPHConstants.LIQUID_TYPE))
			{
				liquidEntity.getGeometries().add(getParticleGeometry(i,stateSet));
			}
			else if(type.equals(SPHConstants.ELASTIC_TYPE))
			{
				elasticEntity.getGeometries().add(getParticleGeometry(i,stateSet));
			}
			else if(type.equals(SPHConstants.BOUNDARY_TYPE))
			{
				boundaryEntity.getGeometries().add(getParticleGeometry(i,stateSet));
			}
		}

		logger.info("Model to scene conversion end, took: " + (System.currentTimeMillis() - starttime) + "ms");
		return scene;
	}

	/**
	 * @param sphp
	 * @return
	 */
	private AGeometry getParticleGeometry(int i, StateSet stateSet)
	{
		Particle p = new Particle();
		Point point = new Point();
		//TODO Workaround: Implement using visitors over values
		point.setX(((FloatValue)stateSet.getLastValueFor(getPropertyPath(i,"pos","x"))).getAsDouble());
		point.setY(((FloatValue)stateSet.getLastValueFor(getPropertyPath(i,"pos","y"))).getAsDouble());
		point.setZ(((FloatValue)stateSet.getLastValueFor(getPropertyPath(i,"pos","z"))).getAsDouble());
		p.setPosition(point);
		p.setId(getParticleId(i));
		return p;
	}

	public static String getPropertyPath(int index, String vector, String property)
	{
		return getParticleId(index)+"." + vector + "." + property;
	}
	
	public static String getParticleId(int index)
	{
		return "p[" + index + "]";
	}
	
}
