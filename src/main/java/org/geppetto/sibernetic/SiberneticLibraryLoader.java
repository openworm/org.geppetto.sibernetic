package org.geppetto.sibernetic;

import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.geppetto.core.common.GeppettoInitializationException;
import org.geppetto.model.GeppettoLibrary;
import org.geppetto.model.GeppettoPackage;

/**
 * @author matteocantarelli
 *
 */
public class SiberneticLibraryLoader
{

	public synchronized static GeppettoLibrary getSiberneticLibrary() throws GeppettoInitializationException
	{
		GeppettoLibrary siberneticLibrary;
		GeppettoPackage.eINSTANCE.eClass();
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("xmi", new XMIResourceFactoryImpl()); // sets the factory for the XMI type
		GeppettoPackage.Registry.INSTANCE.put(GeppettoPackage.eNS_URI, GeppettoPackage.eINSTANCE);

		// We add all supported versions of the schema
		String[] versions = new String[] { "master", "development" };
		for(String version : versions)
		{
			GeppettoPackage.Registry.INSTANCE.put(GeppettoPackage.eNS_URI_TEMPLATE.replace("$VERSION$", version), GeppettoPackage.eINSTANCE);
		}

		ResourceSet resSet = new ResourceSetImpl();
		Resource resource = resSet.createResource(URI.createURI("/SiberneticLibrary.xmi"));
		try
		{
			resource.load(SiberneticLibraryLoader.class.getResourceAsStream("/SiberneticLibrary.xmi"), null);
		}
		catch(IOException e)
		{
			throw new GeppettoInitializationException(e);
		}
		siberneticLibrary = (GeppettoLibrary) resource.getContents().get(0);
		return siberneticLibrary;
	}

}
