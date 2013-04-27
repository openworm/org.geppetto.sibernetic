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
