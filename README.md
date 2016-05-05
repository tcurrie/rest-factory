# rest-factory [![Build Status](https://travis-ci.org/tcurrie/rest-factory.svg?branch=master)](https://travis-ci.org/tcurrie/rest-factory) [![Coverage Status](https://coveralls.io/repos/github/tcurrie/rest-factory/badge.svg?branch=master)](https://coveralls.io/github/tcurrie/rest-factory?branch=master) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/simple/rest.factory/badge.svg?style=flat)](http://search.maven.org/#search|ga|1|g%3Asimple.rest.factory)
_Simple rest client-server factory, you give it a url, it does the rest!_

On the service side:

        @RestService
        public class MyServiceImplementation implements MyApi {

On the client side:

        final MyApi client = RestClientFactory.create(MyApi.class, ()->url);

**_Time for a coffee break!_**
