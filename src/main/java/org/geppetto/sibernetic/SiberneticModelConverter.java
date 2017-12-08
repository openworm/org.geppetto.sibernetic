/**
 * 
 */
package org.geppetto.sibernetic;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.geppetto.core.model.GeppettoModelAccess;
import org.geppetto.model.GeppettoLibrary;
import org.geppetto.model.types.CompositeType;
import org.geppetto.model.types.CompositeVisualType;
import org.geppetto.model.types.Type;
import org.geppetto.model.types.TypesFactory;
import org.geppetto.model.types.TypesPackage;
import org.geppetto.model.util.GeppettoVisitingException;
import org.geppetto.model.values.Particles;
import org.geppetto.model.values.Point;
import org.geppetto.model.values.Quantity;
import org.geppetto.model.values.ValuesFactory;
import org.geppetto.model.variables.Variable;
import org.geppetto.model.variables.VariablesFactory;

/**
 * This class reads a Sibernetic configuration file and creates a Geppetto model
 * 
 * @author matteocantarelli
 *
 */
public class SiberneticModelConverter
{

	private String POSITION_TAG = "[position]";
	private String VELOCITY_TAG = "[velocity]";
	private String CONNECTION_TAG = "[connection]";
	private String MEMBRANES_TAG = "[membranes]";

	private CompositeType model = TypesFactory.eINSTANCE.createCompositeType();

	private GeppettoLibrary siberneticLibrary;
	private GeppettoLibrary library;
	private GeppettoModelAccess modelAccess;

	private Map<String, Particles> particlesMap = null;

	/**
	 * @param siberneticLibrary
	 * @param library
	 * @param modelAccess
	 */
	public SiberneticModelConverter(GeppettoLibrary siberneticLibrary, GeppettoLibrary library, GeppettoModelAccess modelAccess)
	{
		this.siberneticLibrary = siberneticLibrary;
		this.particlesMap = new HashMap<String, Particles>();
		this.library = library;
		this.modelAccess = modelAccess;
		this.model.setId("worm");
		this.model.setName("Worm");
	}

	/**
	 * @param modelConfiguration
	 * @return
	 * @throws GeppettoVisitingException 
	 */
	public Type toGeppettoType(String modelConfiguration) throws GeppettoVisitingException
	{


		String connections = modelConfiguration.substring(modelConfiguration.indexOf(CONNECTION_TAG) + CONNECTION_TAG.length(), modelConfiguration.indexOf(MEMBRANES_TAG));
		String positions = modelConfiguration.substring(modelConfiguration.indexOf(POSITION_TAG) + POSITION_TAG.length(), modelConfiguration.indexOf(VELOCITY_TAG));

		int numberOfMuscles = 0;
		Map<String,String> particlesToMuscleBundles = new HashMap<String,String>();
		//let's first iterate on all the connections to find all the particles that will belong to the same muscle cell
		StringTokenizer cTokenizer = new StringTokenizer(connections, "\r\n");
		while(cTokenizer.hasMoreTokens())
		{
			String connection = cTokenizer.nextToken();
			StringTokenizer connectionTokenizer = new StringTokenizer(connection);
			float particleFloat=Float.parseFloat(connectionTokenizer.nextToken());
			String particleId = ((Integer)((int)particleFloat)).toString();
			connectionTokenizer.nextToken(); //we don't care about the second parameter, the eleastic spring
			float muscleBundleFloat=Float.parseFloat(connectionTokenizer.nextToken());
			int muscleBundleInt = (int) muscleBundleFloat;
			numberOfMuscles = Math.max(numberOfMuscles, muscleBundleInt);
			String muscleBundle = ((Integer)(muscleBundleInt)).toString();
			if(!muscleBundle.equals("0")){
				particlesToMuscleBundles.put(particleId,muscleBundle);
			}
		}
		
		Integer currentParticle=-1;
		// everything else which is not position we don't care
		StringTokenizer tokenizer = new StringTokenizer(positions, "\r\n");
		while(tokenizer.hasMoreTokens())
		{
			String position = tokenizer.nextToken();
			currentParticle++;
			Point particle = ValuesFactory.eINSTANCE.createPoint();
			StringTokenizer positionTokenizer = new StringTokenizer(position);
			particle.setX(Double.parseDouble(positionTokenizer.nextToken()));
			particle.setY(Double.parseDouble(positionTokenizer.nextToken()));
			particle.setZ(Double.parseDouble(positionTokenizer.nextToken()));
			//we are grouping by the type of particle

			float typeFloat=Float.parseFloat(positionTokenizer.nextToken());
			String type = ((Integer)((int)typeFloat)).toString();
			
			String p=currentParticle.toString();
			if(particlesToMuscleBundles.containsKey(p)){
				String muscle=particlesToMuscleBundles.get(p);
				Particles container=getContainer(muscle);
				if(container!=null)
				{
					container.getParticles().add(particle);
				}				
			}
			else{
				Particles container=getContainer(type);
				if(container != null)
				{
					container.getParticles().add(particle);
				}
	
			}
		}
		
		//Let's add the muscle activation signals
		for (int i=0;i<=numberOfMuscles;i++){
			Variable muscle_activation = VariablesFactory.eINSTANCE.createVariable();
			muscle_activation.setId("muscle_activation_" + i);
			Quantity initial = ValuesFactory.eINSTANCE.createQuantity();
			initial.setValue(0d);
			muscle_activation.getInitialValues().put(modelAccess.getType(TypesPackage.Literals.STATE_VARIABLE_TYPE), initial);
			muscle_activation.getTypes().add(modelAccess.getType(TypesPackage.Literals.STATE_VARIABLE_TYPE));
			this.model.getVariables().add(muscle_activation);
		}
		return this.model;
	}

	private Particles getContainer(String type)
	{
		String typeNoDots = type.replace(".", "_");
		switch(typeNoDots)
		{
			case "0":
				return null;
			default:
				if(!particlesMap.containsKey(typeNoDots))
				{
					particlesMap.put(typeNoDots, ValuesFactory.eINSTANCE.createParticles());
					Variable matter = VariablesFactory.eINSTANCE.createVariable();
					matter.setId("matter_" + typeNoDots);
					CompositeVisualType matterType = TypesFactory.eINSTANCE.createCompositeVisualType();
					siberneticLibrary.getTypes().add(matterType);
					matterType.setId("matter_" + typeNoDots + "_Type");
					Variable particles = VariablesFactory.eINSTANCE.createVariable();
					particles.setId("particles");
					matterType.getVariables().add(particles);
					matter.getTypes().add(matterType);
					particles.getInitialValues().put(matterType, particlesMap.get(typeNoDots));
					model.getVariables().add(matter);
				}
				return particlesMap.get(typeNoDots);
		}
	}

}
