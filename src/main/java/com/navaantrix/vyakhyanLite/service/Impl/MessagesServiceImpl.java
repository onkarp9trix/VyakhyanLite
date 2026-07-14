package com.navaantrix.vyakhyanLite.service.Impl;


import com.navaantrix.vyakhyanLite.dto.request.*;
import com.navaantrix.vyakhyanLite.dto.response.MessageFeedBackResponse;
import com.navaantrix.vyakhyanLite.dto.response.MessagesResponse;
import com.navaantrix.vyakhyanLite.entity.Conversation;
import com.navaantrix.vyakhyanLite.entity.Messages;
import com.navaantrix.vyakhyanLite.entity.Status;
import com.navaantrix.vyakhyanLite.exception.BadRequestException;
import com.navaantrix.vyakhyanLite.exception.DataNotFoundException;
import com.navaantrix.vyakhyanLite.exception.InternalServerException;
import com.navaantrix.vyakhyanLite.repository.ConversationRepository;
import com.navaantrix.vyakhyanLite.repository.MessagesRepository;
import com.navaantrix.vyakhyanLite.repository.StatusRepository;
import com.navaantrix.vyakhyanLite.service.MessagesService;
import com.navaantrix.vyakhyanLite.util.ConstantVariables;
import com.navaantrix.vyakhyanLite.util.FileName;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class MessagesServiceImpl implements MessagesService {

    private final MessagesRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final StatusRepository statusRepository;
    private final PythonVyakhyanLiteService pythonVyakhyanLiteService;
    private final FileName fileNameUtil;


    @Override
    public MessagesResponse saveFile(MessageRequest request, String userId) {
        try {
            Conversation conversationEntity = null;

            String reqFileName = request.getFile().getOriginalFilename();

            if (request.getConversationId() != null) {
                Optional<Conversation> optionalConversation =
                        conversationRepository.findByConversationIdAndStatusStatusIdNot(
                                request.getConversationId(),
                                ConstantVariables.DELETE_STATUS
                        );

                if (optionalConversation.isPresent()) {
                    conversationEntity = optionalConversation.get();
                }
                List<String> filenames =  conversationEntity.getFileName();

              String fileName = fileNameUtil.generateUniqueFileName(reqFileName,filenames);
                filenames.add(fileName);
                String conversationalFileName = fileNameUtil.buildConversationFileName(conversationEntity.getConversationId(),request.getFile().getOriginalFilename());
                Map<String, Object> response = pythonVyakhyanLiteService.fetchSchema(conversationalFileName);
                log.info("fetch Schema:" , response);
                Map<String,Object> fileNameAndSchema = conversationEntity.getFileNameAndSchema();
                Map<String, Object> success = (Map<String, Object>) response.get("success");

                if (success != null) {
                    fileNameAndSchema.put(fileName, success.get("Data_Types"));
                } else {
                    throw new RuntimeException("fetchSchema returned invalid response: " + response);
                }
                conversationEntity.setFileNameAndSchema(fileNameAndSchema);
                conversationEntity.setFileName(filenames);
                conversationRepository.save(conversationEntity);
            }

            Status status = statusRepository.findById(ConstantVariables.ACTIVE_STATUS)
                    .orElseThrow(() -> new DataNotFoundException("Status not found"));


            if (conversationEntity == null) {


                conversationEntity = Conversation.builder()
                        .title(request.getFile().getOriginalFilename())
                        .userId(userId)
                        .createdAt(Instant.now())
                        .fileName(Collections.singletonList(request.getFile().getOriginalFilename()))
                        .status(status)
                        .fileName(Collections.singletonList(reqFileName))
                        .isArchived(false)
                        .build();

                conversationEntity = conversationRepository.save(conversationEntity);

                log.info("New Conversation created: {}", conversationEntity.getConversationId());
            }

            //  Save USER message
            Messages userMessage = new Messages();
            userMessage.setConversation(conversationEntity);
            userMessage.setRole(ConstantVariables.USER);
            userMessage.setStatus(status);
            userMessage.setContentText(request.getFile().getOriginalFilename());
            userMessage.setUserId(userId);
            userMessage.setFileUsed(reqFileName);
            userMessage.setCreatedAt(Instant.now());

            userMessage = messageRepository.save(userMessage);

            log.info("USER message saved: {}", userMessage.getMessagesId());

            Messages finalUserMessage = userMessage;
            Conversation finalConversationEntity = conversationEntity;

            List<String> fileNames = conversationEntity.getFileName();

            String OrgFileName  =  fileNames.get(fileNames.size()-1);

            String conversationalFileName = fileNameUtil.buildConversationFileName(conversationEntity.getConversationId(),OrgFileName);

            System.out.println(conversationalFileName);

            log.info("Response Python: ,Before Calling Python Api " );
            Map<String, Object> response = pythonVyakhyanLiteService.fileSavePython( request.getFile(), conversationalFileName);


          conversationalFileName = fileNameUtil.buildConversationFileName(conversationEntity.getConversationId(),request.getFile().getOriginalFilename());
            Map<String, Object> response1 = pythonVyakhyanLiteService.fetchSchema(conversationalFileName);
            log.info("fetch Schema:{}" , response1);
            Map<String,Object> fileNameAndSchema = new HashMap<>();
            Map<String, Object> success1 = (Map<String, Object>) response1.get("success");

            if (success1 != null) {
                fileNameAndSchema.put(reqFileName, success1.get("Data_Types"));
            } else {
                throw new RuntimeException("fetchSchema returned invalid response: " + response1);
            }
            conversationEntity.setFileNameAndSchema(fileNameAndSchema);
           conversationRepository.save(conversationEntity);

             log.info("Response Python:{}" , response);
            Messages assistantMessage = new Messages();
            assistantMessage.setConversation(finalConversationEntity);
            assistantMessage.setStatus(status);
            assistantMessage.setRole(ConstantVariables.ASSISTANT);
            assistantMessage.setParentId(finalUserMessage.getMessagesId());
            assistantMessage.setAnalyzeResponse(response);
            assistantMessage.setFileUsed(reqFileName);
            assistantMessage.setUserId(userId);
            assistantMessage.setCreatedAt(Instant.now());
            messageRepository.save(assistantMessage);

            return mapToResponse(assistantMessage);

        } catch (WebClientResponseException e) {
            log.error("Python API error: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Python API Error: " + e.getResponseBodyAsString());
        } catch (WebClientRequestException e) {
            log.error("Python service unreachable", e);
            throw new RuntimeException("Python service unavailable");
        } catch (DataNotFoundException e) {
            throw new DataNotFoundException(e.getMessage());
        } catch (Exception e) {
            log.error("Python API error", e);
            throw new RuntimeException("Python API Error", e);
        }

    }

    @Override
    public Map<String,Object> descriptiveStatistics(MessageDescriptiveStatisticsRequest request, String userId) {
        try {
            Conversation conversationEntity = null;

            if (request.getConversationId() != null) {
                Optional<Conversation> optionalConversation =
                        conversationRepository.findByConversationIdAndStatusStatusIdNot(
                                request.getConversationId(),
                                ConstantVariables.DELETE_STATUS
                        );

                if (optionalConversation.isPresent()) {
                    conversationEntity = optionalConversation.get();
                }
            }

            Status status = statusRepository.findById(ConstantVariables.ACTIVE_STATUS)
                    .orElseThrow(() -> new DataNotFoundException("Status not found"));



            Conversation finalConversationEntity = conversationEntity;
            List<String> fileNames = conversationEntity.getFileName();

            String OrgFileName  =  fileNames.get(fileNames.size()-1);

            String conversationalFileName = fileNameUtil.buildConversationFileName(conversationEntity.getConversationId(),request.getFile_name());

            System.out.println(conversationalFileName);

            log.info("Response Python: ,Before Calling Python Api " );
            Map<String, Object> response = pythonVyakhyanLiteService.getDescriptiveStatistics(conversationalFileName);
            log.info("Response Python:" , response);


            return response;

        } catch (WebClientResponseException e) {
            log.error("Python API error: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Python API Error: " + e.getResponseBodyAsString());
        } catch (WebClientRequestException e) {
            log.error("Python service unreachable", e);
            throw new RuntimeException("Python service unavailable");
        } catch (DataNotFoundException e) {
            throw new DataNotFoundException(e.getMessage());
        } catch (Exception e) {
            log.error("Python API error", e);
            throw new RuntimeException("Python API Error", e);
        }
    }

    @Override
    public MessagesResponse runQuery(MessageRunQueryRequest request, String userId) {
        try {
            Conversation conversationEntity = null;

            if (request.getConversationId() != null) {
                Optional<Conversation> optionalConversation =
                        conversationRepository.findByConversationIdAndStatusStatusIdNot(
                                request.getConversationId(),
                                ConstantVariables.DELETE_STATUS
                        );

                if (optionalConversation.isPresent()) {
                    conversationEntity = optionalConversation.get();
                }
            }

            Status status = statusRepository.findById(ConstantVariables.ACTIVE_STATUS)
                    .orElseThrow(() -> new DataNotFoundException("Status not found"));


            if (conversationEntity == null) {

                conversationEntity = Conversation.builder()
                        .title(request.getFile_name())
                        .userId(userId)
                        .createdAt(Instant.now())
                        .status(status)
                        .isArchived(false)
                        .build();

                conversationEntity = conversationRepository.save(conversationEntity);

                log.info("New Conversation created: {}", conversationEntity.getConversationId());
            }

            //  Save USER message
            Messages userMessage = new Messages();
            userMessage.setContentText(request.getQuestion());
            userMessage.setConversation(conversationEntity);
            userMessage.setRole(ConstantVariables.USER);
            userMessage.setStatus(status);
            userMessage.setFileUsed(request.getFile_name());
            userMessage.setUserId(userId);
            userMessage.setCreatedAt(Instant.now());

            userMessage = messageRepository.save(userMessage);

            log.info("USER message saved: {}", userMessage.getMessagesId());
            log.info("user Question:-" ,userMessage.getContentText());



            Messages finalUserMessage = userMessage;
            Conversation finalConversationEntity = conversationEntity;
            List<String> fileNames = finalConversationEntity.getFileName();

            String filename;
            if(request.getFile_name()== null) {
                filename  =  fileNames.get(fileNames.size()-1);
            }else {
                filename = request.getFile_name();
            }


            log.info("Response Python: ,Before Calling Python Api " );
            String conversationalFileName = fileNameUtil.buildConversationFileName(conversationEntity.getConversationId(),filename);

            System.out.println(conversationalFileName);
            Map<String, Object> response = pythonVyakhyanLiteService.runQuery(userMessage.getConversation().getConversationId(),userMessage.getMessagesId(),request.getQuestion(),conversationalFileName);
            log.info("Response Python:" , response);
            Messages assistantMessage = new Messages();
            assistantMessage.setConversation(finalConversationEntity);
            assistantMessage.setStatus(status);
            assistantMessage.setRole(ConstantVariables.ASSISTANT);
            assistantMessage.setFileUsed(request.getFile_name());
            assistantMessage.setParentId(finalUserMessage.getMessagesId());
            assistantMessage.setAnalyzeResponse(response);
            assistantMessage.setUserId(userId);
            assistantMessage.setCreatedAt(Instant.now());
            messageRepository.save(assistantMessage);

            return mapToResponse(assistantMessage);

        } catch (WebClientResponseException e) {
            log.error("Python API error: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Python API Error: " + e.getResponseBodyAsString());
        } catch (WebClientRequestException e) {
            log.error("Python service unreachable", e);
            throw new RuntimeException("Python service unavailable");
        } catch (DataNotFoundException e) {
            throw new DataNotFoundException(e.getMessage());
        } catch (Exception e) {
            log.error("Python API error", e);
            throw new RuntimeException("Python API Error", e);
        }
    }

    @Override
    public List<MessagesResponse> getMessageByConversationId(Long conversationId) {
        try {
            return messageRepository.findByConversationConversationIdAndStatusStatusIdNotOrderByMessagesIdDesc(conversationId, ConstantVariables.DELETE_STATUS)
                    .stream()
                    .map(this::mapToResponse)
                    .toList();

        } catch (Exception e) {
            log.error("ERROR :: ConversationServiceImpl :: getAll", e);
            throw new InternalServerException("Error fetching conversations");
        }
    }

    @Override
    public Map<String,Object> viewData(MessageViewDataRequest request, String userId) {
        try {
            Conversation conversationEntity = null;

            if(request.getConversationId() == null){
                throw new BadRequestException("ConversationId is Missing in request");
            }

            if (request.getConversationId() != null) {
                Optional<Conversation> optionalConversation =
                        conversationRepository.findByConversationIdAndStatusStatusIdNot(
                                request.getConversationId(),
                                ConstantVariables.DELETE_STATUS
                        );

                if (optionalConversation.isPresent()) {
                    conversationEntity = optionalConversation.get();
                } else{
                    throw new DataNotFoundException("Conversation with this id is not find " + request.getConversationId());
                }
            }

            Status status = statusRepository.findById(ConstantVariables.ACTIVE_STATUS)
                    .orElseThrow(() -> new DataNotFoundException("Status not found"));



            Conversation finalConversationEntity = conversationEntity;
            List<String> fileNames = finalConversationEntity.getFileName();

            String filename;
            if(request.getFile_name() == null) {
                filename  =  fileNames.get(fileNames.size()-1);
            }else {
                filename = request.getFile_name();
            }


            log.info("Response Python: ,Before Calling Python Api " );
            String conversationalFileName = fileNameUtil.buildConversationFileName(conversationEntity.getConversationId(),filename);

            System.out.println(conversationalFileName);
            Map<String, Object> response = pythonVyakhyanLiteService.viewData(conversationalFileName,request.getPage(),request.getSize());
            log.info("Response Python:" , response);


            return response;

        } catch (WebClientResponseException e) {
            log.error("Python API error: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Python API Error: " + e.getResponseBodyAsString());
        } catch (WebClientRequestException e) {
            log.error("Python service unreachable", e);
            throw new RuntimeException("Python service unavailable");
        } catch (DataNotFoundException e) {
            throw new DataNotFoundException(e.getMessage());
        } catch (Exception e) {
            log.error("Python API error", e);
            throw new RuntimeException("Python API Error", e);
        }
    }

    @Override
    public MessagesResponse fetchSchema(MessageFetchSchemaRequest request, String userId) {
        try {
            Conversation conversationEntity = null;

            if(request.getConversationId() == null){
                throw new BadRequestException("ConversationId is Missing in request");
            }

            if (request.getConversationId() != null) {
                Optional<Conversation> optionalConversation =
                        conversationRepository.findByConversationIdAndStatusStatusIdNot(
                                request.getConversationId(),
                                ConstantVariables.DELETE_STATUS
                        );

                if (optionalConversation.isPresent()) {
                    conversationEntity = optionalConversation.get();
                } else{
                    throw new DataNotFoundException("Conversation with this id is not find " + request.getConversationId());
                }
            }

            Status status = statusRepository.findById(ConstantVariables.ACTIVE_STATUS)
                    .orElseThrow(() -> new DataNotFoundException("Status not found"));


            //  Save USER message
            Messages userMessage = new Messages();
            userMessage.setConversation(conversationEntity);
            userMessage.setRole(ConstantVariables.USER);
            userMessage.setStatus(status);
            userMessage.setFileUsed(request.getFile_name());
            userMessage.setContentText("view Data " + request.getFile_name() );
            userMessage.setUserId(userId);
            userMessage.setCreatedAt(Instant.now());

            userMessage = messageRepository.save(userMessage);

            log.info("USER message saved: {}", userMessage.getMessagesId());



            Messages finalUserMessage = userMessage;
            Conversation finalConversationEntity = conversationEntity;
            List<String> fileNames = finalConversationEntity.getFileName();

            String filename;
            if(request.getFile_name() == null) {
                filename  =  fileNames.get(fileNames.size()-1);
            }else {
                filename = request.getFile_name();
            }


            log.info("Response Python: ,Before Calling Python Api " );
            String conversationalFileName = fileNameUtil.buildConversationFileName(conversationEntity.getConversationId(),filename);

            System.out.println(conversationalFileName);
            Map<String, Object> response = pythonVyakhyanLiteService.fetchSchema(conversationalFileName);
            log.info("Response Python:" , response);
            Messages assistantMessage = new Messages();
            assistantMessage.setConversation(finalConversationEntity);
            assistantMessage.setStatus(status);
            assistantMessage.setRole(ConstantVariables.ASSISTANT);
            assistantMessage.setFileUsed(request.getFile_name());
            assistantMessage.setParentId(finalUserMessage.getMessagesId());
            assistantMessage.setAnalyzeResponse(response);
            assistantMessage.setUserId(userId);
            assistantMessage.setCreatedAt(Instant.now());
            messageRepository.save(assistantMessage);

            return mapToResponse(assistantMessage);

        } catch (WebClientResponseException e) {
            log.error("Python API error: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Python API Error: " + e.getResponseBodyAsString());
        } catch (WebClientRequestException e) {
            log.error("Python service unreachable", e);
            throw new RuntimeException("Python service unavailable");
        } catch (DataNotFoundException e) {
            throw new DataNotFoundException(e.getMessage());
        } catch (Exception e) {
            log.error("Python API error", e);
            throw new RuntimeException("Python API Error", e);
        }
    }

    @Override
    public Map<String,Object> generateDashboard(generateDashboardRequest request, String userId) {
        try {
            Conversation conversationEntity = null;

            if(request.getConversationId() == null){
                throw new BadRequestException("ConversationId is Missing in request");
            }

            if (request.getConversationId() != null) {
                Optional<Conversation> optionalConversation =
                        conversationRepository.findByConversationIdAndStatusStatusIdNot(
                                request.getConversationId(),
                                ConstantVariables.DELETE_STATUS
                        );

                if (optionalConversation.isPresent()) {
                    conversationEntity = optionalConversation.get();
                } else{
                    throw new DataNotFoundException("Conversation with this id is not find " + request.getConversationId());
                }
            }

            Status status = statusRepository.findById(ConstantVariables.ACTIVE_STATUS)
                    .orElseThrow(() -> new DataNotFoundException("Status not found"));


            Conversation finalConversationEntity = conversationEntity;
            List<String> fileNames = finalConversationEntity.getFileName();

            String filename;
            if(request.getFile_name() == null) {
                filename  =  fileNames.get(fileNames.size()-1);
            }else {
                filename = request.getFile_name();
            }


            log.info("Response Python: ,Before Calling Python Api " );
            String conversationalFileName = fileNameUtil.buildConversationFileName(conversationEntity.getConversationId(),filename);

            System.out.println(conversationalFileName);
            Map<String, Object> response = pythonVyakhyanLiteService.generateDashboard(conversationalFileName);
            log.info("Response Python:" , response);


            return response;

        } catch (WebClientResponseException e) {
            log.error("Python API error: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Python API Error: " + e.getResponseBodyAsString());
        } catch (WebClientRequestException e) {
            log.error("Python service unreachable", e);
            throw new RuntimeException("Python service unavailable");
        } catch (DataNotFoundException e) {
            throw new DataNotFoundException(e.getMessage());
        } catch (Exception e) {
            log.error("Python API error", e);
            throw new RuntimeException("Python API Error", e);
        }
    }

    @Override
    public Map<String,Object> viewSchema(MessageFetchSchemaRequest request, String userId) {
        try {
            Conversation conversationEntity = null;
            if(request.getConversationId() == null){
                throw new BadRequestException("ConversationId is Missing in request");
            }
            if (request.getConversationId() != null) {
                Optional<Conversation> optionalConversation =
                        conversationRepository.findByConversationIdAndStatusStatusIdNot(
                                request.getConversationId(),
                                ConstantVariables.DELETE_STATUS
                        );

                if (optionalConversation.isPresent()) {
                    conversationEntity = optionalConversation.get();
                } else{
                    throw new DataNotFoundException("Conversation with this id is not find " + request.getConversationId());
                }
            }

            Status status = statusRepository.findById(ConstantVariables.ACTIVE_STATUS)
                    .orElseThrow(() -> new DataNotFoundException("Status not found"));


//            //  Save USER message
//            Messages userMessage = new Messages();
//            userMessage.setConversation(conversationEntity);
//            userMessage.setRole(ConstantVariables.USER);
//            userMessage.setStatus(status);
//            userMessage.setFileUsed(request.getFile_name());
//            userMessage.setContentText("view Schema " + request.getFile_name() );
//            userMessage.setUserId(userId);
//            userMessage.setCreatedAt(Instant.now());
//
//            userMessage = messageRepository.save(userMessage);
//
//            log.info("USER message saved: {}", userMessage.getMessagesId());


//            Messages finalUserMessage = userMessage;
            Conversation finalConversationEntity = conversationEntity;
            List<String> fileNames = finalConversationEntity.getFileName();

            String filename;
            if(request.getFile_name() == null) {
                filename  =  fileNames.get(fileNames.size()-1);
            }else {
                filename = request.getFile_name();
            }
            Messages assistantMessage = new Messages();
            Map<String,Object> viewSchema = conversationEntity.getFileNameAndSchema();

                Map<String,Object> response1 = new HashMap<>();
                response1.put(request.getFile_name() , viewSchema.get(request.getFile_name()));
//                assistantMessage.setConversation(finalConversationEntity);
//                assistantMessage.setStatus(status);
//                assistantMessage.setRole(ConstantVariables.ASSISTANT);
//                assistantMessage.setParentId(finalUserMessage.getMessagesId());
//                assistantMessage.setFileUsed(request.getFile_name());
//                assistantMessage.setAnalyzeResponse(response1);
//                assistantMessage.setUserId(userId);
//                assistantMessage.setCreatedAt(Instant.now());
//                messageRepository.save(assistantMessage);

            return response1;

        } catch (WebClientResponseException e) {
            log.error("Python API error: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Python API Error: " + e.getResponseBodyAsString());
        } catch (WebClientRequestException e) {
            log.error("Python service unreachable", e);
            throw new RuntimeException("Python service unavailable");
        } catch (DataNotFoundException e) {
            throw new DataNotFoundException(e.getMessage());
        } catch (Exception e) {
            log.error("Python API error", e);
            throw new RuntimeException("Python API Error", e);
        }
    }

    @Override
    public MessagesResponse getInsights(MessageGetInsightsRequest request, String userId) {
        try {
            Conversation conversationEntity = null;

            if(request.getConversationId() == null){
                throw new BadRequestException("ConversationId is Missing in request");
            }

            if (request.getConversationId() != null) {
                Optional<Conversation> optionalConversation =
                        conversationRepository.findByConversationIdAndStatusStatusIdNot(
                                request.getConversationId(),
                                ConstantVariables.DELETE_STATUS
                        );

                if (optionalConversation.isPresent()) {
                    conversationEntity = optionalConversation.get();
                } else{
                    throw new DataNotFoundException("Conversation with this id is not find " + request.getConversationId());
                }
            }

            Status status = statusRepository.findById(ConstantVariables.ACTIVE_STATUS)
                    .orElseThrow(() -> new DataNotFoundException("Status not found"));

            Messages assistantMessage = messageRepository.findById(request.getMessagesId())
                    .orElseThrow(()-> new DataNotFoundException("Message Not found"));



            Conversation finalConversationEntity = conversationEntity;
            List<String> fileNames = finalConversationEntity.getFileName();

            Map<String,Object> responseAss = assistantMessage.getAnalyzeResponse();


          String responsePython = pythonVyakhyanLiteService.getInsights(request.getInside());
          Map<String,Object> response  = new HashMap<>();

          responseAss.put("markdown_Insights",responsePython);

            log.info("Response Python:" , responseAss);
            assistantMessage.setAnalyzeResponse(responseAss);
            messageRepository.save(assistantMessage);
            return mapToResponse(assistantMessage);

        } catch (WebClientResponseException e) {
            log.error("Python API error: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Python API Error: " + e.getResponseBodyAsString());
        } catch (WebClientRequestException e) {
            log.error("Python service unreachable", e);
            throw new RuntimeException("Python service unavailable");
        } catch (DataNotFoundException e) {
            throw new DataNotFoundException(e.getMessage());
        } catch (Exception e) {
            log.error("Python API error", e);
            throw new RuntimeException("Python API Error", e);
        }
    }

    @Override
    public Map<String,Object> updateSchema(UpdateSchemaRequest request, String userId) {
        try {
            Conversation conversationEntity = null;
            if(request.getConversationId() == null){
                throw new BadRequestException("ConversationId is Missing in request");
            }
            if (request.getConversationId() != null) {
                Optional<Conversation> optionalConversation =
                        conversationRepository.findByConversationIdAndStatusStatusIdNot(
                                request.getConversationId(),
                                ConstantVariables.DELETE_STATUS
                        );

                if (optionalConversation.isPresent()) {
                    conversationEntity = optionalConversation.get();
                } else{
                    throw new DataNotFoundException("Conversation with this id is not find " + request.getConversationId());
                }
            }

            Status status = statusRepository.findById(ConstantVariables.ACTIVE_STATUS)
                    .orElseThrow(() -> new DataNotFoundException("Status not found"));




            Map<String, Object> viewSchema = conversationEntity.getFileNameAndSchema();

            viewSchema.put(
                    request.getFile_name(),
                    request.getSchema()
            );

            conversationEntity.setFileNameAndSchema(viewSchema);

            conversationRepository.save(conversationEntity);


//            conversationRepository.save(conversationEntity);

//            log.info("USER message saved: {}", userMessage.getMessagesId());
//
//
//            Messages finalUserMessage = userMessage;
            Conversation finalConversationEntity = conversationEntity;
            List<String> fileNames = finalConversationEntity.getFileName();

            String filename;
            if(request.getFile_name() == null) {
                filename  =  fileNames.get(fileNames.size()-1);
            }else {
                filename = request.getFile_name();
            }
            Messages assistantMessage = new Messages();


            Map<String,Object> response1 = new HashMap<>();


            return response1;

        } catch (WebClientResponseException e) {
            log.error("Python API error: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Python API Error: " + e.getResponseBodyAsString());
        } catch (WebClientRequestException e) {
            log.error("Python service unreachable", e);
            throw new RuntimeException("Python service unavailable");
        } catch (DataNotFoundException e) {
            throw new DataNotFoundException(e.getMessage());
        } catch (Exception e) {
            log.error("Python API error", e);
            throw new RuntimeException("Python API Error", e);
        }
    }

    @Override
    public MessageFeedBackResponse feedBack(MessageFeedBackRequest request) {
        try {
            Messages message = messageRepository
                    .findByMessagesIdAndStatusStatusIdNot(
                            request.getMessagesId(),
                            ConstantVariables.DELETE_STATUS
                    )
                    .orElseThrow(() ->
                            new DataNotFoundException("Message not found with id " + request.getMessagesId())
                    );

            // Update existing entity
            message.setRating(request.getRating());
            message.setComment(request.getComment());

            // Save will perform UPDATE (not INSERT)
            Messages updatedMessage = messageRepository.save(message);

            return MessageFeedBackResponse.builder()
                    .messagesId(updatedMessage.getMessagesId())
                    .rating(updatedMessage.getRating())
                    .Comment(updatedMessage.getComment())
                    .build();

        } catch (DataNotFoundException e) {
            throw new DataNotFoundException(e.getMessage());

        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }


    private MessagesResponse mapToResponse(Messages m) {
        return MessagesResponse.builder()
                .messagesId(m.getMessagesId())
                .conversationId(m.getConversation().getConversationId())
                .statusId(m.getStatus().getStatusId())
                .contentMedia(m.getContentMedia())
                .role(m.getRole())
                .userId(m.getUserId())
                .rating(m.getRating())
                .parentId(m.getParentId())
                .comment(m.getComment())
                .contentText(m.getContentText())
                .createdAt(m.getCreatedAt())
                .fileUsed(m.getFileUsed())
                .fileName(m.getConversation().getFileName())
                .analyzeResponse(m.getAnalyzeResponse() != null ? m.getAnalyzeResponse() : null)
                .build();
    }


}

