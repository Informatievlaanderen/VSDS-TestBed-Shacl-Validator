package be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects;

import com.gitb.core.ConfigurationType;
import com.gitb.core.TypedParameter;
import com.gitb.core.UsageEnumeration;

public class ParameterDefinition {
	private final String name;
	private final String type;
	private final boolean required;
	private final String description;

	public ParameterDefinition(String name, String type, boolean required, String description) {
		this.name = name;
		this.type = type;
		this.required = required;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public TypedParameter convertToTypedParameter() {
		final TypedParameter typedParameter = new TypedParameter();
		typedParameter.setName(name);
		typedParameter.setType(type);
		typedParameter.setUse(required ? UsageEnumeration.R : UsageEnumeration.O);
		typedParameter.setDesc(description);
		typedParameter.setKind(ConfigurationType.SIMPLE);
		return typedParameter;
	}
}
