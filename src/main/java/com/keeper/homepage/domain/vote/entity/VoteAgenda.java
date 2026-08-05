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
import org.hibernate.annotations.Check;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OnDelete;

@DynamicInsert
@DynamicUpdate
@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Check(name = "chk_vote_agenda_select_range",
    constraints = "min_select >= 1 AND min_select <= max_select")
@Table(name = "vote_agenda",
    indexes = @Index(name = "idx_vote_agenda_vote", columnList = "vote_id"),
    uniqueConstraints = @UniqueConstraint(name = "uq_vote_agenda_vote_order",
        columnNames = {"vote_id", "display_order"}))
public class VoteAgenda {

  private static final int MAX_TITLE_LENGTH = 500;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false, updatable = false, columnDefinition = "INT")
  private Long id;

  @ManyToOne(fetch = LAZY)
  @OnDelete(action = CASCADE)
  @JoinColumn(name = "vote_id", nullable = false, columnDefinition = "INT",
      foreignKey = @ForeignKey(name = "fk_vote_agenda_vote"))
  private Vote vote;

  @Column(name = "title", nullable = false, length = MAX_TITLE_LENGTH)
  private String title;

  @Column(name = "display_order", nullable = false, columnDefinition = "INT UNSIGNED")
  private Integer displayOrder;

  @Column(name = "min_select", nullable = false, columnDefinition = "INT UNSIGNED")
  private Integer minSelect;

  @Column(name = "max_select", nullable = false, columnDefinition = "INT UNSIGNED")
  private Integer maxSelect;

  @OneToMany(mappedBy = "agenda")
  private final List<VoteOption> options = new ArrayList<>();

  @Builder
  private VoteAgenda(Vote vote, String title, Integer displayOrder, Integer minSelect,
      Integer maxSelect) {
    this.vote = vote;
    this.title = title;
    this.displayOrder = displayOrder;
    this.minSelect = minSelect;
    this.maxSelect = maxSelect;
  }
}
