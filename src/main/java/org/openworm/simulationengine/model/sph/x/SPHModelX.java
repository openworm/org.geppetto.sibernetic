package org.openworm.simulationengine.model.sph.x;

import java.util.ArrayList;

import org.openworm.simulationengine.core.model.IModel;
import org.openworm.simulationengine.model.sph.SPHCell;
import org.openworm.simulationengine.model.sph.SPHModel;
import org.openworm.simulationengine.model.sph.SPHParticle;

public class SPHModelX extends SPHModel implements IModel {
	
	public SPHModelX(float xMax, float xMin, float yMax, float yMin, float zMax, float zMin){
		this.xMax = xMax;
		this.xMin = xMin;
		this.yMax = yMax;
		this.yMin = yMin;
		this.zMax = zMax;
		this.zMin = zMin;
		
		this.particles = new ArrayList<SPHParticle>();
		this.cells = new ArrayList<SPHCell>();
	}
	
	public int getNumberOfParticals(){
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
	}

	public String getId() {
		return "sph";
	}
}
