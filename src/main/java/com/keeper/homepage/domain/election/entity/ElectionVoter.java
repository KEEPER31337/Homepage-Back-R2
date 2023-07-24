package com.keeper.homepage.domain.election.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.keeper.homepage.domain.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@DynamicInsert
@DynamicUpdate
@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(name = "election_voter")
@IdClass(ElectionVoterPK.class)
public class ElectionVoter {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "voter_id", nullable = false)
  private Member member;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "election_id", nullable = false)
  private Election election;

  @Column(name = "is_voted", nullable = false)
  private Boolean isVoted;

  @Builder
  private ElectionVoter(Member member, Election election, Boolean isVoted) {
    this.member = member;
    this.election = election;
    this.isVoted = isVoted;
  }

}
