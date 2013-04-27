/**
 * 
 */
package org.geppetto.model.sph.x;

import javax.xml.bind.annotation.XmlRegistry;

import org.geppetto.model.sph.ObjectFactory;
import org.geppetto.model.sph.SPHModel;
import org.geppetto.model.sph.SPHParticle;
import org.geppetto.model.sph.Vector3D;

/**
 * @author matteocantarelli
 *
 */
@XmlRegistry
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
