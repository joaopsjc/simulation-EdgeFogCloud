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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import de.nec.nle.siafu.behaviormodels.BaseWorldModel;
import de.nec.nle.siafu.model.Place;
import de.nec.nle.siafu.model.World;
import de.nec.nle.siafu.types.Text;

import static de.nec.nle.siafu.edgeFogCloud.Constants.PlaceFields.*;
/**
 * The world model for the simulation.
 * 
 * @author João Pedro de Souza Jardim da Costa
 */
public class WorldModel extends BaseWorldModel {

	/**
	 * Create the world model.
	 * 
	 * @param world the simulation's world.
	 */
	public WorldModel(final World world) {
		super(world);
	}

	/**
	 * Add fields to Fog,Cloud and Edge servers to control interoperability
	 * and the amount of data they received.
	 * 
	 * @param places an ArrayList with the places created with the images
	 */
	@Override
	public void createPlaces(final ArrayList<Place> places) {
		Integer fogPosition=0;
		Iterator<Place> placesIterator = places.iterator();
		while(placesIterator.hasNext())
		{
			Place currentPlace = placesIterator.next();
			if(currentPlace.getType().contains("Fog"))
			{
				currentPlace.set(APP1PACKAMOUNT, new Text("0"));
				currentPlace.set(APP2PACKAMOUNT, new Text("0"));
				currentPlace.set(APP1TOTALSIZE, new Text("0"));
				currentPlace.set(APP2TOTALSIZE, new Text("0"));
				currentPlace.set(APP1LASTPACKAGE, new Text("none"));
				currentPlace.set(APP2LASTPACKAGE, new Text("none"));
				fogPosition++;
			}
			else if(currentPlace.getType().contains("Cloud"))
			{
				currentPlace.set(APP1PACKAMOUNT, new Text("0"));
				currentPlace.set(APP2PACKAMOUNT, new Text("0"));
				currentPlace.set(APP1TOTALSIZE, new Text("0"));
				currentPlace.set(APP2TOTALSIZE, new Text("0"));
			}
			else if(currentPlace.getType().contains("Edge"))
			{
				currentPlace.set(INTEROPERABILITYPACK, new Text("none"));
				currentPlace.set(RINTEROPERABILITYPACK, new Text("none"));
			}
		}
	}

	/**
	 * Nothing done here.
	 * 
	 * @param places the places in the simulation
	 */
	@Override
	public void doIteration(final Collection<Place> places) {
		// Do nothing
	}
}
