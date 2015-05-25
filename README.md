# Vert.x handlers

Handlers provides a minimal and adaptable interface for developing applications on the vert-x3 platform.

[![Build Status](https://travis-ci.org/spriet2000/vertx-handlers.svg?branch=master)](https://travis-ci.org/spriet2000/vertx-handlers)

## Example

```java
    
    Handlers handlers1 = new Handlers(
            (fail, next) -> (e1 -> {
                builder.append("1");
                next.handle(null);
            }),
            (fail, next) -> (e1 -> {
                builder.append("2");
                next.handle(null);
            }))
            .exceptionHandler((Handler) e1 -> hitException.set(true))
            .completeHandler((Handler) e1 -> {
                hitComplete.set(true);
                assertEquals("1234", builder.toString());
            });
            
    Handlers Handlers = new Handlers(
            (fail, next) -> (e1 -> {
                builder.append("3");
                next.handle(null);
            }),
            (fail, next) -> (e1 -> {
                builder.append("4");
                next.handle(null);
            }));
            
    Handlers handlers3 = Handlers.newMerged(handlers1, Handlers);
    
    handlers3.handle(null);
    
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
