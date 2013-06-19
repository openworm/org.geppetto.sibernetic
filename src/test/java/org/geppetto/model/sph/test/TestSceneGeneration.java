package org.geppetto.model.sph.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.geppetto.core.model.IModel;
import org.geppetto.model.sph.Connection;
import org.geppetto.model.sph.Vector3D;
import org.geppetto.model.sph.services.SPHModelInterpreterService;
import org.geppetto.model.sph.x.SPHModelX;
import org.geppetto.model.sph.x.SPHParticleX;
import org.junit.Test;

public class TestSceneGeneration {

	@Test
	public void TestSceneGeneration_ElasticScene() throws Exception {
		// 1. load reference initial conditions from C++ exported scene
		String positionString = readFile(TestSceneGeneration.class.getResource("/elastic_position_log_0.txt").getPath());
		String velocityString = readFile(TestSceneGeneration.class.getResource("/elastic_velocity_log_0.txt").getPath());
		String connectionsString = readFile(TestSceneGeneration.class.getResource("/elastic_connections_log_0.txt").getPath());
		String[] positionLines = positionString.split(System.getProperty("line.separator"));
		String[] velocityLines = velocityString.split(System.getProperty("line.separator"));
		String[] connectionLines = connectionsString.split(System.getProperty("line.separator"));
		
		// 2. load Java generated scene
		URL url = this.getClass().getResource("/sphModel_Elastic.xml");
		SPHModelInterpreterService modelInterpreter = new SPHModelInterpreterService();
		SPHModelX model = (SPHModelX)modelInterpreter.readModel(url);
		
		// 3. assert number of particles is fine
		Assert.assertTrue("number of lines on velocity and positions files do not match", velocityLines.length == positionLines.length);
		Assert.assertTrue("number of lines on positions and number of particles on sphModel do not match", model.getParticles().size() == positionLines.length);
		Assert.assertTrue("number of lines on elastic connections and number of connections on sphModel do not match", model.getConnections().size() == connectionLines.length);
		
		List<Integer> positionMismatches = new ArrayList<Integer>();
		List<Integer> velocityMismatches = new ArrayList<Integer>();
		List<Integer> connectionsMismatches = new ArrayList<Integer>();
		
		// 4. compare and save differences 
		// 4.1 positions & velocities
		for (int i = 0; i < positionLines.length; i++)
		{
			SPHParticleX p = (SPHParticleX) model.getParticles().get(i);
			Vector3D positionV = get3DVector(positionLines[i]);
			Vector3D velocityV = get3DVector(velocityLines[i]);
			
			// positions
			if ( !(p.getPositionVector().getX().equals(positionV.getX()) &&
				   p.getPositionVector().getY().equals(positionV.getY()) &&
				   p.getPositionVector().getZ().equals(positionV.getZ()) &&
				   Math.round(p.getPositionVector().getP()) == Math.round(positionV.getP())))
			{
				positionMismatches.add(i);
			}
			
			// velocities
			if ( !(p.getVelocityVector().getX().equals(velocityV.getX()) &&
				   p.getVelocityVector().getY().equals(velocityV.getY()) &&
				   p.getVelocityVector().getZ().equals(velocityV.getZ()) &&
				   Math.round(p.getVelocityVector().getP()) == Math.round(velocityV.getP())))
			{
				velocityMismatches.add(i);	
			}
		}
		
		// 4.2 elastic connections
		for (int i = 0; i < connectionLines.length; i++)
		{
			Connection c = model.getConnections().get(i);
			Vector3D connectionV = get3DVector(connectionLines[i]);
			
			// positions
			if ( !(c.getP1() == connectionV.getX().floatValue() &&
				   c.getDistance() == connectionV.getY().floatValue() &&
				   c.getMysteryValue() == connectionV.getZ().floatValue() &&
				   0f == connectionV.getP().floatValue()))
			{
				connectionsMismatches.add(i);
			}
		}
		
		// 5. assert and output differences
		//Assert.assertTrue(positionMismatches.size() + " positions mismatches", positionMismatches.size() == 0);
		//Assert.assertTrue(velocityMismatches.size() + " velocities mismatches", velocityMismatches.size() == 0);
		//Assert.assertTrue(connectionsMismatches.size() + " connections mismatches", connectionsMismatches.size() == 0);
	}
	
	private static String readFile(String path) throws IOException
	{
		FileInputStream stream = new FileInputStream(new File(path));
		try
		{
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			/* Instead of using default, pass in a decoder. */
			return Charset.defaultCharset().decode(bb).toString();
		}
		finally
		{
			stream.close();
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
