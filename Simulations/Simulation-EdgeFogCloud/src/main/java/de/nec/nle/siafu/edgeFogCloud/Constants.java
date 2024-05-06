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

import de.nec.nle.siafu.types.FlatData;
import de.nec.nle.siafu.types.Publishable;
import de.nec.nle.siafu.types.Text;

/**
 * A list of the constants used by this simulation. None of this is strictly
 * needed, but it makes referring to certain values easier and less error prone.
 * 
 * @author João Pedro de Souza Jardim da Costa
 */
public class Constants {

	/** Default agent speed. */
	public static final int DEFAULT_SPEED = 11;

	/** Amount of packages. */
	public static final int POPULATION = 100;

	/** Possible Edge places to use in randomization. */
	public static final String[] EDGEPLACES = {"House", "Edge"};

	/**
	 * The names of the fields in each place object.
	 */
	static class PlaceFields {
		/** Amount of packages the server received from the application 1. */
		public static final String APP1PACKAMOUNT = "Packages received from APP1";
		/** Amount of packages the server received from the application 2. */
		public static final String APP2PACKAMOUNT = "Packages received from APP2";
		/** The total size of all packages the server received from the application 1. */
		public static final String APP1TOTALSIZE = "Total data received from APP1";
		/** The total size of all packages the server received from the application 2. */
		public static final String APP2TOTALSIZE = "Total data received from APP2";
		/** The data of the last package the server received from the application 1. */
		public static final String APP1LASTPACKAGE = "Last package received from APP1";
		/** The data of the last package the server received from the application 2. */
		public static final String APP2LASTPACKAGE = "Last package received from APP2";
		/** The data received through interoperability with the application 1 in its original format. */
		public static final String INTEROPERABILITYPACK = "Interoperability package";
		/** The data received through interoperability with the application 1 in its reduced format. */
		public static final String RINTEROPERABILITYPACK = "Reduced Interoperability package";
	}
	
	/**
	 * The names of the fields in each agent object.
	 */
	static class Fields {
		/** The agent's current activity. */
		public static final String ACTIVITY = "Activity";

		/** The data being transported by the agent. */
		public static final String PDATA = "Package data";

		/** The size of the data being transported by the agent. */
		public static final String PSIZE = "Package size";

		/** Application that sent the data. */
		public static final String SENDER = "Application";

		/** The sender of the data. */
		public static final String ORIGIN = "Place of origin";
		
		/** The sender of the data. */
		public static final String FOGTARGET = "Searching data from";
		
		/** The sender of the data. */
		public static final String TEMPDEST = "Temporary destination";

	}

	/**
	 * List of possible activies. This is implemented as an enum because it helps us
	 * in switch statements. Like the rest of the constants in this class, they
	 * could also have been coded directly in the model
	 */
	enum Activity implements Publishable {
		/** The agent's waiting. */
		ONHOLD("On hold"),
		/** The agent's reached at its destination and set to invisible. */
		INVISIBLE("Invisible"),
		/** The agent is going to a edge server. */
		GOING_2_EDGE_SERVER("Going2Edge"),
		/** The agent is going to a edge server. */
		GOING_2_EDGE_CLIENT("Going2House"),
		/** The agent is going to a fog server. */
		GOING_2_FOG("Going2Fog"),
		/** The agent is going to the cloud server. */
		GOING_2_CLOUD("Going2Cloud");

		/** Human readable description of the activity. */
		private String description;

		/**
		 * Get the description of the activity.
		 * 
		 * @return a string describing the activity
		 */
		public String toString() {
			return description;
		}

		/**
		 * Build an instance of Activity which keeps a human readable description for
		 * when it's flattened.
		 * 
		 * @param description the human readable description of the activity
		 */
		private Activity(final String description) {
			this.description = description;
		}

		/**
		 * Flatten the description of the activity.
		 * 
		 * @return a flatenned text with the description of the activity
		 */
		public FlatData flatten() {
			return new Text(description).flatten();
		}
	}
}
