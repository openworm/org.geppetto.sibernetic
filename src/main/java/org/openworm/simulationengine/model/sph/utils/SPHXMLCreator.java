package org.openworm.simulationengine.model.sph.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.openworm.simulationengine.core.constants.PhysicsConstants;
import org.openworm.simulationengine.core.model.MathUtils;
import org.openworm.simulationengine.model.sph.SPHModel;
import org.openworm.simulationengine.model.sph.SPHParticle;
import org.openworm.simulationengine.model.sph.Vector3D;
import org.openworm.simulationengine.model.sph.common.SPHConstants;
import org.openworm.simulationengine.model.sph.x.SPHFactory;

public class SPHXMLCreator {


	private static final String SPH_XML = "./sphModel.xml";
	
	private static final int PARTICLE_COUNT = 296 + 216;
	public static final float XMIN = 0;
	public static final float XMAX = 11.69f;
	public static final float YMIN = 0;
	public static final float YMAX = 11.69f;
	public static final float ZMIN = 0;
	public static final float ZMAX = 11.69f;


	private static SPHModel createModel()
	{
		SPHFactory factory = new SPHFactory();

		SPHModel model = factory.createSPHModel();

		model.setXMax(XMAX);
		model.setXMin(XMIN);
		model.setYMax(YMAX);
		model.setYMin(YMIN);
		model.setZMax(ZMAX);
		model.setZMin(ZMIN);

		generateBoundaries(model);
		// generateBottomLayerOfLiquid(model);
		generateRandomLiquidConfiguration(model);
		
		return model;
	}
	
	public static void generateBoundaries(SPHModel model){
		SPHFactory factory = new SPHFactory();
		
		// calculate number of boundary particles by X, Y, Z axis. Distance Between two neighbor particle is equal to 
		int n = (int)( ( XMAX - XMIN ) / PhysicsConstants.R0 ); //X
		int m = (int)( ( YMAX - YMIN ) / PhysicsConstants.R0 ); //Y
		int k = (int)( ( ZMAX - ZMIN ) / PhysicsConstants.R0 ); //Z
		
		float x,y,z;
		int i = 0;
		//drop
		//Creation of Boundary Particle
		x = XMIN;
		z = ZMIN;
		y = YMIN;
		float x1, y1, z1;
		y1 = YMAX;
		float speed = 1.0f;
		float normCorner = (float) (1/Math.sqrt(3.f));
		float normBoundary = (float) (1/Math.sqrt(2.f));
		boolean isBoundary = false;
		int count = 2 *( k * n +  n + k );
		for(;i <= count;i+=2)
		{
			Vector3D positionVector1 = factory.createVector3D();
			positionVector1.setX(x);
			positionVector1.setY(y);
			positionVector1.setZ(z);
			positionVector1.setP(3.1f); // 3 = boundary

			Vector3D positionVector2 = factory.createVector3D();
			positionVector2.setX(x);
			positionVector2.setY(y1);
			positionVector2.setZ(z);
			positionVector2.setP(3.1f); // 3 = boundary
			
			Vector3D velocityVector1 = factory.createVector3D();
			Vector3D velocityVector2 = factory.createVector3D();
			
			x+= PhysicsConstants.R0;		
			if(i == 0){
				velocityVector1.setX(normCorner);
				velocityVector1.setY(normCorner);
				velocityVector1.setZ(normCorner);
				velocityVector1.setP(0f);

				velocityVector2.setX(normCorner);
				velocityVector2.setY(-normCorner);
				velocityVector2.setZ(normCorner);
				velocityVector2.setP(0f);
				isBoundary = true;
			}
			if(x >= XMAX && z == ZMIN && !isBoundary){
				velocityVector1.setX(-normCorner);
				velocityVector1.setY(normCorner);
				velocityVector1.setZ(normCorner);
				velocityVector1.setP(0f);

				velocityVector2.setX(-normCorner);
				velocityVector2.setY(-normCorner);
				velocityVector2.setZ(normCorner);
				velocityVector2.setP(0f);
				isBoundary = true;
			}

			if(x >= XMAX && z >= ZMAX - PhysicsConstants.R0 && !isBoundary){
				velocityVector1.setX(-normCorner);
				velocityVector1.setY(normCorner);
				velocityVector1.setZ(-normCorner);
				velocityVector1.setP(0f);

				velocityVector2.setX(-normCorner);
				velocityVector2.setY(-normCorner);
				velocityVector2.setZ(-normCorner);
				velocityVector2.setP(0f);
				isBoundary = true;
			}
			if(x - PhysicsConstants.R0 == XMIN && z >= ZMAX - PhysicsConstants.R0 && !isBoundary){
				velocityVector1.setX(normCorner);
				velocityVector1.setY(normCorner);
				velocityVector1.setZ(-normCorner);
				velocityVector1.setP(0f);

				velocityVector2.setX(normCorner);
				velocityVector2.setY(-normCorner);
				velocityVector2.setZ(-normCorner);
				velocityVector2.setP(0f);
				isBoundary = true;
			}

			if(x >= XMAX && !isBoundary){
				velocityVector1.setX(-normBoundary);
				velocityVector1.setY(normBoundary);
				velocityVector1.setZ(0f);
				velocityVector1.setP(0f);

				velocityVector2.setX(-normBoundary);
				velocityVector2.setY(-normBoundary);
				velocityVector2.setZ(0f);
				velocityVector2.setP(0f);
				isBoundary = true;
			}

			if(z == ZMIN && !isBoundary){
				velocityVector1.setX(0f);
				velocityVector1.setY(normBoundary);
				velocityVector1.setZ(normBoundary);
				velocityVector1.setP(0f);

				velocityVector2.setX(0f);
				velocityVector2.setY(-normBoundary);
				velocityVector2.setZ(normBoundary);
				velocityVector2.setP(0f);
				isBoundary = true;
			}
			if(x - PhysicsConstants.R0 == XMIN && !isBoundary){
				velocityVector1.setX(normBoundary);
				velocityVector1.setY(normBoundary);
				velocityVector1.setZ(0f);
				velocityVector1.setP(0f);

				velocityVector2.setX(normBoundary);
				velocityVector2.setY(-normBoundary);
				velocityVector2.setZ(0f);
				velocityVector2.setP(0f);
				isBoundary = true;
			}
			if(z >= ZMAX - PhysicsConstants.R0 && !isBoundary){
				velocityVector1.setX(0f);
				velocityVector1.setY(normBoundary);
				velocityVector1.setZ(-normBoundary);
				velocityVector1.setP(0f);

				velocityVector2.setX(0f);
				velocityVector2.setY(-normBoundary);
				velocityVector2.setZ(-normBoundary);
				velocityVector2.setP(0f);
				isBoundary = true;
			}
			if(isBoundary == false){
				velocityVector1.setX(0f);
				velocityVector1.setY(speed);
				velocityVector1.setZ(0f);
				velocityVector1.setP(0f);

				velocityVector2.setX(0f);
				velocityVector2.setY(-speed);
				velocityVector2.setZ(0f);
				velocityVector2.setP(0f);
			}
			
			// add particles
			SPHParticle particle1 = factory.createSPHParticle();
			particle1.setPositionVector(positionVector1);
			particle1.setVelocityVector(velocityVector1);
			particle1.setMass(1f);
			model.getParticles().add(particle1);
			
			SPHParticle particle2 = factory.createSPHParticle();
			particle2.setPositionVector(positionVector2);
			particle2.setVelocityVector(velocityVector2);
			particle2.setMass(1f);
			model.getParticles().add(particle2);
			
			// update coordinates for next iteration
			isBoundary = false;
			if(x > XMAX) { 
				x = XMIN; 
				z += PhysicsConstants.R0; 
			}
		}

		x = XMIN;
		y = YMIN + PhysicsConstants.R0;
		z = ZMIN;

		x1 = XMAX;
		isBoundary = false;
		count = 2 *( k * ( m - 2 )  + k + m - 2) + i;
		for(;i <= count;i+=2)
		{
			Vector3D positionVector1 = factory.createVector3D();
			positionVector1.setX(x);
			positionVector1.setY(y);
			positionVector1.setZ(z);
			positionVector1.setP(3.1f); // 3 = boundary

			Vector3D positionVector2 = factory.createVector3D();
			positionVector2.setX(x1);
			positionVector2.setY(y);
			positionVector2.setZ(z);
			positionVector2.setP(3.1f); // 3 = boundary
			
			Vector3D velocityVector1 = factory.createVector3D();
			Vector3D velocityVector2 = factory.createVector3D();

			if(z == ZMIN){
				velocityVector1.setX(normBoundary);
				velocityVector1.setY(0f);
				velocityVector1.setZ(normBoundary);
				velocityVector1.setP(0f);

				velocityVector2.setX(-normBoundary);
				velocityVector2.setY(0f);
				velocityVector2.setZ(normBoundary);
				velocityVector2.setP(0f);
				isBoundary = true;
			}
			if(z >= ZMAX - PhysicsConstants.R0 && !isBoundary){
				velocityVector1.setX(normBoundary);
				velocityVector1.setY(0f);
				velocityVector1.setZ(-normBoundary);
				velocityVector1.setP(0f);

				velocityVector2.setX(-normBoundary);
				velocityVector2.setY(0f);
				velocityVector2.setZ(-normBoundary);
				velocityVector2.setP(0f);
				isBoundary = true;
			}
			if(isBoundary == false){
				velocityVector1.setX(speed);
				velocityVector1.setY(0f);
				velocityVector1.setZ(0f);
				velocityVector1.setP(0f);

				velocityVector2.setX(-speed);
				velocityVector2.setY(0f);
				velocityVector2.setZ(0f);
				velocityVector2.setP(0f);
			}
			
			// add particles
			SPHParticle particle1 = factory.createSPHParticle();
			particle1.setPositionVector(positionVector1);
			particle1.setVelocityVector(velocityVector1);
			particle1.setMass(1f);
			model.getParticles().add(particle1);
			
			SPHParticle particle2 = factory.createSPHParticle();
			particle2.setPositionVector(positionVector2);
			particle2.setVelocityVector(velocityVector2);
			particle2.setMass(1f);
			model.getParticles().add(particle2);
			
			// update coordinates for next iteration
			isBoundary = false;
			y+= PhysicsConstants.R0;

			if(y > YMAX - PhysicsConstants.R0) { 
				y = YMIN + PhysicsConstants.R0; z += PhysicsConstants.R0; 
			}
		}
		
		x = XMIN + PhysicsConstants.R0;
		y = YMIN + PhysicsConstants.R0;
		z = ZMIN;

		z1 = ZMAX;
		count = 2 *( ( n - 2 ) * ( m - 2 )  + n + m - 4) + i;
		for(;i <= count;i+=2)
		{
			Vector3D positionVector1 = factory.createVector3D();
			positionVector1.setX(x);
			positionVector1.setY(y);
			positionVector1.setZ(z);
			positionVector1.setP(3.1f); // 3 = boundary

			Vector3D positionVector2 = factory.createVector3D();
			positionVector2.setX(x);
			positionVector2.setY(y);
			positionVector2.setZ(z1);
			positionVector2.setP(3.1f); // 3 = boundary

			Vector3D velocityVector1 = factory.createVector3D();
			velocityVector1.setX(0f);
			velocityVector1.setY(0f);
			velocityVector1.setZ(speed);
			velocityVector1.setP(0f);

			Vector3D velocityVector2 = factory.createVector3D();
			velocityVector2.setX(0f);
			velocityVector2.setY(0f);
			velocityVector2.setZ(-speed);
			velocityVector2.setP(0f);
			
			// add particles
			SPHParticle particle1 = factory.createSPHParticle();
			particle1.setPositionVector(positionVector1);
			particle1.setVelocityVector(velocityVector1);
			particle1.setMass(1f);
			model.getParticles().add(particle1);
			
			SPHParticle particle2 = factory.createSPHParticle();
			particle2.setPositionVector(positionVector2);
			particle2.setVelocityVector(velocityVector2);
			particle2.setMass(1f);
			model.getParticles().add(particle2);
			
			// update coordinates for next iteration
			y+= PhysicsConstants.R0;

			if(y > YMAX - PhysicsConstants.R0) { 
				y = YMIN + PhysicsConstants.R0; x += PhysicsConstants.R0; 
			}
		}
	}
	
	public static void generateBottomLayerOfLiquid(SPHModel model)
	{
		SPHFactory factory = new SPHFactory();
		
		float coeff = 0.2325f; // for particle mass
		float x,y,z;
		x = PhysicsConstants.R0 * 5 + 0*XMAX/4 + PhysicsConstants.H*coeff;
		y = PhysicsConstants.R0 * 15 + PhysicsConstants.H*coeff;
		z = PhysicsConstants.R0 * 5 + PhysicsConstants.H*coeff;
		
		int pCount = model.getParticles().size();
		for( ; pCount < PARTICLE_COUNT; ++pCount )
		{
			Vector3D positionVector = factory.createVector3D();
			positionVector.setX(x);
			positionVector.setY(y);
			positionVector.setZ(z);
			positionVector.setP(1.1f); // 1 = liquid

			Vector3D velocityVector = factory.createVector3D();
			velocityVector.setX(0f);
			velocityVector.setY(0f);
			velocityVector.setZ(0f);
			velocityVector.setP(0f);
			
			// add particles
			SPHParticle particle = factory.createSPHParticle();
			particle.setPositionVector(positionVector);
			particle.setVelocityVector(velocityVector);
			particle.setMass(1f);
			model.getParticles().add(particle);
			
			// update coordinates for next iteration
			x+= 2*PhysicsConstants.H*coeff;

			if(x>XMAX/2) { 
				x = PhysicsConstants.R0 * 5 + PhysicsConstants.H*coeff; z += 2*PhysicsConstants.H*coeff; 
			}
			if(z>ZMAX/2) { 
				x = PhysicsConstants.R0 * 5 + PhysicsConstants.H*coeff; z = PhysicsConstants.R0 * 5 + PhysicsConstants.H*coeff; y += 2*PhysicsConstants.H*coeff; 
			}
		}
	}
	
	public static void generateRandomLiquidConfiguration(SPHModel model)
	{
		SPHFactory factory = new SPHFactory();
		
		int pCount = model.getParticles().size();
		for( ; pCount < PARTICLE_COUNT; pCount++ )
		{
			float r = ((float)MathUtils.randomGenerator.nextInt(PhysicsConstants.RAND_MAX) / (float)PhysicsConstants.RAND_MAX );

			Vector3D positionVector = factory.createVector3D();
			positionVector.setX(MathUtils.scale(XMIN + 1, (XMAX -1)/10 , r)); 
			r = ((float)MathUtils.randomGenerator.nextInt(PhysicsConstants.RAND_MAX) / (float)PhysicsConstants.RAND_MAX );
			positionVector.setY(MathUtils.scale(YMIN + 1, YMAX - 1 , r)); 
			r = ((float)MathUtils.randomGenerator.nextInt(PhysicsConstants.RAND_MAX) / (float)PhysicsConstants.RAND_MAX );
			positionVector.setZ(MathUtils.scale(ZMIN + 1, ZMAX -1, r));
			positionVector.setP(1.1f);
			
			r = ((float)MathUtils.randomGenerator.nextInt(PhysicsConstants.RAND_MAX) / (float)PhysicsConstants.RAND_MAX );
			
			Vector3D velocityVector = factory.createVector3D();
			velocityVector.setX(MathUtils.scale(-1.0f, 1.0f, r));
			r = ((float)MathUtils.randomGenerator.nextInt(PhysicsConstants.RAND_MAX) / (float)PhysicsConstants.RAND_MAX );
			velocityVector.setY(MathUtils.scale(-1.0f, 1.0f, r));
			r = ((float)MathUtils.randomGenerator.nextInt(PhysicsConstants.RAND_MAX) / (float)PhysicsConstants.RAND_MAX );
			velocityVector.setZ(MathUtils.scale(-1.0f, 1.0f, r));
			velocityVector.setP(0f);

			SPHParticle particle = factory.createSPHParticle();
			particle.setPositionVector(positionVector);
			particle.setVelocityVector(velocityVector);
			particle.setMass(1f);
			model.getParticles().add(particle);
		}
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {


		SPHModel model=createModel();

		// create JAXB context and instantiate marshaller
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(SPHModel.class);

			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(model, System.out);

			Writer w = null;
			try {
				w = new FileWriter(SPH_XML);
				m.marshal(model, w);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					w.close();
				} catch (Exception e) {
				}
			}

			// get variables from our xml file, created before
			System.out.println();
			System.out.println("Output from our XML File: ");
			Unmarshaller um = context.createUnmarshaller();
			SPHModel sphModel = (SPHModel) um.unmarshal(new FileReader(SPH_XML));

			for (int i = 0; i < sphModel.getParticles().toArray().length; i++) {
				System.out.println("Particle " + (i + 1) + ": type "
						+ sphModel.getParticles().get(i).getPositionVector().getP() + " mass "
						+ sphModel.getParticles().get(i).getMass() + " position "
						+ sphModel.getParticles().get(i).getPositionVector() + " velocity "
						+ sphModel.getParticles().get(i).getVelocityVector());
			}

		} catch (JAXBException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
