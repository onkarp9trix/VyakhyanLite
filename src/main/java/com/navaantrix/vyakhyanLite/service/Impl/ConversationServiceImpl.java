package com.navaantrix.vyakhyanLite.service.Impl;

import com.navaantrix.vyakhyanLite.dto.request.ConversationRequest;
import com.navaantrix.vyakhyanLite.dto.response.ConversationResponse;
import com.navaantrix.vyakhyanLite.entity.Conversation;
import com.navaantrix.vyakhyanLite.entity.Status;
import com.navaantrix.vyakhyanLite.exception.DataNotFoundException;
import com.navaantrix.vyakhyanLite.exception.DuplicateDataException;
import com.navaantrix.vyakhyanLite.exception.InternalServerException;
import com.navaantrix.vyakhyanLite.repository.ConversationRepository;
import com.navaantrix.vyakhyanLite.repository.StatusRepository;
import com.navaantrix.vyakhyanLite.service.ConversationService;
import com.navaantrix.vyakhyanLite.util.ConstantVariables;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final StatusRepository statusRepository;

    @Override
    public ConversationResponse createConversation(ConversationRequest request) {
        try {
            log.info("Start :: ConversationServiceImpl :: create :: {}", request.getTitle());

            Optional<Conversation> existing =
                    conversationRepository.findByTitleIgnoreCaseAndUserIdAndStatusStatusIdNot(
                            request.getTitle(),
                            request.getUserId(),
                            ConstantVariables.DELETE_STATUS
                    );

            if (existing.isPresent()) {
                throw new DuplicateDataException("Conversation already exists for this user");
            }


            Status status = statusRepository.findById(ConstantVariables.ACTIVE_STATUS)
                    .orElseThrow(() -> new DataNotFoundException("Status not found"));


            boolean archived = request.isArchived();
            Conversation entity = Conversation.builder()
                    .title(request.getTitle())
                    .userId(request.getUserId())
                    .isArchived(archived)
                    .createdAt(Instant.now())
                    .status(status)
                    .build();

            return mapToResponse(conversationRepository.save(entity));

        } catch (DataNotFoundException e) {
            log.error("ERROR :: ConversationServiceImpl :: create :: Data not found");
            throw new DataNotFoundException(e.getMessage());

        } catch (Exception e) {
            log.error("ERROR :: ConversationServiceImpl :: create :: Unexpected error", e);
            throw new InternalServerException(e.getMessage());
        }
    }

    @Override
    public List<ConversationResponse> getAllConversation() {
        try {
            return conversationRepository.findByStatusStatusIdNotOrderByConversationIdDesc(ConstantVariables.DELETE_STATUS)
                    .stream()
                    .map(this::mapToResponse)
                    .toList();

        } catch (Exception e) {
            log.error("ERROR :: ConversationServiceImpl :: getAll", e);
            throw new InternalServerException("Error fetching conversations");
        }
    }

    @Override
    public ConversationResponse getConversationById(Long id) {
        try {
            Conversation entity = conversationRepository
                    .findByConversationIdAndStatusStatusIdNot(id, ConstantVariables.DELETE_STATUS)
                    .orElseThrow(() -> new DataNotFoundException("Conversation not found"));

            return mapToResponse(entity);

        } catch (DataNotFoundException ex) {
            log.error("ERROR :: ConversationServiceImpl :: getById :: {}", id);
            throw new DataNotFoundException("Conversation not found with id: " + id);

        } catch (Exception e) {
            log.error("ERROR :: ConversationServiceImpl :: getById", e);
            throw new InternalServerException("Error fetching conversation");
        }
    }

    @Override
    public ConversationResponse updateConversationById(Long id, ConversationRequest request) {
        try {
            Conversation entity = conversationRepository
                    .findByConversationIdAndStatusStatusIdNot(id, ConstantVariables.DELETE_STATUS)
                    .orElseThrow(() -> new DataNotFoundException("Conversation not found"));

            Optional<Conversation> duplicate =
                    conversationRepository.findByTitleIgnoreCaseAndUserIdAndConversationIdNot(
                            request.getTitle(),
                            request.getUserId(),
                            id
                    );

            if (duplicate.isPresent()) {
                throw new DuplicateDataException("Conversation already exists");
            }
            boolean archived = request.isArchived();

            entity.setTitle(request.getTitle());
            entity.setUserId(request.getUserId());
            entity.setArchived(archived);

            return mapToResponse(conversationRepository.save(entity));

        } catch (DuplicateDataException ex) {
            log.error("ERROR :: ConversationServiceImpl :: update :: duplicate");
            throw new DuplicateDataException(ex.getMessage());

        } catch (DataNotFoundException ex) {
            log.error("ERROR :: ConversationServiceImpl :: update :: not found {}", id);
            throw new DataNotFoundException("Conversation not found with ID: " + id);

        } catch (Exception e) {
            log.error("ERROR :: ConversationServiceImpl :: update", e);
            throw new InternalServerException("Error updating conversation");
        }
    }

    @Override
    public ConversationResponse deleteConversationById(Long id) {
        try {
            Conversation entity = conversationRepository
                    .findByConversationIdAndStatusStatusIdNot(id, ConstantVariables.DELETE_STATUS)
                    .orElseThrow(() -> new DataNotFoundException("Conversation not found"));

            Status deleteStatus = statusRepository.findById(ConstantVariables.DELETE_STATUS)
                    .orElseThrow(() -> new DataNotFoundException("Delete status not found"));

            entity.setStatus(deleteStatus);

            return mapToResponse(conversationRepository.save(entity));

        } catch (DataNotFoundException ex) {
            log.error("ERROR :: ConversationServiceImpl :: delete :: {}", id);
            throw new DataNotFoundException("Conversation not found with ID: " + id);

        } catch (Exception e) {
            log.error("ERROR :: ConversationServiceImpl :: delete", e);
            throw new InternalServerException("Error deleting conversation");
        }
    }

    @Override
    public ConversationResponse updateStatusById(Long id, Long statusId) {
        try{
            Conversation conversation = conversationRepository
                    .findByConversationIdAndStatusStatusIdNot(id, ConstantVariables.DELETE_STATUS)
                    .orElseThrow(() -> new DataNotFoundException("Conversation not found"));

            Status status = statusRepository.findById(statusId)
                    .orElseThrow(() -> new DataNotFoundException("Status not found with id" +statusId));

            conversation.setStatus(status);

            return mapToResponse(conversation);

        } catch (DataNotFoundException e){
            throw new DataNotFoundException(e.getMessage());
        }
        catch (Exception e) {
            log.error("Error while updating the status ConversationServiceImpl", e);
            throw new InternalServerException(e.getMessage());
        }
    }

    @Override
    public List<ConversationResponse> getConversationByUserId(String userId) {
        try {
            log.info("Start :: ConversationServiceImpl :: getConversationByUserId :: {}", userId);

            List<Conversation> conversations =
                    conversationRepository.findByUserIdAndStatusStatusIdOrderByConversationIdDesc(
                            userId,
                            ConstantVariables.ACTIVE_STATUS
                    );

            if (conversations == null || conversations.isEmpty()) {
                return Collections.emptyList();
            }

            return conversations.stream()
                    .map(this::mapToResponse)
                    .toList();

        } catch (Exception e) {
            log.error("ERROR :: ConversationServiceImpl :: getConversationByUserId :: Unexpected error", e);
            throw new InternalServerException("Error fetching conversations by userId");
        }
    }

    @Override
    public ConversationResponse getLatestConversation(String userId) {
        try{
            log.info("Start :: ConversationServiceImpl :: getLatestConversation :: {}", userId);
            Conversation  conversation = conversationRepository.findTopByUserIdAndStatusStatusIdOrderByConversationIdDesc(userId, ConstantVariables.ACTIVE_STATUS);
            return mapToResponse(conversation);
        }catch (DataNotFoundException ex) {
            log.error("ERROR :: ConversationServiceImpl :: getLatestConversation :: {}", userId);
            throw new DataNotFoundException(ex.getMessage());
        } catch (Exception e) {
            log.error("ERROR :: ConversationServiceImpl :: getLatestConversation :: Unexpected error", e);
            throw new InternalServerException("Error fetching conversations by userId");
        }
    }

    @Override
    public ConversationResponse updateIsArchivedById(Long id, boolean isArchived) {
        try{
            Conversation conversation = conversationRepository
                    .findByConversationIdAndStatusStatusIdNot(id, ConstantVariables.DELETE_STATUS)
                    .orElseThrow(() -> new DataNotFoundException("Conversation not found"));

            conversation.setArchived(isArchived);
            return mapToResponse(conversation);

        } catch (DataNotFoundException e){
            throw new DataNotFoundException(e.getMessage());
        }
        catch (Exception e) {
            log.error("Error while updating the status ConversationServiceImpl", e);
            throw new InternalServerException(e.getMessage());
        }
    }


    private ConversationResponse mapToResponse(Conversation c) {
        return ConversationResponse.builder()
                .conversationId(c.getConversationId())
                .title(c.getTitle())
                .createdAt(c.getCreatedAt())
                .isArchived(c.isArchived())
                .userId(c.getUserId())
                .fileName(c.getFileName())
                .statusId(c.getStatus().getStatusId())
                .build();
    }
}
