/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (c) 2011, 2013 OpenWorm.
 * http://openworm.org
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT License
 * which accompanies this distribution, and is available at
 * http://opensource.org/licenses/MIT
 *
 * Contributors:
 *     	OpenWorm - http://openworm.org/people.html
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
 * USE OR OTHER DEALINGS IN THE SOFTWARE.
 *******************************************************************************/

package org.geppetto.model.sph.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.geppetto.model.sph.Connection;
import org.geppetto.model.sph.Vector3D;
import org.geppetto.model.sph.services.SPHModelInterpreterService;
import org.geppetto.model.sph.x.SPHModelX;
import org.geppetto.model.sph.x.SPHParticleX;
import org.junit.Test;

public class TestStaticGeneratedScene {

	@Test
	public void Test_ElasticScene_InitialConditions() throws Exception {
		// 1. load reference initial conditions from C++ exported scene
		String positionString = readFile(TestStaticGeneratedScene.class.getResource("/elastic_position_log_0.txt").getPath());
		String velocityString = readFile(TestStaticGeneratedScene.class.getResource("/elastic_velocity_log_0.txt").getPath());
		String connectionsString = readFile(TestStaticGeneratedScene.class.getResource("/elastic_connections_log_0.txt").getPath());
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
			if ( !(p.getPositionVector().getX().floatValue() == positionV.getX().floatValue() &&
				   p.getPositionVector().getY().floatValue() == positionV.getY().floatValue() &&
				   p.getPositionVector().getZ().floatValue() == positionV.getZ().floatValue() &&
				   Math.round(p.getPositionVector().getP()) == Math.round(positionV.getP())))
			{
				positionMismatches.add(i);
			}
			
			// velocities
			if ( !(p.getVelocityVector().getX().floatValue() == velocityV.getX().floatValue() &&
				   p.getVelocityVector().getY().floatValue() == velocityV.getY().floatValue() &&
				   p.getVelocityVector().getZ().floatValue() == velocityV.getZ().floatValue() &&
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
			if ( !(round(c.getP1(), 6) == round(connectionV.getX(), 6) &&
				   round(c.getDistance(), 6) == round(connectionV.getY(), 6) &&
				   round(c.getMysteryValue(), 6) == round(connectionV.getZ(), 6) &&
				   0f == connectionV.getP().floatValue()))
			{
				connectionsMismatches.add(i);
			}
		}
		
		// 5. assert and output differences
		Assert.assertTrue(positionMismatches.size() + " positions mismatches", positionMismatches.size() == 0);
		Assert.assertTrue(velocityMismatches.size() + " velocities mismatches", velocityMismatches.size() == 0);
		Assert.assertTrue(connectionsMismatches.size() + " connections mismatches", connectionsMismatches.size() == 0);
	}
	
	private String readFile(String path) throws IOException
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
	
	private Vector3D get3DVector(String triplet)
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
	
	public static float round(float d, int decimalPlace) 
	{
		BigDecimal bd = new BigDecimal(Float.toString(d));
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.floatValue();
	}
}
