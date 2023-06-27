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
import static de.nec.nle.siafu.edgeFogCloud.Constants.POPULATION;
import static de.nec.nle.siafu.edgeFogCloud.Constants.Fields.ACTIVITY;
import static de.nec.nle.siafu.edgeFogCloud.Constants.Fields.SERVER;
import static de.nec.nle.siafu.edgeFogCloud.Constants.Fields.TEMPORARY_DESTINATION;
import static de.nec.nle.siafu.edgeFogCloud.Constants.Fields.TYPE;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;

import de.nec.nle.siafu.behaviormodels.BaseAgentModel;
import de.nec.nle.siafu.edgeFogCloud.Constants.Activity;
import de.nec.nle.siafu.exceptions.InfoUndefinedException;
import de.nec.nle.siafu.exceptions.PlaceNotFoundException;
import de.nec.nle.siafu.exceptions.PlaceTypeUndefinedException;
import de.nec.nle.siafu.model.Agent;
import de.nec.nle.siafu.model.Place;
import de.nec.nle.siafu.model.World;
import de.nec.nle.siafu.types.BooleanType;
import de.nec.nle.siafu.types.EasyTime;
import de.nec.nle.siafu.types.Publishable;
import de.nec.nle.siafu.types.Text;
import de.nec.nle.siafu.types.TimePeriod;

/**
 * This class extends the {@link BaseAgentModel} and implements the behaviour
 * of an agent in the office simulation.
 * 
 * @see de.nec.nle.siafu.edgeFogCloud
 * @author miquel
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
	 * Create the agents for the Office simulation. There's two types: reduced
	 * and original. The main difference lies in if its data is reduced or not.
	 * 
	 * @return the created agents.
	 */
	@Override
	public ArrayList<Agent> createAgents() {
		ArrayList<Agent> packages = new ArrayList<Agent>(POPULATION);
		createPackage(packages, "original", "edge");
		return packages;
	}

	/**
	 * This method creates all the workers for the office simulation.
	 * 
	 * @param packages the array where you need to put your created agents
	 * @param type the type of package (reduced vs original)
	 * @param serverType the type of server of origin (edge vs fog) 
	 */
	private void createPackage(final ArrayList<Agent> packages,
			final String type, final String serverType) {
		Iterator<Place> serverIt;
		try {
			serverIt = world.getPlacesOfType(serverType).iterator();
		} catch (PlaceTypeUndefinedException e) {
			throw new RuntimeException("No staff desks defined", e);
		}

		int i = 0;
		while (serverIt.hasNext()) {
			Place server = serverIt.next();

			Agent a =
					new Agent(type + "-" + i, cloudServer.getPos(), "HumanBlue",
							world);
			a.setVisible(false);
			String packageData;

			a.set(TYPE, new Text(type));
			a.set(SERVER, server);
			a.set(ACTIVITY, Activity.ONHOLD);
			a.set(TEMPORARY_DESTINATION, new Text("None"));
			a.setSpeed(DEFAULT_SPEED);
			packages.add(a);
			i++;
		}
	}

	/**
	 * Handle the agents by checking if they need to respond to an event, go
	 * to the toilet or go/come home.
	 * 
	 * @param agents the people in the simulation
	 */
	@Override
	public void doIteration(final Collection<Agent> agents) {
		Calendar time = world.getTime();
		EasyTime now =
				new EasyTime(time.get(Calendar.HOUR_OF_DAY), time
						.get(Calendar.MINUTE));

		Iterator<Agent> peopleIt = agents.iterator();
		while (peopleIt.hasNext()) {
			handlePerson(peopleIt.next(), now);
		}
	}

	/**
	 * Handle the people in the simulation.
	 * 
	 * @param a the agent to handle
	 * @param now the current time
	 */
	private void handlePerson(final Agent a, final EasyTime now) {
		if (!a.isOnAuto()) {
			return; // This guy's being managed by the user interface
		}
		try {
			switch ((Activity) a.get(ACTIVITY)) {
			case ONHOLD:
				break;
			default:
				throw new RuntimeException("Unknown Activity");
			}

		} catch (InfoUndefinedException e) {
			throw new RuntimeException("Unknown info requested for " + a,
					e);
		}
	}


	/**
	 * Send the agent to a fog server.
	 * 
	 */
	private void goToFog(final Agent a, Place server) {
		a.setDestination(server);
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
	 * The package arrived at its destination and will be made invisible.
	 * 
	 * @param a the agent that will disappear
	 */
	private void goToSleep(final Agent a) {
		a.set(ACTIVITY, Activity.INVISIBLE);
		a.setVisible(false);
	}

}
