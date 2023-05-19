package com.keeper.homepage.global.util.redis;

import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RedisUtilTest extends IntegrationTest {

  @Test
  @DisplayName("직렬화, 역직렬화가 잘 동작해야 한다.")
  void should_serializeAndDeserializeIsOk() {
    TestDto testDto = new TestDto(1, "AA");
    redisUtil.setDataExpire("0", testDto, 10000);

    Optional<TestDto> getTestDto = redisUtil.getData("0", TestDto.class);
    assertThat(getTestDto).isNotEmpty();
    assertThat(getTestDto.get().a).isEqualTo(1);
    assertThat(getTestDto.get().b).isEqualTo("AA");

    testDto.a = 2;
    testDto.b = "BB";
    redisUtil.setDataExpire("0", testDto, 10000); // testDto 갱신

    Optional<TestDto> newTestDto = redisUtil.getData("0", TestDto.class);
    assertThat(newTestDto).isNotEmpty();
    assertThat(newTestDto.get().a).isEqualTo(2);
    assertThat(newTestDto.get().b).isEqualTo("BB");
  }

  private static class TestDto {

    private int a;
    private String b;

    private TestDto() {
    }

    public TestDto(int a, String b) {
      this.a = a;
      this.b = b;
    }

    public int getA() {
      return a;
    }

    public String getB() {
      return b;
    }

    public void setA(int a) {
      this.a = a;
    }

    public void setB(String b) {
      this.b = b;
    }
  }
}
