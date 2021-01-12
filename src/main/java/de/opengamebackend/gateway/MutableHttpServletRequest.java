package de.opengamebackend.gateway;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;

/**
 * Allows modifying HTTP request headers in filters.
 *
 * See https://stackoverflow.com/questions/48240291/adding-custom-header-to-request-via-filter for details.
 */
public class MutableHttpServletRequest extends HttpServletRequestWrapper {
    private final Map<String, String> customHeaders;

    public MutableHttpServletRequest(HttpServletRequest request){
        super(request);

        this.customHeaders = new HashMap<>();
    }

    @Override
    public String getHeader(String name) {
        // Check the custom headers first.
        String headerValue = customHeaders.get(name);

        if (headerValue != null) {
            return headerValue;
        }

        // Return from into the original wrapped object.
        return ((HttpServletRequest) getRequest()).getHeader(name);
    }

    public void setHeader(String name, String value) {
        this.customHeaders.put(name, value);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        Set<String> headers = new HashSet<>();

        String customHeader = this.customHeaders.get(name);

        if (customHeader != null) {
            headers.add(customHeader);
        }

        Enumeration<String> wrappedHeaders = ((HttpServletRequest) getRequest()).getHeaders(name);
        while (wrappedHeaders.hasMoreElements()) {
            String wrappedHeader = wrappedHeaders.nextElement();
            headers.add(wrappedHeader);
        }

        return Collections.enumeration(headers);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        // Ccreate a set of the custom header names.
        Set<String> set = new HashSet<>(customHeaders.keySet());

        // Add the headers from the wrapped request object as well.
        Enumeration<String> wrappedHeaders = ((HttpServletRequest) getRequest()).getHeaderNames();
        while (wrappedHeaders.hasMoreElements()) {
            String wrappedHeader = wrappedHeaders.nextElement();
            set.add(wrappedHeader);
        }

        return Collections.enumeration(set);
    }
}
