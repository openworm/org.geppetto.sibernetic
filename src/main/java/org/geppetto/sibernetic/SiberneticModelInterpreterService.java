package org.geppetto.sibernetic;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geppetto.core.beans.ModelInterpreterConfig;
import org.geppetto.core.beans.SimulatorConfig;
import org.geppetto.core.common.GeppettoInitializationException;
import org.geppetto.core.data.model.IAspectConfiguration;
import org.geppetto.core.model.AModelInterpreter;
import org.geppetto.core.model.GeppettoModelAccess;
import org.geppetto.core.model.ModelInterpreterException;
import org.geppetto.core.services.registry.ServicesRegistry;
import org.geppetto.core.simulator.ExternalSimulatorConfig;
import org.geppetto.core.utilities.URLReader;
import org.geppetto.model.GeppettoLibrary;
import org.geppetto.model.ModelFormat;
import org.geppetto.model.types.Type;
import org.geppetto.model.values.Pointer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author matteocantarelli
 * 
 */
@Service
public class SiberneticModelInterpreterService extends AModelInterpreter
{

	@Autowired
	private ModelInterpreterConfig siberneticModelInterpreterConfig;

	@Autowired
	private SimulatorConfig siberneticSimulatorConfig;

	@Autowired
	private ExternalSimulatorConfig siberneticExternalSimulatorConfig;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geppetto.core.model.IModelInterpreter#getName()
	 */
	@Override
	public String getName()
	{
		return this.siberneticModelInterpreterConfig.getModelInterpreterName();
	}

	@Override
	public void registerGeppettoService()
	{
		List<ModelFormat> modelFormats = new ArrayList<ModelFormat>(Arrays.asList(ServicesRegistry.registerModelFormat("Sibernetic")));
		ServicesRegistry.registerModelInterpreterService(this, modelFormats);
	}

	@Override
	public List<ModelFormat> getSupportedOutputs(Pointer pointer) throws ModelInterpreterException
	{
		List<ModelFormat> supportedOutputs = super.getSupportedOutputs(pointer);
		supportedOutputs.add(ServicesRegistry.getModelFormat("Sibernetic"));

		return supportedOutputs;
	}

	public String getSiberneticPath()
	{
		return this.siberneticExternalSimulatorConfig.getSimulatorPath();
	}

	@Override
	public Type importType(URL url, String typeName, GeppettoLibrary library, GeppettoModelAccess modelAccess) throws ModelInterpreterException
	{

		dependentModels.clear();
		GeppettoLibrary siberneticLibrary;
		try
		{
			siberneticLibrary = SiberneticLibraryLoader.getSiberneticLibrary();

			modelAccess.addLibrary(siberneticLibrary);
			// url in this case is just an id that points to a file that has to exist inside Sibernetic
			String modelToLoad = getSiberneticPath() + File.pathSeparator + "configuration" + File.pathSeparator + url;

			String modelConfiguration = URLReader.readStringFromURL(new URL(modelToLoad));
			SiberneticModelConverter converter = new SiberneticModelConverter(siberneticLibrary, library, modelAccess);
			Type siberneticType = converter.toGeppettoType(modelConfiguration);
			return siberneticType;
		}
		catch(GeppettoInitializationException e)
		{
			throw new ModelInterpreterException(e);
		}
		catch(MalformedURLException e)
		{
			throw new ModelInterpreterException(e);
		}
		catch(IOException e)
		{
			throw new ModelInterpreterException(e);
		}

	}

	@Override
	public File downloadModel(Pointer pointer, ModelFormat format, IAspectConfiguration aspectConfiguration) throws ModelInterpreterException
	{
		throw new ModelInterpreterException("Download model not implemented for Sibernetic model interpreter");
	}

}
