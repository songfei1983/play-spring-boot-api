package fei.song.play_spring_boot_api.ads.service;

import fei.song.play_spring_boot_api.ads.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * BidServer 单体测试
 */
@ExtendWith(MockitoExtension.class)
class BidServerTest {

    @Mock
    private FraudDetectionService fraudDetectionService;

    @Mock
    private AdSlotFilterService adSlotFilterService;

    @Mock
    private BiddingAlgorithm biddingAlgorithm;

    @Mock
    private BudgetService budgetService;

    @InjectMocks
    private BidServer bidServer;

    private BidRequest testBidRequest;
    private Impression testImpression;
    private BidCandidate testCandidate;

    @BeforeEach
    void setUp() {
        // 创建测试用的竞价请求
        testImpression = Impression.builder()
            .id("imp1")
            .bidfloor(1.0)
            .bidfloorCurrency("USD")
            .build();

        testBidRequest = BidRequest.builder()
            .id("request1")
            .imp(Arrays.asList(testImpression))
            .build();

        // 创建测试用的竞价候选
        testCandidate = BidCandidate.builder()
            .adId("ad1")
            .campaignId("campaign1")
            .creativeId("creative1")
            .bidPrice(2.0)
            .width(300)
            .height(250)
            .adMarkup("<div>Test Ad</div>")
            .notificationUrl("http://example.com/win")
            .advertiserDomains(Arrays.asList("example.com"))
            .categories(Arrays.asList("IAB1"))
            .budgetAvailable(true)
            .build();
    }

    @Test
    void testProcessBidRequest_Success() {
        // 准备测试数据
        when(fraudDetectionService.isFraudulent(testBidRequest)).thenReturn(false);
        when(biddingAlgorithm.generateBidCandidates(testImpression, testBidRequest))
            .thenReturn(Arrays.asList(testCandidate));
        when(adSlotFilterService.filterCandidatesForImpression(eq(testImpression), eq(testBidRequest), anyList()))
            .thenReturn(Arrays.asList(testCandidate));
        when(biddingAlgorithm.sortCandidates(anyList()))
            .thenReturn(Arrays.asList(testCandidate));
        when(biddingAlgorithm.selectWinningBid(anyList(), eq(testImpression)))
            .thenReturn(testCandidate);
        when(budgetService.checkBudget("campaign1", 2.0)).thenReturn(true);
        when(budgetService.reserveBudget("campaign1", 2.0, "ad1")).thenReturn("reservation1");

        // 执行测试
        BidResponse response = bidServer.processBidRequest(testBidRequest);

        // 验证结果
        assertNotNull(response);
        assertEquals("request1", response.getId());
        assertNotNull(response.getSeatbid());
        assertEquals(1, response.getSeatbid().size());
        assertEquals(1, response.getSeatbid().get(0).getBid().size());
        
        BidResponse.Bid bid = response.getSeatbid().get(0).getBid().get(0);
        assertEquals("ad1", bid.getId());
        assertEquals("imp1", bid.getImpid());
        assertEquals(2.0, bid.getPrice());
        assertEquals("campaign1", bid.getCid());

        // 验证方法调用
        verify(fraudDetectionService).isFraudulent(testBidRequest);
        verify(biddingAlgorithm).generateBidCandidates(testImpression, testBidRequest);
        verify(adSlotFilterService).filterCandidatesForImpression(eq(testImpression), eq(testBidRequest), anyList());
        verify(budgetService).checkBudget("campaign1", 2.0);
        verify(budgetService).reserveBudget("campaign1", 2.0, "ad1");
    }

    @Test
    void testProcessBidRequest_FraudDetected() {
        // 准备测试数据
        when(fraudDetectionService.isFraudulent(testBidRequest)).thenReturn(true);

        // 执行测试
        BidResponse response = bidServer.processBidRequest(testBidRequest);

        // 验证结果
        assertNotNull(response);
        assertEquals("request1", response.getId());
        assertTrue(response.getSeatbid().isEmpty());
        assertEquals(2, response.getNbr()); // 欺诈流量

        // 验证方法调用
        verify(fraudDetectionService).isFraudulent(testBidRequest);
        verifyNoInteractions(biddingAlgorithm);
        verifyNoInteractions(adSlotFilterService);
        verifyNoInteractions(budgetService);
    }

    @Test
    void testProcessBidRequest_NoCandidates() {
        // 准备测试数据
        when(fraudDetectionService.isFraudulent(testBidRequest)).thenReturn(false);
        when(biddingAlgorithm.generateBidCandidates(testImpression, testBidRequest))
            .thenReturn(Collections.emptyList());

        // 执行测试
        BidResponse response = bidServer.processBidRequest(testBidRequest);

        // 验证结果
        assertNotNull(response);
        assertEquals("request1", response.getId());
        assertTrue(response.getSeatbid().isEmpty());
        assertEquals(0, response.getNbr()); // 未知原因

        // 验证方法调用
        verify(fraudDetectionService).isFraudulent(testBidRequest);
        verify(biddingAlgorithm).generateBidCandidates(testImpression, testBidRequest);
        verifyNoInteractions(adSlotFilterService);
        verifyNoInteractions(budgetService);
    }

    @Test
    void testProcessBidRequest_AllCandidatesFiltered() {
        // 准备测试数据
        when(fraudDetectionService.isFraudulent(testBidRequest)).thenReturn(false);
        when(biddingAlgorithm.generateBidCandidates(testImpression, testBidRequest))
            .thenReturn(Arrays.asList(testCandidate));
        when(adSlotFilterService.filterCandidatesForImpression(eq(testImpression), eq(testBidRequest), anyList()))
            .thenReturn(Collections.emptyList());

        // 执行测试
        BidResponse response = bidServer.processBidRequest(testBidRequest);

        // 验证结果
        assertNotNull(response);
        assertEquals("request1", response.getId());
        assertTrue(response.getSeatbid().isEmpty());
        assertEquals(0, response.getNbr());

        // 验证方法调用
        verify(fraudDetectionService).isFraudulent(testBidRequest);
        verify(biddingAlgorithm).generateBidCandidates(testImpression, testBidRequest);
        verify(adSlotFilterService).filterCandidatesForImpression(eq(testImpression), eq(testBidRequest), anyList());
        verifyNoInteractions(budgetService);
    }

    @Test
    void testProcessBidRequest_BudgetCheckFailed() {
        // 准备测试数据
        when(fraudDetectionService.isFraudulent(testBidRequest)).thenReturn(false);
        when(biddingAlgorithm.generateBidCandidates(testImpression, testBidRequest))
            .thenReturn(Arrays.asList(testCandidate));
        when(adSlotFilterService.filterCandidatesForImpression(eq(testImpression), eq(testBidRequest), anyList()))
            .thenReturn(Arrays.asList(testCandidate));
        when(biddingAlgorithm.sortCandidates(anyList()))
            .thenReturn(Arrays.asList(testCandidate));
        when(biddingAlgorithm.selectWinningBid(anyList(), eq(testImpression)))
            .thenReturn(testCandidate);
        when(budgetService.checkBudget("campaign1", 2.0)).thenReturn(false);

        // 执行测试
        BidResponse response = bidServer.processBidRequest(testBidRequest);

        // 验证结果
        assertNotNull(response);
        assertEquals("request1", response.getId());
        assertTrue(response.getSeatbid().isEmpty());
        assertEquals(0, response.getNbr());

        // 验证方法调用
        verify(budgetService).checkBudget("campaign1", 2.0);
        verify(budgetService, never()).reserveBudget(anyString(), anyDouble(), anyString());
    }

    @Test
    void testProcessBidRequest_BudgetReservationFailed() {
        // 准备测试数据
        when(fraudDetectionService.isFraudulent(testBidRequest)).thenReturn(false);
        when(biddingAlgorithm.generateBidCandidates(testImpression, testBidRequest))
            .thenReturn(Arrays.asList(testCandidate));
        when(adSlotFilterService.filterCandidatesForImpression(eq(testImpression), eq(testBidRequest), anyList()))
            .thenReturn(Arrays.asList(testCandidate));
        when(biddingAlgorithm.sortCandidates(anyList()))
            .thenReturn(Arrays.asList(testCandidate));
        when(biddingAlgorithm.selectWinningBid(anyList(), eq(testImpression)))
            .thenReturn(testCandidate);
        when(budgetService.checkBudget("campaign1", 2.0)).thenReturn(true);
        when(budgetService.reserveBudget("campaign1", 2.0, "ad1")).thenReturn(null);

        // 执行测试
        BidResponse response = bidServer.processBidRequest(testBidRequest);

        // 验证结果
        assertNotNull(response);
        assertEquals("request1", response.getId());
        assertTrue(response.getSeatbid().isEmpty());
        assertEquals(0, response.getNbr());

        // 验证方法调用
        verify(budgetService).checkBudget("campaign1", 2.0);
        verify(budgetService).reserveBudget("campaign1", 2.0, "ad1");
    }

    @Test
    void testProcessBidRequest_Exception() {
        // 准备测试数据
        when(fraudDetectionService.isFraudulent(testBidRequest))
            .thenThrow(new RuntimeException("Service error"));

        // 执行测试
        BidResponse response = bidServer.processBidRequest(testBidRequest);

        // 验证结果
        assertNotNull(response);
        assertEquals("request1", response.getId());
        assertTrue(response.getSeatbid().isEmpty());
        assertEquals(1, response.getNbr()); // 技术错误

        // 验证方法调用
        verify(fraudDetectionService).isFraudulent(testBidRequest);
    }

    @Test
    void testHandleWinNotification_Success() {
        // 执行测试
        bidServer.handleWinNotification("bid1", 2.5);

        // 验证方法调用
        verify(budgetService).confirmBudgetSpend("bid1", 2.5);
    }

    @Test
    void testHandleWinNotification_NullPrice() {
        // 执行测试
        bidServer.handleWinNotification("bid1", null);

        // 验证方法调用
        verify(budgetService, never()).confirmBudgetSpend(anyString(), anyDouble());
    }

    @Test
    void testHandleLossNotification_Success() {
        // 执行测试
        bidServer.handleLossNotification("bid1", 2.5, 1);

        // 验证方法调用
        verify(budgetService).releaseBudgetReservation("bid1");
    }

    @Test
    void testGetServerStatistics() {
        // 准备测试数据
        Map<String, Object> fraudStats = new HashMap<>();
        fraudStats.put("totalChecked", 1000);
        fraudStats.put("fraudDetected", 50);
        
        Map<String, Object> filterStats = new HashMap<>();
        filterStats.put("totalFiltered", 500);
        
        Map<String, Object> biddingStats = new HashMap<>();
        biddingStats.put("totalBids", 200);
        
        Map<String, Object> budgetStats = new HashMap<>();
        budgetStats.put("totalSpent", 10000.0);

        when(fraudDetectionService.getFraudStatistics()).thenReturn(fraudStats);
        when(adSlotFilterService.getFilterStatistics()).thenReturn(filterStats);
        when(biddingAlgorithm.getBiddingStatistics()).thenReturn(biddingStats);
        when(budgetService.getBudgetStatistics()).thenReturn(budgetStats);

        // 执行测试
        Map<String, Object> stats = bidServer.getServerStatistics();

        // 验证结果
        assertNotNull(stats);
        assertEquals("running", stats.get("serverStatus"));
        assertEquals(fraudStats, stats.get("fraudDetection"));
        assertEquals(filterStats, stats.get("filtering"));
        assertEquals(biddingStats, stats.get("bidding"));
        assertEquals(budgetStats, stats.get("budgetStats"));

        // 验证方法调用
        verify(fraudDetectionService).getFraudStatistics();
        verify(adSlotFilterService).getFilterStatistics();
        verify(biddingAlgorithm).getBiddingStatistics();
        verify(budgetService).getBudgetStatistics();
    }

    @Test
    void testProcessBidRequest_MultipleImpressions() {
        // 准备测试数据 - 多个广告位
        Impression impression2 = Impression.builder()
            .id("imp2")
            .bidfloor(1.5)
            .bidfloorCurrency("USD")
            .build();

        testBidRequest = BidRequest.builder()
            .id("request1")
            .imp(Arrays.asList(testImpression, impression2))
            .build();

        BidCandidate candidate2 = BidCandidate.builder()
            .adId("ad2")
            .campaignId("campaign2")
            .creativeId("creative2")
            .bidPrice(3.0)
            .width(728)
            .height(90)
            .adMarkup("<div>Test Ad 2</div>")
            .notificationUrl("http://example.com/win2")
            .advertiserDomains(Arrays.asList("example2.com"))
            .categories(Arrays.asList("IAB2"))
            .budgetAvailable(true)
            .build();

        when(fraudDetectionService.isFraudulent(testBidRequest)).thenReturn(false);
        
        // 为第一个广告位设置mock
        when(biddingAlgorithm.generateBidCandidates(testImpression, testBidRequest))
            .thenReturn(Arrays.asList(testCandidate));
        when(adSlotFilterService.filterCandidatesForImpression(eq(testImpression), eq(testBidRequest), anyList()))
            .thenReturn(Arrays.asList(testCandidate));
        when(biddingAlgorithm.sortCandidates(Arrays.asList(testCandidate)))
            .thenReturn(Arrays.asList(testCandidate));
        when(biddingAlgorithm.selectWinningBid(Arrays.asList(testCandidate), testImpression))
            .thenReturn(testCandidate);
        when(budgetService.checkBudget("campaign1", 2.0)).thenReturn(true);
        when(budgetService.reserveBudget("campaign1", 2.0, "ad1")).thenReturn("reservation1");
        
        // 为第二个广告位设置mock
        when(biddingAlgorithm.generateBidCandidates(impression2, testBidRequest))
            .thenReturn(Arrays.asList(candidate2));
        when(adSlotFilterService.filterCandidatesForImpression(eq(impression2), eq(testBidRequest), anyList()))
            .thenReturn(Arrays.asList(candidate2));
        when(biddingAlgorithm.sortCandidates(Arrays.asList(candidate2)))
            .thenReturn(Arrays.asList(candidate2));
        when(biddingAlgorithm.selectWinningBid(Arrays.asList(candidate2), impression2))
            .thenReturn(candidate2);
        when(budgetService.checkBudget("campaign2", 3.0)).thenReturn(true);
        when(budgetService.reserveBudget("campaign2", 3.0, "ad2")).thenReturn("reservation2");

        // 执行测试
        BidResponse response = bidServer.processBidRequest(testBidRequest);

        // 验证结果
        assertNotNull(response);
        assertEquals("request1", response.getId());
        assertNotNull(response.getSeatbid());
        assertEquals(2, response.getSeatbid().size()); // 两个座位竞价

        // 验证方法调用
        verify(biddingAlgorithm).generateBidCandidates(testImpression, testBidRequest);
        verify(biddingAlgorithm).generateBidCandidates(impression2, testBidRequest);
        verify(budgetService).checkBudget("campaign1", 2.0);
        verify(budgetService).checkBudget("campaign2", 3.0);
    }
}