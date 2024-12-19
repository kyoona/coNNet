package houseInception.connet.service.util;

import houseInception.connet.exception.FileException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static houseInception.connet.response.status.BaseErrorCode.NO_VALID_FILE_NAME;

@Component
public class FileUtil {

    public static boolean isValidFile(MultipartFile file){
        return file != null && !file.getOriginalFilename().isBlank();
    }

    public static String getUniqueFileName(String originalFileName){
        int extensionIndex = originalFileName.lastIndexOf(".");
        if(extensionIndex == -1){
            throw new FileException(NO_VALID_FILE_NAME);
        }

        String extension = originalFileName.substring(extensionIndex);

        return UUID.randomUUID() + extension;
    }
}
