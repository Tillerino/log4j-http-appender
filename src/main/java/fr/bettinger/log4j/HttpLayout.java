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
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

/**
 * HttpLayout 
 * @author S. Bettinger
 *
 */
public class HttpLayout extends Layout {
	private final class URLEncodingPatternLayout extends PatternLayout {
		private URLEncodingPatternLayout(String pattern) {
			super(pattern);
		}

		@Override
		public String format(LoggingEvent event) {
			String value = super.format(event);

			if (urlEncode) {
				try {
					value = URLEncoder.encode(value, encoding);
				} catch (UnsupportedEncodingException e) {
					LogLog.warn(e.toString());
				}
			}

			return value;
		}
	}
	
	public static class URLParameterNameLayout extends Layout {
		private final String type;
		private final String value;
		
		public URLParameterNameLayout(String type, String value) {
			super();
			this.type = type;
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		@Override
		public void activateOptions() {
			
		}
		
		@Override
		public boolean ignoresThrowable() {
			return true;
		}
		
		@Override
		public String format(LoggingEvent event) {
			return type + value + "=";
		}
	}


	public static final Pattern URL_PARAMETER_PATTERN = Pattern.compile("(\\?|&)(\\w+)=");
	
	List<Layout> subLayouts = new ArrayList<Layout>();
	
	public void setConversionPattern(String conversionPattern) throws MalformedURLException {
		Matcher m = URL_PARAMETER_PATTERN.matcher(conversionPattern);
		if(!m.find() || m.start() != 0 || !m.group(1).equals("?")) {
			throw new MalformedURLException("You need to provide a proper query string starting with ?");
		}
		
		for(;;) {
			subLayouts.add(new URLParameterNameLayout(m.group(1), m.group(2)));
			int end = m.end();
			if(!m.find()) {
				subLayouts.add(new URLEncodingPatternLayout(conversionPattern.substring(end)));
				return;
			}
			subLayouts.add(new URLEncodingPatternLayout(conversionPattern.substring(end, m.start())));
		}
	}

	public String encoding = "UTF-8";
	public void setEncoding(String encoding) throws UnsupportedEncodingException {
		if(encoding == null || !Charset.isSupported(encoding)) {
			throw new UnsupportedEncodingException(encoding);
		}
		this.encoding = encoding.trim();
	}

	public boolean urlEncode = true;
	public void setUrlEncode(boolean urlEncode) {
		this.urlEncode = urlEncode;
	}

	@Override
	public void activateOptions() { }

	@Override
	public String format(LoggingEvent paramLoggingEvent) {
		StringBuilder builder = new StringBuilder();
		
		for (Layout layout : subLayouts) {
			builder.append(layout.format(paramLoggingEvent));
		}
		
		return builder.toString();
	}

	@Override
	public boolean ignoresThrowable() {
		for (Layout layout : subLayouts) {
			if(!layout.ignoresThrowable()) {
				return false;
			}
		}
		return true;
	}
}
