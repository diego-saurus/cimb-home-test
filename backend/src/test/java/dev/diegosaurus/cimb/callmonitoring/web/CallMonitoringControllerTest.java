package dev.diegosaurus.cimb.callmonitoring.web;

import dev.diegosaurus.cimb.callmonitoring.config.CallMonitoringProperties;
import dev.diegosaurus.cimb.callmonitoring.dto.CallMonitoringSearchResponse;
import dev.diegosaurus.cimb.callmonitoring.dto.CallMonitoringSearchResponse.Item;
import dev.diegosaurus.cimb.callmonitoring.exception.GlobalExceptionHandler;
import dev.diegosaurus.cimb.callmonitoring.exception.InvalidDateRangeException;
import dev.diegosaurus.cimb.callmonitoring.service.CallMonitoringService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CallMonitoringController.class)
@Import({GlobalExceptionHandler.class, CallMonitoringControllerTest.PropsConfig.class})
class CallMonitoringControllerTest {

    @EnableConfigurationProperties(CallMonitoringProperties.class)
    static class PropsConfig { }

    @Autowired private MockMvc mockMvc;
    @MockitoBean private CallMonitoringService service;

    @Test
    void returns200WithPagedPayload() throws Exception {
        when(service.search(any())).thenReturn(new CallMonitoringSearchResponse(
                List.of(new Item(1L, "C-1", LocalDate.of(2025, 5, 1).atStartOfDay(),
                        "Ahmad", "Budi", java.math.BigDecimal.valueOf(80))),
                1L, 1, 0, 5, null));

        mockMvc.perform(get("/api/call-monitoring")
                        .param("sortBy", "callTimestamp")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].callId").value("C-1"))
                .andExpect(jsonPath("$.items[0].csAgentName").value("Ahmad"))
                .andExpect(jsonPath("$.items[0].customerName").value("Budi"))
                .andExpect(jsonPath("$.items[0].sentimentScore").value(80))
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
}
