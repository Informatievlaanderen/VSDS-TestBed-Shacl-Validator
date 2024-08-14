package be.vlaanderen.informatievlaanderen.ldes.ldio;


import be.vlaanderen.informatievlaanderen.ldes.http.Request;
import be.vlaanderen.informatievlaanderen.ldes.http.RequestExecutor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.*;

@Service
public class LdioManager {
    private static final String PIPELINE_FILE_PATH = "src/main/resources/ldio-pipeline.json";
    private static final String PIPELINE_NAME = "validation-pipeline";
    private final RequestExecutor requestExecutor;

    public LdioManager(RequestExecutor requestExecutor) {
        this.requestExecutor = requestExecutor;
    }


    public void initPipeline(String serverUrl) throws IOException {
        String ldioUrl = "http://localhost:8082" + "/admin/api/v1/pipeline";

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(new File(PIPELINE_FILE_PATH));
        setUrl(jsonNode, serverUrl);
        setRepository(jsonNode, serverUrl);
        setVersionOf(jsonNode, serverUrl);

        requestExecutor.execute(new Request(ldioUrl, jsonNode.toString(), RequestMethod.POST, ContentType.APPLICATION_JSON));
        waitForReplication(ldioUrl);

    }

    private void setUrl(JsonNode jsonNode, String serverUrl) {
        ((ObjectNode) jsonNode.path("input").path("config")).replace("urls", new TextNode(serverUrl));
    }
    private void setVersionOf(JsonNode jsonNode, String serverUrl) {
        ObjectNode config = ((ObjectNode) jsonNode.path("transformers").get(0).get("config"));
        config.replace("versionOf-property", new TextNode(serverUrl));
    }

    private void setRepository(JsonNode jsonNode, String serverUrl) {
        ObjectNode config = ((ObjectNode) jsonNode.path("outputs").get(0).get("config"));
        config.replace("sparql-host", new TextNode(serverUrl));
        config.replace("repository-id", new TextNode(serverUrl));
    }

    private void waitForReplication(String ldioUrl) throws IOException {
        int seconds = 5;
        boolean replicating = true;
        while (replicating) {
            try {
                Thread.sleep(seconds * 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            if (getIsPipelineFinished(ldioUrl)) {
                requestExecutor.execute(
                        new Request(ldioUrl + "/" + PIPELINE_NAME, RequestMethod.DELETE)
                );
                replicating = false;
            }
        }
    }


//    Checks if the pipeline is finished replicating
    private boolean getIsPipelineFinished(String ldioUrl) throws IOException {
        InputStream in = requestExecutor.execute(
                new Request(ldioUrl + "/ldes-client/" + PIPELINE_NAME + "/status", "",
                        RequestMethod.GET, ContentType.DEFAULT_TEXT)).getContent();
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(in));
        StringBuilder responseStrBuilder = new StringBuilder();

        String inputStr;
        while ((inputStr = streamReader.readLine()) != null)
            responseStrBuilder.append(inputStr);
        String responseStr = responseStrBuilder.toString().toUpperCase();
        if (!responseStr.contains("REPLICATING")) {
            if (responseStr.contains("SYNCHRONISING") || responseStr.contains("COMPLETED")) {
                return true;
            }
            else {
                throw new RuntimeException();
            }
        }
        else {
            return false;
        }
    }
}
