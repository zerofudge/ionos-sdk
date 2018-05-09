/*
   Copyright 2018 Profitbricks GmbH

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.profitbricks.sdk.model

import com.profitbricks.sdk.annotation.*
import groovy.transform.EqualsAndHashCode
import groovy.util.logging.Log4j2

import java.lang.annotation.Annotation

import static com.profitbricks.sdk.Common.*

/**
 * base class for all model entities
 * all operations are blocking until a proper request status is received
 *
 * @author fudge <frank.geusch@profitbricks.com>
 */
@Log4j2
@EqualsAndHashCode
abstract class ModelBase {
    def id

    /**
     * all properties from the response data
     */
    protected final Map<String, ?> rawProperties = [:]

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
    final List<String> getAll(Map options = [:]) {
        API.get(requestFor(resource, options))?.data?.items?.collect{it.id}
    }

    /**
     * provides the creation REST call
     * @return the response JSON object
     */
    def create(final Map options = [:]) {
        from waitFor(API.post(requestFor(resource, options) + [body: createBody]), options)?.data
    }

    /**
     * provides the 'get resource' REST call
     * @return the response JSON object
     */
    def read(final id = id, final Map options = [:]) {
        from API.get(requestFor("${resource}/${id ?: ''}", options))?.data
    }

    /**
     * provides the 'update resource' REST call, this is confined to only use PUT (full update)
     * instead of PATCH (partial update)
     * @return the response JSON object
     */
    boolean update(final Map options = [:]) {
        waitFor(API.put(requestFor("${resource}/${id}", options) + [body: updateBody]), options)?.status == 202
    }

    /**
     * provides the 'delete resource' REST call
     * @return the response JSON object
     */
    boolean delete(final Map options = [:]) {
        waitFor(API.delete(requestFor("${resource}/$id", options)), options)?.status == 202
    }

    /**
     * provides generic request body creation from a list of field names
     * @param propNames list of property names to take into account
     * @return a properly filled request body
     */
    protected final Map bodyFrom(final List propNames) {
        final props = metaClass.properties.findAll{ propNames.contains(it.name) && this."${it.name}" != null }.collectEntries{ [(it.name): this."${it.name}"] }

        final rtn = [properties: [:]]
        // we have to spice up keywords with underscores
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

            (e.propertyNames([Creatable, Updatable, Readable]))?.each {
                def val = data.properties?."${it.replaceAll(/_/, '')}"
                if (val != null && !(val =~ /(?i)null/)) // NB: PUT seems to be non RFC-like for some entities in the PBC API
                    e."$it" = val
            }

            data.properties?.each { _k, _v ->
                rawProperties["${_k}"] = _v
            }
        }
        return e
    }
}
