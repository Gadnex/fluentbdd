package io.github.gadnex.fluentbdd;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/** JUnit extension that initializes the driver selected by {@link BddDriver} before each test. */
class BddDriverExtension implements BeforeEachCallback {

  /** Creates the driver initialization extension. */
  BddDriverExtension() {}

  /**
   * Retrieves the configured driver from Spring and initializes it for the current test.
   *
   * @param context the current JUnit extension context
   */
  @Override
  public void beforeEach(ExtensionContext context) {
    var driverType = context.getRequiredTestClass().getAnnotation(BddDriver.class).value();
    var driver = SpringExtension.getApplicationContext(context).getBean(driverType);
    driver.setupTest(context::publishReportEntry);
  }
}
