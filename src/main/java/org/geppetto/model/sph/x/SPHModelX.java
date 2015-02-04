/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (c) 2011 - 2015 OpenWorm.
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

package org.geppetto.model.sph.x;

import java.util.ArrayList;

import org.geppetto.core.model.IModel;
import org.geppetto.model.sph.Connection;
import org.geppetto.model.sph.Membrane;
import org.geppetto.model.sph.SPHCell;
import org.geppetto.model.sph.SPHModel;
import org.geppetto.model.sph.SPHParticle;
import org.geppetto.model.sph.Vector3D;

public class SPHModelX extends SPHModel implements IModel, Comparable<SPHModelX> {
	
	
	private String _instancePath;

	public SPHModelX(float xMax, float xMin, float yMax, float yMin, float zMax, float zMin){
		this.xMax = xMax;
		this.xMin = xMin;
		this.yMax = yMax;
		this.yMin = yMin;
		this.zMax = zMax;
		this.zMin = zMin;
		this.connections = new ArrayList<Connection>();
		this.particles = new ArrayList<SPHParticle>();
		this.cells = new ArrayList<SPHCell>();
	}
	
	public int getNumberOfParticles(){
		return particles.size();
	}
	
	public SPHModelX() {
		super();
	}

	public SPHModelX(SPHModel sphModel)
	{
		
		this.xMax = sphModel.getXMax();
		this.xMin = sphModel.getXMin();
		this.yMax = sphModel.getYMax();
		this.yMin = sphModel.getYMin();
		this.zMax = sphModel.getZMax();
		this.zMin = sphModel.getZMin();
		this.mass = sphModel.getMass();
		this.timeStep = sphModel.getTimeStep();
		this.surfTensionCoeff = sphModel.getSurfTensionCoeff();
		this.elasticitiCoeff = sphModel.getElasticitiCoeff();
		this.viscosityCoeff = sphModel.getViscosityCoeff();
		this.elasticBundles = sphModel.getElasticBundles();
		this.connections = new ArrayList<Connection>();
		this.membranes = new ArrayList<Membrane>();
		this.particleMembranesList = new ArrayList<Integer>();
		this.particles = new ArrayList<SPHParticle>();
		this.cells = new ArrayList<SPHCell>();
		for(SPHParticle p:sphModel.getParticles())
		{
			getParticles().add(new SPHParticleX(p));
		}
		for(SPHCell c:sphModel.getCells())
		{
			getCells().add(c);
		}
		for(Connection c:sphModel.getConnections())
		{
			getConnections().add(c);
		}
		for(Membrane m:sphModel.getMembranes())
		{
			getMembranes().add(m);
		}
		for(Integer index:sphModel.getParticleMembranesList())
		{
			getParticleMembranesList().add(index);
		}
	}

	public String getId() {
		return "sph";
	}

	@Override
	public int compareTo(SPHModelX o) {
		int different=0;
		Object[] parray= particles.toArray();
		Object[] poarray= o.particles.toArray();
		for(int i=0;i<parray.length;i++)
		{
			Vector3D p=((SPHParticleX)parray[i]).getPositionVector();
			Vector3D v=((SPHParticleX)parray[i]).getVelocityVector();
			
			Vector3D po=((SPHParticleX)poarray[i]).getPositionVector();
			Vector3D vo=((SPHParticleX)poarray[i]).getVelocityVector();
			
			boolean sameP=
					p.getX().equals(po.getX()) &&
					p.getY().equals(po.getY()) &&
					p.getZ().equals(po.getZ()) &&
					p.getP().equals(po.getP());
			boolean sameV=
					v.getX().equals(vo.getX()) &&
					v.getY().equals(vo.getY()) &&
					v.getZ().equals(vo.getZ()) &&
					v.getP().equals(vo.getP());
			
			if(!(sameP && sameV)) 
			{
				different++;
			}
			
		}
		return different;
	}

	@Override
	public void setInstancePath(String instancePath)
	{
		_instancePath=instancePath;
	}

	@Override
	public String getInstancePath()
	{
		return _instancePath;
	}
	
}
