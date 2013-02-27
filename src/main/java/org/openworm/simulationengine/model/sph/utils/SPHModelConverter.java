package org.openworm.simulationengine.model.sph.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.openworm.simulationengine.core.constants.PhysicsConstants;
import org.openworm.simulationengine.model.sph.SPHModel;
import org.openworm.simulationengine.model.sph.Vector3D;
import org.openworm.simulationengine.model.sph.common.SPHConstants;
import org.openworm.simulationengine.model.sph.x.SPHModelX;
import org.openworm.simulationengine.model.sph.x.SPHParticleX;

public class SPHModelConverter
{

	private static final String POSITION_FILE = "/Users/matteocantarelli/Documents/Development/MetaCellWorkspace/org.openworm.simulationengine.model.sph/src/main/resources/positionPureLiquid.txt";
	private static final String VELOCITY_FILE = "/Users/matteocantarelli/Documents/Development/MetaCellWorkspace/org.openworm.simulationengine.model.sph/src/main/resources/velocityPureLiquid.txt";
	private static final String SPH_XML = "./sphModelConverted.xml";

	
	private static String readFile(String path) throws IOException {
		  FileInputStream stream = new FileInputStream(new File(path));
		  try {
		    FileChannel fc = stream.getChannel();
		    MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
		    /* Instead of using default, pass in a decoder. */
		    return Charset.defaultCharset().decode(bb).toString();
		  }
		  finally {
		    stream.close();
		  }
		}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		SPHModel model = new SPHModelX();

		try
		{
			model.setCellX((int)( ( SPHConstants.XMAX - SPHConstants.XMIN ) / PhysicsConstants.H ) + 1);
			model.setCellY((int)( ( SPHConstants.YMAX - SPHConstants.YMIN ) / PhysicsConstants.H ) + 1);
			model.setCellZ((int)( ( SPHConstants.ZMAX - SPHConstants.ZMIN ) / PhysicsConstants.H ) + 1);
			String positionString = readFile(POSITION_FILE);
			String velocityString = readFile(VELOCITY_FILE);

			String[] positionLines = positionString.split(System.getProperty("line.separator"));
			String[] velocityLines = velocityString.split(System.getProperty("line.separator"));

			for (int i = 0; i < positionLines.length; i++)
			{
				SPHParticleX p = new SPHParticleX();
				p.setPositionVector(get3DVector(positionLines[i]));
				p.setVelocityVector(get3DVector(velocityLines[i]));
				p.setMass(1f);
				model.getParticles().add(p);
				
			}
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// create JAXB context and instantiate marshaller
		JAXBContext context;
		try
		{
			context = JAXBContext.newInstance(SPHModel.class);

			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(model, System.out);

			Writer w = null;
			try
			{
				w = new FileWriter(SPH_XML);
				m.marshal(model, w);
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally
			{
				try
				{
					w.close();
				}
				catch (Exception e)
				{
				}
			}

			

		}
		catch (JAXBException e1)
		{
			e1.printStackTrace();
		}

	}

	private static Vector3D get3DVector(String triplet)
	{
		Vector3D v = new Vector3D();
		String[] coordinates = triplet.split("\t");
		v.setX(new Float(coordinates[0].trim()));
		v.setY(new Float(coordinates[1].trim()));
		v.setZ(new Float(coordinates[2].trim()));
		if (coordinates.length > 3)
		{
			v.setP(new Float(coordinates[3].trim()));
		}
		return v;
	}

}
