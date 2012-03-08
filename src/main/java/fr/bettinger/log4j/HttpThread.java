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

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.spi.ErrorHandler;

/**
 * HttpThread provide threa ability
 * to http appender.
 * In order to spend less time as possible for http notification,
 * it's possible to use a thread to send http message
 * @author S. Bettinger
 *
 */
public class HttpThread implements Runnable {

	private HttpClient httpClient = null;
	private HttpMethodBase httpMethod = null;
	private ErrorHandler errorHandler = null;

	public HttpThread(HttpClient httpClient, ErrorHandler errorHandler) {
		this.httpClient = httpClient;
		this.errorHandler = errorHandler;
	}

	public void setMethod(HttpMethodBase httpMethod) {
		this.httpMethod = httpMethod;
	}

	public void run() {
		int statusCode = 0;
		try {		
			statusCode = httpClient.executeMethod(httpMethod);
			if (statusCode != HttpStatus.SC_OK) {
				errorHandler.error("Error Server URL " + httpMethod.getHostConfiguration().getHostURL() + " return status code " + statusCode);				
			}
		} catch (HttpException e) {
			errorHandler.error( "HttpException error in sending request to server: URL: " + httpMethod.getHostConfiguration().getHostURL() + " returned: " + statusCode, e, statusCode);
		} catch (IOException e) {
			errorHandler.error( "Io error in sending request to server: URL: " + httpMethod.getHostConfiguration().getHostURL() + " returned: " + statusCode, e, statusCode);
		} finally {
			httpMethod.releaseConnection();
		}
	}
}
