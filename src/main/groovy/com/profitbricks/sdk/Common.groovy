package com.profitbricks.sdk

import groovy.time.TimeCategory
import groovy.util.logging.Log4j2
import groovyx.net.http.RESTClient
import org.apache.http.client.*
import org.codehaus.groovy.runtime.StackTraceUtils

import static groovyx.net.http.ContentType.JSON

/**
 * some functions enclosing convenience functionality
 *
 * Created by fudge on 01/02/17.
 * (c)2015 Profitbricks.com
 */
@Log4j2
final class Common {
    private final static RESTClient client = new RESTClient(URLParts.host)

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
         path: "${URLParts.path}/${path}",
         headers: [
             'User-Agent': 'profitbricks-groovy-sdk/1.0',
             'Accept': JSON.acceptHeader,
             // omit resend-on-401 scheme
             'Authorization': "Basic " + Base64.encoder.encodeToString("${prop('api.user')}:${prop('api.password')}".bytes)
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
        String loc = "${response?.headers?.Location}".trim()
        if (loc && !(loc =~ /(?i)null/)) {
            int sleep = prop('api.wait.init.milliseconds') as Integer ?: 100
            def start = new Date()
            long max = prop('api.wait.max.milliseconds') as Long ?: 1500

            while (true) {
                def _resp = API.get(requestFor(loc.toURL().path - URLParts.path))
                if (_resp?.data?.metadata?.status =~ /(?i)done/) break

                int timeout = (prop('api.wait.timeout.seconds') as Integer ?: 120)
                if (TimeCategory.minus(new Date(), start).seconds > timeout) {
                    throw new ClientProtocolException('timeout while waiting for status DONE')
                }

                print '.'
                Thread.sleep Math.min(max, (sleep *= (prop('api.wait.factor') ?: 1.87)) as long)
            }
        }
        return response
    }

    /**
     * get system property values
     *
     * @param name the name of a system property
     * @return the value of the property or null
     */
    private final static prop(final String name) { System.getProperty name }

    private final static getURLParts() {
        def url = new URL(prop('api.URL') ?: 'https://api.profitbricks.com/cloudapi/v3/')
        [host: "$url" - url.path, path: url.path]
    }
}
