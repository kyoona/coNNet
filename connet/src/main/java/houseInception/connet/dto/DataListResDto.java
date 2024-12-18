package houseInception.connet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
public class DataListResDto<T> {

    private int page = 0;
    private List<T> data;
}
