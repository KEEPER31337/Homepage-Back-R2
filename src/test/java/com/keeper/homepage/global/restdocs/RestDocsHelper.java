package com.keeper.homepage.global.restdocs;

import com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.snippet.Attributes.Attribute;
import org.springframework.security.access.annotation.Secured;

import static java.util.stream.Collectors.joining;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.snippet.Attributes.key;

public class RestDocsHelper {

  public static Attribute customFormat(String value) {
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

  public static FieldDescriptor field(String path, String description, boolean optional) {
    if (optional) {
      return fieldWithPath(path).description(description)
          .optional();
    }
    return fieldWithPath(path).description(description);
  }

  public static FieldDescriptor[] pageHelper(FieldDescriptor... fieldDescriptors) {
    List<FieldDescriptor> descriptorList = new ArrayList<>(
        List.of(listHelper("content", fieldDescriptors)));
    descriptorList.add(field("empty", "가져오는 페이지가 비어 있는 지"));
    descriptorList.add(field("first", "첫 페이지인지"));
    descriptorList.add(field("last", "마지막 페이지인지"));
    descriptorList.add(field("number", "페이지 number (0부터 시작)"));
    descriptorList.add(field("numberOfElements", "현재 페이지의 데이터 개수"));
    descriptorList.add(subsectionWithPath("pageable").description("페이지에 대한 부가 정보"));
    descriptorList.add(field("sort.empty", "정렬 기준이 비어 있는 지"));
    descriptorList.add(field("sort.sorted", "정렬이 되었는지"));
    descriptorList.add(field("sort.unsorted", "정렬이 되지 않았는지"));
    descriptorList.add(field("totalPages", "총 페이지 수"));
    descriptorList.add(field("totalElements", "총 요소 수"));
    descriptorList.add(field("size", "한 페이지당 데이터 개수"));
    return descriptorList.toArray(new FieldDescriptor[0]);
  }

  public static FieldDescriptor[] listHelper(String objectName,
      FieldDescriptor... fieldDescriptors) {
    List<FieldDescriptor> descriptorList = new ArrayList<>();
    for (FieldDescriptor descriptor : fieldDescriptors) {
      descriptorList.add(
          field(objectName + "[]." + descriptor.getPath(), descriptor.getDescription().toString(),
              descriptor.isOptional()));
    }
    return descriptorList.toArray(new FieldDescriptor[0]);
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

    return "(" + MemberJobType.ROLE_회원 + ")";
  }

  public static String stringArrayToString(String[] arr) {
    return "(" + Arrays.stream(arr)
        .map(i -> new String(i.getBytes()))
        .collect(joining(", ")) + ")";
  }
}
