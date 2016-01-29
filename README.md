# Handlers

Handlers provides a minimal and adaptable interface for chaining handlers.

[![Build Status](https://travis-ci.org/spriet2000/handlers.svg?branch=master)](https://travis-ci.org/spriet2000/handlers)

## Example success

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

## Example fail


```java

        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);

        Handlers<Void> handlers = compose(
                (f, n) -> n::accept,
                (f, n) -> a -> f.accept(new RuntimeException()),
                (f, n) -> n::accept );

        Consumer<Void> handler = handlers.apply(
                a -> hitException.set(true),
                a -> hitComplete.set(true));

        handler.accept(null);

        assertEquals(true, hitException.get());
        assertEquals(false, hitComplete.get());

```


## Example handler implementation 

```java

    public class ExampleHandler<StringBuilder> implements BiFunction<BiConsumer<StringBuilder, Throwable>,
            Consumer<StringBuilder>, Consumer<StringBuilder>> {

        @Override
        public Consumer<StringBuilder> apply(BiConsumer<StringBuilder, Throwable> fail,
                                                     Consumer<StringBuilder> next) {
            return next::accept;
        }
    }

```

## Example success bi handlers

```java

        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);

        BiHandlers<StringBuilder, Void> handlers = compose(
                (f, n) -> (e, a) -> {
                    e.append("1");
                    n.accept(e, a);
                }, (f, n) -> (e, a) -> {
                    e.append("2");
                    n.accept(e, a);
                }, (f, n) -> (e, a) -> {
                    e.append("3");
                    n.accept(e, a);
                });

        BiConsumer<StringBuilder, Void> handler = handlers.apply(
                (e, a) -> hitException.set(true),
                (e, a) -> hitComplete.set(true));

        StringBuilder builder = new StringBuilder();
        handler.accept(builder, null);

        assertEquals("123", builder.toString());

        assertEquals(false, hitException.get());
        assertEquals(true, hitComplete.get());

```
## Example bi handler implementation

```java

    public class ExampleHandler<StringBuilder> implements  BiFunction<BiConsumer<StringBuilder, Throwable>,
            BiConsumer<StringBuilder, Void>, BiConsumer<StringBuilder, Void>> {

        @Override
        public BiConsumer<StringBuilder, Void> apply(BiConsumer<StringBuilder, Throwable> fail,
                                                  BiConsumer<StringBuilder, Void> next) {
            return next::accept;
        }
    }

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
