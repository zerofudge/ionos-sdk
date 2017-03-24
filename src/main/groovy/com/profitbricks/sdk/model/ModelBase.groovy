package com.profitbricks.sdk.model

import com.profitbricks.sdk.annotation.*
import groovy.transform.EqualsAndHashCode
import groovy.util.logging.Log4j2

import java.lang.annotation.Annotation

import static com.profitbricks.sdk.Common.*

/**
 * base class for all model entities
 * all operations are blocking until the API result status is received
 *
 * Created by fudge on 01/02/17.
 * (c)2017 Profitbricks.com
 */
@EqualsAndHashCode
@Log4j2
abstract class ModelBase {
    def id

    /**
     * the resource part of the API URL path
     * @return the resource path for this REST resource
     */
    abstract String getResource()

    /**
     * provides the request body for POST requests
     * @return the request body
     */
    protected Map getCreateBody() { bodyFrom propertyNames([Creatable]) }

    /**
     * provides the request body for PUT requests
     * @return the request body
     */
    protected Map getUpdateBody() { bodyFrom propertyNames([Updatable]) }

    /**
     * provides the 'list all' REST call
     * @return a list of resource IDs
     */
    final List<String> getAll() {
        API.get(requestFor(resource))?.data?.items?.collect{it.id}
    }

    /**
     * provides the creation REST call
     * @return the response JSON object
     */
    def create() {
        from waitFor(API.post(requestFor(resource) + [body: createBody]))?.data
    }

    /**
     * provides the 'get resource' REST call
     * @return the response JSON object
     */
    def read(final id = id) {
        from API.get(requestFor("${resource}/${id}"))?.data
    }

    /**
     * provides the 'update resource' REST call, this is confined to only use PUT (full update)
     * instead of PATCH (partial update)
     * @return the response JSON object
     */
    boolean update() {
        waitFor(API.put(requestFor("${resource}/${id}") + [body: updateBody]))?.status == 202
    }

    /**
     * provides the 'delete resource' REST call
     * @return the response JSON object
     */
    final boolean delete() {
        waitFor(API.delete(requestFor("${resource}/$id")))?.status == 202
    }

    /**
     * provides generic request body creation from a list of field names
     * @param propNames list of property names to take into account
     * @return a properly filled request body
     */
    protected final Map bodyFrom(final List propNames) {
        final props = metaClass.properties.findAll{def n=it.name; propNames.contains(n) && this."$n"}.collectEntries{def n=it.name; ["$n": this."$n"]}

        final rtn = [properties: [:]]
        // surely looks a little dumb, but shortening or omitting this leads to a weird:
        // net.sf.json.JSONException: java.lang.ClassCastException: JSON keys must be strings
        // also, we have to spice up keywords with underscores
        props.each { k, v -> rtn.properties."${k.toString().replaceAll(/_/, '')}" = v }
        if (log.isDebugEnabled()) log.debug "body: ${rtn}"
        return rtn
    }

    /**
     * assembles a list of property names annotated by at least one of the given
     * annotation list
     *
     * @param annotations only fields annotated with the given classes are considered
     * @return a list of property names of the current entity
     */
    final List propertyNames(final List<Class<? extends Annotation>> annotations) {
        this.class.declaredFields.findAll {
            annotations.find { a ->
                //noinspection GroovyInArgumentCheck
                a in it.declaredAnnotations*.annotationType() }
        }.collect{it.name}
    }

    /**
     * unmarshalls an entity from a given JSON representation
     *
     * @param data a JSON object
     * @return the unmarshalled entity
     */
    def from(final data) {
        def e = null

        if (data) {
            e = this.class.newInstance(id: data.id)
            if (!e) throw new InvalidObjectException("cannot construct ${this.class.name}")

            (e?.propertyNames([Creatable, Updatable, Readable]))?.each {
                def val = data.properties?."${it.replaceAll(/_/, '')}"
                if (val && !(val =~ /(?i)null/))
                    e."$it" = val
            }
        }
        return e
    }
}
