package com.navaantrix.vyakhyanLite.repository;

import com.navaantrix.vyakhyanLite.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByTitleIgnoreCaseAndUserIdAndStatusStatusIdNot(
            String title,
            String userId,
            Long statusId
    );

    Optional<Conversation> findByTitleIgnoreCaseAndUserIdAndConversationIdNot(
            String title,
            String userId,
            Long id
    );

    List<Conversation> findByStatusStatusIdNot(Long statusId);

    Optional<Conversation> findByConversationIdAndStatusStatusIdNot(
            Long id,
            Long statusId
    );


    List<Conversation> findByUserIdAndStatusStatusIdOrderByConversationIdDesc(
            String userId,
            Long statusId
    );

    List<Conversation> findByStatusStatusIdNotOrderByConversationIdDesc(Long statusId);


    List<Conversation> findByStatusStatusIdOrderByConversationIdDesc(
            Long clientId,
            boolean globallyAvailable,
            Long activeStatus);

    List<Conversation> findByStatusStatusIdOrderByConversationIdDesc( Long activeStatus);


    Optional<Conversation> findByConversationIdAndStatusStatusId(Long conversationId, Long activeStatus);


    List<Conversation> findByIsArchivedAndStatusStatusIdOrderByConversationIdDesc(
            boolean isArchived,
            Long statusId
    );

    Conversation findTopByUserIdAndStatusStatusIdOrderByConversationIdDesc(String userId, Long activeStatus);
}

