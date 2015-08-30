# Handlers

Handlers provides a minimal and adaptable interface for developing applications.

[![Build Status](https://travis-ci.org/spriet2000/vertx-handlers.svg?branch=master)](https://travis-ci.org/spriet2000/vertx-handlers)

## Example

```java
    
       AtomicBoolean hitException = new AtomicBoolean(false);
       AtomicBoolean hitComplete = new AtomicBoolean(false);

       StringBuilder builder = new StringBuilder();

       Handlers<StringBuilder> handlers = new Handlers<>(
               (e, a) -> hitException.set(true),
               (e, a) -> hitComplete.set(true),
               (f, n) -> (e, a) -> {
                   e.append("1");
                   n.accept("A");
               }, (f, n) -> (e, a) -> {
                   assertEquals("A", a);
                   e.append("2");
                   n.accept("B");
               }, (f, n) -> (e, a) -> {
                   assertEquals("B", a);
                   e.append("3");
                   n.accept(null);
               });

       handlers.accept(builder, null);

       assertEquals("123", builder.toString());
       assertEquals(false, hitException.get());
       assertEquals(true, hitComplete.get());

```
## Installation

### Maven

```xml

    <dependency>
        <groupId>com.github.spriet2000</groupId>
        <artifactId>vertx-handlers</artifactId>
        <version>0.1.0-SNAPSHOT</version>
    </dependency>

```

### Without maven

[https://oss.sonatype.org/content/repositories/snapshots/com/github/spriet2000](https://oss.sonatype.org/content/repositories/snapshots/com/github/spriet2000)
