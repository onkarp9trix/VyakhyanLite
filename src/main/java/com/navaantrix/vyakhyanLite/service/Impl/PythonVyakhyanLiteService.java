package com.navaantrix.vyakhyanLite.service.Impl;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PythonVyakhyanLiteService {

    private final WebClient webClient;


    public Map<String, Object> fileSavePython(MultipartFile file, String fileName) {
        try {
            MultipartBodyBuilder builder = new MultipartBodyBuilder();

            builder.part("file", file.getResource())
                    .filename(file.getOriginalFilename());

            builder.part("file_name",fileName);

            String responseBody = webClient.post()
                    .uri("/save_file")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("Python Response: {}", responseBody);

            ObjectMapper objectMapper = new ObjectMapper();

            return objectMapper.readValue(
                    responseBody,
                    new TypeReference<Map<String, Object>>() {}
            );

        } catch (Exception e) {

            log.error("Python API Exception", e);

            throw new RuntimeException(
                    "Python API Error: " + e.getMessage(),
                    e
            );
        }
    }

    public Map<String, Object> getDescriptiveStatistics(String fileName) {

        Map<String, String> requestBody = Map.of(
                "file_name", fileName
        );

        return webClient.post()
                .uri("/descriptive_statistics")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }

    public Map<String,Object> runQuery(Long conversationId, Long messageId , String question, String fileName){
        try{
        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        builder.part("conversation_id", conversationId.toString());
        builder.part("query_id", messageId.toString());
        builder.part("question" , question);
        builder.part("file_name" ,fileName);

        String responseBody = webClient.post()
                .uri("/run_query/v2")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchangeToMono(response -> {

                    log.info("HTTP Status = {}", response.statusCode());

                    return response.bodyToMono(String.class)
                            .doOnNext(body ->
                                    log.info("Response Body = {}", body));
                })
//                .retrieve()
//                .bodyToMono(String.class)
                .block();

        log.info("Python Response: {}", responseBody);

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(
                responseBody,
                new TypeReference<Map<String, Object>>() {}
        );
    } catch (Exception e) {
        log.error("Python API Exception", e);
        throw new RuntimeException("Python API Error: " + e.getMessage(), e);
    }
    }

    public Map<String,Object> viewData(String fileName, Long page , Long size){
        try{
            Map<String, Object> payload = new HashMap<>();
            payload.put("file_name",fileName);
            payload.put("page",page);
            payload.put("size" ,size);

            return webClient.post()
                    .uri("/view_data")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> fetchSchema(String fileName){
        try{
            Map<String, String> requestBody = Map.of(
                    "file_name", fileName
            );

            String response = webClient.post()
                    .uri("/fetch_schema")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            ObjectMapper objectMapper = new ObjectMapper();

            return objectMapper.readValue(
                    response,
                    new TypeReference<Map<String, Object>>() {}
            );

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String,Object> generateDashboard( String fileName) {
        try{
            Map<String, String> requestBody = Map.of(
                    "file_name", fileName
            );

            String response = webClient.post()
                    .uri("/generate-dashboard")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            ObjectMapper objectMapper = new ObjectMapper();

            return objectMapper.readValue(
                    response,
                    new TypeReference<Map<String, Object>>() {}
            );

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String getInsights( Map<String,Object> request){
        try{
            Map<String, Object> requestBody =request;

            String response = webClient.post()
                    .uri("/get_insights")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
           return response;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());

        }
    }

}