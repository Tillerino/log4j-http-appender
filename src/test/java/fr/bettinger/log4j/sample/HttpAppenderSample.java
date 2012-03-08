/*
 *   This file is part of Log4jHttpAppender.
 *
 *   Log4jHttpAppender is free software: you can redistribute it and/or modify
 *   it under the terms of the Lesser GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Log4jHttpAppender is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   Lesser GNU General Public License for more details.
 *
 *   You should have received a copy of the Lesser GNU General Public License
 *   along with Log4jHttpAppender.  If not, see <http://www.gnu.org/licenses/>.
 *   
 *   The original code was written by Sebastien Bettinger <sebastien.bettinger@gmail.com>
 *   
 */

package fr.bettinger.log4j.sample;

import org.apache.log4j.Logger;

import fr.bettinger.log4j.HttpMessage;


/**
 * Just a tiny sample
 * @author S. Bettinger
 *
 */
public class HttpAppenderSample {

	static final String id = "id";
	static final String uid = "uid";
	static final String evt = "evt";
	
	/**
	 * Logger log4j
	 */
	protected static final Logger log = Logger.getLogger(HttpAppenderSample.class.getName());

	
	/**
	 * 
	 */
	public static void main(String[] args) {
		
		//Create a new message
		HttpMessage message = new HttpMessage ();
		message.addParameter(id, "1");
		message.addParameter(uid, "sebastien");
		message.addParameter(evt, "just a test");
		
		//send message via httpAppender
		log.debug(message);
		
	}

}
