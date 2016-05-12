# rest-factory [![Build Status](https://travis-ci.org/tcurrie/rest-factory.svg?branch=master)](https://travis-ci.org/tcurrie/rest-factory) [![Coverage Status](https://coveralls.io/repos/github/tcurrie/rest-factory/badge.svg?branch=master)](https://coveralls.io/github/tcurrie/rest-factory?branch=master) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.tcurrie/rest.factory/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.tcurrie/rest.factory)

_Simple rest client-server factory, you give it a url, it does the rest!_

On the service side:
```java
        @RestService
        public class MyServiceImplementation implements MyApi {
```
On the client side:
```java
        final MyApi client = RestClientFactory.create(MyApi.class, ()->url);
```
**_Time for a coffee break!_**

_[tfm...](https://github.com/tcurrie/rest-factory/wiki)_
