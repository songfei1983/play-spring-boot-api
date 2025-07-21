package fei.song.play_spring_boot_api.ads;

import com.fasterxml.jackson.databind.ObjectMapper;
import fei.song.play_spring_boot_api.ads.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * OpenRTB 端到端测试
 * 模拟真实的广告交易流程，包括：
 * 1. 完整的竞价流程
 * 2. 并发竞价处理
 * 3. 性能测试
 * 4. 真实场景模拟
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("OpenRTB 端到端测试")
public class OpenRTBEndToEndTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("完整竞价流程端到端测试")
    void testCompleteRTBFlow() throws Exception {
        // 1. 发送竞价请求
        BidRequest bidRequest = createRealisticBidRequest();
        String requestJson = objectMapper.writeValueAsString(bidRequest);

        MvcResult bidResult = mockMvc.perform(post("/api/v1/bid/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .header("X-Real-IP", "203.0.113.1")
                .header("User-Agent", "AdExchange/1.0"))
                .andExpect(status().isOk())
                .andReturn();

        // 2. 解析竞价响应
        String responseJson = bidResult.getResponse().getContentAsString();
        BidResponse bidResponse = objectMapper.readValue(responseJson, BidResponse.class);

        assertNotNull(bidResponse);
        assertEquals(bidRequest.getId(), bidResponse.getId());
        assertNotNull(bidResponse.getBidid());
        assertFalse(bidResponse.getSeatbid().isEmpty());

        // 3. 提取竞价信息
        BidResponse.SeatBid seatBid = bidResponse.getSeatbid().get(0);
        BidResponse.Bid winningBid = seatBid.getBid().get(0);
        String bidId = winningBid.getId();
        Double bidPrice = winningBid.getPrice();

        // 4. 模拟拍卖结果 - 获胜场景
        Double finalPrice = bidPrice * 0.9; // 第二价格拍卖
        
        mockMvc.perform(post("/api/v1/bid/win/{bidId}", bidId)
                .param("winPrice", finalPrice.toString())
                .header("X-Real-IP", "203.0.113.1"))
                .andExpect(status().isOk());

        // 5. 验证服务器状态
        MvcResult statusResult = mockMvc.perform(get("/api/v1/bid/status"))
                .andExpect(status().isOk())
                .andReturn();

        String statusJson = statusResult.getResponse().getContentAsString();
        @SuppressWarnings("unchecked")
        Map<String, Object> status = objectMapper.readValue(statusJson, Map.class);
        assertEquals("running", status.get("serverStatus"));
    }

    @Test
    @DisplayName("竞价失败流程测试")
    void testBidLossFlow() throws Exception {
        // 1. 发送竞价请求
        BidRequest bidRequest = createRealisticBidRequest();
        String requestJson = objectMapper.writeValueAsString(bidRequest);

        MvcResult bidResult = mockMvc.perform(post("/api/v1/bid/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andReturn();

        // 2. 解析竞价响应
        String responseJson = bidResult.getResponse().getContentAsString();
        BidResponse bidResponse = objectMapper.readValue(responseJson, BidResponse.class);

        if (!bidResponse.getSeatbid().isEmpty()) {
            // 3. 模拟竞价失败
            BidResponse.Bid bid = bidResponse.getSeatbid().get(0).getBid().get(0);
            String bidId = bid.getId();
            Double winPrice = bid.getPrice() + 0.1; // 被更高价格击败
            Integer lossReason = 1; // 竞价失败

            mockMvc.perform(post("/api/v1/bid/loss/{bidId}", bidId)
                    .param("winPrice", winPrice.toString())
                    .param("lossReason", lossReason.toString()))
                    .andExpect(status().isOk());
        }
    }

    @Test
    @DisplayName("并发竞价处理测试")
    void testConcurrentBidding() throws Exception {
        int concurrentRequests = 10;
        ExecutorService executor = Executors.newFixedThreadPool(concurrentRequests);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // 创建并发竞价请求
        for (int i = 0; i < concurrentRequests; i++) {
            final int requestId = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    BidRequest bidRequest = createRealisticBidRequest();
                    bidRequest.setId("concurrent_request_" + requestId);
                    String requestJson = objectMapper.writeValueAsString(bidRequest);

                    mockMvc.perform(post("/api/v1/bid/request")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson)
                            .header("X-Real-IP", "192.168.1." + (100 + requestId)))
                            .andExpect(status().isOk());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, executor);
            futures.add(future);
        }

        // 等待所有请求完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .get(30, TimeUnit.SECONDS);

        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("性能基准测试")
    void testPerformanceBenchmark() throws Exception {
        int requestCount = 50;
        long startTime = System.currentTimeMillis();
        List<Long> responseTimes = new ArrayList<>();

        for (int i = 0; i < requestCount; i++) {
            long requestStart = System.currentTimeMillis();
            
            BidRequest bidRequest = createRealisticBidRequest();
            bidRequest.setId("perf_test_" + i);
            String requestJson = objectMapper.writeValueAsString(bidRequest);

            mockMvc.perform(post("/api/v1/bid/request")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                    .andExpect(status().isOk());

            long responseTime = System.currentTimeMillis() - requestStart;
            responseTimes.add(responseTime);
        }

        long totalTime = System.currentTimeMillis() - startTime;
        double avgResponseTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0);
        double qps = (double) requestCount / (totalTime / 1000.0);

        System.out.println("性能测试结果:");
        System.out.println("总请求数: " + requestCount);
        System.out.println("总耗时: " + totalTime + "ms");
        System.out.println("平均响应时间: " + String.format("%.2f", avgResponseTime) + "ms");
        System.out.println("QPS: " + String.format("%.2f", qps));

        // 性能断言
        assertTrue(avgResponseTime < 200, "平均响应时间应小于200ms");
        assertTrue(qps > 10, "QPS应大于10");
    }

    @Test
    @DisplayName("移动端竞价测试")
    void testMobileBidding() throws Exception {
        BidRequest mobileBidRequest = createMobileBidRequest();
        String requestJson = objectMapper.writeValueAsString(mobileBidRequest);

        MvcResult result = mockMvc.perform(post("/api/v1/bid/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .header("X-Real-IP", "192.168.1.200")
                .header("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X)"))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        BidResponse response = objectMapper.readValue(responseJson, BidResponse.class);

        assertNotNull(response);
        if (!response.getSeatbid().isEmpty()) {
            BidResponse.Bid bid = response.getSeatbid().get(0).getBid().get(0);
            // 验证移动端广告尺寸
            assertTrue(bid.getW() <= 320 || bid.getH() <= 480, "移动端广告尺寸应适配移动设备");
        }
    }

    @Test
    @DisplayName("视频广告竞价测试")
    void testVideoBidding() throws Exception {
        BidRequest videoBidRequest = createVideoBidRequest();
        String requestJson = objectMapper.writeValueAsString(videoBidRequest);

        mockMvc.perform(post("/api/v1/bid/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(mvcResult -> {
                    int status = mvcResult.getResponse().getStatus();
                    assertTrue(status == 200 || status == 204, "状态码应为200或204");
                });
    }

    @Test
    @DisplayName("多种货币竞价测试")
    void testMultiCurrencyBidding() throws Exception {
        // 测试EUR货币
        BidRequest eurRequest = createRealisticBidRequest();
        eurRequest.setCurrencies(Arrays.asList("EUR"));
        eurRequest.getImp().get(0).setBidfloorCurrency("EUR");
        
        String requestJson = objectMapper.writeValueAsString(eurRequest);

        MvcResult result = mockMvc.perform(post("/api/v1/bid/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(mvcResult -> {
                    int status = mvcResult.getResponse().getStatus();
                    assertTrue(status == 200 || status == 204, "状态码应为200或204");
                })
                .andReturn();

        if (result.getResponse().getStatus() == 200) {
            String responseJson = result.getResponse().getContentAsString();
            BidResponse response = objectMapper.readValue(responseJson, BidResponse.class);
            // 验证响应货币（系统默认返回USD）
            assertEquals("USD", response.getCur());
        }
    }

    @Test
    @DisplayName("地理位置定向测试")
    void testGeoTargeting() throws Exception {
        BidRequest geoRequest = createGeoTargetedBidRequest();
        String requestJson = objectMapper.writeValueAsString(geoRequest);

        mockMvc.perform(post("/api/v1/bid/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .header("X-Real-IP", "8.8.8.8")) // 美国IP
                .andExpect(mvcResult -> {
                    int status = mvcResult.getResponse().getStatus();
                    assertTrue(status == 200 || status == 204, "状态码应为200或204");
                });
    }

    @Test
    @DisplayName("私有交易竞价测试")
    void testPrivateMarketplaceBidding() throws Exception {
        BidRequest pmpRequest = createPrivateMarketplaceBidRequest();
        String requestJson = objectMapper.writeValueAsString(pmpRequest);

        mockMvc.perform(post("/api/v1/bid/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(mvcResult -> {
                    int status = mvcResult.getResponse().getStatus();
                    assertTrue(status == 200 || status == 204, "状态码应为200或204");
                });
    }

    /**
     * 创建真实的竞价请求
     */
    private BidRequest createRealisticBidRequest() {
        return BidRequest.builder()
                .id(UUID.randomUUID().toString())
                .imp(Arrays.asList(
                        Impression.builder()
                                .id("imp_1")
                                .banner(Banner.builder()
                                        .w(300)
                                        .h(250)
                                        .pos(1)
                                        .mimes(Arrays.asList("image/jpeg", "image/png", "image/gif"))
                                        .build())
                                .bidfloor(0.5)
                                .bidfloorCurrency("USD")
                                .secure(1)
                                .build()
                ))
                .site(Site.builder()
                        .id("site_12345")
                        .domain("news.example.com")
                        .page("https://news.example.com/article/123")
                        .cat(Arrays.asList("IAB12", "IAB12-1")) // 新闻类别
                        .sectioncat(Arrays.asList("IAB12-1"))
                        .pagecat(Arrays.asList("IAB12-1"))
                        .publisher(Publisher.builder()
                                .id("pub_789")
                                .name("Example News")
                                .cat(Arrays.asList("IAB12"))
                                .build())
                        .build())
                .device(Device.builder()
                        .ua("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                        .ip("203.0.113.1")
                        .geo(Geo.builder()
                                .country("USA")
                                .region("CA")
                                .city("San Francisco")
                                .zip("94102")
                                .lat(37.7749)
                                .lon(-122.4194)
                                .build())
                        .devicetype(1) // 移动/平板
                        .make("Apple")
                        .model("iPhone")
                        .os("iOS")
                        .osv("14.6")
                        .h(812)
                        .w(375)
                        .ppi(326)
                        .connectiontype(2) // WiFi
                        .build())
                .user(User.builder()
                        .id("user_" + UUID.randomUUID().toString().substring(0, 8))
                        .buyeruid("buyer_" + UUID.randomUUID().toString().substring(0, 8))
                        .yob(1985)
                        .gender("F")
                        .keywords("technology,news,mobile")
                        .build())
                .auctionType(1) // 第一价格拍卖
                .timeoutMs(120) // 120ms超时
                .currencies(Arrays.asList("USD"))
                .blacklistedCategories(Arrays.asList("IAB25", "IAB26")) // 禁止的广告类别
                .blacklistedAdvertisers(Arrays.asList("competitor.com")) // 禁止的广告主域名
                .build();
    }

    /**
     * 创建移动端竞价请求
     */
    private BidRequest createMobileBidRequest() {
        return BidRequest.builder()
                .id("mobile_" + UUID.randomUUID().toString())
                .imp(Arrays.asList(
                        Impression.builder()
                                .id("mobile_imp_1")
                                .banner(Banner.builder()
                                        .w(320)
                                        .h(50)
                                        .pos(1)
                                        .build())
                                .bidfloor(0.3)
                                .bidfloorCurrency("USD")
                                .build()
                ))
                .app(App.builder()
                        .id("app_123")
                        .name("Example Mobile App")
                        .bundle("com.example.app")
                        .categories(Arrays.asList("IAB1"))
                        .version("1.0.0")
                        .publisher(Publisher.builder()
                                .id("mobile_pub_456")
                                .name("Mobile Publisher")
                                .build())
                        .build())
                .device(Device.builder()
                        .ua("Mozilla/5.0 (iPhone; CPU iPhone OS 14_6 like Mac OS X)")
                        .ip("192.168.1.200")
                        .devicetype(1)
                        .make("Apple")
                        .model("iPhone 12")
                        .os("iOS")
                        .osv("14.6")
                        .h(812)
                        .w(375)
                        .build())
                .user(User.builder()
                        .id("mobile_user_123")
                        .build())
                .auctionType(1)
                .timeoutMs(100)
                .currencies(Arrays.asList("USD"))
                .build();
    }

    /**
     * 创建视频广告竞价请求
     */
    private BidRequest createVideoBidRequest() {
        return BidRequest.builder()
                .id("video_" + UUID.randomUUID().toString())
                .imp(Arrays.asList(
                        Impression.builder()
                                .id("video_imp_1")
                                .video(Video.builder()
                                        .mimes(Arrays.asList("video/mp4", "video/webm"))
                                        .minDuration(15)
                                        .maxDuration(30)
                                        .w(640)
                                        .h(480)
                                        .startDelay(0)
                                        .protocols(Arrays.asList(2, 3, 5, 6))
                                        .build())
                                .bidfloor(2.0)
                                .bidfloorCurrency("USD")
                                .build()
                ))
                .site(Site.builder()
                        .id("video_site_123")
                        .domain("video.example.com")
                        .build())
                .device(Device.builder()
                        .ua("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                        .ip("203.0.113.50")
                        .devicetype(2) // 个人电脑
                        .build())
                .user(User.builder()
                        .id("video_user_123")
                        .build())
                .auctionType(1)
                .timeoutMs(150)
                .currencies(Arrays.asList("USD"))
                .build();
    }

    /**
     * 创建地理位置定向竞价请求
     */
    private BidRequest createGeoTargetedBidRequest() {
        return BidRequest.builder()
                .id("geo_" + UUID.randomUUID().toString())
                .imp(Arrays.asList(
                        Impression.builder()
                                .id("geo_imp_1")
                                .banner(Banner.builder()
                                        .w(728)
                                        .h(90)
                                        .pos(1)
                                        .build())
                                .bidfloor(1.0)
                                .bidfloorCurrency("USD")
                                .build()
                ))
                .site(Site.builder()
                        .id("geo_site_123")
                        .domain("local.example.com")
                        .build())
                .device(Device.builder()
                        .ua("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                        .ip("8.8.8.8")
                        .geo(Geo.builder()
                                .country("USA")
                                .region("NY")
                                .city("New York")
                                .zip("10001")
                                .lat(40.7128)
                                .lon(-74.0060)
                                .build())
                        .devicetype(2)
                        .build())
                .user(User.builder()
                        .id("geo_user_123")
                        .build())
                .auctionType(1)
                .timeoutMs(120)
                .currencies(Arrays.asList("USD"))
                .build();
    }

    /**
     * 创建私有交易竞价请求
     */
    private BidRequest createPrivateMarketplaceBidRequest() {
        return BidRequest.builder()
                .id("pmp_" + UUID.randomUUID().toString())
                .imp(Arrays.asList(
                        Impression.builder()
                                .id("pmp_imp_1")
                                .banner(Banner.builder()
                                        .w(300)
                                        .h(250)
                                        .pos(1)
                                        .build())
                                .bidfloor(5.0)
                                .bidfloorCurrency("USD")
                                .pmp(Pmp.builder()
                                        .privateAuction(1)
                                        .deals(Arrays.asList(
                                                Pmp.Deal.builder()
                                                        .id("deal_123")
                                                        .bidfloor(5.0)
                                                        .bidfloorCurrency("USD")
                                                        .whitelistedSeats(Arrays.asList("seat_1"))
                                                        .build()
                                        ))
                                        .build())
                                .build()
                ))
                .site(Site.builder()
                        .id("pmp_site_123")
                        .domain("premium.example.com")
                        .build())
                .device(Device.builder()
                        .ua("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                        .ip("203.0.113.100")
                        .devicetype(2)
                        .build())
                .user(User.builder()
                        .id("pmp_user_123")
                        .build())
                .auctionType(1)
                .timeoutMs(120)
                .currencies(Arrays.asList("USD"))
                .build();
    }
}