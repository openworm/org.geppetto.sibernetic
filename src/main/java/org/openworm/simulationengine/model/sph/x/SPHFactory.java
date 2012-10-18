/**
 * 
 */
package org.openworm.simulationengine.model.sph.x;

import org.openworm.simulationengine.model.sph.ObjectFactory;
import org.openworm.simulationengine.model.sph.SPHModel;
import org.openworm.simulationengine.model.sph.SPHParticle;
import org.openworm.simulationengine.model.sph.Vector3D;

/**
 * @author matteocantarelli
 *
 */
public class SPHFactory extends ObjectFactory {
	

    /**
     * Create an instance of {@link Vector3DX }
     * 
     */
	@Override
    public Vector3D createVector3D() {
        return new Vector3DX();
    }

	@Override
	public SPHModel createSPHModel() {
		return new SPHModelX();
	}

	@Override
	public SPHParticle createSPHParticle() {
		return new SPHParticleX();
	}
	
}
