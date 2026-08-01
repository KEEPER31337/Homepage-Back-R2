package com.keeper.homepage.domain.vote.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static org.hibernate.annotations.OnDeleteAction.RESTRICT;
import static org.hibernate.generator.EventType.INSERT;

import com.keeper.homepage.domain.member.entity.Member;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.type.SqlTypes;

@DynamicInsert
@DynamicUpdate
@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Check(name = "chk_vote_period", constraints = "end_at > start_at")
@Table(name = "vote",
    indexes = {
        @Index(name = "idx_vote_created_by", columnList = "created_by"),
        @Index(name = "idx_vote_start_at", columnList = "start_at")
    })
public class Vote {

  private static final int MAX_TITLE_LENGTH = 500;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false, updatable = false, columnDefinition = "INT")
  private Long id;

  @Column(name = "title", nullable = false, length = MAX_TITLE_LENGTH)
  private String title;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @JdbcTypeCode(SqlTypes.JSON)
  @ColumnDefault("(JSON_ARRAY())")
  @Column(name = "permit_by_role", nullable = false, columnDefinition = "JSON")
  private List<String> permitByRole = new ArrayList<>();

  @JdbcTypeCode(SqlTypes.JSON)
  @ColumnDefault("(JSON_ARRAY())")
  @Column(name = "permit_by_member", nullable = false, columnDefinition = "JSON")
  private List<Long> permitByMember = new ArrayList<>();

  @Column(name = "start_at", nullable = false, columnDefinition = "DATETIME(6)")
  private LocalDateTime startAt;

  @Column(name = "end_at", nullable = false, columnDefinition = "DATETIME(6)")
  private LocalDateTime endAt;

  @Generated(event = INSERT)
  @ColumnDefault("CURRENT_TIMESTAMP(6)")
  @Column(name = "created_at", nullable = false, insertable = false, updatable = false,
      columnDefinition = "DATETIME(6)")
  private LocalDateTime createdAt;

  @ManyToOne(fetch = LAZY)
  @OnDelete(action = RESTRICT)
  @JoinColumn(name = "created_by", nullable = false, columnDefinition = "INT",
      foreignKey = @ForeignKey(name = "fk_vote_created_by_member"))
  private Member createdBy;

  @OneToMany(mappedBy = "vote")
  private final List<VoteAgenda> agendas = new ArrayList<>();

  @OneToMany(mappedBy = "vote")
  private final List<VoteParticipation> participations = new ArrayList<>();

  @OneToMany(mappedBy = "vote")
  private final List<VoteReceipt> receipts = new ArrayList<>();

  @Builder
  private Vote(String title, String description, List<String> permitByRole,
      List<Long> permitByMember, LocalDateTime startAt, LocalDateTime endAt, Member createdBy) {
    this.title = title;
    this.description = description;
    this.permitByRole = permitByRole == null ? new ArrayList<>() : new ArrayList<>(permitByRole);
    this.permitByMember = permitByMember == null
        ? new ArrayList<>()
        : new ArrayList<>(permitByMember);
    this.startAt = startAt;
    this.endAt = endAt;
    this.createdBy = createdBy;
  }
}
