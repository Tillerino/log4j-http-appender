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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

import fr.bettinger.log4j.HttpLayout.URLParameterNameLayout;

/**
 * HttpAppender is used to send an httpEvent to a server
 * This appender must be used with a HttpLayout type
 * 
 * @author S. Bettinger
 *
 */
public class HttpAppender extends AppenderSkeleton {
	final Logger log = Logger.getLogger(HttpAppender.class);

	/**
	 * Valeur par défaut du Timeout de la connexion http
	 */
	private final static int DEFAULT_TIMEOUT = 0;

	
	private final static String METHOD_GET = "GET";
	private final static String METHOD_POST = "POST";
	/**
	 * Method par defaut POST/GET
	 */
	private final static String DEFAULT_METHOD = METHOD_GET;

	private final static String POST_PARAMETERS = "PARAMETERS";
	private final static String POST_QUERY_STRING = "QUERY_STRING";
	/**
	 * Passage de paramètre par défaut pour la méthode post
	 */
	private final static String POST_DEFAULT = POST_PARAMETERS;

	/**
	 * Valeur par défaut de la connexion par thread 
	 */
	private final static boolean THREAD_DEFAULT = true;
	
	/**
	 * Gestion du timeout de la connexion HTTP
	 */
	private int timeOut = DEFAULT_TIMEOUT;
	
	/**
	 * URL d'appel du logger
	 */
	private String logURL = null;

	private String HttpMethodBase = DEFAULT_METHOD;

	private String postMethod = POST_DEFAULT;
	
	private boolean thread = THREAD_DEFAULT;
	
	private ExecutorService executorService = Executors.newCachedThreadPool();

	public void close() {
		executorService.shutdown();
	}

	public boolean requiresLayout() {
		return true;
	}

	@Override
	protected void append(LoggingEvent paramLoggingEvent) {
		/*
		 * Dans le cas ou le layout n'est pas de type HttpLayout, on ne fait rien
		 */
		if (!(this.getLayout() instanceof HttpLayout)) {
			errorHandler.error("you must use a HttpLayout type");
			return;
		}
		HttpLayout layout = (HttpLayout) getLayout();
		
		HttpMethodBase httpMethod = null;
		HttpClient httpClient = new HttpClient();
		/*
		 * On positionne le timeout
		 */
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(timeOut);
		httpClient.getParams().setSoTimeout(timeOut);		

		if (this.HttpMethodBase.equalsIgnoreCase(METHOD_GET)) {
			String query = layout.format(paramLoggingEvent);
			String url = this.logURL + query;
			LogLog.debug(url);
			httpMethod = new GetMethod(url);
		} else {
			if (this.postMethod.equalsIgnoreCase(POST_PARAMETERS)) {
				httpMethod = new PostMethod(this.logURL);
				// post requests don't need to be encoded manually
				layout.urlEncode = false;
				for (int i = 0; i < layout.subLayouts.size(); i+=2) {
					URLParameterNameLayout nameLayout = (URLParameterNameLayout) layout.subLayouts.get(i);
					String value = layout.subLayouts.get(i + 1).format(paramLoggingEvent);
					((PostMethod) httpMethod).addParameter(nameLayout.getValue(), value);
				}
			} else {
				String message = layout.format(paramLoggingEvent);
				StringBuffer sb = new StringBuffer(this.logURL);
				sb.append(message);
				httpMethod = new PostMethod(sb.toString());
			}
		}
		HttpThread httpThread = new HttpThread(httpClient,errorHandler);
		httpThread.setMethod(httpMethod);

		if (thread) {
			executorService.submit(httpThread);
		} else {
			httpThread.run();
		}
	}
	
	/*
	 * Getter
	 */
	public void setPost(String method) {
		this.postMethod = method;
	}

	public void setMethod(String method) {
		this.HttpMethodBase = method;
	}

	public void setLogURL(String logURL) {
		this.logURL = logURL;
	}
	
	public void setThread(boolean b) {
		this.thread = b;
	}

	public void setTimeout(int to) {
		this.timeOut = to;
	}
}
