/*
 * Copyright NEC Europe Ltd. 2006-2007
 * 
 * This file is part of the context simulator called Siafu.
 * 
 * Siafu is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * Siafu is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.nec.nle.siafu.edgeFogCloud;

import static de.nec.nle.siafu.edgeFogCloud.Constants.DEFAULT_SPEED;
import static de.nec.nle.siafu.edgeFogCloud.Constants.EDGEPLACES;

import static de.nec.nle.siafu.edgeFogCloud.Constants.PlaceFields.*;

import static de.nec.nle.siafu.edgeFogCloud.Constants.Fields.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import de.nec.nle.siafu.behaviormodels.BaseAgentModel;
import de.nec.nle.siafu.edgeFogCloud.Constants.Activity;
import de.nec.nle.siafu.edgeFogCloud.ontology.DiseaseOntologyController;
import de.nec.nle.siafu.exceptions.InfoUndefinedException;
import de.nec.nle.siafu.exceptions.PlaceNotFoundException;
import de.nec.nle.siafu.exceptions.PlaceTypeUndefinedException;
import de.nec.nle.siafu.model.Agent;
import de.nec.nle.siafu.model.Place;
import de.nec.nle.siafu.model.Position;
import de.nec.nle.siafu.model.World;
import de.nec.nle.siafu.types.Publishable;
import de.nec.nle.siafu.types.Text;

/**
 * This class extends the {@link BaseAgentModel} and implements the behaviour of
 * an agent in the simulation.
 * 
 * @see de.nec.nle.siafu.edgeFogCloud
 * @author João Pedro de Souza Jardim da Costa
 * 
 */
public class AgentModel extends BaseAgentModel {

	/** The unique cloud server. */
	private Place cloudServer;

	/**
	 * Instantiates this agent model.
	 * 
	 * @param world the simulation's world
	 */
	public AgentModel(final World world) {
		super(world);
		try {
			cloudServer = world.getPlacesOfType("Cloud").iterator().next();

		} catch (PlaceTypeUndefinedException e) {
			throw new RuntimeException("The cloud server is undefined", e);
		}
	}

	/**
	 * Create the agents for the simulation. There's two types: reduced and
	 * original. The main difference lies in if its data is reduced or not.
	 * 
	 * @return the created agents.
	 */
	@Override
	public ArrayList<Agent> createAgents() {
		
		ArrayList<Agent> packages = new ArrayList<Agent>();
		createPackageSensitiveToOrigin(packages);
		return packages;
	}

	/**
	 * This method creates all the workers for the simulation.
	 * 
	 * @param packages the array where you need to put your created agents
	 * @param origin   the type of place of origin (edge vs house)
	 */
	private void createPackage(final ArrayList<Agent> packages, final String origin) {
		Iterator<Place> originIt;
		Place randomEdgeOrigin;
		try {
			randomEdgeOrigin = world.getRandomPlaceOfType("Edge");
			originIt = world.getPlacesOfType(origin).iterator();
		} catch (PlaceTypeUndefinedException e) {
			throw new RuntimeException("No houses defined", e);
		} catch (PlaceNotFoundException e) {
			throw new RuntimeException("No edge servers defined", e);
		}

		int i = 0;
		while (originIt.hasNext() && i<40) {
			Place placeOfOrigin = originIt.next();
			String packageData;

			Agent a = new Agent("Package" + i, placeOfOrigin.getPos(), "CarBlue", world);
			a.setVisible(false);

			packageData = DiseaseOntologyController.getInstance().getRandomSetOfValues();
			Integer packageSize = packageData.length()-4;
			int packageSpeed = calculateSpeed(packageSize);
			
			a.set(PDATA, new Text(packageData));
			a.set(PSIZE, new Text(packageSize.toString()));
			a.set(ORIGIN, placeOfOrigin);
			a.set(ACTIVITY, Activity.ONHOLD);
			a.set(FOGTARGET, new Text("none"));
			a.set(TEMPDEST, new Text("none"));
			a.setSpeed(packageSpeed);
			packages.add(a);
			i++;
		}
		
		Agent interoperabilityPackage = new Agent("InteroperabilityPackage",
				randomEdgeOrigin.getPos(), "CarRed", world);
		
		restartInteroperability(interoperabilityPackage);
		packages.add(interoperabilityPackage);
		
	}

	/**
	 * This method creates all the workers for the simulation.
	 * 
	 * @param packages the array where you need to put your created agents
	 * @param origin   the type of place of origin (edge vs house)
	 */
	private void createPackageSensitiveToOrigin(final ArrayList<Agent> packages) {

		Place randomEdgeOrigin;
		try {
			randomEdgeOrigin = world.getRandomPlaceOfType("Edge");
			int i = 0;
			while (i<40) {
				Place placeOfOrigin;
				if(i%2==0)
				{
					placeOfOrigin = world.getRandomPlaceOfType("Edge");
				}
				else
				{
					placeOfOrigin = world.getRandomPlaceOfType("House");
				}
				String packageData;

				Agent a = new Agent("Package" + i, placeOfOrigin.getPos(), "CarBlue", world);
				a.setVisible(false);

				packageData = DiseaseOntologyController.getInstance().getRandomSetOfValues();
				Integer packageSize = packageData.length()-4;
				int packageSpeed = calculateSpeed(packageSize);
				
				a.set(PDATA, new Text(packageData));
				a.set(PSIZE, new Text(packageSize.toString()));
				a.set(ORIGIN, placeOfOrigin);
				a.set(ACTIVITY, Activity.ONHOLD);
				a.set(FOGTARGET, new Text("none"));
				a.set(TEMPDEST, new Text("none"));
				a.setSpeed(packageSpeed);
				packages.add(a);
				i++;
			}
			
		} catch (PlaceTypeUndefinedException e) {
			throw new RuntimeException("No houses defined", e);
		} catch (PlaceNotFoundException e) {
			throw new RuntimeException("No edge servers defined", e);
		}
		
		Agent interoperabilityPackage = new Agent("InteroperabilityPackage",
				randomEdgeOrigin.getPos(), "CarRed", world);
		
		restartInteroperability(interoperabilityPackage);
		packages.add(interoperabilityPackage);
		
	}

	/**
	 * Handle the agents by checking if they need to respond to an event, go to the
	 * toilet or go/come home.
	 * 
	 * @param agents the people in the simulation
	 */
	@Override
	public void doIteration(final Collection<Agent> agents) {

		Iterator<Agent> packagesIt = agents.iterator();
		while (packagesIt.hasNext()) {
			
			Agent currentPackage = packagesIt.next();
			
			if(!currentPackage.getName().contains("InteroperabilityPackage"))
			{
				handlePackage(currentPackage);
			}
			else
			{
				handleInteroperabilityPackage(currentPackage);
			}
		}
	}

	/**
	 * Handle the people in the simulation.
	 * 
	 * @param a   the agent to handle
	 */
	private void handlePackage(final Agent a) {
		if (!a.isOnAuto()) {
			return; // This guy's being managed by the user interface
		}
		Place originPlace = (Place) a.get(ORIGIN);
		try {
			switch ((Activity) a.get(ACTIVITY)) {
			case ONHOLD:
				if(a.getPos()==originPlace.getPos())
				{
					if(originPlace.getName().contains("Edge"))
					{
						reducePackageData(a);
					}
					a.setVisible(true);
					Place nearestFog = getNearestFogServer(a);
					goToFog(a, nearestFog);
				}
				break;
			case GOING_2_CLOUD:
				if(a.isAtDestination())
				{
					Place currentLocation = cloudServer;
					int packageSize = Integer.parseInt(a.get(PSIZE).toString());
					if(originPlace.getName().contains("House"))
					{
						updateReceivedDataFromAPP1(currentLocation, packageSize, null);
					}
					else
					{
						updateReceivedDataFromAPP2(currentLocation, packageSize, null);
					}
					Place newOrigin = getRandomPlaceEqualsToOrigin(a);
					setOrigin(a, newOrigin);
					sendToOrigin(a);
				}
				break;
			case GOING_2_FOG:
				if(a.isAtDestination())
				{
					Place currentLocation = getNearestFogServer(a);
					int packageSize = 0;
					if(originPlace.getName().contains("House"))
					{
						reducePackageData(a);
						packageSize = Integer.parseInt(a.get(PSIZE).toString());
						updateReceivedDataFromAPP1(currentLocation, packageSize, a.get(PDATA));
					}
					else
					{
						packageSize = Integer.parseInt(a.get(PSIZE).toString());
						updateReceivedDataFromAPP2(currentLocation, packageSize, a.get(PDATA));
					}
					goToCloud(a);
				}
				break;
			default:
				throw new RuntimeException("Unknown Activity");
			}

		} catch (InfoUndefinedException e) {
			throw new RuntimeException("Unknown info requested for " + a, e);
		}
	}
	
	/**
	 * Handle the package that will handle the interoperability
	 *  between the applications in the simulation.
	 * 
	 * @param a   the agent to handle
	 */
	private void handleInteroperabilityPackage(final Agent a) {
		if (!a.isOnAuto()) {
			return; // This guy's being managed by the user interface
		}
		Place originPlace = (Place) a.get(ORIGIN);
		try {
			switch ((Activity) a.get(ACTIVITY)) {
			case ONHOLD:
				if(a.getPos()==originPlace.getPos())
				{
					Place interoperabilityTarget = (Place) a.get(FOGTARGET);
					if(!interoperabilityTarget.get(APP1LASTPACKAGE).toString().equals("none"))
					{
						a.setVisible(true);
						Place nearestFog = getNearestFogServer(a);
						a.set(TEMPDEST, nearestFog);
						goToFog(a, nearestFog);
					}
					else
					{
						restartInteroperability(a);
					}
				}
				break;
			case GOING_2_FOG:
				if(a.isAtDestination())
				{
					Place tempDestination = (Place) a.get(TEMPDEST);
					if(tempDestination.equals(a.get(FOGTARGET)))
					{
						a.set(PDATA, tempDestination.get(APP1LASTPACKAGE));
						a.set(PSIZE,new Text("16"));
						goToEdgeServer(a, (Place) a.get(ORIGIN));
					}
					else if(tempDestination.getType().equals("Fog"))
					{
						goToCloud(a);
					}
					else
					{
						goToEdgeServer(a, tempDestination);
					}
				}
				break;
			case GOING_2_CLOUD:
				if(a.isAtDestination())
				{
					Place fogTarget = (Place) a.get(FOGTARGET);
					Place tempDestination = (Place) a.get(TEMPDEST);
					a.set(PDATA, fogTarget.get(APP1LASTPACKAGE));
					a.set(PSIZE,new Text("16"));
					a.set(TEMPDEST, a.get(ORIGIN));
					goToFog(a, tempDestination);
				}
				break;
			case GOING_2_EDGE_SERVER:
				if(a.isAtDestination())
				{
					Place destination = (Place) a.get(ORIGIN);
					destination.set(RINTEROPERABILITYPACK, a.get(PDATA));
					
					expandPackageData(a);
					
					destination.set(INTEROPERABILITYPACK, a.get(PDATA));
					restartInteroperability(a);
				}
				break;
			default:
				throw new RuntimeException("Unknown Activity");
			}
	
		} catch (InfoUndefinedException e) {
			throw new RuntimeException("Unknown info requested for " + a, e);
		}
	}

	/**
	 * Send the agent to a fog server.
	 * @param edgeServer the edge server the agent is sent to.
	 * 
	 */
	private void goToEdgeServer(final Agent a, Place edgeServer) {
		a.setDestination(edgeServer);
		a.set(ACTIVITY, Activity.GOING_2_EDGE_SERVER);
	}

	/**
	 * Send the agent to a fog server.
	 * @param fogServer the fog server the agent is sent to.
	 * 
	 */
	private void goToFog(final Agent a, Place fogServer) {
		a.setDestination(fogServer);
		a.set(ACTIVITY, Activity.GOING_2_FOG);
	}

	/**
	 * Send the agent to a cloud server.
	 * 
	 */
	private void goToCloud(final Agent a) {
		a.setDestination(cloudServer);
		a.set(ACTIVITY, Activity.GOING_2_CLOUD);
	}

	/**
	 * Send the agent back to origin.
	 * 
	 */
	private void sendToOrigin(final Agent a) {
		String packageData;
		Integer packageSize;
		
		packageData = DiseaseOntologyController.getInstance().getRandomSetOfValues();
		packageSize = packageData.length()-4;
		int packageSpeed = calculateSpeed(packageSize);
		
		Place originPlace = (Place) a.get(ORIGIN);
		Position originPos = originPlace.getPos();
		a.setImage("CarBlue");
		a.set(PDATA, new Text(packageData));
		a.set(PSIZE, new Text(packageSize.toString()));
		a.setPos(originPos);
		a.setVisible(false);
		a.set(ACTIVITY, Activity.ONHOLD);
		a.setSpeed(packageSpeed);
	}
	
	/**
	 * Restart the interoperability process 
	 * from a new edge server.
	 * 
	 * @param a the package designated for interoperability.
	 */
	private void restartInteroperability(final Agent a) {
		
		Place randomEdgeOrigin;
		try {
			Place randomFogForInteroperability = world.getRandomPlaceOfType("Fog");
			randomEdgeOrigin = world.getRandomPlaceOfType("Edge");
			Position originPos = randomEdgeOrigin.getPos();

			a.setVisible(false);
			a.setPos(originPos);
			a.setImage("CarRed");
			a.set(PDATA, new Text("none"));
			a.set(PSIZE, new Text("none"));
			a.set(ORIGIN, randomEdgeOrigin);
			a.set(ACTIVITY, Activity.ONHOLD);
			a.set(FOGTARGET, randomFogForInteroperability);
			a.set(TEMPDEST, new Text("none"));
			a.setSpeed(DEFAULT_SPEED);
			
			
			
		} catch (PlaceNotFoundException e) {
			throw new RuntimeException("No Edge or Fog servers defined", e);
		}
		
	}

	/**
	 * Set the agent's new origin.
	 * @param newOrigin the new origin given to the agent.
	 * 
	 */
	private void setOrigin(final Agent a, Place newOrigin) {
		a.set(ORIGIN, newOrigin);
	}
	
	private Place getRandomPlaceEqualsToOrigin(Agent a)
	{
		Place originPlace = (Place) a.get(ORIGIN);
		if(originPlace.getName().contains("Edge"))
		{
			return getRandomHospital();
		}
		else
		{
			return getRandomHouse();
		}
	}
	/**
	 * get a random Place from House or Edge.
	 * @return the random Place.
	 * 
	 */
	private Place getRandomEdgePlace()
	{
		int quantEdgeTypes = EDGEPLACES.length;
		int houseOrEdge = getRandomInt(quantEdgeTypes);
		String placeName = EDGEPLACES[houseOrEdge];
		Place randomPlace;
		try {
			randomPlace = world.getRandomPlaceOfType(placeName);
		} catch (PlaceNotFoundException e) {
			throw new RuntimeException("No houses or edge servers defined", e);
		}
		
		return randomPlace;
	}
	

	/**
	 * get a Place from House.
	 * @return the random Place.
	 * 
	 */
	private Place getRandomHospital()
	{

		String placeName = EDGEPLACES[1];
		Place randomPlace;
		try {
			randomPlace = world.getRandomPlaceOfType(placeName);
		} catch (PlaceNotFoundException e) {
			throw new RuntimeException("No houses or edge servers defined", e);
		}
		
		return randomPlace;
	}
	
	/**
	 * get a Place from Edge.
	 * @return the random Place.
	 * 
	 */
	private Place getRandomHouse()
	{

		String placeName = EDGEPLACES[0];
		Place randomPlace;
		try {
			randomPlace = world.getRandomPlaceOfType(placeName);
		} catch (PlaceNotFoundException e) {
			throw new RuntimeException("No houses or edge servers defined", e);
		}
		
		return randomPlace;
	}
	/**
	 * get the nearest fog server to the position of the Agent.
	 * @param a the agent used to get the position as reference.
	 * @return the Place of the nearest fog server.
	 * 
	 */
	private Place getNearestFogServer(Agent a)
	{
		Position agentPos = a.getPos();
		Place nearestFogServer;
		
		try {
			nearestFogServer = world.getNearestPlaceOfType("Fog", agentPos);
		} catch (PlaceTypeUndefinedException e) {
			throw new RuntimeException("No houses or edge servers defined", e);
		} 
		
		return nearestFogServer;
	}

	
	/**
	 * Updates the reduced data in the package with 
	 * the data in its original format.
	 * 
	 * @param a the Agent that represents the package.
	 */
	private void expandPackageData(Agent a)
	{
		String packageData = a.get(PDATA).toString();
		String[] packageSplitData = packageData.split(";;");
		String diseaseIDString = packageSplitData[0];
		String diseaseDriverIDString = packageSplitData[1];
		String symptomIDString = packageSplitData[2];
		String transmissionIDString = packageSplitData[3];
		
		int diseaseID = Integer.parseInt(diseaseIDString),
				diseaseDriverID = Integer.parseInt(diseaseDriverIDString),
				symptomID = Integer.parseInt(symptomIDString),
				transmissionID = Integer.parseInt(transmissionIDString);
		
		String originalDiseaseData = DiseaseOntologyController
				.getInstance().getOriginalValue(diseaseID);
		String originalDiseaseDriverData = DiseaseOntologyController
				.getInstance().getOriginalValue(diseaseDriverID);
		String originalSymptomData = DiseaseOntologyController
				.getInstance().getOriginalValue(symptomID);
		String originalTransmissionData = DiseaseOntologyController
				.getInstance().getOriginalValue(transmissionID);
		
		String expandedData = originalDiseaseData.concat(";;")
				.concat(originalDiseaseDriverData).concat(";;")
				.concat(originalSymptomData).concat(";;")
				.concat(originalTransmissionData);
		
		int originalDataSize = expandedData.length()-4;
		int packageSpeed = calculateSpeed(originalDataSize);
		a.set(PDATA, new Text(expandedData));
		a.set(PSIZE, new Text(String.valueOf(originalDataSize)));
		a.setSpeed(packageSpeed);
		a.setImage("CarBlue");
	}


	/**
	 * Updates the data in the package with 
	 * the data in its reduced format.
	 * 
	 * @param a the Agent that represents the package.
	 */
	private void reducePackageData(Agent a)
	{
		String packageData = a.get(PDATA).toString();
		String[] packageSplitData = packageData.split(";;");
		String diseaseLabel = packageSplitData[0];
		String diseaseDriverLabel = packageSplitData[1];
		String symptomLabel = packageSplitData[2];
		String transmissionLabel = packageSplitData[3];
		
		int reducedDiseaseData = DiseaseOntologyController
				.getInstance().getReducedDiseaseValue(diseaseLabel);
		int reducedDiseaseDriverData = DiseaseOntologyController
				.getInstance().getReducedDiseaseDriverValue(diseaseDriverLabel);
		int reducedSymptomData = DiseaseOntologyController
				.getInstance().getReducedSymptomValue(symptomLabel);
		int reducedTransmissionData = DiseaseOntologyController
				.getInstance().getReducedTranmissionValue(transmissionLabel);
		
		String reducedData = String.valueOf(reducedDiseaseData).concat(";;")
				.concat(String.valueOf(reducedDiseaseDriverData)).concat(";;")
				.concat(String.valueOf(reducedSymptomData)).concat(";;")
				.concat(String.valueOf(reducedTransmissionData));
		int reducedDataSize = 16;
		int packageSpeed = calculateSpeed(reducedDataSize);
		a.set(PDATA, new Text(reducedData));
		a.set(PSIZE, new Text(String.valueOf(reducedDataSize)));
		a.setSpeed(packageSpeed);
		a.setImage("CarYellow");
	}

	/**
	 * Update the amount of packages and the total amount of data the current
	 * server received from the application 1.
	 * 
	 * @param currentLocation the server to have its data updated.
	 * @param the size of the data the server received.
	 */
	private void updateReceivedDataFromAPP1(Place currentLocation, int dataSize, Publishable receivedData)
	{
		if(currentLocation!=null)
		{
			int packsReceived = Integer.parseInt(currentLocation.get(APP1PACKAMOUNT).toString());
			int totalSizeReceived = Integer.parseInt(currentLocation.get(APP1TOTALSIZE).toString());
			packsReceived++;
			totalSizeReceived=totalSizeReceived+dataSize;
			currentLocation.set(APP1PACKAMOUNT, new Text(String.valueOf(packsReceived)));
			currentLocation.set(APP1TOTALSIZE, new Text(String.valueOf(totalSizeReceived)));
			
			if(receivedData!=null)
			{
				currentLocation.set(APP1LASTPACKAGE, receivedData);
			}
		}
	}

	/**
	 * Update the amount of packages and the total amount of data the current
	 * server received from the application 2.
	 * 
	 * @param currentLocation The server to have its data updated.
	 * @param The size of the data the server received.
	 */
	private void updateReceivedDataFromAPP2(Place currentLocation, int dataSize, Publishable receivedData)
	{
		if(currentLocation!=null)
		{
			int packsReceived = Integer.parseInt(currentLocation.get(APP2PACKAMOUNT).toString());
			int totalSizeReceived = Integer.parseInt(currentLocation.get(APP2TOTALSIZE).toString());
			packsReceived++;
			totalSizeReceived=totalSizeReceived+dataSize;
			currentLocation.set(APP2PACKAMOUNT, new Text(String.valueOf(packsReceived)));
			currentLocation.set(APP2TOTALSIZE, new Text(String.valueOf(totalSizeReceived)));
			
			if(receivedData!=null)
			{
				currentLocation.set(APP2LASTPACKAGE, receivedData);
			}
		}
	}
	
	
	/**
	 * Get a random Int between 0 and an upper limit-1.
	 * @param upperLimit The upper limit to the range of possible numbers.
	 * @return The random int generated.
	 * 
	 */
	private int getRandomInt(int upperLimit)
	{
		Random numberGenerator = new Random();
		int randomNumber = (numberGenerator.nextInt(upperLimit));
		return randomNumber;
	}
	
	/**
	 * Calculates the new speed of the package according with the size
	 * of the data the package is transporting
	 * 
	 * @param dataSize An int representing the size of the data in bytes
	 * @return The new speed as int
	 */
	private int calculateSpeed(int dataSize)
	{
		int newSpeed = DEFAULT_SPEED*16;
		float decimalSpeed = (float)newSpeed /dataSize;
		
		newSpeed = Math.round(decimalSpeed);
		
		if(newSpeed>0)
		{
			return newSpeed;
		}
		else
		{
			return 1;
		}
	}
}
