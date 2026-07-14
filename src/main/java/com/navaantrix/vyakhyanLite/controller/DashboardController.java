package com.navaantrix.vyakhyanLite.controller;


import com.navaantrix.vyakhyanLite.dto.request.ConversationRequest;
import com.navaantrix.vyakhyanLite.dto.request.DashboardRequest;
import com.navaantrix.vyakhyanLite.dto.response.ConversationResponse;
import com.navaantrix.vyakhyanLite.dto.response.DashboardResponse;
import com.navaantrix.vyakhyanLite.service.DashboardService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@Validated
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @PostMapping("/saveDashBoard")
    public ResponseEntity<DashboardResponse> saveDashBoard(
            @RequestBody DashboardRequest request) {
        DashboardResponse response = dashboardService.saveDashBoard(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/getDashBoardByConversationId/{conversationId}")
    public ResponseEntity<List<DashboardResponse>> getDashBoardByConversationId(@PathVariable Long conversationId){
        List<DashboardResponse> response = dashboardService.getDashboardByConversationIdAndStatusActive(conversationId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @DeleteMapping("/deleteSavedDashBoard/{dashBoardId}")
    public ResponseEntity<DashboardResponse> deleteSavedDashBoard(@PathVariable Long dashBoardId){
        DashboardResponse response = dashboardService.deleteDashBoard(dashBoardId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PutMapping("/updateSaveDashboard/{dashBoardId}")
    public ResponseEntity<DashboardResponse> updateDashboard(@RequestBody DashboardRequest request,@PathVariable Long dashBoardId){
        DashboardResponse response = dashboardService.updateDashBoardName(dashBoardId,request);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }


}
