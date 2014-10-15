/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (c) 2011, 2013 OpenWorm.
 * http://openworm.org
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT License
 * which accompanies this distribution, and is available at
 * http://opensource.org/licenses/MIT
 *
 * Contributors:
 *     	OpenWorm - http://openworm.org/people.html
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
 * USE OR OTHER DEALINGS IN THE SOFTWARE.
 *******************************************************************************/

package org.geppetto.model.sph.common;

import org.springframework.beans.support.ArgumentConvertingMethodInvoker;

public class SPHConstants {
	public static final int MAX_NEIGHBOR_COUNT = 32;
	public static final int MAX_MEMBRANES_INCLUDING_SAME_PARTICLE = 7;
	
	public static final float LIQUID_TYPE = 1.1f;
	public static final float ELASTIC_TYPE = 2.1f;
	public static final float BOUNDARY_TYPE = 3.1f;
	
	public static final int NO_CELL_ID = -1;
	public static final int NO_PARTICLE_ID = -1;
	
	public static final int DECIMAL_ROUNDING_FACTOR = 1000000;
	
	public static final float M_PI = 3.1415927f;
	public static final int RAND_MAX = 0x7fff;

	public static final float RHO0 = 1000.0f;					// Standard value of liquid density for water (kg/m^3)
	public static final float STIFFNESS = 0.75f;
	public static final float H = 3.34f;						// Smoothed radius value. This is dimensionless invariant parameter.
																// For taken real value in meter you need multiple this on simulationScale.
																// h is a spatial distance, over which their properties are "smoothed" by a kernel function [1].
    															// [1] https://en.wikipedia.org/wiki/Smoothed-particle_hydrodynamics
	
	public static final float R0 = 0.5f * H;					// Standard distance between two boundary particle == equilibrium distance between 2 particles [1]
    															// [1] M. Ihmsen, N. Akinci, M. Gissler, M. Teschner, Boundary Handling and Adaptive Time-stepping for PCISPH Proc. VRIPHYS, Copenhagen, Denmark, pp. 79-88, Nov 11-12, 2010.
	//TODO I think using constant like static is potential problem this will 
	//be changed each time when simulation will be restart or another user load his own config
	//We need make this non static
	public static final float MASS = 0.0003f;					// Mass for one particle (kg).
															    // TODO: make it as an input parameter non static
	
	public static final float HASH_GRID_CELL_SIZE = 2.0f * H;	// All bounding box is divided on small spatial cells with size of side == h. Size of side for one spatial cell
    															// This require for spatial hashing and => searching a neighbors
	
	public static final float HASH_GRID_CELL_SIZE_INV = 1.0f / HASH_GRID_CELL_SIZE;// Inverted value for hashGridCellSize
	public static final float SIMULATION_SCALE = (float) ( 0.004f * Math.pow( MASS, 1.f/3.f ) / Math.pow( 0.00025f, 1.f/3.f ) );// Simulation scale coefficient. It means that N * simulationScale
																															    // converts from simulation scale to meters N / simulationScale convert from meters simulation scale
																															    // If you want to take real value of distance in meters you need multiple on simulation scale
																															    // NOTE: simulationScale depends from mass of particle. If we place one particle
																																// into volume with some size of side we want that density in this value is equal to rho0
	
	public static final float SIMULATION_SCALE_INV = 1.0f / SIMULATION_SCALE;// Inverted value for simulationScale
	
	public static final float viscosity = 1.f;					// liquid viscosity value //why this value? Dynamic viscosity of water at 25 C = 0.89e-3 Pa*s
	
	public static final float TIME_STEP = 0.001f; 				// Time step of simulation (s)
															    // NOTE: "For numerical stability and convergence, several time step
															    // constraints must be satisfied. The Courant-Friedrich-Levy
															    // (CFL) condition for SPH (dt <= lambda_v*(h/v_max))
															    // states that the speed of numerical propagation must be
															    // higher than the speed of physical propagation, where v_max = max(||v_i(t)||)
															    // is the maximum magnitude of the velocity throughout the simulation. lambda_v is a constant factor, e. g.
															    // lambda_v = 0.4. In other words, a particle i must not move more than its smoothing length h in one time step. Fur-
															    // thermore, high accelerations might influence the simulation
															    // results negatively. Therefore, the time step must also satisfy
															    // dt <= lambda_f*sqrt(h/F_max)
															    // where F_max=max(||F_i(t)||) denotes the magnitude of
															    // the maximum force per unit mass for all particles throughout
															    // the simulation. lambda_f = 0.25."
															    // For more info [1, page 5].
															    // NOTE: actually it depends on mass too for bigger value of mass it possible
															    // to use bigger value of time step. Dependence on mass could be described by following
															    // mass influent on simulation scale and due to the fact that we're simulating incompressible
															    // liquid start configuration of particles should satisfy condition that density in
															    // every particle of configuration <= rh0. So if we decrease mass we should decrease
															    // distance among particles than it leads to that we should decrease time step.
															    // TODO: find dependence and make choice automatically
															    // [1] M. Ihmsen, N. Akinci, M. Gissler, M. Teschner, Boundary Handling and Adaptive Time-stepping for PCISPH Proc. VRIPHYS, Copenhagen, Denmark, pp. 79-88, Nov 11-12, 2010.
															    // ATTENTION! too large values can lead to 'explosion' of elastic matter objects
	
	public static final float CFLLimit = 100.0f;
	//Looks Like this is useless constant and will be removed but I need ask Andrey before
	public static float INTERNAL_PARTICLE_DISTANCE;
	public static float PREMILINARY_WORM_LENGTH;
	public static final float DAMPING = 0.75f; // TODO delete this unused parameter 
	
	public static final int MUSCLE_COUNT = 100;//increase this value and modify corresponding code if you plan to add more than 10 muscles

	public static double W_POLY_6_COEFFICIENT;					// Wpoly6Coefficient for kernel Wpoly6 [1]
																// [1] Solenthaler (Dissertation) page 17 eq. (2.20)
	
	public static double GRAD_W_SPIKY_COEFFICIENT;				// gradWspikyCoefficient for kernel gradWspiky [1]
    															// [1] Solenthaler (Dissertation) page 18 eq. (2.21)
	
	public static double DEL_2_W_VISCOSITY_COEFFICIENT;			// divgradWviscosityCoefficient for kernel Viscous [1]
    															// [1] Solenthaler (Dissertation) page 18 eq. (2.22)
		
	public static float MASS_MULT_WPOLY6COEFFICIENT;			// Conversion of double value to float. For work with only 1st precision arithmetic.
	public static float MASS_MULT_GRADWSPIKYCOEFFICIENT;		// It needs for work with devices don't support double precision.
	public static float MASS_MULT_DIVGRADWVISCOSITYCOEFFICIENT;	// Also it helps to increase performance and memory consumption
	/* We' re using Cartesian coordinate system
				y|
				 |___x
				 /
			   z/
	*/
	public static final float GRAVITY_X = 0.0f;					// Value of vector Gravity component x
	public static final float GRAVITY_Y = -9.8f;				// Value of vector Gravity component y
	public static final float GRAVITY_Z = 0.0f;					// Value of vector Gravity component z
	
	
	public static final float SURFACE_TENSION_COEFFICIENT = -0.0013f;// Taking from input configuration
	
	public static final float ELASTICITY_COEFFICIENT = 100000.0f * MASS;// Taking from input configuration

	public static double BETA = 0.0;							// B. Solenthaler's dissertation, formula 3.6 (end of page 30)
	public static double BETA_INV;								// BETA invariant
	public static float DELTA;
	public static float _hScaled;								// scaled smoothing radius
	public static float _hScaled2;								// squared scaled smoothing radius
	/**
	 * Recalculating all main simulation parameters from new value of simulationScale and mass 
	 * @param simulationScale
	 * new value of simulationScale
	 * @param mass
	 * new value of mass
	 */
	public static void setDependingParammeters(float simulationScale, float mass){
		if(simulationScale == 0.0f || mass == 0.0f)
			throw new IllegalArgumentException("Simulation parametrs couldn't be zero, check mass and simulationScale parametrs.");
		//TODO delete this unused constant
		INTERNAL_PARTICLE_DISTANCE = 0.5f * H * simulationScale;
		PREMILINARY_WORM_LENGTH = 311 * INTERNAL_PARTICLE_DISTANCE;
		//
		W_POLY_6_COEFFICIENT = (315.0 / ( 64.0 * M_PI * Math.pow( (double) (H * simulationScale), 9.0 ) ));
		GRAD_W_SPIKY_COEFFICIENT= (-45.0 / ( M_PI * Math.pow( (double)(H * simulationScale), 6.0 ) ));
		DEL_2_W_VISCOSITY_COEFFICIENT = -GRAD_W_SPIKY_COEFFICIENT;
		MASS_MULT_WPOLY6COEFFICIENT = (float) ( (double)mass * W_POLY_6_COEFFICIENT );
		MASS_MULT_GRADWSPIKYCOEFFICIENT = (float) ( (double)mass * GRAD_W_SPIKY_COEFFICIENT );
		MASS_MULT_DIVGRADWVISCOSITYCOEFFICIENT = (float) ( (double)mass * DEL_2_W_VISCOSITY_COEFFICIENT );
		_hScaled = H * simulationScale;
		_hScaled2 = _hScaled * _hScaled;
		DELTA = getDELTA(simulationScale, mass);
	}
	/**
	 * Recalculating beta from new value of timeStep and mass. New value of this parameters
	 * could be set from configuration.
	 * @param timeStep
	 * new value of timeStep
	 * @param mass
	 * new value of mass
	 */
	public static void setBeta(float timeStep, float mass){
		BETA = timeStep * timeStep * mass * mass * 2 / ( RHO0 * RHO0 );
		if(BETA != 0)
			BETA_INV = 1.0f / BETA;
		else
			throw new ArithmeticException("BETA wasn't init. Check configuration one of simulation's parametters is equal to zero check mass and timeStep.");
	}
	/**
	 * Calculating delta parameter.
	 * "In these situations,
	 * the SPH equations result in falsified values. To circumvent that problem, we pre-
	 * compute a single scaling factor δ according to the following formula [1, eq. 8] which is
	 * evaluated for a prototype particle with a filled neighborhood. The resulting value
	 * is then used for all particles. Finally, we end up with the following equations
	 * which are used in the PCISPH method" [1].
	 * [1] http://www.ifi.uzh.ch/vmml/publications/pcisph/pcisph.pdf
	 * 
	 * @param simulationScale
	 * @param mass
	 * @return
	 */
	private static float getDELTA(float simulationScale, float mass){
	    float x[] = { 1, 1, 0,-1,-1,-1, 0, 1, 1, 1, 0,-1,-1,-1, 0, 1, 1, 1, 0,-1,-1,-1, 0, 1, 2,-2, 0, 0, 0, 0, 0, 0 };
	    float y[] = { 0, 1, 1, 1, 0,-1,-1,-1, 0, 1, 1, 1, 0,-1,-1,-1, 0, 1, 1, 1, 0,-1,-1,-1, 0, 0, 2,-2, 0, 0, 0, 0 };
	    float z[] = { 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1,-1,-1,-1,-1,-1,-1,-1,-1, 0, 0, 0, 0, 2,-2, 1,-1 };

		float sum1_x = 0.f;
		float sum1_y = 0.f;
		float sum1_z = 0.f;
	    double sum1 = 0.f, sum2 = 0.f;
		float v_x = 0.f;
		float v_y = 0.f;
		float v_z = 0.f;
		float dist;
		float particleRadius = (float) Math.pow(mass/RHO0,1.f/3.f);
		float h_r_2;									

	    for (int i = 0; i < 32; i++)
	    {
			v_x = x[i] * 0.8f * particleRadius;
			v_y = y[i] * 0.8f * particleRadius;
			v_z = z[i] * 0.8f * particleRadius;

	        dist = (float) Math.sqrt(v_x*v_x+v_y*v_y+v_z*v_z);

	        if (dist <= H)
	        {
				h_r_2 = (float) Math.pow((H*simulationScale - dist),2);

	            sum1_x += h_r_2 * v_x / dist;
				sum1_y += h_r_2 * v_y / dist;
				sum1_z += h_r_2 * v_z / dist;

	            sum2 += h_r_2 * h_r_2;
	        }
	    }

		sum1 = sum1_x*sum1_x + sum1_y*sum1_y + sum1_z*sum1_z;
		if(BETA != 0.0)
			return (float) (1.0f / (BETA * GRAD_W_SPIKY_COEFFICIENT * GRAD_W_SPIKY_COEFFICIENT * (sum1 + sum2)));
		else
			throw new ArithmeticException("BETA wasn't initialized it equal to zero. DELTA isn't init.");
	}
}
