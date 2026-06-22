package com.navaantrix.vyakhyanLite.repository;

import com.navaantrix.vyakhyanLite.entity.Messages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessagesRepository extends JpaRepository<Messages,Long> {
    Optional<Messages> findByMessagesIdAndStatusStatusIdNot(Long messagesId, Long statusId);

    List<Messages> findByStatusStatusIdNotOrderByMessagesIdDesc(Long statusId);

    List<Messages> findByConversationConversationIdAndStatusStatusIdNotOrderByMessagesIdDesc(
            Long conversationId,
            Long statusId
    );

    List<Messages> findTop5ByConversation_ConversationIdAndRoleOrderByMessagesIdDesc(
            Long conversationId,
            String role
    );

    Optional<Messages> findByMessagesIdAndStatusStatusId(Long messageId, Long activeStatus);

    Optional<Messages> findByParentId(Long messagesId);
}
