package org.geppetto.sibernetic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private static final String PARTICLES_POSITION_FILE = "position_buffer.txt";
	private static final String WORM_MIDLINE_FILE = "worm_motion_log.txt";

	private GeppettoRecordingCreator recordingCreator;
	private GeppettoModelAccess geppettoModelAccess;
	private String siberneticRecordingFolder;

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
		for(double i = 0; i <= 4.4; i = i + 0.01d)
		{

			time.add((double) Math.round(i * 100d) / 100d);
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
		// In the muscles activation file every line is a timestep
		for(String line; (line = midlineFile.readLine()) != null;)
		{

			{
				// every column is a different muscle
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

		process(x, "x");
		process(y, "y");
		process(z, "z");

		midlineFile.close();
	}

	private void process(List<List<Double>> coordinate, String name) throws GeppettoModelException
	{
		String instancePath = "worm.midline." + name;
		Pointer pointer = geppettoModelAccess.getPointer(instancePath);

		Double[][] array = new Double[coordinate.size()][];
		for(int i = 0; i < coordinate.size(); i++)
		{
			List<Double> row = coordinate.get(i);
			array[i] = row.toArray(new Double[row.size()]);
		}

		recordingCreator.addValues(pointer.getInstancePath(), array, "", TypesPackage.Literals.STATE_VARIABLE_TYPE.getName(), false);
	}

	private void convertMusclesActivationSignal() throws GeppettoModelException, NumberFormatException, IOException
	{
		BufferedReader musclesFile = getBufferedReader(MUSCLES_ACTIVATION_FILE);
		Map<Integer, List<Double>> activationValues = new HashMap<Integer, List<Double>>();
		// In the muscles activation file every line is a timestep
		for(String line; (line = musclesFile.readLine()) != null;)
		{
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
			String instancePath = "worm.muscle_activation_" + muscle + "";
			Pointer pointer = geppettoModelAccess.getPointer(instancePath);

			recordingCreator.addValues(pointer.getInstancePath(), activationValues.get(muscle).toArray(new Double[] {}), "", TypesPackage.Literals.STATE_VARIABLE_TYPE.getName(), false);
		}

		musclesFile.close();
	}

	private BufferedReader getBufferedReader(String file) throws FileNotFoundException
	{
		return new BufferedReader(new FileReader(this.siberneticRecordingFolder + File.separator + file));
	}

	private void convertParticlesPosition()
	{
		// TODO Auto-generated method stub

	}

	public H5File getRecordingsFile()
	{
		return this.recordingCreator.getRecordingsFile();
	}
}