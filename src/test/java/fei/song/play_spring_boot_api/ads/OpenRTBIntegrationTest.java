package fei.song.play_spring_boot_api.ads;

import com.fasterxml.jackson.databind.ObjectMapper;
import fei.song.play_spring_boot_api.ads.domain.model.*;
import fei.song.play_spring_boot_api.ads.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * OpenRTB 功能集成测试
 * 测试整个竞价流程，包括：
 * 1. 正常竞价流程
 * 2. 反欺诈检测
 * 3. 预算控制
 * 4. 获胜/损失通知
 * 5. 错误处理
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("OpenRTB 集成测试")
public class OpenRTBIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FraudDetectionService fraudDetectionService;

    @MockBean
    private AdSlotFilterService adSlotFilterService;

    @MockBean
    private BiddingAlgorithm biddingAlgorithm;

    @MockBean
    private BudgetService budgetService;

    private BidRequest validBidRequest;
    private List<BidCandidate> mockCandidates;

    @BeforeEach
    void setUp() {
        // 创建有效的竞价请求
        validBidRequest = createValidBidRequest();
        
        // 创建模拟候选广告
        mockCandidates = createMockBidCandidates();
        
        // 设置默认的Mock行为
        setupDefaultMockBehavior();
    }

    @Test
    @DisplayName("正常竞价流程测试")
    void testSuccessfulBiddingFlow() throws Exception {
        // Given: 正常的竞价请求和响应
        String requestJson = objectMapper.writeValueAsString(validBidRequest);

        // When: 发送竞价请求
        MvcResult result = mockMvc.perform(post("/api/v1/bid/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .header("X-Real-IP", "192.168.1.100"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Then: 验证响应
        String responseJson = result.getResponse().getContentAsString();
        BidResponse response = objectMapper.readValue(responseJson, BidResponse.class);

        assertNotNull(response);
        assertEquals(validBidRequest.getId(), response.getId());
        assertNotNull(response.getBidid());
        assertNotNull(response.getSeatbid());
        assertFalse(response.getSeatbid().isEmpty());
        assertEquals("USD", response.getCur());

        // 验证竞价详情
        BidResponse.SeatBid seatBid = response.getSeatbid().get(0);
        assertNotNull(seatBid.getBid());
        assertFalse(seatBid.getBid().isEmpty());
        assertEquals("seat_1", seatBid.getSeat());

        BidResponse.Bid bid = seatBid.getBid().get(0);
        assertEquals(validBidRequest.getImp().get(0).getId(), bid.getImpid());
        assertTrue(bid.getPrice() > 0);
        assertNotNull(bid.getAdm());
        assertNotNull(bid.getNurl());

        // 验证服务调用
        verify(fraudDetectionService).isFraudulent(any(BidRequest.class));
        verify(biddingAlgorithm).generateBidCandidates(any(Impression.class), any(BidRequest.class));
        verify(adSlotFilterService).filterCandidatesForImpression(any(), any(), any());
        verify(budgetService).checkBudget(anyString(), anyDouble());
        verify(budgetService).reserveBudget(anyString(), anyDouble(), anyString());
    }

    @Test
    @DisplayName("反欺诈检测拒绝测试")
    void testFraudDetectionReject() throws Exception {
        // Given: 欺诈请求
        when(fraudDetectionService.isFraudulent(any(BidRequest.class))).thenReturn(true);
        
        String requestJson = objectMapper.writeValueAsString(validBidRequest);

        // When: 发送竞价请求
        MvcResult result = mockMvc.perform(post("/api/v1/bid/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .header("X-Real-IP", "192.168.1.100"))
                .andExpect(status().isNoContent())
                .andReturn();

        // Then: 验证无竞价响应
        String responseJson = result.getResponse().getContentAsString();
        BidResponse response = objectMapper.readValue(responseJson, BidResponse.class);

        assertNotNull(response);
        assertEquals(validBidRequest.getId(), response.getId());
        assertTrue(response.getSeatbid().isEmpty());
        assertEquals(2, response.getNbr()); // 欺诈流量

        // 验证只调用了反欺诈检测
        verify(fraudDetectionService).isFraudulent(any(BidRequest.class));
        verify(biddingAlgorithm, never()).generateBidCandidates(any(), any());
        verify(budgetService, never()).checkBudget(anyString(), anyDouble());
    }

    @Test
    @DisplayName("预算不足测试")
    void testInsufficientBudget() throws Exception {
        // Given: 预算不足
        when(budgetService.checkBudget(anyString(), anyDouble())).thenReturn(false);
        
        String requestJson = objectMapper.writeValueAsString(validBidRequest);

        // When: 发送竞价请求
        MvcResult result = mockMvc.perform(post("/api/v1/bid/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isNoContent())
                .andReturn();

        // Then: 验证无竞价响应
        String responseJson = result.getResponse().getContentAsString();
        BidResponse response = objectMapper.readValue(responseJson, BidResponse.class);

        assertNotNull(response);
        assertTrue(response.getSeatbid().isEmpty());
        assertEquals(0, response.getNbr()); // 未知原因

        // 验证预算检查被调用但预扣没有被调用
        verify(budgetService).checkBudget(anyString(), anyDouble());
        verify(budgetService, never()).reserveBudget(anyString(), anyDouble(), anyString());
    }

    @Test
    @DisplayName("无候选广告测试")
    void testNoBidCandidates() throws Exception {
        // Given: 没有候选广告
        when(biddingAlgorithm.generateBidCandidates(any(), any())).thenReturn(Collections.emptyList());
        
        String requestJson = objectMapper.writeValueAsString(validBidRequest);

        // When: 发送竞价请求
        mockMvc.perform(post("/api/v1/bid/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isNoContent());

        // Then: 验证服务调用
        verify(biddingAlgorithm).generateBidCandidates(any(), any());
        verify(adSlotFilterService, never()).filterCandidatesForImpression(any(), any(), any());
    }

    @Test
    @DisplayName("获胜通知测试")
    void testWinNotification() throws Exception {
        // Given: 竞价ID和获胜价格
        String bidId = "bid_12345";
        Double winPrice = 1.25;

        // When: 发送获胜通知
        mockMvc.perform(post("/api/v1/bid/win/{bidId}", bidId)
                .param("winPrice", winPrice.toString())
                .header("X-Real-IP", "192.168.1.100"))
                .andExpect(status().isOk());

        // Then: 验证预算确认被调用
        verify(budgetService).confirmBudgetSpend(bidId, winPrice);
    }

    @Test
    @DisplayName("损失通知测试")
    void testLossNotification() throws Exception {
        // Given: 竞价ID、获胜价格和损失原因
        String bidId = "bid_12345";
        Double winPrice = 1.25;
        Integer lossReason = 1; // 竞价失败

        // When: 发送损失通知
        mockMvc.perform(post("/api/v1/bid/loss/{bidId}", bidId)
                .param("winPrice", winPrice.toString())
                .param("lossReason", lossReason.toString())
                .header("X-Real-IP", "192.168.1.100"))
                .andExpect(status().isOk());

        // Then: 验证预算释放被调用
        verify(budgetService).releaseBudgetReservation(bidId);
    }

    @Test
    @DisplayName("无效请求测试")
    void testInvalidRequest() throws Exception {
        // Given: 无效的竞价请求（缺少ID）
        BidRequest invalidRequest = BidRequest.builder()
                .imp(validBidRequest.getImp())
                .build();
        
        String requestJson = objectMapper.writeValueAsString(invalidRequest);

        // When & Then: 发送无效请求应返回400错误
        mockMvc.perform(post("/api/v1/bid/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("空广告位请求测试")
    void testEmptyImpressions() throws Exception {
        // Given: 没有广告位的请求
        BidRequest emptyImpRequest = BidRequest.builder()
                .id("test_request_123")
                .imp(Collections.emptyList())
                .build();
        
        String requestJson = objectMapper.writeValueAsString(emptyImpRequest);

        // When & Then: 发送空广告位请求应返回400错误
        mockMvc.perform(post("/api/v1/bid/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("服务器状态检查测试")
    void testServerStatus() throws Exception {
        // Given: 模拟统计数据
        Map<String, Object> mockStats = new HashMap<>();
        mockStats.put("serverStatus", "running");
        mockStats.put("fraudDetection", Map.of("totalRequests", 1000, "fraudBlocked", 50));
        mockStats.put("budgetStats", Map.of("totalBudget", 10000.0, "spentBudget", 2500.0));
        
        when(fraudDetectionService.getFraudStatistics()).thenReturn(Map.of("totalRequests", 1000, "fraudBlocked", 50));
        when(adSlotFilterService.getFilterStatistics()).thenReturn(Map.of("totalFiltered", 100));
        when(biddingAlgorithm.getBiddingStatistics()).thenReturn(Map.of("totalBids", 500));
        when(budgetService.getBudgetStatistics()).thenReturn(Map.of("totalBudget", 10000.0, "spentBudget", 2500.0));

        // When: 获取服务器状态
        MvcResult result = mockMvc.perform(get("/api/v1/bid/status"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Then: 验证状态响应
        String responseJson = result.getResponse().getContentAsString();
        @SuppressWarnings("unchecked")
        Map<String, Object> status = objectMapper.readValue(responseJson, Map.class);
        
        assertEquals("running", status.get("serverStatus"));
        assertNotNull(status.get("fraudDetection"));
        assertNotNull(status.get("budgetStats"));
    }

    @Test
    @DisplayName("健康检查测试")
    void testHealthCheck() throws Exception {
        // When: 健康检查
        MvcResult result = mockMvc.perform(get("/api/v1/bid/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Then: 验证健康状态
        String responseJson = result.getResponse().getContentAsString();
        @SuppressWarnings("unchecked")
        Map<String, String> health = objectMapper.readValue(responseJson, Map.class);
        
        assertEquals("UP", health.get("status"));
        assertEquals("OpenRTB Bid Server", health.get("service"));
        assertNotNull(health.get("timestamp"));
    }

    @Test
    @DisplayName("多广告位竞价测试")
    void testMultipleImpressionsBidding() throws Exception {
        // Given: 多个广告位的请求
        BidRequest multiImpRequest = createMultipleImpressionsBidRequest();
        String requestJson = objectMapper.writeValueAsString(multiImpRequest);

        // When: 发送竞价请求
        MvcResult result = mockMvc.perform(post("/api/v1/bid/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andReturn();

        // Then: 验证多个竞价响应
        String responseJson = result.getResponse().getContentAsString();
        BidResponse response = objectMapper.readValue(responseJson, BidResponse.class);

        assertNotNull(response);
        assertFalse(response.getSeatbid().isEmpty());
        
        // 验证每个广告位都被处理
        verify(biddingAlgorithm, times(multiImpRequest.getImp().size()))
                .generateBidCandidates(any(Impression.class), any(BidRequest.class));
    }

    /**
     * 创建有效的竞价请求
     */
    private BidRequest createValidBidRequest() {
        return BidRequest.builder()
                .id("test_request_123")
                .imp(Arrays.asList(
                        Impression.builder()
                                .id("imp_1")
                                .banner(Banner.builder()
                                        .w(300)
                                        .h(250)
                                        .pos(1)
                                        .build())
                                .bidfloor(0.5)
                                .bidfloorCurrency("USD")
                                .build()
                ))
                .site(Site.builder()
                        .id("site_123")
                        .domain("example.com")
                        .page("http://example.com/page1")
                        .cat(Arrays.asList("IAB1"))
                        .build())
                .device(Device.builder()
                        .ua("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                        .ip("192.168.1.100")
                        .devicetype(1)
                        .make("Apple")
                        .model("iPhone")
                        .os("iOS")
                        .osv("14.0")
                        .build())
                .user(User.builder()
                        .id("user_123")
                        .buyeruid("buyer_456")
                        .yob(1990)
                        .gender("M")
                        .build())
                .auctionType(1) // 第一价格拍卖
                .timeoutMs(120) // 120ms超时
                .currencies(Arrays.asList("USD"))
                .build();
    }

    /**
     * 创建多广告位竞价请求
     */
    private BidRequest createMultipleImpressionsBidRequest() {
        return BidRequest.builder()
                .id("multi_imp_request_123")
                .imp(Arrays.asList(
                        Impression.builder()
                                .id("imp_1")
                                .banner(Banner.builder().w(300).h(250).pos(1).build())
                                .bidfloor(0.5)
                                .bidfloorCurrency("USD")
                                .build(),
                        Impression.builder()
                                .id("imp_2")
                                .banner(Banner.builder().w(728).h(90).pos(2).build())
                                .bidfloor(0.8)
                                .bidfloorCurrency("USD")
                                .build()
                ))
                .site(Site.builder()
                        .id("site_123")
                        .domain("example.com")
                        .build())
                .device(Device.builder()
                        .ip("192.168.1.100")
                        .devicetype(1)
                        .build())
                .user(User.builder()
                        .id("user_123")
                        .build())
                .auctionType(1)
                .timeoutMs(120)
                .currencies(Arrays.asList("USD"))
                .build();
    }

    /**
     * 创建模拟候选广告
     */
    private List<BidCandidate> createMockBidCandidates() {
        return Arrays.asList(
                BidCandidate.builder()
                        .adId("ad_123")
                        .campaignId("campaign_456")
                        .creativeId("creative_789")
                        .bidPrice(1.25)
                        .width(300)
                        .height(250)
                        .adMarkup("<img src='http://example.com/ad.jpg' width='300' height='250'/>")
                        .notificationUrl("http://example.com/win?price=${AUCTION_PRICE}")
                        .advertiserDomains(Arrays.asList("advertiser.com"))
                        .categories(Arrays.asList("IAB1"))
                        .budgetAvailable(true)
                        .build()
        );
    }

    /**
     * 设置默认的Mock行为
     */
    private void setupDefaultMockBehavior() {
        // 反欺诈检测默认通过
        when(fraudDetectionService.isFraudulent(any(BidRequest.class))).thenReturn(false);
        
        // 生成候选广告
        when(biddingAlgorithm.generateBidCandidates(any(Impression.class), any(BidRequest.class)))
                .thenReturn(mockCandidates);
        
        // 过滤候选广告（不过滤）
        when(adSlotFilterService.filterCandidatesForImpression(any(), any(), any()))
                .thenAnswer(invocation -> invocation.getArgument(2));
        
        // 排序候选广告
        when(biddingAlgorithm.sortCandidates(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        
        // 选择获胜竞价
        when(biddingAlgorithm.selectWinningBid(any(), any()))
                .thenAnswer(invocation -> {
                    List<BidCandidate> candidates = invocation.getArgument(0);
                    return candidates.isEmpty() ? null : candidates.get(0);
                });
        
        // 预算检查通过
        when(budgetService.checkBudget(anyString(), anyDouble())).thenReturn(true);
        
        // 预算预扣成功
        when(budgetService.reserveBudget(anyString(), anyDouble(), anyString()))
                .thenReturn("reservation_123");
        
        // 统计数据
        when(fraudDetectionService.getFraudStatistics())
                .thenReturn(Map.of("totalRequests", 1000, "fraudBlocked", 50));
        when(adSlotFilterService.getFilterStatistics())
                .thenReturn(Map.of("totalFiltered", 100));
        when(biddingAlgorithm.getBiddingStatistics())
                .thenReturn(Map.of("totalBids", 500));
        when(budgetService.getBudgetStatistics())
                .thenReturn(Map.of("totalBudget", 10000.0, "spentBudget", 2500.0));
    }
}