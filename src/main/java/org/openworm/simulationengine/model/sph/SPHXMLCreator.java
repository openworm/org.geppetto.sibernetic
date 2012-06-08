package org.openworm.simulationengine.model.sph;

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
import org.openworm.simulationengine.model.sph.x.SPHFactory;

public class SPHXMLCreator {


	private static final String SPH_XML = "./sphModel.xml";


	private static SPHModel createModel()
	{
		int gridCellsX, gridCellsY, gridCellsZ;

		SPHFactory factory = new SPHFactory();

		gridCellsX = (int)( ( SPHConstants.XMAX - SPHConstants.XMIN ) / PhysicsConstants.H ) + 1;
		gridCellsY = (int)( ( SPHConstants.YMAX - SPHConstants.YMIN ) / PhysicsConstants.H ) + 1;
		gridCellsZ = (int)( ( SPHConstants.ZMAX - SPHConstants.ZMIN ) / PhysicsConstants.H ) + 1;

		SPHModel model = factory.createSPHModel();

		model.setCellX(gridCellsX);
		model.setCellY(gridCellsY);
		model.setCellZ(gridCellsZ);

		int index = 0;

		for(int i = 0;i<SPHConstants.PARTICLE_COUNT;i++)
		{
			if(i != 0)
			{
				index = index + 4;
			}

			float r = ((float)MathUtils.randomGenerator.nextInt(PhysicsConstants.RAND_MAX) / (float)PhysicsConstants.RAND_MAX );//tr.rand.next();

			Vector3D positionVector = factory.createVector3D();
			Vector3D velocityVector = factory.createVector3D();
			positionVector.setX(MathUtils.scale(SPHConstants.XMIN, SPHConstants.XMAX/10 , r)); 
			r = ((float)MathUtils.randomGenerator.nextInt(PhysicsConstants.RAND_MAX) / (float)PhysicsConstants.RAND_MAX );//tr.rand.next();
			positionVector.setY(MathUtils.scale(SPHConstants.YMIN, SPHConstants.YMAX , r)); 
			r = ((float)MathUtils.randomGenerator.nextInt(PhysicsConstants.RAND_MAX) / (float)PhysicsConstants.RAND_MAX );//tr.rand.next();
			positionVector.setZ(MathUtils.scale(SPHConstants.ZMIN, SPHConstants.ZMAX , r));
			positionVector.setP(0f);
			r = ((float)MathUtils.randomGenerator.nextInt(PhysicsConstants.RAND_MAX) / (float)PhysicsConstants.RAND_MAX );//tr.rand.next();
			velocityVector.setX(MathUtils.scale(-1.0f, 1.0f, r));
			r = ((float)MathUtils.randomGenerator.nextInt(PhysicsConstants.RAND_MAX) / (float)PhysicsConstants.RAND_MAX );//tr.rand.next();
			velocityVector.setY(MathUtils.scale(-1.0f, 1.0f, r));
			r = ((float)MathUtils.randomGenerator.nextInt(PhysicsConstants.RAND_MAX) / (float)PhysicsConstants.RAND_MAX );//tr.rand.next();
			velocityVector.setZ(MathUtils.scale(-1.0f, 1.0f, r));
			velocityVector.setP(0f);

			SPHParticle particle = factory.createSPHParticle();
			particle.setPositionVector(positionVector);
			particle.setVelocityVector(velocityVector);
			particle.setMass(1f);
			model.getParticles().add(particle);

		}
		return model;
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
				System.out.println("Particle " + (i + 1) + ": mass "
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
