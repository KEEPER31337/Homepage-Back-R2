package com.keeper.homepage.global.restdocs;

import java.lang.reflect.Method;
import java.util.Arrays;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.snippet.Attributes.Attribute;
import org.springframework.security.access.annotation.Secured;

import static java.util.stream.Collectors.joining;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.key;

public class RestDocsHelper {

  public Attribute customFormat(String value) {
    return key("format").value(value);
  }

  public static Attribute dateTimeFormat() {
    return key("format").value("yyyy-MM-dd HH:mm:ss");
  }

  public static Attribute dateFormat() {
    return key("format").value("yyyy-MM-dd");
  }

  public static FieldDescriptor field(String path, String description, Attribute attribute) {
    return fieldWithPath(path).description(description)
        .attributes(attribute);
  }

  public static FieldDescriptor field(String path, String description) {
    return fieldWithPath(path).description(description);
  }

  public static String getSecuredValue(Class<?> controller, String methodName) {
    Method method = Arrays.stream(controller.getDeclaredMethods())
        .filter(i -> i.getName().equals(methodName))
        .findAny().orElseThrow();

    Class<Secured> securedClass = Secured.class;
    if (method.isAnnotationPresent(securedClass)) {
      Secured annotationMethod = method.getAnnotation(securedClass);
      return stringArrayToString(annotationMethod.value());
    }

    if (controller.isAnnotationPresent(securedClass)) {
      Secured annotationClass = controller.getDeclaredAnnotation(securedClass);
      return stringArrayToString(annotationClass.value());
    }

    return "(None)";
  }

  public static String stringArrayToString(String[] arr) {
    return "(" + Arrays.stream(arr)
        .map(i -> new String(i.getBytes()))
        .collect(joining(", ")) + ")";
  }
}
