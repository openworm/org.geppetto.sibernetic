package org.geppetto.sibernetic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geppetto.core.model.GeppettoModelAccess;
import org.geppetto.core.recordings.GeppettoRecordingCreator;
import org.geppetto.model.types.TypesPackage;
import org.geppetto.model.util.GeppettoModelException;
import org.geppetto.model.values.Pointer;

import ncsa.hdf.object.h5.H5File;

public class ConvertSiberneticToRecording
{

	private static Log logger = LogFactory.getLog(ConvertSiberneticToRecording.class);

	private static final String CONNECTIONS_FILE = "connection_buffer.txt";
	private static final String MUSCLES_ACTIVATION_FILE = "muscles_activity_buffer.txt";
	private static final String MEMBRANES_BUFFER_FILE = "membranes_buffer.txt";
	private static final String PARTICLES_POSITION_FILE = "position_buffer.txt";
	private static final String WORM_MIDLINE_FILE = "worm_motion_log.txt";

	private GeppettoRecordingCreator recordingCreator;
	private GeppettoModelAccess geppettoModelAccess;
	private String siberneticRecordingFolder;

	private final int SAMPLING = 200;

	/**
	 * @param geppettoRecordingFile
	 */
	public ConvertSiberneticToRecording(String siberneticRecordingFolder, String geppettoRecordingFile, GeppettoModelAccess modelAccess)
	{
		// call the class in charge of creating the hdf5
		recordingCreator = new GeppettoRecordingCreator(geppettoRecordingFile);
		this.siberneticRecordingFolder = siberneticRecordingFolder;
		geppettoModelAccess = modelAccess;
	}

	/**
	 * @throws Exception
	 */
	public void convert() throws Exception
	{
		convertParticlesPosition();
		convertMusclesActivationSignal();
		convertWormMidline();

		List<Double> time = new ArrayList<Double>();
		for(double i = 0; i <= 5; i = i + 0.1d)
		{

			time.add((double) Math.round(i * 1000d) / 1000d);
		}
		Pointer pointer = geppettoModelAccess.getPointer("time");
		recordingCreator.addValues(pointer.getInstancePath(), time.toArray(new Double[] {}), "", TypesPackage.Literals.STATE_VARIABLE_TYPE.getName(), false);
		recordingCreator.create();
	}

	private void convertWormMidline() throws NumberFormatException, IOException, GeppettoModelException
	{
		BufferedReader midlineFile = getBufferedReader(WORM_MIDLINE_FILE);
		List<List<Double>> x = new ArrayList<List<Double>>();
		List<List<Double>> y = new ArrayList<List<Double>>();
		List<List<Double>> z = new ArrayList<List<Double>>();
		int timestep = 0;
		int start = -1;
		for(String line; (line = midlineFile.readLine()) != null;)
		{
			start++;
			if(start % SAMPLING != 0)
			{
				continue;
			}
			{
				String[] columns = line.split("\\s+");
				List<Double> variable = null;
				for(int i = 0; i < columns.length; i++)
				{
					if(i == 0)
					{
						// column timestep
						x.add(new ArrayList<Double>());
						y.add(new ArrayList<Double>());
						z.add(new ArrayList<Double>());
					}
					else if(columns[i].equalsIgnoreCase("X:"))
					{
						variable = x.get(timestep);
					}
					else if(columns[i].equalsIgnoreCase("Y:"))
					{
						variable = y.get(timestep);
					}
					else if(columns[i].equalsIgnoreCase("Z:"))
					{
						variable = z.get(timestep);
					}
					else
					{
						variable.add(Double.valueOf(columns[i]));
					}
				}
			}
			timestep++;
		}

		processMDArray(x, "worm(worm).midline(midline).x(StateVariable)");
		processMDArray(y, "worm(worm).midline(midline).y(StateVariable)");
		processMDArray(z, "worm(worm).midline(midline).z(StateVariable)");

		midlineFile.close();
	}

	private void processMDArray(List<List<Double>> coordinate, String instancePath) throws GeppettoModelException
	{

		// Pointer pointer = geppettoModelAccess.getPointer(instancePath);

		Double[][] array = new Double[coordinate.size()][];
		for(int i = 0; i < coordinate.size(); i++)
		{
			List<Double> row = coordinate.get(i);
			array[i] = row.toArray(new Double[row.size()]);
		}

		recordingCreator.addValues(instancePath, array, "", TypesPackage.Literals.STATE_VARIABLE_TYPE.getName(), false);
	}

	private void convertMusclesActivationSignal() throws GeppettoModelException, NumberFormatException, IOException
	{
		BufferedReader musclesFile = getBufferedReader(MUSCLES_ACTIVATION_FILE);
		Map<Integer, List<Double>> activationValues = new HashMap<Integer, List<Double>>();
		// In the muscles activation file every line is a timestep
		int start = -1;

		for(String line; (line = musclesFile.readLine()) != null;)
		{
			start++;
			if(start % SAMPLING != 0)
			{
				continue;
			}
			{
				// every column is a different muscle
				String[] columns = line.split("\\s+");
				for(int i = 0; i < columns.length; i++)
				{
					if(activationValues.get(i) == null)
					{
						activationValues.put(i, new ArrayList<Double>());
					}
					if(columns[i].equalsIgnoreCase("nan"))
					{
						activationValues.get(i).add(Double.NaN);
					}
					else
					{
						activationValues.get(i).add(Double.valueOf(columns[i]));
					}
				}
			}
		}

		// Let's write to the Geppetto recording
		for(Integer muscle : activationValues.keySet())
		{
			String instancePath = "worm.muscle_activation_" + (muscle + 1) + "";
			Pointer pointer = geppettoModelAccess.getPointer(instancePath);

			recordingCreator.addValues(pointer.getInstancePath(), activationValues.get(muscle).toArray(new Double[] {}), "", TypesPackage.Literals.STATE_VARIABLE_TYPE.getName(), false);
		}

		musclesFile.close();
	}

	private BufferedReader getBufferedReader(String file) throws FileNotFoundException
	{
		return new BufferedReader(new FileReader(this.siberneticRecordingFolder + File.separator + file));
	}

	private void convertParticlesPosition() throws NumberFormatException, IOException, GeppettoModelException
	{
		BufferedReader particlesFile = getBufferedReader(PARTICLES_POSITION_FILE);
		BufferedReader connectionsFile = getBufferedReader(CONNECTIONS_FILE);
		BufferedReader membranesFile = getBufferedReader(MEMBRANES_BUFFER_FILE);

		Set<Integer> cuticleParticles = new HashSet<Integer>();
		membranesFile.readLine(); //the first line is not a triangle
		for(String membrane; (membrane = membranesFile.readLine()) != null;)
		{
			StringTokenizer membraneTokenizer = new StringTokenizer(membrane);
			cuticleParticles.add(Integer.parseInt(membraneTokenizer.nextToken()));
			if(membraneTokenizer.hasMoreTokens()){
				cuticleParticles.add(Integer.parseInt(membraneTokenizer.nextToken()));
			}
			if(membraneTokenizer.hasMoreTokens()){
				cuticleParticles.add(Integer.parseInt(membraneTokenizer.nextToken()));
			}
		}

		// Let's first read the connections to know what particles belong to what muscles
		Map<String, String> particlesToMuscleBundles = new HashMap<String, String>();
		int numberOfMuscles = 0;
		for(String connection; (connection = connectionsFile.readLine()) != null;)
		{
			StringTokenizer connectionTokenizer = new StringTokenizer(connection);
			float particleFloat = Float.parseFloat(connectionTokenizer.nextToken());
			String particleId = ((Integer) ((int) particleFloat)).toString();
			connectionTokenizer.nextToken(); // we don't care about the second parameter, the eleastic spring
			float muscleBundleFloat = Float.parseFloat(connectionTokenizer.nextToken());
			int muscleBundleInt = (int) muscleBundleFloat;
			numberOfMuscles = Math.max(numberOfMuscles, muscleBundleInt);
			String muscleBundle = ((Integer) (muscleBundleInt)).toString();
			if(!muscleBundle.equals("0"))
			{
				particlesToMuscleBundles.put(particleId, muscleBundle);
			}
		}

		Map<String, List<List<Double>>> x = new HashMap<String, List<List<Double>>>();
		Map<String, List<List<Double>>> y = new HashMap<String, List<List<Double>>>();
		Map<String, List<List<Double>>> z = new HashMap<String, List<List<Double>>>();

		Integer currentLine = -1;
		Integer currentParticle = -1;
		Integer currentTimestep = 0;
		Integer elasticParticles = -1;
		Integer liquidParticles = -1;
		Integer boundaryParticles = -1;
		Float timestep = -1f;
		Integer sampling = -1;
		int start = 0;
		for(String position; (position = particlesFile.readLine()) != null;)
		{
			currentLine++;
			// the particles file has a bunch of parameters in the first ten lines...
			if(currentLine == 0 || currentLine == 1 || currentLine == 2 || currentLine == 3 || currentLine == 4 || currentLine == 5)
			{
				// boundary box, we don't need it
				continue;
			}
			else if(currentLine == 6)
			{
				elasticParticles = Integer.parseInt(position);
			}
			else if(currentLine == 7)
			{
				liquidParticles = Integer.parseInt(position);
			}
			else if(currentLine == 8)
			{
				boundaryParticles = Integer.parseInt(position);
			}
			else if(currentLine == 9)
			{
				timestep = Float.parseFloat(position);
			}
			else if(currentLine == 10)
			{
				sampling = Integer.parseInt(position);
			}
			else
			{
				currentParticle++;
				StringTokenizer positionTokenizer = new StringTokenizer(position);

				if(currentTimestep == 0 && currentParticle > (elasticParticles + liquidParticles + boundaryParticles - 1))
				{
					currentTimestep++;
					currentParticle = 0;
				}
				else if(currentTimestep > 0 && currentParticle > (elasticParticles + liquidParticles - 1))
				{
					currentTimestep++;
					currentParticle = 0;
				}
				if(currentTimestep % SAMPLING != 0)
				{
					continue;
				}
				String p = currentParticle.toString();
				Double singleX = Double.parseDouble(positionTokenizer.nextToken());
				Double singleY = Double.parseDouble(positionTokenizer.nextToken());
				Double singleZ = Double.parseDouble(positionTokenizer.nextToken());
				float typeFloat = Float.parseFloat(positionTokenizer.nextToken());
				String singleType = ((Integer) ((int) typeFloat)).toString();
				if(singleType.equals("3"))
				{
					continue;
				}

				List<String> variableIds = new ArrayList<String>();
				boolean accountedFor = false;
				if(cuticleParticles.contains(currentParticle))
				{
					variableIds.add("cuticle");
					accountedFor = true;
				}
				if(particlesToMuscleBundles.containsKey(p))
				{
					variableIds.add("muscle_" + particlesToMuscleBundles.get(p).replace(".", "_"));
					accountedFor = true;
				}
				if(!accountedFor)
				{
					variableIds.add("matter_" + singleType.toString().replace(".", "_"));
				}

				for(String variableId : variableIds)
				{
					if(currentTimestep == 0 && !x.containsKey(variableId))
					{
						x.put(variableId, new ArrayList<List<Double>>());
						y.put(variableId, new ArrayList<List<Double>>());
						z.put(variableId, new ArrayList<List<Double>>());
					}
					else if(!x.containsKey(variableId))
					{
						throw new GeppettoModelException("We should not discover new variable IDs after the first timestep!");
					}

					List<List<Double>> particlesX = x.get(variableId);
					List<List<Double>> particlesY = y.get(variableId);
					List<List<Double>> particlesZ = z.get(variableId);

					if(particlesX.size() <= currentTimestep / SAMPLING)
					{
						particlesX.add(new ArrayList<Double>());
						particlesY.add(new ArrayList<Double>());
						particlesZ.add(new ArrayList<Double>());
					}

					particlesX.get(currentTimestep / SAMPLING).add(singleX);
					particlesY.get(currentTimestep / SAMPLING).add(singleY);
					particlesZ.get(currentTimestep / SAMPLING).add(singleZ);
				}
			}

		}

		for(String variableId : x.keySet())
		{
			processMDArray(x.get(variableId), "worm(worm)." + variableId + "(particles).x");
		}
		for(String variableId : y.keySet())
		{
			processMDArray(y.get(variableId), "worm(worm)." + variableId + "(particles).y");
		}
		for(String variableId : z.keySet())
		{
			processMDArray(z.get(variableId), "worm(worm)." + variableId + "(particles).z");
		}

	}

	public H5File getRecordingsFile()
	{
		return this.recordingCreator.getRecordingsFile();
	}
}