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
import org.openworm.simulationengine.model.sph.SPHModel;
import org.openworm.simulationengine.model.sph.x.SPHModelX;
import org.springframework.stereotype.Service;

/**
 * @author matteocantarelli
 *
 */
@Service
public class SPHModelInterpreter implements IModelInterpreter {

	/* (non-Javadoc)
	 * @see org.openworm.simulationengine.core.model.IModelProvider#readModel(java.lang.String)
	 */
	public List<IModel> readModel(URL url) 
	{
		JAXBContext context;
		
		List<IModel> sphModels = new ArrayList<IModel>();
		try 
		{
			context = JAXBContext.newInstance(SPHModel.class);
			Unmarshaller um = context.createUnmarshaller();
			SPHModelX sphModel = (SPHModelX) um.unmarshal(url);
			sphModels.add(sphModel);
		} 
		catch (JAXBException e1) 
		{
			e1.printStackTrace();
		}
		return sphModels;
	}

}
