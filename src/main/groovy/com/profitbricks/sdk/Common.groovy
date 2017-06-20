package com.profitbricks.sdk

import groovy.time.TimeCategory
import groovy.util.logging.Log4j2
import groovyx.net.http.RESTClient
import org.apache.http.client.*
import org.apache.http.impl.client.*
import org.apache.http.impl.conn.PoolingClientConnectionManager
import org.apache.http.params.*
import org.codehaus.groovy.runtime.StackTraceUtils

import static groovyx.net.http.ContentType.JSON
import static org.apache.commons.codec.binary.Base64.encodeBase64String

/**
 * some static functions enclosing convenience functionality
 *
 * Created by fudge on 01/02/17.
 * Copyright (c) 2017, ProfitBricks GmbH
 */
@Log4j2
final class Common {
    private final static RESTClient client = new PooledClient(URLParts.prefix)

    // statically initializes the REST client
    static {
        if (prop('api.verifySSL') =~ /(?i)false|no/) API.ignoreSSLIssues()

        API.handler.'404' = { log.debug "--> Not found! ($it)" }
        API.handler.'401' = { log.debug "--> Access denied! ($it)" }
        API.handler.failure = {
            throw StackTraceUtils.deepSanitize(new HttpResponseException(it.status as int, it.data ?: it.statusLine as String))
        }
    }

    /**
     * exposes the REST client itself
     * @return the initialized REST client instance
     */
    final static RESTClient getAPI() { client }

    /**
     * creates a request object to be handed to the REST client
     *
     * @param path the path part of the target URL
     * @return a new request object
     */
    final static Map requestFor(final String path) {
        [
            path              : "${URLParts.path}/${path}",
            headers           : [
                'User-Agent'   : 'profitbricks-groovy-sdk/1.4',
                'Accept'       : JSON.acceptHeader,
                // omit resend-on-401 scheme
                'Authorization': "Basic " + encodeBase64String("${prop('api.user')}:${prop('api.password')}".bytes)
            ],

            requestContentType: JSON
        ]
    }

    /**
     * takes the response of a prior REST request and queries the 'Location' target
     * for as long as that returns 'done'
     * timings are configurable
     * throws a ClientProtocolException if no 'done' was returned in time
     *
     * @param a valid REST response from the API
     * @return the original response object
     */
    final static waitFor(final response) {
        final String loc = "${response?.headers?.Location}".trim()
        if (loc && !(loc =~ /(?i)null/)) {
            long sleep = prop('api.wait.init.milliseconds') as Long ?: 100
            final start = new Date()
            final long max = prop('api.wait.max.milliseconds') as Long ?: 1500

            while (true) {
                final path = loc.toURL().path - URLParts.path
                if (path.getAt(0) == '/') {
                    path = path.substring(1)
                }
                final resp = API.get(requestFor(path))
                final status = resp?.data?.metadata?.status

                if (log.isTraceEnabled())
                    log.trace "request ${loc.substring(loc.lastIndexOf('/') + 1)}: ${status}"

                if (status =~ /(?i)done/) break
                if (status =~ /(?i)failed/)
                    throw new ClientProtocolException("${path}: FAILED!")

                final int timeout = (prop('api.wait.timeout.seconds') as Integer ?: 120)
                if (TimeCategory.minus(new Date(), start).toMilliseconds() > (timeout * 1000)) {
                    throw new ClientProtocolException("timeout (${timeout}s) exceeded while waiting for status DONE")
                }

                Thread.sleep Math.min(max, Math.abs((sleep *= (prop('api.wait.factor') ?: 1.53)) as long))
            }
        }
        return response
    }

    private final static class PooledClient extends RESTClient {
        PooledClient(final Object defaultURI) throws URISyntaxException { super(defaultURI) }

        @Override
        protected final HttpClient createClient(final HttpParams params) {
            final cm = new PoolingClientConnectionManager()
            cm.maxTotal = 200
            cm.defaultMaxPerRoute = 20
            new DefaultHttpClient(cm, params)
        }
    }

    /**
     * get system property values
     *
     * @param name the name of a system property
     * @return the value of the property or null
     */
    private final static prop(final String name) { System.getProperty name }

    private final static getURLParts() {
        def url = new URL(prop('api.URL') ?: 'https://api.profitbricks.com/cloudapi/v3')
        [prefix: "$url" - url.path, path: url.path]
    }
}
