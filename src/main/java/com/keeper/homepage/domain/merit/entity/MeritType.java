package com.keeper.homepage.domain.merit.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "merit_type")
public class MeritType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "merit", nullable = false)
    private Integer merit;

    @Column(name = "is_merit", nullable = false)
    private Boolean isMerit;

    @Column(name = "detail", nullable = false)
    private String detail;

    @Builder
    public MeritType(Integer merit, Boolean isMerit, String detail) {
        this.merit = merit;
        this.isMerit = isMerit;
        this.detail = detail;
    }




}
