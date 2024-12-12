package gae.piaz.pattern.events;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gae.piaz.pattern.events.publisher.EventNotificationResponseBodyAdvice;
import gae.piaz.pattern.events.service.DataChangeEvent;
import gae.piaz.pattern.events.service.OperationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = "events.notification-response-enabled=true")
@ActiveProfiles("test")
class EventNotificationResponseBodyAdviceTest {

    @Autowired private MockMvc mockMvc;

    @Mock private ApplicationEventPublisher springEventPublisher;

    @Autowired private EventNotificationResponseBodyAdvice eventNotificationResponseBodyAdvice;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                eventNotificationResponseBodyAdvice, "springEventPublisher", springEventPublisher);
    }

    @Test
    void testInsertEventNotification() throws Exception {

        mockMvc.perform(
                        post("/api/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                {
                                    "title": "The Lord of the Rings",
                                    "description": "The Lord of the Rings is an epic high-fantasy novel written by English author and scholar J. R. R. Tolkien."
                                }
                                """))
                .andExpect(status().isOk());

        ArgumentCaptor<DataChangeEvent> eventCaptor =
                ArgumentCaptor.forClass(DataChangeEvent.class);
        verify(springEventPublisher).publishEvent(eventCaptor.capture());
        DataChangeEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent).isNotNull();
        assertThat(capturedEvent.getBody()).isNotNull();
        assertThat(capturedEvent.getBody().toString()).contains("The Lord of the Rings");
        assertThat(capturedEvent.getOperationType()).isEqualTo(OperationType.CREATE);
    }

    @Test
    void testUpdateEventNotification() throws Exception {

        mockMvc.perform(
                        put("/api/books/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                {
                                   "description": "An Updated Description"
                                }
                                """))
                .andExpect(status().isOk());

        ArgumentCaptor<DataChangeEvent> eventCaptor =
                ArgumentCaptor.forClass(DataChangeEvent.class);
        verify(springEventPublisher).publishEvent(eventCaptor.capture());
        DataChangeEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent).isNotNull();
        assertThat(capturedEvent.getBody()).isNotNull();
        assertThat(capturedEvent.getBody().toString()).contains("An Updated Description");
        assertThat(capturedEvent.getOperationType()).isEqualTo(OperationType.UPDATE);
    }

    @Test
    void testDeleteEventNotification() throws Exception {

        mockMvc.perform(delete("/api/books/2")).andExpect(status().isNoContent());

        ArgumentCaptor<DataChangeEvent> eventCaptor =
                ArgumentCaptor.forClass(DataChangeEvent.class);
        verify(springEventPublisher).publishEvent(eventCaptor.capture());
        DataChangeEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent).isNotNull();
        assertThat(capturedEvent.getBody()).isNull();
        assertThat(capturedEvent.getOperationType()).isEqualTo(OperationType.DELETE);
    }
}
