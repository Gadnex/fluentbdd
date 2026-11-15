package io.github.gadnex.fluentbdd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig({BddStepAspectTest.class, BddStepAspectTest.DriverConfiguration.class})
@BddDriver(BddStepAspectTest.StepProbeDriver.class)
class BddStepAspectTest {

  @Autowired private StepProbeDriver driver;

  private Map<String, String> reportEntries;

  @BeforeEach
  void setUp() {
    reportEntries = new LinkedHashMap<>();
    driver.setupTest(reportEntries::putAll);
  }

  @Test
  void substitutesArgumentsAndKeepsFluentCallsOnTheProxy() {
    String value = "Ada {literal} $1\\";

    StepProbeDriver returnedDriver = driver.givenText(value, "Lovelace").thenTextIs(value);

    assertThat(returnedDriver).isSameAs(driver);
    assertThat(reportEntries)
        .containsExactly(
            Map.entry(
                "Step 1",
                "Given text '" + value + "' again '" + value + "' with suffix 'Lovelace'"),
            Map.entry("Step 2", "Then text is '" + value + "'"));
  }

  @Test
  void formatsNullArguments() {
    driver.givenText(null, "Lovelace");

    assertThat(reportEntries)
        .containsEntry("Step 1", "Given text 'null' again 'null' with suffix 'Lovelace'");
  }

  @Test
  void publishesTheStepBeforeTheAnnotatedMethodFails() {
    assertThatThrownBy(() -> driver.failWith("expected failure"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("expected failure");

    assertThat(reportEntries)
        .containsEntry("Step 1", "When failure is requested: expected failure");
  }

  @Test
  void rejectsPlaceholdersThatDoNotNameMethodParameters() {
    assertThatThrownBy(driver::useUnknownPlaceholder)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("{missing}")
        .hasMessageContaining("useUnknownPlaceholder");

    assertThat(driver.methodWasInvoked).isFalse();
    assertThat(reportEntries).isEmpty();
  }

  @Configuration(proxyBeanMethods = false)
  static class DriverConfiguration {

    @Bean
    StepProbeDriver stepProbeDriver() {
      return new StepProbeDriver();
    }
  }

  static class StepProbeDriver extends BddTestDriver {

    private String value;
    private boolean methodWasInvoked;

    @Step("Given text '{text}' again '{text}' with suffix '{suffix}'")
    StepProbeDriver givenText(String text, String suffix) {
      value = text;
      return this;
    }

    @Step("Then text is '{expected}'")
    StepProbeDriver thenTextIs(String expected) {
      assertThat(value).isEqualTo(expected);
      return this;
    }

    @Step("When failure is requested: {reason}")
    void failWith(String reason) {
      throw new IllegalStateException(reason);
    }

    @Step("Given unknown '{missing}'")
    void useUnknownPlaceholder() {
      methodWasInvoked = true;
    }

    @Override
    protected void clearState() {
      value = null;
      methodWasInvoked = false;
    }
  }
}
