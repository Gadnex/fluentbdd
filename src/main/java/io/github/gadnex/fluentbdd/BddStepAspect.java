package io.github.gadnex.fluentbdd;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;

@Aspect
class BddStepAspect {

  private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{([^{}]*)}");

  @Around("@annotation(step)")
  Object reportStep(ProceedingJoinPoint joinPoint, Step step) throws Throwable {
    if (!(joinPoint.getTarget() instanceof BddTestDriver driver)) {
      throw new IllegalStateException("@Step methods must be declared on a BddTestDriver");
    }

    Method method =
        AopUtils.getMostSpecificMethod(
            ((MethodSignature) joinPoint.getSignature()).getMethod(),
            joinPoint.getTarget().getClass());
    String description = formatDescription(step.value(), method, joinPoint.getArgs());
    driver.step(description);

    Object result = joinPoint.proceed();
    return result == joinPoint.getTarget() ? joinPoint.getThis() : result;
  }

  private String formatDescription(String template, Method method, Object[] arguments) {
    Matcher matcher = PLACEHOLDER_PATTERN.matcher(template);
    StringBuilder description = new StringBuilder();
    int previousEnd = 0;

    while (matcher.find()) {
      description.append(template, previousEnd, matcher.start());
      String parameterName = matcher.group(1);
      int parameterIndex = findParameterIndex(parameterName, method);
      description.append(arguments[parameterIndex]);
      previousEnd = matcher.end();
    }

    return description.append(template, previousEnd, template.length()).toString();
  }

  private int findParameterIndex(String name, Method method) {
    Parameter[] parameters = method.getParameters();
    for (int i = 0; i < parameters.length; i++) {
      if (parameters[i].getName().equals(name)) {
        return i;
      }
    }

    throw new IllegalArgumentException(
        "@Step placeholder '{"
            + name
            + "}' does not match a parameter of "
            + method.toGenericString()
            + "; compile test sources with parameter names enabled");
  }
}
