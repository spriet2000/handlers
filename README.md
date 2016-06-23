# Railways

Railways provides a minimal and adaptable interface for chaining methods.

[![Build Status](https://travis-ci.org/spriet2000/railways.svg?branch=master)](https://travis-ci.org/spriet2000/railways)

## Example 

```java

AtomicBoolean hitException = new AtomicBoolean(false);
AtomicBoolean hitComplete = new AtomicBoolean(false);

Railway<StringBuilder> handlers = compose(
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

Consumer<StringBuilder> handler = handlers.apply(
        a -> hitException.set(true),
        a -> hitComplete.set(true));

StringBuilder builder = new StringBuilder();
handler.accept(builder);

assertEquals("123", builder.toString());

assertEquals(false, hitException.get());
assertEquals(true, hitComplete.get());

```


## Example railways implementation 

```java

public class ExampleRailway implements BiFunction<BiConsumer<StringBuilder, Throwable>,
        Consumer<StringBuilder>, Consumer<StringBuilder>> {

    @Override
    public Consumer<StringBuilder> apply(BiConsumer<StringBuilder, Throwable> fail,
                                                 Consumer<StringBuilder> next) {
        return next;
    }
}

```
