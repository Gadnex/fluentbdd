package consumer;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.gadnex.fluentbdd.BddDriver;
import io.github.gadnex.fluentbdd.BddTestDriver;
import io.github.gadnex.fluentbdd.Step;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestReporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig({ExternalDriverTest.class, ExternalDriverTest.Driver.class})
@BddDriver(ExternalDriverTest.Driver.class)
class ExternalDriverTest {

  @Autowired private Driver driver;

  @Test
  void initializesAndRunsAnExternalDriver() {
    assertThat(driver.isInitialized()).isTrue();
    Map<String, String> entries = new LinkedHashMap<>();
    driver.captureEntries(entries);
    assertThat(driver.execute()).isSameAs(driver);
    assertThat(entries)
        .containsExactly(Map.entry("Step 1", "Execute"), Map.entry("Step 2", "Manual step"));
  }

  @Component
  static class Driver extends BddTestDriver {
    boolean initialized;

    boolean isInitialized() {
      return initialized;
    }

    void captureEntries(Map<String, String> entries) {
      TestReporter reporter = testReporter;
      assertThat(testReporter).isNotNull();
      testReporter =
          values -> {
            entries.putAll(values);
            reporter.publishEntry(values);
          };
    }

    @Override
    protected void clearState() {
      initialized = true;
    }

    @Step("Execute")
    public Driver execute() {
      step("Manual step");
      return this;
    }
  }
}
