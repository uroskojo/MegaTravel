package com.example.ISAums.repository;

import com.example.ISAums.model.Friendship;
import com.example.ISAums.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.UUID;

public interface FriendshipRepository extends JpaRepository<Friendship, UUID> {

    @Query(value = "select u.id  from friendship f left join user u on u.id = (case when f.user_sender_id = ?1 then f.user_invited_id " +
            "when f.user_invited_id = ?1 then f.user_sender_id  end) where u.id != ?1 and f.invitation_status = 'ACCEPTED'", nativeQuery = true)
    List<UUID> getFriends(String user_id);

    @Modifying
    @Query(value = "delete from friendship where friendship.user_sender_id = ?1 and friendship.user_invited_id = ?2 or friendship.user_sender_id = ?2 and friendship.user_invited_id = ?1", nativeQuery = true)
    void removeFriendship(String mine_id, String friend_id);

    @Query(value = "select * from isa_database.friendship f where f.invitation_status != \"REJECTED\" and f.user_sender_id = ?1 and f.user_invited_id = ?2 or f.user_sender_id = ?2 and f.user_invited_id = ?1", nativeQuery = true)
    Friendship isItFriendOfMine(String mineId, String userId);

    @Query(value = "select * from isa_database.friendship f where f.user_invited_id = ?1 and f.invitation_status = 'PENDING'", nativeQuery = true)
    List<Friendship> getFriendshipRequestsOfMine(String mine_id);

    @Query(value = "select u.id  from friendship f left join user u on u.id = (case when f.user_sender_id = ?1 then f.user_invited_id " +
            "when f.user_invited_id = ?1 then f.user_sender_id  end) where u.id != ?1 and f.invitation_status = 'ACCEPTED'", nativeQuery = true)
    List<User> sortByFirstName(String username);

    @Query(value = "SELECT u.id  FROM friendship f " +
                   "LEFT JOIN USER u ON u.id = " +
                   "(CASE WHEN f.user_sender_id = :userId THEN f.user_invited_id " +
                   "WHEN f.user_invited_id = :userId THEN f.user_sender_id  END) " +
                   "WHERE u.id != :userId " +
                   "AND f.invitation_status = 'ACCEPTED'" +
                   "ORDER BY ", nativeQuery = true)
    List<User> sortByLastName(String userId);

}
