package app.urbanist.cloud;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.internal.LinkedTreeMap;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

@Component
public class CloudImageUploader {
    private static final String PICTURE_FOLDER_ID = "1651488059";

    private static final String QUERY_PATH_SEPARATOR = "?";

    private static final String QUERY_PARAMETER_SEPARATOR = "&";

    private static final String AUTH_PARAMETER = "auth=";

    private static final String FOLDER_ID_PARAMETER = "folderid=";

    private static final String FILE_NAME_PARAMETER = "filename=";

    private static final String UPLOAD_FILE_URL
            = "https://api.pcloud.com/uploadfile";

    private final HttpRequestExecutor httpRequestExecutor;

    private final CloudAuthorizationService cloudAuthorizationService;

    public CloudImageUploader(HttpRequestExecutor httpRequestExecutor, CloudAuthorizationService cloudAuthorizationService) {
        this.httpRequestExecutor = httpRequestExecutor;
        this.cloudAuthorizationService = cloudAuthorizationService;
    }

    public String uploadFile(MultipartFile file) throws IOException {

        Gson gson = new Gson();

        String response = this.httpRequestExecutor.executePostRequest(
                (UPLOAD_FILE_URL
                        + QUERY_PATH_SEPARATOR
                        + FOLDER_ID_PARAMETER
                        + PICTURE_FOLDER_ID
                        + QUERY_PARAMETER_SEPARATOR
                        + FILE_NAME_PARAMETER
                        + file.getOriginalFilename()
                        + QUERY_PARAMETER_SEPARATOR
                        + AUTH_PARAMETER
                        + this.cloudAuthorizationService.getAccessToken())
                , file.getContentType()
                , file.getBytes()
        ).body().string();

        Map<String, Object> fileUploadData = gson.fromJson(response, Map.class);

        ArrayList<LinkedTreeMap> metadata = (ArrayList<LinkedTreeMap>) fileUploadData.get("metadata");

        Map ltp = metadata.get(0);

        return (String) ltp.get("id");
    }
}
