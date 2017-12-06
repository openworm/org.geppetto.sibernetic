/**
 * 
 */
package org.geppetto.sibernetic;

import static org.junit.Assert.assertNotNull;

import org.eclipse.emf.ecore.EClass;
import org.geppetto.core.common.GeppettoInitializationException;
import org.geppetto.core.manager.SharedLibraryManager;
import org.geppetto.core.model.GeppettoModelAccess;
import org.geppetto.model.ExperimentState;
import org.geppetto.model.GeppettoFactory;
import org.geppetto.model.GeppettoLibrary;
import org.geppetto.model.GeppettoModel;
import org.geppetto.model.VariableValue;
import org.geppetto.model.types.StateVariableType;
import org.geppetto.model.types.Type;
import org.geppetto.model.types.TypesPackage;
import org.geppetto.model.util.GeppettoVisitingException;
import org.geppetto.model.values.Pointer;
import org.geppetto.model.values.PointerElement;
import org.geppetto.model.values.ValuesFactory;
import org.geppetto.model.variables.Variable;
import org.geppetto.model.variables.VariablesFactory;
import org.junit.Test;

import junit.framework.Assert;
import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.h5.H5File;

/**
 * @author matteocantarelli
 *
 */
public class ConvertSiberneticToRecordingTest
{

	/**
	 * Test method for {@link org.geppetto.sibernetic.ConvertSiberneticToRecording#convert()}.
	 * @throws Exception 
	 */
	@Test
	public void testConvert() throws Exception
	{
		ExperimentState experimentState = GeppettoFactory.eINSTANCE.createExperimentState();
		GeppettoModel gm = GeppettoFactory.eINSTANCE.createGeppettoModel();
		
		addVariableValue(gm,experimentState, "time");
		for(int i=0;i<=94;i++){
			addVariableValue(gm,experimentState, "worm.muscle_activation["+i+"]");	
		}
		
		gm.getLibraries().add(SharedLibraryManager.getSharedCommonLibrary());
		GeppettoModelAccess geppettoModelAccess = new GeppettoModelAccess(gm);
		ConvertSiberneticToRecording converter=new ConvertSiberneticToRecording("siberneticRecording", "siberneticGeppettoRecording", geppettoModelAccess);
		converter.convert();
		
		assertNotNull(converter.getRecordingsFile());

		H5File file = converter.getRecordingsFile();
		file.open();
		Dataset dataset0 = (Dataset) file.findObject(file, "/worm.muscle_activation[0](StateVariable)");
		double[] value = (double[]) dataset0.read();
		Assert.assertEquals(0.0d, value[0]);
		Assert.assertEquals(0.3297d, value[161]);
		Assert.assertEquals(0.05759016d, value[439]);

		Dataset dataset2 = (Dataset) file.findObject(file, "/worm.muscle_activation[2](StateVariable)");
		double[] value2 = (double[]) dataset2.read();
		Assert.assertEquals(0.0d, value2[0]);
		Assert.assertEquals(0.0d, value2[161]);

		Dataset dataset94 = (Dataset) file.findObject(file, "/worm.muscle_activation[94](StateVariable)");
		double[] value94 = (double[]) dataset94.read();
		Assert.assertEquals(0.0d, value94[0]);
		Assert.assertEquals(0.0d, value94[161]);

		file.close();
		
	}

	
	private void addVariableValue(GeppettoModel gm, ExperimentState experimentState, String variable) throws GeppettoVisitingException, GeppettoInitializationException
	{
		VariableValue vv = GeppettoFactory.eINSTANCE.createVariableValue();
		Pointer p = ValuesFactory.eINSTANCE.createPointer();
		StateVariableType stateVariableType = (StateVariableType) getType(SharedLibraryManager.getSharedCommonLibrary(), TypesPackage.Literals.STATE_VARIABLE_TYPE);
		Variable v = VariablesFactory.eINSTANCE.createVariable();
		v.setId(variable);
		v.setName(variable);
		v.getTypes().add(stateVariableType);
		gm.getVariables().add(v);
		PointerElement pelem = ValuesFactory.eINSTANCE.createPointerElement();
		pelem.setVariable(v);
		pelem.setType(stateVariableType);
		p.getElements().add(pelem);
		vv.setPointer(p);
		experimentState.getRecordedVariables().add(vv);
	}
	
	/**
	 * Usage commonLibraryAccess.getType(TypesPackage.Literals.PARAMETER_TYPE);
	 * 
	 * @return
	 * @throws GeppettoVisitingException
	 */
	public Type getType(GeppettoLibrary library, EClass eclass) throws GeppettoVisitingException
	{
		for(Type type : library.getTypes())
		{
			if(type.eClass().equals(eclass))
			{
				return type;
			}
		}
		throw new GeppettoVisitingException("Type for eClass " + eclass + " not found in common library.");
	}

}
