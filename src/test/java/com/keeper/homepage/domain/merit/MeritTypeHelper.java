package com.keeper.homepage.domain.merit;

import com.keeper.homepage.domain.merit.dao.MeritTypeRepository;
import com.keeper.homepage.domain.merit.entity.MeritType;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MeritTypeHelper {

  @Autowired
  MeritTypeRepository meritTypeRepository;

  public MeritType generate() {
    return this.builder().build();
  }

  public MeritTypeBuilder builder() {
    return new MeritTypeBuilder();
  }

  public final class MeritTypeBuilder {

    private Integer merit;
    private String detail;

    private MeritTypeBuilder() {
    }

    public MeritTypeBuilder merit(Integer merit) {
      this.merit = merit;
      return this;
    }

    public MeritTypeBuilder detail(String detail) {
      this.detail = detail;
      return this;
    }

    public MeritType build() {
      return meritTypeRepository.save(MeritType.builder()
          .merit(merit != null ? merit : 3)
          .detail(detail != null ? detail : UUID.randomUUID().toString())
          .build());
    }
  }
}
