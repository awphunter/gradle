/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.internal.resource.transport.aws.s3

import org.gradle.internal.resource.transport.http.HttpProxySettings
import org.jets3t.service.Constants
import spock.lang.Specification

class S3ConnectionPropertiesTest extends Specification {

    final S3ConnectionProperties s3ConnectionProperties = new S3ConnectionProperties()

    def "should report invalid scheme"() {
        when:
        s3ConnectionProperties.configureEndpoint(endpoint)
        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == "System property [org.gradle.s3.endpoint=$endpoint] must have a scheme of 'http' or 'https'"

        where:
        endpoint << ['httpd//somewhere', 'httpd://somewhere', 's3://somewhere']
    }

    def "should report invalid uri"() {
        when:
        s3ConnectionProperties.configureEndpoint('httpdasd%:/ads')
        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == "System property [org.gradle.s3.endpoint=httpdasd%:/ads]  must be a valid URI"
    }

    def "should allow case insensitive schemes"() {
        expect:
        endpoint == s3ConnectionProperties.configureEndpoint(endpoint).get().toString()
        where:
        endpoint << ['http://some', 'httP://some', 'httpS://some', 'HTTpS://some']
    }

    def "should get secure proxy for default s3 host when no endpoint override present"() {
        HttpProxySettings.HttpProxy secureProxy = Mock()
        HttpProxySettings secureHttpProxySettings = Mock()

        1 * secureHttpProxySettings.getProxy(Constants.S3_DEFAULT_HOSTNAME) >> secureProxy

        when:
        S3ConnectionProperties properties = new S3ConnectionProperties(Mock(HttpProxySettings), secureHttpProxySettings, null)

        then:
        properties.getProxy().get() == secureProxy
    }

    def "should get non-secure http proxy for override host"() {
        String endpoint = "http://someproxy"
        HttpProxySettings.HttpProxy proxy = Mock()
        HttpProxySettings httpProxySettings = Mock()

        1 * httpProxySettings.getProxy(_) >> proxy

        when:
        S3ConnectionProperties properties = new S3ConnectionProperties(httpProxySettings, Mock(HttpProxySettings), new URI(endpoint))

        then:
        properties.getProxy().get() == proxy
    }

    def "should get secure http proxy for override host"() {
        String endpoint = "https://someproxy"
        HttpProxySettings.HttpProxy secureProxy = Mock()
        HttpProxySettings secureHttpProxySettings = Mock()

        1 * secureHttpProxySettings.getProxy(_) >> secureProxy

        when:
        S3ConnectionProperties properties = new S3ConnectionProperties(Mock(HttpProxySettings), secureHttpProxySettings, new URI(endpoint))

        then:
        properties.getProxy().get() == secureProxy
    }
}
