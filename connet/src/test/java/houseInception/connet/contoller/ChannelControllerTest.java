package houseInception.connet.contoller;

import com.fasterxml.jackson.databind.ObjectMapper;
import houseInception.connet.dto.channel.ChannelDto;
import houseInception.connet.dto.channel.TapDto;
import houseInception.connet.service.ChannelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ChannelControllerTest {

    private MockMvc mockMvc;

    @Mock
    ChannelController channelController;
    @InjectMocks
    ChannelService channelService;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(channelController)
                .build();
    }

    @Test
    void addChannel() throws Exception {
        ChannelDto channelDto = new ChannelDto("channel");

        mockMvc.perform(post("/groups/uuid/channels")
                        .content(objectMapper.writeValueAsString(channelDto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void addChannel_채널이름_공백() throws Exception {
        ChannelDto channelDto = new ChannelDto(" ");

        mockMvc.perform(post("/groups/uuid/channels")
                        .content(objectMapper.writeValueAsString(channelDto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateChannel() throws Exception {
        ChannelDto channelDto = new ChannelDto("channel");

        mockMvc.perform(patch("/groups/uuid/channels/1")
                        .content(objectMapper.writeValueAsString(channelDto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void updateChannel_채널이름_공백() throws Exception {
        ChannelDto channelDto = new ChannelDto(" ");

        mockMvc.perform(patch("/groups/uuid/channels/1")
                        .content(objectMapper.writeValueAsString(channelDto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addTap() throws Exception {
        TapDto tapDto = new TapDto("tap");

        mockMvc.perform(post("/groups/uuid/channels/1/taps")
                        .content(objectMapper.writeValueAsString(tapDto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void addTap_탭이름_공백() throws Exception {
        TapDto tapDto = new TapDto(" ");

        mockMvc.perform(post("/groups/uuid/channels/1/taps")
                        .content(objectMapper.writeValueAsString(tapDto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateTap() throws Exception {
        TapDto tapDto = new TapDto("tap");

        mockMvc.perform(patch("/groups/uuid/channels/taps/1")
                        .content(objectMapper.writeValueAsString(tapDto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}