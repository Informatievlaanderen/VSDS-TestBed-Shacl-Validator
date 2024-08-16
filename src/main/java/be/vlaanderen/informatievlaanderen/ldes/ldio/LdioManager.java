package be.vlaanderen.informatievlaanderen.ldes.ldio;


import be.vlaanderen.informatievlaanderen.ldes.http.Request;
import be.vlaanderen.informatievlaanderen.ldes.http.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.ValidationPipelineFactory;
import org.apache.http.entity.ContentType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Service
public class LdioManager {
    private static final String PIPELINE_NAME = "validation-pipeline";
    private final RequestExecutor requestExecutor;
    private final LdioConfigProperties ldioConfigProperties;

    public LdioManager(RequestExecutor requestExecutor, LdioConfigProperties ldioConfigProperties) {
        this.requestExecutor = requestExecutor;
	    this.ldioConfigProperties = ldioConfigProperties;
    }


    public void initPipeline(String serverUrl) throws IOException {
        String ldioAdminPipelineUrl = ldioConfigProperties.getLdioAdminPipelineUrl();

        final String json = new ValidationPipelineFactory(serverUrl, ldioConfigProperties.getSparqlHost()).createValidationPipelineAsJson();

        requestExecutor.execute(new Request(ldioAdminPipelineUrl, json, RequestMethod.POST, ContentType.APPLICATION_JSON));
        waitForReplication(ldioAdminPipelineUrl);

    }

    private void waitForReplication(String ldioUrl) throws IOException {
        int seconds = 5;
        boolean replicating = true;
        while (replicating) {
            try {
                Thread.sleep(seconds * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            if (isPipelineFinished(ldioUrl)) {
                requestExecutor.execute(
                        new Request(ldioUrl + "/" + PIPELINE_NAME, RequestMethod.DELETE)
                );
                replicating = false;
            }
        }
    }


//    Checks if the pipeline is finished replicating
    private boolean isPipelineFinished(String ldioUrl) throws IOException {
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
