package org.geppetto.sibernetic;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geppetto.core.beans.SimulatorConfig;
import org.geppetto.core.common.GeppettoExecutionException;
import org.geppetto.core.common.GeppettoInitializationException;
import org.geppetto.core.data.model.IAspectConfiguration;
import org.geppetto.core.model.GeppettoModelAccess;
import org.geppetto.core.services.registry.ServicesRegistry;
import org.geppetto.core.simulation.ISimulatorCallbackListener;
import org.geppetto.core.simulator.AExternalProcessSimulator;
import org.geppetto.core.simulator.ExternalSimulatorConfig;
import org.geppetto.model.DomainModel;
import org.geppetto.model.ExperimentState;
import org.geppetto.model.ModelFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Wrapper class for sibernetic
 * 
 * @author Sergey Khayrulin (skhayrulin@openworm.org)
 *
 */

@Service
public class SiberneticWrapperService extends AExternalProcessSimulator
{

	protected File filePath = null;

	private String gResultFolder = "gresult";

	private String gResultFileName = "gresult"; // I think generation of file name should be dynamic
												// For different instance for example take Id of service or make static member
												// and add it in the end of file name
	
	private float simulationLenght = 0;
	
	private static Log logger = LogFactory.getLog(SiberneticWrapperService.class);

	private static String OS = System.getProperty("os.name").toLowerCase();

	@Autowired
	private SimulatorConfig siberneticSimulatorConfig;
	
	@Autowired
	private ExternalSimulatorConfig siberneticExternalSimulatorConfig;
	
	@Override
	public void initialize(DomainModel model, IAspectConfiguration aspectConfiguration, ExperimentState experimentState, ISimulatorCallbackListener listener, GeppettoModelAccess modelAccess) throws GeppettoInitializationException, GeppettoExecutionException
	{
		super.initialize(model, aspectConfiguration, experimentState, listener, geppettoModelAccess);


		/*MOdel file name*/

		this.originalFileName = (String) model.getDomainModel();//"/home/serg/git/openworm/geppetto/org.geppetto.simulator.sph/src/test/resources/demo1"
		this.createCommands(this.originalFileName, aspectConfiguration);
	}

	private boolean isWindows()
	{
		return (OS.indexOf("win") >= 0);
	}

	/**
	 * Creates command to be executed by an external process
	 * 
	 * @param modelFileName
	 */
	public void createCommands(String modelFileName, IAspectConfiguration aspectConfiguration)
	{
		filePath = new File(modelFileName);
		logger.info("Creating command to run " + modelFileName);
		directoryToExecuteFrom = getSimulatorPath();//filePath.getParentFile().getAbsolutePath();
		outputFolder = directoryToExecuteFrom + gResultFolder; 
		if(isWindows())
		{
			// commands = new String[] { getSimulatorPath() + "mkdir.exe " + gResultFolder,
			// getSimulatorPath() + ".exe" + " -f " + siberneticModelConfig }; // without this " -f " + siberneticModelConfig it will run default demo1 rom configuration folder
		}
		else
		{
			commands = new String[] { "mkdir gresult", 
					getSimulatorPath() + "Release/Sibernetic" + " -f " + modelFileName 
					+ " timelimit=" + aspectConfiguration.getSimulatorConfiguration().getLength() 
					+ " timestep=" + aspectConfiguration.getSimulatorConfiguration().getTimestep() + " -gmode logstep=100"}; // option logstep is needed to indicate how often loging is needed by default it is 10
		
		}

		logger.info("Command to Execute: " + commands + " ...");
		logger.info("From directory : " + directoryToExecuteFrom);

	}

	@Override
	public String getName()
	{
		return this.siberneticSimulatorConfig.getSimulatorName();
	}

	@Override
	public String getId()
	{
		return this.siberneticSimulatorConfig.getSimulatorID();
	}

	@Override
	public String getSimulatorPath()
	{
		return this.siberneticExternalSimulatorConfig.getSimulatorPath();
	}
	
	@Override
	public void registerGeppettoService() throws Exception
	{
		List<ModelFormat> modelFormats = new ArrayList<ModelFormat>(Arrays.asList(ServicesRegistry.registerModelFormat("SIBERNETIC")));
		ServicesRegistry.registerSimulatorService(this, modelFormats);
	}
	
	@Override
	public void processDone(String[] processCommand) throws GeppettoExecutionException
	{

	}

	@Override
	public void processFailed(String message, Exception e)
	{
		// TODO Auto-generated method stub
		
	}
	
}
