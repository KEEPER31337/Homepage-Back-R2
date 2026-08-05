package com.keeper.homepage.domain.vote.entity;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;
import static org.hibernate.annotations.OnDeleteAction.CASCADE;

import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OnDelete;

@DynamicInsert
@DynamicUpdate
@Getter
@Entity
@IdClass(VoteChoiceId.class)
@NoArgsConstructor(access = PROTECTED)
@Table(name = "vote_choice",
    indexes = @Index(name = "idx_vote_choice_option", columnList = "option_id"))
public class VoteChoice {

  @Id
  @ManyToOne(fetch = LAZY)
  @OnDelete(action = CASCADE)
  @JoinColumn(name = "receipt_token", nullable = false, updatable = false,
      columnDefinition = "BINARY(16)",
      foreignKey = @ForeignKey(name = "fk_vote_choice_receipt"))
  private VoteReceipt receipt;

  @Id
  @ManyToOne(fetch = LAZY)
  @OnDelete(action = CASCADE)
  @JoinColumn(name = "option_id", nullable = false, updatable = false, columnDefinition = "INT",
      foreignKey = @ForeignKey(name = "fk_vote_choice_option"))
  private VoteOption option;

  @Builder
  private VoteChoice(VoteReceipt receipt, VoteOption option) {
    this.receipt = receipt;
    this.option = option;
  }
}
