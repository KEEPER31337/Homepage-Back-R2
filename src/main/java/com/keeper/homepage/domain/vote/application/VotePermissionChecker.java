package com.keeper.homepage.domain.vote.application;

import com.keeper.homepage.domain.vote.entity.Vote;
import java.util.Set;

final class VotePermissionChecker {

  private VotePermissionChecker() {
  }

  static boolean isPermitted(Vote vote, long memberId, Set<String> memberRoles) {
    boolean permittedByMember = vote.getPermitByMember().contains(memberId);
    boolean permittedByRole = vote.getPermitByRole().stream().anyMatch(memberRoles::contains);
    if (!permittedByMember && !permittedByRole) {
      return false;
    }
    return true;
  }
}
