package com.navaantrix.vyakhyanLite.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSchemaRequest {

    private String file_name;

    private Long conversationId;

    private List<Map<String, Object>> schema;
}