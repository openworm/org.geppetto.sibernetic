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

package org.geppetto.model.sph.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.geppetto.core.constants.PhysicsConstants;
import org.geppetto.model.sph.Connection;
import org.geppetto.model.sph.SPHModel;
import org.geppetto.model.sph.SPHParticle;
import org.geppetto.model.sph.Vector3D;
import org.geppetto.model.sph.common.SPHConstants;
import org.geppetto.model.sph.x.SPHModelX;
import org.geppetto.model.sph.x.SPHParticleX;

public class SPHModelConverter
{
	private static final boolean TXT_TO_XML = false;

	private static final String POSITION_FILE_SOURCE = "./position_source.txt";
	private static final String VELOCITY_FILE_SOURCE = "./velocity_source.txt";
	private static final String SPH_XML_TARGET = "./sphModel_converted.xml";

	private static final String POSITION_FILE_TARGET = "./position_converted.txt";
	private static final String VELOCITY_FILE_TARGET = "./velocity_converted.txt";
	private static final String ELASTIC_FILE_TARGET = "./elastic_connections_converted.txt";
	private static final String SPH_XML_SOURCE = "./sphModel_source.xml";
	
	public static final float XMIN = 0;
	public static final float XMAX = 120.24f;
	public static final float YMIN = 0;
	public static final float YMAX = 80.16f;
	public static final float ZMIN = 0;
	public static final float ZMAX = 182.03f;

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

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

		if (TXT_TO_XML)
		{
			SPHModel model = new SPHModelX();

			try
			{
				model.setXMax(XMAX);
				model.setXMin(XMIN);
				model.setYMax(YMAX);
				model.setYMin(YMIN);
				model.setZMax(ZMAX);
				model.setZMin(ZMIN);
				
				String positionString = readFile(POSITION_FILE_SOURCE);
				String velocityString = readFile(VELOCITY_FILE_SOURCE);

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
					w = new FileWriter(SPH_XML_TARGET);
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
		else{
			SPHModel model = new SPHModelX();

			try
			{
				// parse XML file into SPHModel
				JAXBContext jc = JAXBContext.newInstance(SPHModel.class);
				Unmarshaller um = jc.createUnmarshaller();
				model = (SPHModel) um.unmarshal(new java.io.FileInputStream(SPH_XML_SOURCE));
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Writer positionWriter = null;
			Writer velocityWriter = null;
			Writer elasticWriter = null;
			try
			{
				// write txt files
				positionWriter = new FileWriter(POSITION_FILE_TARGET);
				velocityWriter = new FileWriter(VELOCITY_FILE_TARGET);
				
				for (SPHParticle p : model.getParticles())
				{
					positionWriter.write(formatFloatInCPPNotation(p.getPositionVector().getX()) + "\t" + 
										 formatFloatInCPPNotation(p.getPositionVector().getY()) + "\t" + 
										 formatFloatInCPPNotation(p.getPositionVector().getZ()) + "\t" +
										 formatFloatInCPPNotation(p.getPositionVector().getP()) + "\r\n");
					velocityWriter.write(formatFloatInCPPNotation(p.getVelocityVector().getX()) + "\t" + 
										 formatFloatInCPPNotation(p.getVelocityVector().getY()) + "\t" + 
										 formatFloatInCPPNotation(p.getVelocityVector().getZ()) + "\t" +
										 formatFloatInCPPNotation(p.getVelocityVector().getP()) + "\r\n");
				}
				
				positionWriter.flush();
				velocityWriter.flush();
				
				elasticWriter = new FileWriter(ELASTIC_FILE_TARGET);
				
				for (Connection c : model.getConnections())
				{
					elasticWriter.write(c.getP1() + "\t" + 
										c.getDistance() + "\t" + 
										c.getMysteryValue() + "\t" +
										0.0f + "\r\n");
				}
				
				elasticWriter.flush();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally
            {
                  try
                  {
                	  positionWriter.close(); 
                	  velocityWriter.close();
                  }
                  catch(IOException ex)
                  {
                        System.out.println("TXT File Closing Error");
                 }
            }     
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
	
	private static String formatFloatInCPPNotation(float val){
		NumberFormat formatter = new DecimalFormat("0.000000E000");
		String formattedVal = formatter.format(val).replace("E", "e");
		
		if(formattedVal.charAt(formattedVal.indexOf("e") + 1) != '-')
		{
			formattedVal = formattedVal.substring(0, formattedVal.indexOf("e") + 1) + "+" + formattedVal.substring(formattedVal.indexOf("e") + 1, formattedVal.length());
		}
		
		return formattedVal;
	}

}
