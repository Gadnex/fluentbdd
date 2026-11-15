package io.github.gadnex.fluentbdd;

import java.util.concurrent.atomic.AtomicInteger;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.TestReporter;

/** Base class for Spring-managed drivers that report BDD steps to JUnit. */
public abstract class BddTestDriver {

  /** The current test's reporter, or {@code null} before initialization. */
  protected @Nullable TestReporter testReporter;

  private @Nullable AtomicInteger stepCounter;

  /** Creates a driver that has not yet been initialized for a test. */
  protected BddTestDriver() {}

  /**
   * Initializes reporting and resets the driver's state for a test.
   *
   * @param testReporter the reporter used to publish step descriptions
   */
  protected void setupTest(TestReporter testReporter) {
    this.testReporter = testReporter;
    stepCounter = new AtomicInteger(1);
    clearState();
  }

  /**
   * Publishes a numbered step, starting at one for each test. Does nothing before initialization.
   *
   * @param description the step description to publish
   */
  protected void step(String description) {
    if (testReporter != null && stepCounter != null) {
      testReporter.publishEntry("Step " + stepCounter.getAndIncrement(), description);
    }
  }

  /** Resets a scenario-specific state before each test. */
  protected abstract void clearState();
}
