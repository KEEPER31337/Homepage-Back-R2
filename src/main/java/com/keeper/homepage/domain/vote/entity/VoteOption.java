package com.keeper.homepage.domain.vote.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static org.hibernate.annotations.OnDeleteAction.CASCADE;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;
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
@NoArgsConstructor(access = PROTECTED)
@Table(name = "vote_option",
    indexes = @Index(name = "idx_vote_option_agenda", columnList = "agenda_id"),
    uniqueConstraints = @UniqueConstraint(name = "uq_vote_option_agenda_order",
        columnNames = {"agenda_id", "display_order"}))
public class VoteOption {

  private static final int MAX_CONTENT_LENGTH = 500;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false, updatable = false, columnDefinition = "INT")
  private Long id;

  @ManyToOne(fetch = LAZY)
  @OnDelete(action = CASCADE)
  @JoinColumn(name = "agenda_id", nullable = false, columnDefinition = "INT",
      foreignKey = @ForeignKey(name = "fk_vote_option_agenda"))
  private VoteAgenda agenda;

  @Column(name = "content", nullable = false, length = MAX_CONTENT_LENGTH)
  private String content;

  @Column(name = "display_order", nullable = false, columnDefinition = "INT UNSIGNED")
  private Integer displayOrder;

  @OneToMany(mappedBy = "option")
  private final List<VoteChoice> choices = new ArrayList<>();

  @Builder
  private VoteOption(VoteAgenda agenda, String content, Integer displayOrder) {
    this.agenda = agenda;
    this.content = content;
    this.displayOrder = displayOrder;
  }
}
