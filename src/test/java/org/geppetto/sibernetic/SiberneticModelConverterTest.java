/**
 * 
 */
package org.geppetto.sibernetic;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.geppetto.core.common.GeppettoInitializationException;
import org.geppetto.model.GeppettoLibrary;
import org.junit.Test;

/**
 * @author matteocantarelli
 *
 */
public class SiberneticModelConverterTest
{

	@Test
	public void test() throws IOException, GeppettoInitializationException
	{
		GeppettoLibrary siberneticLibrary = SiberneticLibraryLoader.getSiberneticLibrary();
		String modelConfiguration = IOUtils.toString(SiberneticModelConverterTest.class.getClassLoader().getResourceAsStream("sampleConfiguration")); 
		SiberneticModelConverter converter = new SiberneticModelConverter(siberneticLibrary, null, null);
		converter.toGeppettoType(modelConfiguration);
		
	}

}
