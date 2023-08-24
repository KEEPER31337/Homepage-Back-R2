package com.keeper.homepage.domain.election.api;

import com.keeper.homepage.domain.election.application.AdminElectionService;
import com.keeper.homepage.domain.election.dto.request.ElectionCandidateRegisterRequest;
import com.keeper.homepage.domain.election.dto.request.ElectionCandidatesRegisterRequest;
import com.keeper.homepage.domain.election.dto.request.ElectionCreateRequest;
import com.keeper.homepage.domain.election.dto.request.ElectionUpdateRequest;
import com.keeper.homepage.domain.election.dto.request.ElectionVotersRequest;
import com.keeper.homepage.domain.election.dto.response.ElectionResponse;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.config.security.annotation.LoginMember;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/elections")
@Secured("ROLE_회장")
public class AdminElectionController {

  private final AdminElectionService adminElectionService;

  @PostMapping
  public ResponseEntity<Void> createElection(
      @LoginMember Member member,
      @RequestBody @Valid ElectionCreateRequest request) {
    adminElectionService.createElection(member, request.getName(), request.getDescription(), request.getIsAvailable());
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @DeleteMapping("/{electionId}")
  public ResponseEntity<Void> deleteElection(
      @PathVariable long electionId) {
    adminElectionService.deleteElection(electionId);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{electionId}")
  public ResponseEntity<Void> updateElection(
      @PathVariable long electionId,
      @RequestBody @Valid ElectionUpdateRequest request) {
    adminElectionService.updateElection(electionId, request.getName(), request.getDescription(),
        request.getIsAvailable());
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping
  public ResponseEntity<Page<ElectionResponse>> getElections(
      @RequestParam(defaultValue = "0") @PositiveOrZero int page,
      @RequestParam(defaultValue = "10") @PositiveOrZero int size) {
    Page<ElectionResponse> response = adminElectionService.getElections(PageRequest.of(page, size));
    return ResponseEntity.ok(response);
  }

  @PostMapping("/{electionId}/candidates/{candidateId}")
  public ResponseEntity<Void> registerCandidate(
      @RequestBody @Valid ElectionCandidateRegisterRequest request,
      @PathVariable long electionId,
      @PathVariable long candidateId) {
    adminElectionService.registerCandidate(request.getDescription(), request.getMemberJobId(), electionId, candidateId);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PostMapping("/{electionId}/candidates")
  public ResponseEntity<Void> registerCandidates(
      @RequestBody @Valid ElectionCandidatesRegisterRequest request,
      @PathVariable long electionId) {
    adminElectionService.registerCandidates(request.getCandidateIds(), request.getDescription(),
        request.getMemberJobId(), electionId);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @DeleteMapping("/{electionId}/candidates/{candidateId}")
  public ResponseEntity<Void> deleteCandidate(
      @PathVariable long electionId,
      @PathVariable long candidateId) {
    adminElectionService.deleteCandidate(electionId, candidateId);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{electionId}/voters")
  public ResponseEntity<Void> registerVoters(
      @RequestBody @Valid ElectionVotersRequest request,
      @PathVariable long electionId) {
    adminElectionService.registerVoters(request.getVoterIds(), electionId);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @DeleteMapping("/{electionId}/voters")
  public ResponseEntity<Void> deleteVoters(
      @RequestBody @Valid ElectionVotersRequest request,
      @PathVariable long electionId) {
    adminElectionService.deleteVoters(request.getVoterIds(), electionId);
    return ResponseEntity.noContent().build();
  }

}
