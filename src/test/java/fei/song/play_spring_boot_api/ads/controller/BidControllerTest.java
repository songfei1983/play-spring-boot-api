package fei.song.play_spring_boot_api.ads.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fei.song.play_spring_boot_api.ads.domain.model.BidRequest;
import fei.song.play_spring_boot_api.ads.domain.model.BidResponse;
import fei.song.play_spring_boot_api.ads.domain.model.Impression;
import fei.song.play_spring_boot_api.ads.service.BidRequestMetricsService;
import fei.song.play_spring_boot_api.ads.service.BidServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * BidController 单元测试
 */
@ExtendWith(MockitoExtension.class)
class BidControllerTest {

    @Mock
    private BidServer bidServer;

    @Mock
    private BidRequestMetricsService metricsService;

    @InjectMocks
    private BidController bidController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bidController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testProcessBidRequest_Success() throws Exception {
        // 准备测试数据
        Impression impression = Impression.builder()
            .id("imp1")
            .bidfloor(1.0)
            .bidfloorCurrency("USD")
            .build();
            
        BidRequest bidRequest = new BidRequest();
        bidRequest.setId("test-bid-request-1");
        bidRequest.setImp(Arrays.asList(impression));
        
        // 创建有竞价的响应
        BidResponse.SeatBid seatBid = new BidResponse.SeatBid();
        BidResponse bidResponse = new BidResponse();
        bidResponse.setId("test-bid-response-1");
        bidResponse.setBidid("test-bid-1");
        bidResponse.setSeatbid(Arrays.asList(seatBid)); // 非空的seatbid列表意味着有竞价

        // Mock 服务调用
        when(bidServer.processBidRequest(any(BidRequest.class))).thenReturn(bidResponse);
        doNothing().when(metricsService).recordBidRequest(anyString(), anyString(), anyBoolean(), anyLong());

        // 执行测试
        mockMvc.perform(post("/api/v1/bid/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bidRequest)))
                .andExpect(status().isOk()) // 有竞价返回200
                .andExpect(jsonPath("$.id").value("test-bid-response-1"))
                .andExpect(jsonPath("$.bidid").value("test-bid-1"));

        // 验证服务调用
        verify(bidServer, times(1)).processBidRequest(any(BidRequest.class));
        verify(metricsService, times(1)).recordBidRequest(anyString(), anyString(), anyBoolean(), anyLong());
    }

    @Test
    void testProcessBidRequest_NoContent() throws Exception {
        // 准备测试数据
        Impression impression = Impression.builder()
            .id("imp1")
            .bidfloor(1.0)
            .bidfloorCurrency("USD")
            .build();
            
        BidRequest bidRequest = new BidRequest();
        bidRequest.setId("test-bid-request-1");
        bidRequest.setImp(Arrays.asList(impression));
        
        BidResponse bidResponse = new BidResponse();
        bidResponse.setId("test-bid-response-1");
        bidResponse.setBidid("test-bid-1");
        bidResponse.setSeatbid(Arrays.asList()); // 空的seatbid列表意味着无竞价

        // Mock 服务调用
        when(bidServer.processBidRequest(any(BidRequest.class))).thenReturn(bidResponse);
        doNothing().when(metricsService).recordBidRequest(anyString(), anyString(), anyBoolean(), anyLong());

        // 执行测试
        mockMvc.perform(post("/api/v1/bid/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bidRequest)))
                .andExpect(status().isNoContent()) // 空的seatbid会返回204
                .andExpect(jsonPath("$.id").value("test-bid-response-1"))
                .andExpect(jsonPath("$.bidid").value("test-bid-1"));

        // 验证服务调用
        verify(bidServer, times(1)).processBidRequest(any(BidRequest.class));
        verify(metricsService, times(1)).recordBidRequest(anyString(), anyString(), anyBoolean(), anyLong());
    }

    @Test
    void testProcessBidRequest_InvalidRequest() throws Exception {
        // 准备无效的请求数据
        String invalidJson = "{\"invalid\": \"data\"}";

        // 执行测试
        mockMvc.perform(post("/api/v1/bid/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());

        // 验证服务未被调用
        verify(bidServer, never()).processBidRequest(any(BidRequest.class));
        verify(metricsService, never()).recordBidRequest(anyString(), anyString(), anyBoolean(), anyLong());
    }

    @Test
    void testProcessBidRequest_ServiceException() throws Exception {
        // 准备测试数据
        Impression impression = Impression.builder()
            .id("imp1")
            .bidfloor(1.0)
            .bidfloorCurrency("USD")
            .build();
            
        BidRequest bidRequest = new BidRequest();
        bidRequest.setId("test-bid-request-1");
        bidRequest.setImp(Arrays.asList(impression));

        // Mock 服务抛出异常
        when(bidServer.processBidRequest(any(BidRequest.class)))
                .thenThrow(new RuntimeException("Service error"));
        doNothing().when(metricsService).recordBidRequest(anyString(), anyString(), anyBoolean(), anyLong());

        // 执行测试
        mockMvc.perform(post("/api/v1/bid/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bidRequest)))
                .andExpect(status().isInternalServerError());

        // 验证服务调用
        verify(bidServer, times(1)).processBidRequest(any(BidRequest.class));
        verify(metricsService, times(1)).recordBidRequest(anyString(), anyString(), anyBoolean(), anyLong());
    }

    @Test
    void testProcessBidRequest_EmptyResponse() throws Exception {
        // 准备测试数据
        Impression impression = Impression.builder()
            .id("imp1")
            .bidfloor(1.0)
            .bidfloorCurrency("USD")
            .build();
            
        BidRequest bidRequest = new BidRequest();
        bidRequest.setId("test-bid-request-1");
        bidRequest.setImp(Arrays.asList(impression));

        // Mock 服务返回空响应
        when(bidServer.processBidRequest(any(BidRequest.class))).thenReturn(null);
        doNothing().when(metricsService).recordBidRequest(anyString(), anyString(), anyBoolean(), anyLong());

        // 执行测试
        mockMvc.perform(post("/api/v1/bid/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bidRequest)))
                .andExpect(status().isInternalServerError()); // null响应会导致NullPointerException，返回500

        // 验证服务调用
        verify(bidServer, times(1)).processBidRequest(any(BidRequest.class));
        verify(metricsService, times(1)).recordBidRequest(anyString(), anyString(), anyBoolean(), anyLong());
    }
}