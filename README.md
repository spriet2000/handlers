# Handlers

Handlers provides a minimal and adaptable interface for chaining methods.

[![Build Status](https://travis-ci.org/spriet2000/railways.svg?branch=master)](https://travis-ci.org/spriet2000/railways)

## Example 

```java

AtomicBoolean hitStop = new AtomicBoolean(false);
AtomicBoolean hitComplete = new AtomicBoolean(false);

Handlers<StringBuilder> handlers = build(
        (f, n) -> a -> {
            a.append("1");
            n.accept(a);
        }, (f, n) -> a -> {
            a.append("2");
            n.accept(a);
        }, (f, n) -> a -> {
            a.append("3");
            n.accept(a);
        });

Consumer<StringBuilder> consumer = handlers.apply(
        a -> hitStop.set(true),
        a -> hitComplete.set(true));

StringBuilder builder = new StringBuilder();
consumer.accept(builder);

assertEquals("123", builder.toString());

assertEquals(false, hitStop.get());
assertEquals(true, hitComplete.get());

```


## Example handler implementation 

```java

public class Station implements BiFunction<BiConsumer<StringBuilder, Throwable>,
        Consumer<StringBuilder>, Consumer<StringBuilder>> {

    @Override
    public Consumer<StringBuilder> apply(BiConsumer<StringBuilder, Throwable> stop,
                                                 Consumer<StringBuilder> next) {
        return next;
    }
}

```
