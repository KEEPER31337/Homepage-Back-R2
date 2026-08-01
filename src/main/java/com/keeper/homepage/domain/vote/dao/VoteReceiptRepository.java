package com.keeper.homepage.domain.vote.dao;

import com.keeper.homepage.domain.vote.entity.VoteReceipt;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteReceiptRepository extends JpaRepository<VoteReceipt, UUID> {

}
