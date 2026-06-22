package com.navaantrix.vyakhyanLite.service;

import com.navaantrix.vyakhyanLite.dto.request.ConversationRequest;
import com.navaantrix.vyakhyanLite.dto.response.ConversationResponse;

import java.util.List;

public interface ConversationService {

    ConversationResponse createConversation(ConversationRequest request);

    List<ConversationResponse> getAllConversation();

    ConversationResponse getConversationById(Long id);

    ConversationResponse updateConversationById(Long id, ConversationRequest request);

    ConversationResponse deleteConversationById(Long id);

    ConversationResponse updateStatusById(Long id, Long statusId);

    List<ConversationResponse> getConversationByUserId(String userId);

    ConversationResponse getLatestConversation(String userId);

    ConversationResponse updateIsArchivedById(Long id,boolean isArchived);



}
