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

package org.geppetto.model.sph.x;

import java.util.List;

import org.geppetto.model.sph.SPHParticle;
import org.geppetto.model.sph.Vector3D;

public class SPHParticleX extends SPHParticle {

	String _id;
	
	
	public String getId()
	{
		return _id;
	}

	public void setId(String id)
	{
		this._id = id;
	}

	public void setPosition(float x, float y, float z){
		positionVector.setX(x);
		positionVector.setY(y);
		positionVector.setZ(z);
	}
	
	public void setPosistion(Vector3D position){
		positionVector = position;
	}
	

	public List<Integer> getNeighbourIndexes(){
		return neighbourIndexes;
	}
	/**Constructor
	 * @param x
	 * @param y
	 * @param z
	 * @param particleMass
	 */
	public SPHParticleX(float x, float y, float z, float particleMass){
		positionVector = new Vector3DX(x,y,z);
		velocityVector = new Vector3DX();
		mass = particleMass;
	}
	public SPHParticleX(Vector3D position, Vector3D velocity, float particleMass){
		positionVector = position;
		velocityVector = velocity;
		mass = particleMass;
	}

	public SPHParticleX() {
		super();
	}

	public SPHParticleX(SPHParticle p)
	{
		positionVector = new Vector3DX(p.getPositionVector());
		velocityVector = new Vector3DX(p.getVelocityVector());
		mass = p.getMass();
	}
	
}
