package org.geppetto.sibernetic;

import java.io.File;
import java.util.Map;

import javax.annotation.Resource;

import org.geppetto.core.common.GeppettoExecutionException;
import org.geppetto.core.common.GeppettoInitializationException;
import org.geppetto.core.data.model.IAspectConfiguration;
import org.geppetto.core.data.model.ResultsFormat;
import org.geppetto.core.simulation.ISimulatorCallbackListener;
import org.junit.Before;
import org.junit.Test;

/**
 * @author matteocantarelli
 *
 */
public class SiberneticWrapperServiceTest implements ISimulatorCallbackListener
{

		
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
	}

	@Test
	public void testSibernetic() throws GeppettoInitializationException, GeppettoExecutionException, InterruptedException
	{
//		SiberneticWrapperService sibernetic = new SiberneticWrapperService();
//		DomainModel siberneticModel=GeppettoFactory.eINSTANCE.createDomainModel();
//		siberneticModel.setDomainModel("/home/serg/Documents/git/openworm/geppetto/org.geppetto.simulator.sph/src/test/resources/demo1");//"/home/serg/git/openworm/Smoothed-Particle-Hydrodynamics/Release/");
//		float timestep=5.0e-06f;
//		float length=0.0001f;
//		LocalSimulatorConfiguration simulatorConfiguration=new LocalSimulatorConfiguration(0l, "sibernetic", "", timestep, length, null);
//
//		IAspectConfiguration config=new LocalAspectConfiguration(0l, "siberneticModel", null, null, simulatorConfiguration);
//		sibernetic.initialize(siberneticModel,config,null,this,null);
//		sibernetic.simulate();
//		Thread.sleep(2000);
		
	}



	@Override
	public void endOfSteps(IAspectConfiguration arg0, Map<File, ResultsFormat> arg1) throws GeppettoExecutionException
	{
		System.out.println("The simulation was over");
	}

	@Override
	public void externalProcessFailed(String message, Exception e)
	{
		// TODO Auto-generated method stub
		
	}

}
