# Vert.x handlers

Handlers provides a minimal and adaptable interface for developing applications on the vert-x3 platform.

[![Build Status](https://travis-ci.org/spriet2000/vertx-handlers.svg?branch=master)](https://travis-ci.org/spriet2000/vertx-handlers)

## Example

```java
    
AtomicBoolean hitException = new AtomicBoolean(false);
AtomicBoolean hitComplete = new AtomicBoolean(false);

Handlers handlers1 = new Handlers(
        (fail, next) -> builder -> {
            builder.append("1");
            next.handle(null);
        }),
        (fail, next) -> builder -> {
            builder.append("2");
            next.handle(null);
        }))
        .exceptionHandler((Handler2) builder -> hitException.set(true))
        .completeHandler((Handler2) builder -> {
            hitComplete.set(true);
            assertEquals("1234", builder.toString());
        });

Handlers handlers2 = new Handlers(
        (fail, next) -> builder -> {
            builder.append("3");
            next.handle(null);
        }),
        (fail, next) -> builder -> {
            builder.append("4");
            next.handle(null);
        }));

Handlers handlers3 = Handlers.merge(handlers1, handlers2);

handlers3.handle(new StringBuilder());

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
