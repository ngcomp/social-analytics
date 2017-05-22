package com.ngcomp.analytics.engine.connector.common.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Utility class used to send a POST/GET request to a given URL and receive responses. It
 * declares two methods as follows:
 * <ul>
 * <li>{@link #requestRESTGet(String)} : method to send a GET request at the
 * given URL.</li>
 * <li>{@link #requestRESTPost(String)} : method to send a POST request at the
 * given URL.</li>
 * </ul>
 * 
 */
public class RESTPosterClient {

	public static final String UTF8_CHARACTER_ENCODING = "UTF-8";

	/**
	 * The method sends a GET request. It accepts a JSON string in response from
	 * any URL. It also throws a Runtime Exception if the response code is other
	 * than 200.
	 * 
	 * @param requestURL
	 *            a string which internally has a REST end point URL and the
	 *            query parameters.
	 * @return a string containing the response
	 */

	public static String requestRESTGet(String requestURL) {
		return getResponse(requestURL, "GET", 0);
	}

	/**
	 * The method sends a GET request. It accepts a JSON string in response from
	 * any URL. It also throws a Runtime Exception if the response code is other
	 * than 200 or if the response is not received with in the given waitTime.
	 * 
	 * @param requestURL
	 *            a string which internally has a REST end point URL and the
	 *            query parameters.
	 * @param waitTime
	 *            the time for which the method waits for response.
	 * @return a string containing the response.
	 */

	public static String requestRESTGet(String requestURL, int waitTime)
			{
		return getResponse(requestURL, "GET", waitTime);
	}

	/**
	 * The method sends a POST request. It accepts a JSON string in response
	 * from any URL. It also throws a Runtime Exception if the response code is
	 * other than 200.
	 * 
	 * @param requestURL
	 *            a string which internally has a REST end point URL and the
	 *            query parameters.
	 * @return a string containing the response.
	 */
	public static String requestRESTPost(String requestURL) {
		return getResponse(requestURL, "POST", 0);
	}

	/**
	 * The method sends a GET request. It accepts a JSON string in response from
	 * any URL. It also throws a Runtime Exception if the response code is other
	 * than 200.
	 * 
	 * @param requestURL
	 *            a string which internally has a REST end point URL and the
	 *            query parameters.
	 * @return a string containing the response
	 */
	public static String getResponse(String requestURL, String httpMethod,
                                     int waitTime){

		if (requestURL == null || requestURL.length() == 0
				|| httpMethod == null || httpMethod.length() == 0
				|| waitTime < 0) {
			return null;
		}
		
		HttpURLConnection conn = null;
		StringBuilder responseBuilder = new StringBuilder();
		try {
			URL url = new URL(requestURL);

			URLConnection urlConnection = url.openConnection();
			if (urlConnection instanceof HttpURLConnection) {
				conn = (HttpURLConnection) urlConnection;
				conn.setRequestMethod(httpMethod);
				conn.setRequestProperty("Accept", "application/json");
				conn.setReadTimeout(waitTime);
				if (conn.getResponseCode() != 200) {
					throw new RuntimeException("Failed : HTTP error code : "
							+ conn.getResponseCode());
				}
				BufferedReader br = new BufferedReader(new InputStreamReader(
						(conn.getInputStream())));
				String output = null;
				while ((output = br.readLine()) != null) {
					responseBuilder.append(output);
				}
			} else {
				return null;
			}

		} catch (Exception e) {
			//ignore
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return responseBuilder.toString();
	}

	public static void main(String[] args) {
		System.out.println(getResponse("https://api.facebook.com/method/fql.query?query=select%20total_count,like_count,comment_count,share_count,click_count%20from%20link_stat%20where%20url='http://www.groupon.com/deals/seattlehelitourscom-by-classic-helicopter-corp'&format=json", "GET", 1000));
	}


}
