# Vert.x handlers

Handlers provides a minimal and adaptable interface for developing applications on the vert-x3 platform.

[![Build Status](https://travis-ci.org/spriet2000/vertx-handlers.svg?branch=master)](https://travis-ci.org/spriet2000/vertx-handlers)

## Example

```java
    
AtomicBoolean hitException = new AtomicBoolean(false);
AtomicBoolean hitComplete = new AtomicBoolean(false);

Handlers2 handlers1 = new Handlers2(
        (fail, next) -> ((builder, args) -> {
            builder.append("1");
            next.handle(builder);
        }),
        (fail, next) -> ((builder, args) -> {
            builder.append("2");
            next.handle(builder);
        }))
        .exceptionHandler((Handler2) (e, args) -> hitException.set(true))
        .completeHandler((Handler2) (builder, args) -> {
            hitComplete.set(true);
            assertEquals("1234", builder.toString());
        })
        .with(() -> new StringBuilder());
        
Handlers2 handlers2 = new Handlers2(
        (fail, next) -> ((builder, args) -> {
            builder.append("3");
            next.handle(builder);
        }),
        (fail, next) -> ((builder, args) -> {
            builder.append("4");
            next.handle(builder);
        }));
        
Handlers2 handles3 = Handlers2.merge(handlers1, handlers2);

handles3.handle();

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
