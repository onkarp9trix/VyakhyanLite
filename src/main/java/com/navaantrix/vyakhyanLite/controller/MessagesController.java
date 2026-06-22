package com.navaantrix.vyakhyanLite.controller;


import com.navaantrix.vyakhyanLite.dto.request.*;
import com.navaantrix.vyakhyanLite.dto.response.MessagesResponse;
import com.navaantrix.vyakhyanLite.service.MessagesService;
import com.navaantrix.vyakhyanLite.util.JwtUtil;
import org.springframework.core.io.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@Validated
@RequestMapping("/api/v1/messages")
public class MessagesController {

    private final MessagesService messagesService;

    @PostMapping(path = "/save_file",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessagesResponse>  fileSave(
            @ModelAttribute MessageRequest request , HttpServletRequest Hrequest
            ){
        //  Get token from header
        String authHeader = Hrequest.getHeader("Authorization");
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        //  Extract userId (sub)
        JwtUtil jwtUtil = new JwtUtil();
        String userId = jwtUtil.extractUserId(token);

        MessagesResponse response = messagesService.saveFile(request,userId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/descriptive_statistics")
    public ResponseEntity<MessagesResponse>  descriptive_statistics(
            @RequestBody MessageDescriptiveStatisticsRequest request , HttpServletRequest Hrequest
    ){
        //  Get token from header
        String authHeader = Hrequest.getHeader("Authorization");
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        //  Extract userId (sub)
        JwtUtil jwtUtil = new JwtUtil();
        String userId = jwtUtil.extractUserId(token);

        MessagesResponse response = messagesService.descriptiveStatistics(request,userId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/run_query")
    public ResponseEntity<MessagesResponse>  run_query(
            @RequestBody MessageRunQueryRequest request , HttpServletRequest Hrequest
    ){
        //  Get token from header
        String authHeader = Hrequest.getHeader("Authorization");
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        //  Extract userId (sub)
        JwtUtil jwtUtil = new JwtUtil();
        String userId = jwtUtil.extractUserId(token);

        MessagesResponse response = messagesService.runQuery(request,userId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/getByConversationId/{conversationId}")
    public ResponseEntity<List<MessagesResponse>> getAllMessageByConversationId(@PathVariable  Long conversationId){
        List<MessagesResponse> responses = messagesService.getMessageByConversationId(conversationId);
        return new ResponseEntity<>(responses,HttpStatus.OK);
    }

    @PostMapping("/view_data")
    public ResponseEntity<MessagesResponse> viewData(@RequestBody MessageViewDataRequest request , HttpServletRequest Hrequest){
        //  Get token from header
        String authHeader = Hrequest.getHeader("Authorization");
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        //  Extract userId (sub)
        JwtUtil jwtUtil = new JwtUtil();
        String userId = jwtUtil.extractUserId(token);

        MessagesResponse response = messagesService.viewData(request,userId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/fetch_schema")
    public ResponseEntity<MessagesResponse> fetchSchema(@RequestBody MessageFetchSchemaRequest request , HttpServletRequest Hrequest){
        //  Get token from header
        String authHeader = Hrequest.getHeader("Authorization");
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        //  Extract userId (sub)
        JwtUtil jwtUtil = new JwtUtil();
        String userId = jwtUtil.extractUserId(token);

        MessagesResponse response = messagesService.fetchSchema(request,userId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/generate_dashboard")
    public ResponseEntity<MessagesResponse> generateDashboard(@RequestBody generateDashboardRequest request,HttpServletRequest Hrequest){
        //  Get token from header
        String authHeader = Hrequest.getHeader("Authorization");
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        //  Extract userId (sub)
        JwtUtil jwtUtil = new JwtUtil();
        String userId = jwtUtil.extractUserId(token);

        MessagesResponse response = messagesService.generateDashboard(request,userId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/dashboard/view/{fileName}")
    public ResponseEntity<Resource> viewDashboard(@PathVariable String fileName) {

        // Base folder where Python generates HTML
        String basePath = "/home/tatva/poirot/Analytical_Output/dashboard/";

        // Build full path
        Path path = Paths.get(basePath, fileName);

        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(path.toFile());

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @PostMapping("/get_insights")
    public ResponseEntity<MessagesResponse> getInsights(@RequestBody MessageGetInsightsRequest request,HttpServletRequest Hrequest){
        //  Get token from header
        String authHeader = Hrequest.getHeader("Authorization");
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        //  Extract userId (sub)
        JwtUtil jwtUtil = new JwtUtil();
        String userId = jwtUtil.extractUserId(token);

        MessagesResponse response = messagesService.getInsights(request,userId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
