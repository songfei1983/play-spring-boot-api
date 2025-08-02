package fei.song.play_spring_boot_api.ads.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import fei.song.play_spring_boot_api.ads.controller.UserSegmentController;
import fei.song.play_spring_boot_api.ads.infrastructure.persistence.entity.UserSegmentEntity;
import fei.song.play_spring_boot_api.ads.service.UserSegmentService;
import fei.song.play_spring_boot_api.ads.service.UserSegmentMappingService;
import fei.song.play_spring_boot_api.ads.service.SegmentFilterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserSegmentController.class)
class UserSegmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserSegmentService userSegmentService;

    @MockBean
    private UserSegmentMappingService userSegmentMappingService;

    @MockBean
    private SegmentFilterService segmentFilterService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserSegmentEntity testSegment;

    @BeforeEach
    void setUp() {
        testSegment = new UserSegmentEntity();
        testSegment.setId("segment1");
        testSegment.setSegmentName("Tech Enthusiasts");
        testSegment.setDescription("Users interested in technology");
        testSegment.setStatus("ACTIVE");
        testSegment.setCreatedAt(LocalDateTime.now());
        testSegment.setUpdatedAt(LocalDateTime.now());
        
        // 设置规则
        UserSegmentEntity.SegmentRule rule = new UserSegmentEntity.SegmentRule();
        rule.setField("interests.category");
        rule.setOperator("equals");
        rule.setValue("technology");
        testSegment.setRules(Arrays.asList(rule));
    }

    @Test
    void testCreateSegment_Success() throws Exception {
        when(userSegmentService.createSegment(any(UserSegmentEntity.class)))
            .thenReturn(testSegment);

        mockMvc.perform(post("/api/ads/segments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSegment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.segmentName").value("Tech Enthusiasts"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(userSegmentService).createSegment(any(UserSegmentEntity.class));
    }

    @Test
    void testCreateSegment_InvalidRequest() throws Exception {
        UserSegmentEntity emptySegment = new UserSegmentEntity();
        when(userSegmentService.createSegment(any(UserSegmentEntity.class)))
            .thenReturn(emptySegment);

        mockMvc.perform(post("/api/ads/segments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testGetSegment_Success() throws Exception {
        when(userSegmentService.findSegmentById("segment1"))
            .thenReturn(Optional.of(testSegment));

        mockMvc.perform(get("/api/ads/segments/segment1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("segment1"))
                .andExpect(jsonPath("$.segmentName").value("Tech Enthusiasts"));

        verify(userSegmentService).findSegmentById("segment1");
    }

    @Test
    void testGetSegment_NotFound() throws Exception {
        when(userSegmentService.findSegmentById("nonexistent"))
            .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/ads/segments/nonexistent"))
                .andExpect(status().isNotFound());

        verify(userSegmentService).findSegmentById("nonexistent");
    }

    @Test
    void testUpdateSegment_Success() throws Exception {
        when(userSegmentService.updateSegment(eq("segment1"), any(UserSegmentEntity.class)))
            .thenReturn(testSegment);

        mockMvc.perform(put("/api/ads/segments/segment1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSegment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.segmentName").value("Tech Enthusiasts"));

        verify(userSegmentService).updateSegment(eq("segment1"), any(UserSegmentEntity.class));
    }

    @Test
    void testUpdateSegment_NotFound() throws Exception {
        when(userSegmentService.updateSegment(eq("nonexistent"), any(UserSegmentEntity.class)))
            .thenThrow(new IllegalArgumentException("Segment not found"));

        mockMvc.perform(put("/api/ads/segments/nonexistent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSegment)))
                .andExpect(status().isBadRequest());

        verify(userSegmentService).updateSegment(eq("nonexistent"), any(UserSegmentEntity.class));
    }

    @Test
    void testDeleteSegment_Success() throws Exception {
        doNothing().when(userSegmentService).deleteSegment("segment1");

        mockMvc.perform(delete("/api/ads/segments/segment1"))
                .andExpect(status().isNoContent());

        verify(userSegmentService).deleteSegment("segment1");
    }

    @Test
    void testDeleteSegment_NotFound() throws Exception {
        doThrow(new IllegalArgumentException("Segment not found"))
            .when(userSegmentService).deleteSegment("nonexistent");

        mockMvc.perform(delete("/api/ads/segments/nonexistent"))
                .andExpect(status().isBadRequest());

        verify(userSegmentService).deleteSegment("nonexistent");
    }

    @Test
    void testGetAllSegments_Success() throws Exception {
        Page<UserSegmentEntity> page = new PageImpl<>(Arrays.asList(testSegment), PageRequest.of(0, 10), 1);
        when(userSegmentService.findSegments(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/ads/segments")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].segmentName").value("Tech Enthusiasts"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(userSegmentService).findSegments(any(PageRequest.class));
    }

    @Test
    void testGetActiveSegments_Success() throws Exception {
        List<UserSegmentEntity> segments = Arrays.asList(testSegment);
        when(userSegmentService.findActiveSegments()).thenReturn(segments);

        mockMvc.perform(get("/api/ads/segments/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].segmentName").value("Tech Enthusiasts"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        verify(userSegmentService).findActiveSegments();
    }

    @Test
    void testGetActiveSegments_EmptyResult() throws Exception {
        when(userSegmentService.findActiveSegments()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/ads/segments/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(userSegmentService).findActiveSegments();
    }

    @Test
    void testGetSegmentsByName_Success() throws Exception {
        List<UserSegmentEntity> segments = Arrays.asList(testSegment);
        when(userSegmentService.searchSegments("Tech")).thenReturn(segments);

        mockMvc.perform(get("/api/ads/segments/search")
                .param("keyword", "Tech"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].segmentName").value("Tech Enthusiasts"));

        verify(userSegmentService).searchSegments("Tech");
    }

    @Test
    void testActivateSegment_Success() throws Exception {
        when(userSegmentService.activateSegment("segment1")).thenReturn(testSegment);

        mockMvc.perform(post("/api/ads/segments/segment1/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(userSegmentService).activateSegment("segment1");
    }

    @Test
    void testDeactivateSegment_Success() throws Exception {
        testSegment.setStatus("INACTIVE");
        when(userSegmentService.deactivateSegment("segment1")).thenReturn(testSegment);

        mockMvc.perform(post("/api/ads/segments/segment1/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));

        verify(userSegmentService).deactivateSegment("segment1");
    }

    @Test
    void testServiceException_InternalServerError() throws Exception {
        when(userSegmentService.findSegmentById("segment1"))
            .thenThrow(new RuntimeException("Database connection failed"));

        mockMvc.perform(get("/api/ads/segments/segment1"))
                .andExpect(status().isInternalServerError());

        verify(userSegmentService).findSegmentById("segment1");
    }
}