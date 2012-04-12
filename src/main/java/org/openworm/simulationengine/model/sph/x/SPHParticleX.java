package org.openworm.simulationengine.model.sph.x;

import java.util.List;

import org.openworm.simulationengine.model.sph.SPHParticle;
import org.openworm.simulationengine.model.sph.Vector3D;

public class SPHParticleX extends SPHParticle {

	public void setPosition(float x, float y, float z){
		positionVector.setX(x);
		positionVector.setY(y);
		positionVector.setZ(z);
	}
	
	public void setPosistion(Vector3D position){
		positionVector = position;
	}
	
	public float gtDensety(){
		return density;
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
	
}
