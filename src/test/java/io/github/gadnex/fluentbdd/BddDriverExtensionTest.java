package io.github.gadnex.fluentbdd;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig({BddDriverExtensionTest.class, BddDriverExtensionTest.ProbeDriver.class})
@BddDriver(BddDriverExtensionTest.ProbeDriver.class)
class BddDriverExtensionTest {

  @Autowired private ProbeDriver driver;

  @Test
  void initializesDriverBeforeEachTest() {
    assertThat(driver.state).isEqualTo("cleared");
    assertThat(driver.hasReporter()).isTrue();
    driver.state = "dirty";
  }

  @Test
  void resetsDriverForEachTest() {
    assertThat(driver.state).isEqualTo("cleared");
    assertThat(driver.hasReporter()).isTrue();
  }

  @Component
  static class ProbeDriver extends BddTestDriver {

    private String state;

    boolean hasReporter() {
      return testReporter != null;
    }

    @Override
    protected void clearState() {
      state = "cleared";
    }
  }
}
