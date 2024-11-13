package houseInception.connet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DataListResDto<T> {

    private int page;
    private List<T> data;
}
