package org.cc.web;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class CharacterEncodingFilter implements Filter {

	/**
	 * The default character encoding to set for requests that pass through this
	 * filter.
	 */
	private String encoding = null;

	/**
	 * Should a character encoding specified by the client be ignored?
	 */
	private boolean ignore = true;

	public void destroy() {
		this.encoding = null;
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		// Conditionally select and set the character encoding to be used
		if (ignore || (request.getCharacterEncoding() == null)) {
			String encode = selectEncoding(request);
			if (encode != null) {
				request.setCharacterEncoding(encode);
			}
		}

		chain.doFilter(request, response);

	}

	/**
	 * Place this filter into service.
	 * 
	 * @param filterConfig
	 *            The filter configuration object
	 */
	public void init(FilterConfig filterConfig) throws ServletException {

		this.encoding = filterConfig.getInitParameter("encoding");
		String value = filterConfig.getInitParameter("ignore");
		if (value == null) {
			this.ignore = true;
		} else if (value.equalsIgnoreCase("true")) {
			this.ignore = true;
		} else if (value.equalsIgnoreCase("yes")) {
			this.ignore = true;
		} else {
			this.ignore = false;
		}
	}

	protected String selectEncoding(ServletRequest request) {
		return (this.encoding);
	}

}