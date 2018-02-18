/**
 * 
 */
package org.geppetto.sibernetic;

import static org.junit.Assert.assertNotNull;

import org.apache.commons.io.IOUtils;
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
	public void testCrawlConvert() throws Exception
	{
		GeppettoModel gm = GeppettoFactory.eINSTANCE.createGeppettoModel();
		gm.getLibraries().add(SharedLibraryManager.getSharedCommonLibrary());
		GeppettoLibrary siberneticLibrary = SiberneticLibraryLoader.getSiberneticLibrary();
		gm.getLibraries().add(siberneticLibrary);
		GeppettoModelAccess geppettoModelAccess = new GeppettoModelAccess(gm);

		String modelConfiguration = IOUtils.toString(SiberneticModelConverterTest.class.getClassLoader().getResourceAsStream("C2_FW/worm_crawl_half_resolution")); 
		SiberneticModelConverter converter = new SiberneticModelConverter(siberneticLibrary, SharedLibraryManager.getSharedCommonLibrary(), geppettoModelAccess);
		Type model = converter.toGeppettoType(modelConfiguration);
		Variable worm = VariablesFactory.eINSTANCE.createVariable();
		worm.setId("worm");
		worm.getTypes().add(model);
		
		gm.getVariables().add(worm);
		
		ExperimentState experimentState = GeppettoFactory.eINSTANCE.createExperimentState();
		addVariableValue(gm,experimentState, "time");
		for(int i=1;i<=96;i++){
			addVariableValue(gm,experimentState, "worm.muscle_activation_"+i+"");	
		}
		

		ConvertSiberneticToRecording recordingConverter=new ConvertSiberneticToRecording("src/test/resources/C2_FW", "siberneticGeppettoCrawlRecording.h5", geppettoModelAccess);
		recordingConverter.convert();
				
	}
	
//	/**
//	 * Test method for {@link org.geppetto.sibernetic.ConvertSiberneticToRecording#convert()}.
//	 * @throws Exception 
//	 */
//	@Test
//	public void testConvert() throws Exception
//	{
//		GeppettoModel gm = GeppettoFactory.eINSTANCE.createGeppettoModel();
//		gm.getLibraries().add(SharedLibraryManager.getSharedCommonLibrary());
//		GeppettoLibrary siberneticLibrary = SiberneticLibraryLoader.getSiberneticLibrary();
//		gm.getLibraries().add(siberneticLibrary);
//		GeppettoModelAccess geppettoModelAccess = new GeppettoModelAccess(gm);
//
//		String modelConfiguration = IOUtils.toString(SiberneticModelConverterTest.class.getClassLoader().getResourceAsStream("siberneticRecording/worm_alone_half_resolution")); 
//		SiberneticModelConverter converter = new SiberneticModelConverter(siberneticLibrary, SharedLibraryManager.getSharedCommonLibrary(), geppettoModelAccess);
//		Type model = converter.toGeppettoType(modelConfiguration);
//		Variable worm = VariablesFactory.eINSTANCE.createVariable();
//		worm.setId("worm");
//		worm.getTypes().add(model);
//		
//		gm.getVariables().add(worm);
//		
//		ExperimentState experimentState = GeppettoFactory.eINSTANCE.createExperimentState();
//		addVariableValue(gm,experimentState, "time");
//		for(int i=1;i<=96;i++){
//			addVariableValue(gm,experimentState, "worm.muscle_activation_"+i+"");	
//		}
//		
//
//		ConvertSiberneticToRecording recordingConverter=new ConvertSiberneticToRecording("src/test/resources/siberneticRecording", "siberneticGeppettoRecording.h5", geppettoModelAccess);
//		recordingConverter.convert();
//		
//		assertNotNull(recordingConverter.getRecordingsFile());
//
//		H5File file = recordingConverter.getRecordingsFile();
//		file.open();
//		Dataset dataset0 = (Dataset) file.findObject(file, "/worm(worm)/muscle_activation_2(StateVariable)");
//		double[] value = (double[]) dataset0.read();
//		Assert.assertEquals(0.0d, value[0]);
//		Assert.assertEquals(0.3092529d, value[16]);
//		Assert.assertEquals(0.0, value[43]);
//
//		Dataset dataset2 = (Dataset) file.findObject(file, "/worm(worm)/muscle_activation_3(StateVariable)");
//		double[] value2 = (double[]) dataset2.read();
//		Assert.assertEquals(0.0d, value2[0]);
//		Assert.assertEquals(0.0d, value2[16]);
//
//		Dataset dataset94 = (Dataset) file.findObject(file, "/worm(worm)/muscle_activation_95(StateVariable)");
//		double[] value94 = (double[]) dataset94.read();
//		Assert.assertEquals(0.0d, value94[0]);
//		Assert.assertEquals(0.0d, value94[16]);
//		
//		Dataset dataset95 = (Dataset) file.findObject(file, "/worm(worm)/muscle_activation_96(StateVariable)");
//		double[] value95 = (double[]) dataset95.read();
//		Assert.assertEquals(0.0d, value94[0]);
//		Assert.assertEquals(0.0d, value94[16]);
//		Dataset dataset96 = (Dataset) file.findObject(file, "/worm(worm)/muscle_activation_97(StateVariable)");
//		Assert.assertNull(dataset96);
//		
//		
//		Dataset midline = (Dataset) file.findObject(file, "/worm(worm)/midline(midline)/x(StateVariable)");
//		double[] midlineValues = (double[]) midline.read();
//		Assert.assertEquals(0.1046513d, midlineValues[0]);
//		Assert.assertEquals(0.2276419d, midlineValues[10]);
//		Assert.assertEquals(0.2394234d, midlineValues[11]);
//		file.close();
//		
//	}

	
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
