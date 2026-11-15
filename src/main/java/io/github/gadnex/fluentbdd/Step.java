package io.github.gadnex.fluentbdd;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Reports a driver method as a BDD step when invoked through its Spring proxy. Placeholders in the
 * description use method parameter names, which must be retained by compiling with {@code
 * -parameters}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Step {
  /**
   * Provides the step description, optionally containing {@code {parameterName}} placeholders.
   *
   * @return the description template
   */
  String value();
}
