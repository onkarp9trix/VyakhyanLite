package com.navaantrix.vyakhyanLite.service.Impl;

import com.navaantrix.vyakhyanLite.dto.request.DashboardRequest;
import com.navaantrix.vyakhyanLite.dto.response.DashboardResponse;
import com.navaantrix.vyakhyanLite.dto.response.MessagesResponse;
import com.navaantrix.vyakhyanLite.entity.Conversation;
import com.navaantrix.vyakhyanLite.entity.Dashboard;
import com.navaantrix.vyakhyanLite.entity.Messages;
import com.navaantrix.vyakhyanLite.entity.Status;
import com.navaantrix.vyakhyanLite.exception.DataNotFoundException;
import com.navaantrix.vyakhyanLite.exception.DuplicateDataException;
import com.navaantrix.vyakhyanLite.repository.ConversationRepository;
import com.navaantrix.vyakhyanLite.repository.DashboardRepository;
import com.navaantrix.vyakhyanLite.repository.StatusRepository;
import com.navaantrix.vyakhyanLite.service.DashboardService;
import com.navaantrix.vyakhyanLite.util.ConstantVariables;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final DashboardRepository dashboardRepository;
    private final StatusRepository statusRepository;
    private final ConversationRepository conversationRepository;

    @Override
    public DashboardResponse saveDashBoard(DashboardRequest request) {
        try{

            Optional<Dashboard> existing = dashboardRepository.findByDashboardNameIgnoreCaseAndConversationConversationIdAndStatusStatusIdNot(
                    request.getDashboardName(),
                    request.getConversationId(),
                    ConstantVariables.DELETE_STATUS
            );
            log.info("Start");

            if (existing.isPresent()) {
                throw new DuplicateDataException("DashBoard Name is present in this conversation");
            }

            Status status = statusRepository.findById(ConstantVariables.ACTIVE_STATUS)
                    .orElseThrow(() -> new DataNotFoundException("Status not found"));

            Optional<Conversation> isConversation = conversationRepository.findByConversationIdAndStatusStatusIdNot(request.getConversationId(),ConstantVariables.DELETE_STATUS);

            if(isConversation == null){
                throw  new DataNotFoundException("Conversation is not found with  the id {}" + request.getConversationId());
            }

            Conversation conversation = isConversation.get();

            String dashboardName;

            if(request.getDashboardName() == null){
                dashboardName = request.getDashboardFileName();

                if (dashboardName != null && dashboardName.contains(".")) {
                    dashboardName = dashboardName.substring(0, dashboardName.lastIndexOf('.'));
                }
            }else{
                dashboardName = request.getDashboardName();
            }

            Dashboard entity = Dashboard.builder()
                    .dashboardName(dashboardName)
                    .dashboardFileName(request.getDashboardFileName())
                    .conversation(conversation)
                    .status(status)
                    .saveAt(Instant.now())
                    .build();

            return mapToResponse(dashboardRepository.save(entity));

        }catch (DataNotFoundException e) {
            throw new DataNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<DashboardResponse> getDashboardByConversationIdAndStatusActive(Long conversationId) {
        try{
           List<Dashboard>  getDashboard = dashboardRepository.findByConversationConversationIdAndStatusStatusId(conversationId,ConstantVariables.ACTIVE_STATUS);

           if(getDashboard.isEmpty()){
               throw new DataNotFoundException("There is not found DashBoard with the in this Conversation Id {}" + conversationId);
           }

           return getDashboard.stream()
                   .map(this::mapToResponse)
                   .toList();

        }catch (DataNotFoundException e) {
            throw new DataNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public DashboardResponse deleteDashBoard(Long dashBoardId) {
        try{
            Dashboard getDashboard = dashboardRepository.findByDashboardIdAndStatusStatusIdNot(dashBoardId,ConstantVariables.DELETE_STATUS);

            if(getDashboard == null){
                throw new DataNotFoundException("There is not found DashBoard with the in this dashBoardId Id {}" + dashBoardId);
            }

            Status deleteStatus = statusRepository.findById(ConstantVariables.DELETE_STATUS)
                    .orElseThrow(() -> new DataNotFoundException("Status not found"));

            getDashboard.setStatus(deleteStatus);

            dashboardRepository.save(getDashboard);
            return mapToResponse(getDashboard);

        }catch (DataNotFoundException e) {
            throw new DataNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public DashboardResponse updateDashBoardName(Long dashBoardId, DashboardRequest request) {
        try{
            Dashboard getDashboard = dashboardRepository.findByDashboardIdAndStatusStatusIdNot(dashBoardId,ConstantVariables.DELETE_STATUS);

            if(getDashboard == null){
                throw new DataNotFoundException("There is not found DashBoard with the in this dashBoardId Id {}" + dashBoardId);
            }

            if(request.getDashboardName() != null){
                getDashboard.setDashboardName(request.getDashboardName());
            }

            if(request.getDashboardFileName() != null && getDashboard.getDashboardFileName() != request.getDashboardFileName()){
                getDashboard.setDashboardFileName(request.getDashboardFileName());
            }

            dashboardRepository.save(getDashboard);
            return mapToResponse(getDashboard);

        }catch (DataNotFoundException e) {
            throw new DataNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private DashboardResponse mapToResponse(Dashboard D) {
        return DashboardResponse.builder()
                .dashboardId(D.getDashboardId())
                .dashboardName(D.getDashboardName())
                .dashboardFileName(D.getDashboardFileName())
                .statusId(D.getStatus().getStatusId())
                .conversationId(D.getConversation().getConversationId())
                .saveAt(D.getSaveAt())
                .build();
    }
}
