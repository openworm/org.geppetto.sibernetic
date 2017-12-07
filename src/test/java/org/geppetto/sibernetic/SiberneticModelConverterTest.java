/**
 * 
 */
package org.geppetto.sibernetic;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.emfjson.jackson.resource.JsonResourceFactory;
import org.geppetto.core.common.GeppettoInitializationException;
import org.geppetto.model.GeppettoLibrary;
import org.geppetto.model.GeppettoPackage;
import org.geppetto.model.types.Type;
import org.geppetto.model.util.GeppettoVisitingException;
import org.junit.Test;

/**
 * @author matteocantarelli
 *
 */
public class SiberneticModelConverterTest
{

	@Test
	public void test() throws IOException, GeppettoInitializationException, GeppettoVisitingException
	{
		GeppettoLibrary siberneticLibrary = SiberneticLibraryLoader.getSiberneticLibrary();
		String modelConfiguration = IOUtils.toString(SiberneticModelConverterTest.class.getClassLoader().getResourceAsStream("sampleConfiguration")); 
		SiberneticModelConverter converter = new SiberneticModelConverter(siberneticLibrary, null, null);
		Type model = converter.toGeppettoType(modelConfiguration);
		// // Initialize the factory and the resource set
		GeppettoPackage.eINSTANCE.eClass();
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("xmi", new XMIResourceFactoryImpl()); // sets the factory for the XMI type
		m.put("json", new JsonResourceFactory()); // sets the factory for the XMI type
		ResourceSet resSet = new ResourceSetImpl();

		Resource resource = resSet.createResource(URI.createURI("./src/test/resources/siberneticModel.json"));
		resource.getContents().add(model);
		resource.save(null);
	}

}
