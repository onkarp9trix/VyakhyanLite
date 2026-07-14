package com.navaantrix.vyakhyanLite.controller;

import com.navaantrix.vyakhyanLite.dto.request.ConversationRequest;
import com.navaantrix.vyakhyanLite.dto.response.ConversationResponse;
import com.navaantrix.vyakhyanLite.service.ConversationService;
import com.navaantrix.vyakhyanLite.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
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
@RequestMapping("/api/v1/conversation")
public class ConversationController {

    private final ConversationService conversationService;

    @PostMapping
    public ResponseEntity<ConversationResponse> createConversation(
            @RequestBody ConversationRequest request) {
        ConversationResponse response = conversationService.createConversation(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ConversationResponse>> getAllConversation() {
        List<ConversationResponse> response = conversationService.getAllConversation();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConversationResponse> getConversationById(
            @PathVariable Long id) {
        ConversationResponse response = conversationService.getConversationById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ConversationResponse> updateConversationById(@PathVariable  Long id, @RequestBody ConversationRequest request, HttpServletRequest Hrequest) {
        //  Get token from header
        String authHeader = Hrequest.getHeader("Authorization");

        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        //  Extract userId (sub)
        JwtUtil jwtUtil = new JwtUtil();
        String userId = jwtUtil.extractUserId(token);
        request.setUserId(userId);
        ConversationResponse response = conversationService.updateConversationById(id, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ConversationResponse> deleteConversationById(
            @PathVariable  Long id) {
        ConversationResponse response = conversationService.deleteConversationById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/{id}/updateConversationStatus/{statusId}")
    public ResponseEntity<ConversationResponse> updateStatusById(
            @PathVariable  Long id,
            @PathVariable Long statusId) {
        ConversationResponse response = conversationService.updateStatusById(id, statusId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PutMapping("/updateIsArchive/{clientId}")
    public ResponseEntity<ConversationResponse> updateIsArchived(@PathVariable Long clientId,
                                                                 @PathVariable boolean isArchived){
        ConversationResponse response = conversationService.updateIsArchivedById(clientId,isArchived);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/getConversationByUserId")
    public ResponseEntity<List<ConversationResponse>> getConversationByUserId(HttpServletRequest Hrequest){
        //  Get token from header
        String authHeader = Hrequest.getHeader("Authorization");

        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        //  Extract userId (sub)
        JwtUtil jwtUtil = new JwtUtil();
        String userId = jwtUtil.extractUserId(token);
       List<ConversationResponse> response = conversationService.getConversationByUserId(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
