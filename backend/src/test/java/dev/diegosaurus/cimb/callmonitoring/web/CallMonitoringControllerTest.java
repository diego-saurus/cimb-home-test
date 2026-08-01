package dev.diegosaurus.cimb.callmonitoring.web;

import dev.diegosaurus.cimb.callmonitoring.config.CallMonitoringProperties;
import dev.diegosaurus.cimb.callmonitoring.dto.CallMonitoringSearchResponse;
import dev.diegosaurus.cimb.callmonitoring.exception.GlobalExceptionHandler;
import dev.diegosaurus.cimb.callmonitoring.exception.InvalidDateRangeException;
import dev.diegosaurus.cimb.callmonitoring.service.CallMonitoringService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CallMonitoringController.class)
@Import({GlobalExceptionHandler.class, CallMonitoringControllerTest.PropsConfig.class})
class CallMonitoringControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private CallMonitoringService service;

    @Test
    void returns200WithPagedPayload() throws Exception {
        CallMonitoringSearchResponse item = CallMonitoringSearchResponse.builder()
                .id(1L)
                .callId("C-1")
                .callTimestamp(LocalDateTime.of(2025, 5, 1, 0, 0))
                .csAgentName("Ahmad")
                .customerName("Budi")
                .sentimentScore(BigDecimal.valueOf(80))
                .build();
        Page<CallMonitoringSearchResponse> page = new PageImpl<>(
                List.of(item),
                org.springframework.data.domain.PageRequest.of(0, 5),
                1);

        when(service.search(any())).thenReturn(page);

        mockMvc.perform(get("/api/call-monitoring")
                        .param("sortBy", "callTimestamp")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].callId").value("C-1"))
                .andExpect(jsonPath("$.content[0].csAgentName").value("Ahmad"))
                .andExpect(jsonPath("$.content[0].customerName").value("Budi"))
                .andExpect(jsonPath("$.content[0].sentimentScore").value(80))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void returns400WhenStartDateAfterEndDate() throws Exception {
        when(service.search(any())).thenThrow(new InvalidDateRangeException(
                LocalDate.of(2025, 5, 10), LocalDate.of(2025, 5, 1)));

        mockMvc.perform(get("/api/call-monitoring")
                        .param("startDate", "2025-05-10")
                        .param("endDate", "2025-05-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("invalid_date_range"));
    }

    @Test
    void rejectsUnknownSortColumnWith400() throws Exception {
        mockMvc.perform(get("/api/call-monitoring")
                        .param("sortBy", "evilColumn"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("invalid_sort_column"));
    }

    @Test
    void acceptsCustomerNameSortBy() throws Exception {
        when(service.search(any())).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/call-monitoring")
                        .param("sortBy", "customerName")
                        .param("direction", "asc"))
                .andExpect(status().isOk());
    }

    @Test
    void acceptsCsAgentNameSortBy() throws Exception {
        when(service.search(any())).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/call-monitoring")
                        .param("sortBy", "csAgentName")
                        .param("direction", "asc"))
                .andExpect(status().isOk());
    }

    @Test
    void returns404ForUnknownRoute() throws Exception {
        mockMvc.perform(get("/api/not-real"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not_found"));
    }

    @Test
    void returns405ForUnsupportedMethod() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .post("/api/call-monitoring"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.error").value("method_not_allowed"));
    }

    @EnableConfigurationProperties(CallMonitoringProperties.class)
    static class PropsConfig {
    }
}
