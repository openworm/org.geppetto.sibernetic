package org.openworm.simulationengine.model.sph.x;

import java.util.ArrayList;

import org.openworm.simulationengine.model.sph.SPHCell;
import org.openworm.simulationengine.model.sph.SPHModel;
import org.openworm.simulationengine.model.sph.SPHParticle;

public class SPHModelX extends SPHModel {

	
	public SPHModelX(int cellX, int cellY, int cellZ){
		this.cellX = cellX;
		this.cellY = cellY;
		this.cellZ = cellZ;
		this.particles = new ArrayList<SPHParticle>();
		this.cells = new ArrayList<SPHCell>();
	}
	
	public int getNumberOfParticals(){
		return particles.size();
	}
	
	public SPHModelX() {
		super();
	}

	public float getParticleRadius(){
		return 1.f;//particle_radius;
	}
	
	public void findNeibghor(){
		
	}
	
	public void advanceInTime(){
		
	}
	
	private void init3DGrid(){
		
	}
}
