package com.keeper.homepage.domain.election.entity;

import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@DynamicInsert
@DynamicUpdate
@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "election")
public class Election extends BaseEntity {

  private static final int MAX_NAME_LENGTH = 45;
  private static final int MAX_DESCRIPTION_LENGTH = 200;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "name", nullable = false, length = MAX_NAME_LENGTH)
  private String name;

  @Column(name = "description", length = MAX_DESCRIPTION_LENGTH)
  private String description;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "creator", nullable = false)
  private Member member;

  @Column(name = "is_available", nullable = false)
  private Boolean isAvailable;

  @OneToMany(mappedBy = "election", cascade = REMOVE)
  private final List<ElectionCandidate> electionCandidates = new ArrayList<>();

  @OneToMany(mappedBy = "election")
  private final List<ElectionVoter> electionVoters = new ArrayList<>();

  @Builder
  private Election(String name, String description, Member member, Boolean isAvailable) {
    this.name = name;
    this.description = description;
    this.member = member;
    this.isAvailable = isAvailable;
  }

}
