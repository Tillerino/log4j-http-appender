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

package fr.bettinger.log4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

/**
 * HttpLayout 
 * @author S. Bettinger
 *
 */
public class HttpLayout extends Layout {

	public String conversionPattern = "";
	public void setConversionPattern(String conversionPattern) {
		this.conversionPattern = conversionPattern;
	}
	
	public String encoding = "UTF-8";
	public void setEncoding(String encoding) {
		this.encoding = encoding.trim();
	}

	public void activateOptions() { }

	@Override
	public String format(LoggingEvent paramLoggingEvent) {
		if (paramLoggingEvent.getMessage() instanceof HttpMessage) {
			String returnMessage = new String(conversionPattern);
			HttpMessage message = (HttpMessage)paramLoggingEvent.getMessage();

			Map<String, String> map = message.getParametersMap();
			String key = null;
			String value= null;
			for (Iterator<String> i = map.keySet().iterator() ; i.hasNext() ; ){
			    key = i.next();
			    value = map.get(key);
			    try {
			    	value = URLEncoder.encode(value, encoding);
			    } catch (UnsupportedEncodingException e) {}
			    if (value != null) {
			    	returnMessage = returnMessage.replaceAll("%"+key,value);
			    }
			}
			return returnMessage;
		} else 
			return paramLoggingEvent.getMessage().toString();
	}

	@Override
	public boolean ignoresThrowable() {
		return true;
	}

}
