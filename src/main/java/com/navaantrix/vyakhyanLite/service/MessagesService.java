package com.navaantrix.vyakhyanLite.service;

import com.navaantrix.vyakhyanLite.dto.request.*;
import com.navaantrix.vyakhyanLite.dto.response.MessageFeedBackResponse;
import com.navaantrix.vyakhyanLite.dto.response.MessagesResponse;


import java.util.List;
import java.util.Map;


public interface MessagesService {

    MessagesResponse saveFile(MessageRequest request, String userId);

    Map<String,Object> descriptiveStatistics(MessageDescriptiveStatisticsRequest request, String userId);

    MessagesResponse runQuery(MessageRunQueryRequest request, String userId);

    List<MessagesResponse> getMessageByConversationId(Long conversationId);

    Map<String,Object> viewData(MessageViewDataRequest request, String userId);

    MessagesResponse fetchSchema(MessageFetchSchemaRequest request, String userId);

    Map<String,Object> generateDashboard(generateDashboardRequest request, String userId);

    Map<String,Object> viewSchema(MessageFetchSchemaRequest request,String userId);

    MessagesResponse getInsights(MessageGetInsightsRequest request, String userId);

    Map<String,Object> updateSchema(UpdateSchemaRequest request, String userId);

    MessageFeedBackResponse feedBack(MessageFeedBackRequest request);

}
