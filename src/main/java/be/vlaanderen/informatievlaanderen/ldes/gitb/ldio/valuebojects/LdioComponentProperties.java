package be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.valuebojects;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record LdioComponentProperties(@JsonProperty("config") Map<String, Object> properties) {
}
