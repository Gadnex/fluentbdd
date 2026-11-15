package io.github.gadnex.fluentbdd;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;

/**
 * Initializes a Spring-managed BDD driver before each test. The driver must be registered as a
 * Spring bean, for example, with {@code @Import}. Because drivers commonly hold mutable scenario
 * state, tests using the same driver bean must not run concurrently. Methods annotated with {@link
 * Step} must be called through the injected Spring proxy; self-invocation, private methods, and
 * final methods are not intercepted.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(BddDriverExtension.class)
@Import(BddStepConfiguration.class)
public @interface BddDriver {

  /**
   * Identifies the Spring bean type to initialize before each test.
   *
   * @return the driver type
   */
  Class<? extends BddTestDriver> value();
}
