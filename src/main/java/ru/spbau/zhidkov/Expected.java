package ru.spbau.zhidkov;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Method marked with this annotation shows that correct
 * result of this method invocations is an exception.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Expected {
    public Class<? extends Throwable> expectedException();
}
