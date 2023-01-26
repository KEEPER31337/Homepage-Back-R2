package com.keeper.homepage.domain.member.dao.friend;

import com.keeper.homepage.domain.member.entity.friend.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Long> {

}
