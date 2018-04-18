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
 * static functions enclosing common rest client functionality
 *
 * Created by fudge on 01/02/17.
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
             'User-Agent'   : 'profitbricks-groovy-sdk/3.0.0',
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
     * @throws ClientProtocolException if no 'done' was returned in time
     *
     * @param a valid REST response from the API
     * @return the original response object
     */
    final static waitFor(final response) {
        final String loc = "${response?.headers?.Location}".trim()

        if (loc && !(loc =~ /(?i)null/)) {
            double factor = prop('api.wait.factor') as Double ?: 1.87
            BigDecimal wait = (prop('api.wait.init.milliseconds') as Integer ?: 100) / factor

            final start = new Date()
            while (true) {
                def path = loc.toURL().path - URLParts.path
                if (path[0] == '/') {
                    path = path.substring(1)
                }
                final resp = API.get(requestFor(path))
                final status = resp?.data?.metadata?.status

                if (log.isTraceEnabled())
                    log.trace "request ${loc.substring(loc.lastIndexOf('/') + 1)}: ${status}"

                if (status =~ /(?i)done/) break
                if (status =~ /(?i)failed/)
                    throw new ClientProtocolException("${path}: FAILED!")

                final long timeout = (prop('api.wait.timeout.seconds') as Long ?: 240) * 1000
                if (TimeCategory.minus(new Date(), start).toMilliseconds() > timeout)
                    throw new ClientProtocolException("timeout (${timeout}s) exceeded while waiting for status DONE")

                Thread.sleep(Math.min(wait *= factor, prop('api.wait.max.milliseconds') as Double ?: 1500) as Long)
            }
        }
        return response
    }

    private final static class PooledClient extends RESTClient {
        PooledClient(final Object defaultURI) throws URISyntaxException {
            super(defaultURI)
        }

        @Override
        protected final HttpClient createClient(final HttpParams params) {
            new DefaultHttpClient(new PoolingClientConnectionManager(maxTotal: 200, defaultMaxPerRoute: 20), params)
        }
    }

    private final static prop(final String name) { System.getProperty name }

    private final static getURLParts() {
        def url = new URL(prop('api.URL') ?: 'https://api.profitbricks.com/cloudapi/v4')
        [prefix: "$url" - url.path, path: url.path]
    }
}
