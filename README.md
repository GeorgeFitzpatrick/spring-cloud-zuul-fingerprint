Spring Cloud Zuul Fingerprint
=============================

[epq]: https://www.aqa.org.uk/subjects/projects/project-qualifications/EPQ-7993

[valid]: https://docs.oracle.com/javaee/7/api/javax/validation/Valid.html

[notNull]: https://docs.oracle.com/javaee/7/api/javax/validation/constraints/NotNull.html

[positive]: spring-cloud-zuul-fingerprint-core/src/main/java/com/georgefitzpatrick/cloud/autoconfigure/zuul/fingerprint/config/properties/validators/Positive.java

[score]: spring-cloud-zuul-fingerprint-core/src/main/java/com/georgefitzpatrick/cloud/autoconfigure/zuul/fingerprint/config/properties/validators/Score.java

[bucket]: spring-cloud-zuul-fingerprint-core/src/main/java/com/georgefitzpatrick/cloud/autoconfigure/zuul/fingerprint/config/Bucket.java

[abstractBucket]: spring-cloud-zuul-fingerprint-core/src/main/java/com/georgefitzpatrick/cloud/autoconfigure/zuul/fingerprint/config/data/AbstractBucket.java

[dataset]: spring-cloud-zuul-fingerprint-core/src/main/java/com/georgefitzpatrick/cloud/autoconfigure/zuul/fingerprint/config/Dataset.java

[ja3Fingerprinter]: spring-cloud-zuul-fingerprint-core/src/main/java/com/georgefitzpatrick/cloud/autoconfigure/zuul/fingerprint/ssl/JA3Fingerprinter.java

[http2Fingerprinter]: spring-cloud-zuul-fingerprint-core/src/main/java/com/georgefitzpatrick/cloud/autoconfigure/zuul/fingerprint/http2/HTTP2Fingerprinter.java

[ja3Fingerprinting]: https://github.com/salesforce/ja3

[akamaiHttp2Fingerprinting]: https://www.blackhat.com/docs/eu-17/materials/eu-17-Shuster-Passive-Fingerprinting-Of-HTTP2-Clients-wp.pdf

[fingerprinter]: spring-cloud-zuul-fingerprint-core/src/main/java/com/georgefitzpatrick/cloud/autoconfigure/zuul/fingerprint/config/Fingerprinter.java

[matcher]: spring-cloud-zuul-fingerprint-core/src/main/java/com/georgefitzpatrick/cloud/autoconfigure/zuul/fingerprint/util/Matcher.java

[keyGenerator]: spring-cloud-zuul-fingerprint-core/src/main/java/com/georgefitzpatrick/cloud/autoconfigure/zuul/fingerprint/config/KeyGenerator.java

Overview
--------

I made this project as the artefact for my [extended project qualification (EPQ)][epq] titled 'What measures could be
taken to protect web and mobile APIs against automated attacks?'.

I designed the project to help identify the clients that interact with a server. The purpose of this was to develop more
sophisticated rate-limiting strategies that were weighted against clients that attempted to mask their identity. To
accomplish this, this library generates and compares fingerprints for a given client to calculate a trust score between
0 and 1 that represent the uncertainty in a client's advertised identity. The library can then be configured to apply 
different limits to clients based on their trust score.

Usage
-----

### Maven

<table>
<tr>
<td><strong>Latest version</strong></td>
<td>



</td>
</tr>
</table>

#### pom.xml

```xml
<dependency>
    <groupId>com.georgefitzpatrick.cloud</groupId>
    <artifactId>spring-cloud-starter-zuul-fingerprint</artifactId>
    <version>${latest-version}</version>
</dependency>
```

### Configurable Properties

<table>
<tr>
<td><strong>Namespace</strong></td>
<td><strong>Property</strong></td>
<td><strong>Type & Validation</strong></td>
<td><strong>Default Value</strong></td>
</tr>

<tr>
<td rowspan="5">

`zuul.fingerprint`

</td>
<td>

`enabled`

</td>
<td>

[@NotNull][notNull] Boolean

</td>
<td>

````java
false
````

</td>
</tr>

<tr>
<td>

`filter-order`

</td>
<td>

[@Positive][positive] Integer

</td>
<td>

````java
0
````

</td>
</tr>

<tr>
<td>

`behind-proxy`

</td>
<td>

[@NotNull][notNull] Boolean

</td>
<td>

````java
false
````

</td>
</tr>

<tr>
<td>

`score-weights`

</td>
<td>

[@NotNull][notNull] TreeMap&lt;[@Score][score] Double, [@Positive][positive] Long&gt;

</td>
<td>-</td>
</tr>

<tr>
<td>

`policies`

</td>
<td>

[@NotNull][notNull] LinkedList&lt;[@Valid][valid] [@NotNull][notNull] Policy&gt;

</td>
<td>-</td>
</tr>

<tr>
<td rowspan="3">

`zuul.fingerprint.policies[index]`

</td>
<td>

`limit`

</td>
<td>

[@Positive][positive] Long

</td>
<td>

````java
false
````

</td>
</tr>

<tr>
<td>

`refresh`

</td>
<td>

[@NotNull][notNull] Duration

</td>
<td>

````java
null
````

</td>
</tr>

<tr>
<td>

`penalties`

</td>
<td>

[@NotNull][notNull] Map&lt;[@Positive][positive] Long, [@NotNull][notNull] Duration&gt;

</td>
<td>-</td>
</tr>
</table>

#### Example - application.yml

```yaml
zuul:
  fingerprint:
    enabled: true
    filter-order: 0
    behind-proxy: false
    score-weights:
      '0.5': 2
    policies:
      - limit: 10
        refresh: PT1M
        penalties:
          '5': PT10M
```

Buckets & Datasets
------------------

### Redis

Add and configure [Spring Data Redis](https://spring.io/projects/spring-data-redis) via your `application.properties`
/`application.yml` file. When you next run your application a bucket and a dataset been will be automatically registered
via Spring autoconfiguration.

#### Maven (pom.xml)

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

### Custom

#### Buckets

Custom buckets must implement the [Bucket][bucket] interface. For your custom bucket to be used it must then be
registered as a Spring bean. Optionally, you can extend [AbstractBucket][abstractBucket] instead which provides a great
template for creating your own cache backed buckets.

```java
@Component
public class CustomBucket implements Bucket {

    @Override
    public long consume(String key, Policy policy, long used) {
        // Update the provided key's usage, remembering to expire 
        // the key's usage once the refresh duration defined in 
        // the provided policy has elapsed.

        // Return the number of remaining requests a key has.
    }

}
```

```java
@Component
public class CustomAbstractBucket extends AbstractBucket {

    @Override
    protected long increment(String key, long value) {

    }

    @Override
    protected void expire(String key, Duration duration) {
        // Expire the usage of the provided key after the 
        // provided duration has elapsed.
    }

}
```

#### Datasets

Custom datasets must implement the [Dataset][dataset] interface and should store data and a `key:value` format. For your
custom dataset to be used it must then be registered as a Spring bean. Custom implementations must implement methods to
retrieve and modify collections of keys and values as those methods are used to batch data requests to reduce filtering
times when working with external data sources.

```java
@Component
public class CustomDataset implements Dataset {

    public @Nullable
    String get(String key) {
        // Return et the value for the provided
        // key or null if the key does not exist.
    }

    public @NotNull
    List<String> getAll(Collection<String> keys) {
        // Return a List of values for a collection of keys.
    }

    public void set(@NotNull String key, String value) {
        // Set the provided value for the provided key.
    }

    public void setAll(@NotNull Map<String, String> data) {
        // Set the provided values for the provided keys.
    }

}
```

Fingerprinters
--------------

Both a [JA3Fingerprinter][ja3Fingerprinter] and a [HTTP2Fingerprinter][http2Fingerprinter] are included in the starter
and are registered via Spring autoconfiguration assuming the `server.ssl.enabled` and `server.http2.enabled` properties
are true respectively.

### JA3

<table>
<tr>
<td><strong>Format</strong></td>
<td>

`CV,CS[-],ET[-],SG[-],ECPF[-]`

</td>
</tr>
</table>

(as defined in the [official Salesforce JA3 repository][ja3Fingerprinting])

**CV** - The unsigned 16-bit integer **c**lient **v**ersion (SSL protocol) present in the client hello.

**CS[-]** - An unsigned 16-bit integer SSL **c**ipher **s**uite code present in the client hello. Multiple cipher suites
are concatenated using a dash (-) according to the order of their appearance.

**ET[-]** - An unsigned 16-bit integer SSL **e**xtension **t**ype code present in the client hello. Multiple extension
types are concatenated using a dash (-) according to the order of their appearance.

**SG[-]** - An unsigned 16-bit integer **s**upported **g**roup code present in the supported groups extension in the
client hello. Multiple supported groups are concatenated using a dash (-) according to the order of their appearance.

**ECPF[-]** - An unsigned 8-bit integer **e**lliptic **c**urve **p**oint **f**ormat code present in the elliptic curve
point formats extension in the client hello. Multiple elliptic curve point formats are concatenated using a dash (-)
according to the order of their appearance.

### HTTP/2

<table>
<tr>
<td><strong>Format</strong></td>
<td>

`S[;]|WU[,]|P[,]`

</td>
</tr>
</table>

(inspired by [Akamai's HTTP/2 fingerprinting white paper][akamaiHttp2Fingerprinting])

**S[,]** - a parameter and its value from a SETTINGS frame in the form `key:value`. Multiple parameters are concatenated
using a semicolon (;) according the order of their appearance.

**WU[,]** - the window delta value from a WINDOW_UPDATE frame. Multiple frames are concatenated using a comma (,)
according to the order of their appearance.

**P[,]** - the stream id, exclusivity, parent stream id, and weight of a PRIORITY frame in the
form `streamId:exclusivity:parentStreamId:weight`. Multiple frames are concatenated by a comma (,) according to the
order of their appearance.

### Custom

Custom fingerprinters must implement the [Fingerprinter][fingerprinter] interface. For your custom fingerprinter to be
used it must then be registered as a Spring bean. Fingerprinters are used to produce a trust score that can be used in a
rate limiting strategy. When multiple fingerprinters are defined the average of their cumulative trust scores is used.

```java
@Component
public class CustomFingerprinter implements Fingerprinter {

    public @Nullable
    String fingerprint(RequestContext ctx) {
        // Calculate the fingerprint for the provided Netflix 
        // Zuul request context.

        // If for whatever reason a fingerprint could not be 
        // calculated return null; otherwise return the 
        // calculated fingerprint.
    }

}
```

Matchers
--------

Matchers are used to determine whether the user agents determined from the fingerprints taken for a given request match
the user agent advertised by the client

### Default

If no [Matcher][matcher] beans of type String are present the default matcher below will be registered via Spring
autoconfiguration.

```java
@Bean
public Matcher<String> matcher() {
    return String::equals;
}
```

### Custom

Custom matchers must implement the [Matcher][matcher] interface and be of type String. For your custom matcher to be
used it must then be registered as a Spring bean.

```java
@Component
public class CustomMatcher implements Matcher<String> {

    public boolean matches(@NotNull T s1, @NotNull T s2) {
        // Return whether the two identities are deemed to
        // match.
    }

}
```

Key Generators
--------------

Key generators are used to apply rate limits. A generated key should be unique to a client or collection of clients that
you wish usage restrictions to be applied to.

### Default

If no [KeyGenerator][keyGenerator] beans are present the default key generator below will be registered via Spring
autoconfiguration. The default key generator combines the Zuul route id and origin of the request causing rate limits to
be applied per route per origin.

```java
@Bean
public KeyGenerator keyGenerator( ){
    return(request, route, origin) -> String.format("%s:%s", route.getId(), origin);
}
```

### Custom

Custom key generators must implement the [KeyGenerator][keyGenerator] interface. For your custom key generator to be
used it must then be registered as a Spring bean.

```java
@Component
public class CustomKeyGenerator implements KeyGenerator {

    public @NotNull
    String generate(HttpServletRequest request, Route route, String origin) {
        // Return a key derived for the provided parameters.
    }

}
```