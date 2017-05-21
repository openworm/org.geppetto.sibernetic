/**
 * 
 */
package org.geppetto.sibernetic;

import java.util.StringTokenizer;

import org.geppetto.core.model.GeppettoModelAccess;
import org.geppetto.model.GeppettoLibrary;
import org.geppetto.model.types.CompositeType;
import org.geppetto.model.types.Type;
import org.geppetto.model.types.TypesFactory;
import org.geppetto.model.values.ArrayValue;
import org.geppetto.model.values.Composite;
import org.geppetto.model.values.Particles;
import org.geppetto.model.values.Point;
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

	private CompositeType model = TypesFactory.eINSTANCE.createCompositeType();

	private GeppettoLibrary siberneticLibrary;
	private GeppettoLibrary library;
	private GeppettoModelAccess modelAccess;

	private Particles liquidParticles = null;
	private Particles membraneParticles = null;
	private Particles contractileParticles = null;

	/**
	 * @param siberneticLibrary
	 * @param library
	 * @param modelAccess
	 */
	public SiberneticModelConverter(GeppettoLibrary siberneticLibrary, GeppettoLibrary library, GeppettoModelAccess modelAccess)
	{
		this.siberneticLibrary = siberneticLibrary;
		this.library = library;
		this.modelAccess = modelAccess;
		this.model.setId("worm");
		this.model.setName("Worm");
	}

	/**
	 * @param modelConfiguration
	 * @return
	 */
	public Type toGeppettoType(String modelConfiguration)
	{

		String positions = modelConfiguration.substring(modelConfiguration.indexOf(POSITION_TAG) + POSITION_TAG.length(), modelConfiguration.indexOf(VELOCITY_TAG));
		// everything else which is not position we don't care
		StringTokenizer tokenizer = new StringTokenizer(positions, "\r\n");
		while(tokenizer.hasMoreTokens())
		{
			String position = tokenizer.nextToken();
			Point particle = ValuesFactory.eINSTANCE.createPoint();
			StringTokenizer positionTokenizer = new StringTokenizer(position);
			particle.setX(Double.parseDouble(positionTokenizer.nextToken()));
			particle.setY(Double.parseDouble(positionTokenizer.nextToken()));
			particle.setZ(Double.parseDouble(positionTokenizer.nextToken()));
			String type = positionTokenizer.nextToken();
			if(getContainer(type) != null)
			{
				getContainer(type).getParticles().add(particle);
			}
		}
		//System.out.println(liquidParticles.getParticles());
		return this.model;
	}

	private Particles getContainer(String type)
	{
		switch(type)
		{
			case "3.1":
				if(liquidParticles == null)
				{
					liquidParticles = ValuesFactory.eINSTANCE.createParticles();
					Variable liquid = VariablesFactory.eINSTANCE.createVariable();
					liquid.setId("liquid");
					Type liquidType = siberneticLibrary.getTypeById("liquid");
					liquid.getTypes().add(liquidType);
					Composite value = ValuesFactory.eINSTANCE.createComposite();
					value.getValue().put("particles", liquidParticles);
					liquid.getInitialValues().put(liquidType, value);
					model.getVariables().add(liquid);
				}
				return liquidParticles;
			case "1.1":
			default:
				break;
		}
		return null;
	}

}
