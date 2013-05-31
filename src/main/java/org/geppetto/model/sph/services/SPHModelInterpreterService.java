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
import org.geppetto.core.model.state.StateTreeRoot;
import org.geppetto.core.visualisation.model.Scene;
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
	public Scene getSceneFromModel(IModel model, StateTreeRoot stateTree)
	{
		long starttime = System.currentTimeMillis();
		logger.info("SPH Model to scene conversion starting...");
		CreateSPHSceneVisitor createSceneVisitor=new CreateSPHSceneVisitor(model.getId());
		stateTree.apply(createSceneVisitor);
		logger.info("Model to scene conversion end, took: " + (System.currentTimeMillis() - starttime) + "ms");
		return createSceneVisitor.getScene();
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
