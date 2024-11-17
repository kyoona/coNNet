package houseInception.connet.contoller;

import houseInception.connet.dto.PrivateChatAddDto;
import houseInception.connet.dto.PrivateChatAddRestDto;
import houseInception.connet.response.BaseResponse;
import houseInception.connet.service.PrivateRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/privateRooms")
@RequiredArgsConstructor
@RestController
public class PrivateRoomController {

    private final PrivateRoomService privateRoomService;

    @PostMapping("/{targetId}")
    public BaseResponse<PrivateChatAddRestDto> addPrivateChat(@PathVariable Long targetId,
                                                              @ModelAttribute PrivateChatAddDto chatAddDto){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        PrivateChatAddRestDto result = privateRoomService.addPrivateChat(userId, targetId, chatAddDto);

        return new BaseResponse<>(result);
    }
}
