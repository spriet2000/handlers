# Handlers

Handlers provides a minimal and adaptable interface for chaining handlers.

[![Build Status](https://travis-ci.org/spriet2000/handlers.svg?branch=master)](https://travis-ci.org/spriet2000/handlers)

## Example

```java
    
        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);

        Handlers<StringBuilder> handlers = compose(
                (f, n) -> a -> {
                    a.append("1");
                    n.accept(a);
                }, (f, n) -> a -> {
                    a.append("2");
                    n.accept(a);
                }, (f, n) -> a -> {
                    a.append("3");
                    n.accept(null);
                });

        Consumer<StringBuilder> handler = handlers.apply(
                a -> hitException.set(true),
                a -> hitComplete.set(true));

        StringBuilder builder = new StringBuilder();
        handler.accept(builder);

        assertEquals("123", builder.toString());
        assertEquals(false, hitException.get());
        assertEquals(true, hitComplete.get());

```

## Installation

### Maven

```xml

    <dependency>
        <groupId>com.github.spriet2000</groupId>
        <artifactId>handlers</artifactId>
        <version>0.1.0-SNAPSHOT</version>
    </dependency>

```

### Without maven

[https://oss.sonatype.org/content/repositories/snapshots/com/github/spriet2000](https://oss.sonatype.org/content/repositories/snapshots/com/github/spriet2000)
