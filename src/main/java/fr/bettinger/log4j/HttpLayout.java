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
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

/**
 * HttpLayout 
 * @author S. Bettinger
 *
 */
public class HttpLayout extends Layout {

	/**
	 * Attribut positionné lorsque les messages ne sont pas de type
	 * HttpMessage<br/>
	 * Thanks to Kjetil Thuen
	 */
	private final static String UNHANDLEDMESSAGE = "UnhandledMessage";

	/**
	 * String value for null string
	 */
	private final static String NULL = "null";

	public String conversionPattern = "";
	public void setConversionPattern(String conversionPattern) {
		this.conversionPattern = conversionPattern;
	}

	public String encoding = "UTF-8";
	public void setEncoding(String encoding) {
		this.encoding = encoding.trim();
	}

	public boolean urlEncode = true;
	public void setUrlEncode(boolean urlEncode) {
		this.urlEncode = urlEncode;
	}

	public void activateOptions() { }

	@Override
	public String format(LoggingEvent paramLoggingEvent) {
		String returnMessage = new String(conversionPattern);
		String value= null;
		if (paramLoggingEvent.getMessage() instanceof HttpMessage) {
			HttpMessage message = (HttpMessage)paramLoggingEvent.getMessage();
			Map<String, String> map = message.getParametersMap();
			String key = null;

			for (Iterator<String> i = map.keySet().iterator() ; i.hasNext() ; ){
				key = i.next();
				value = map.get(key);
				returnMessage = formatMessage(returnMessage, key, value);

				
			}
		} else {
			if (paramLoggingEvent.getMessage() == null) {
				value = null;
			} else {
				value = paramLoggingEvent.getMessage().toString();
			}
			returnMessage = formatMessage(returnMessage, UNHANDLEDMESSAGE, value);

		}
		return returnMessage.toString();
	}

	@Override
	public boolean ignoresThrowable() {
		return true;
	}

	private String formatMessage(String returnMessage, String key, String value) {
		if (value == null) {
			LogLog.warn("Setting NULL value for " + key);
			value = NULL;
		} 

		if (urlEncode) {
			try {
				value = URLEncoder.encode(value, encoding);
			} catch (UnsupportedEncodingException e) {
				LogLog.warn(e.toString());

			}
		}
		returnMessage = returnMessage.replaceAll("%"+key,value);
		return returnMessage;
	}
}
