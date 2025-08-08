package hello.upload.file;

import hello.upload.domain.UploadFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component

public class FileStore {
    @Value("${file.dir}")
    public String fileDir;

    public String getFullPath(String filename){
        return fileDir + filename;

    }

//파일 여러개를 업로드할때
    public  List<UploadFile> storeFiles(List<MultipartFile>multipartFiles) throws IOException {
        List<UploadFile> storeFileResult=new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if(!multipartFile.isEmpty()){
                UploadFile uploadFile = storeFile(multipartFile);
                storeFileResult.add(uploadFile);
            }
        }
    return storeFileResult;
    }


//파일 1개만 업로드 할때
    public UploadFile storeFile(MultipartFile multipartFile) throws IOException {
        if(multipartFile.isEmpty()){
            return null;
        }
        String originalFilename= multipartFile.getOriginalFilename();
        //    서버에 저장하는 파일명
        String storeFileName = createStoreFileName(originalFilename);
        multipartFile.transferTo(new File(getFullPath(storeFileName)));
        return new UploadFile(originalFilename, storeFileName);
    }

    private String createStoreFileName(String originalFilename) {
        String uuid = UUID.randomUUID().toString();

//    만약 image.png 일 경우
//    확장자를 꺼내는 방법
//    ctrl + alt + M 메소드 변환
        String ext = extractExt(originalFilename);
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }


}
