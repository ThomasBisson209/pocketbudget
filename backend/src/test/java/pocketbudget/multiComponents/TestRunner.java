package pocketbudget.multiComponents;

import org.junit.platform.suite.api.*;

@Suite
@IncludeEngines("cucumber")
@ConfigurationParameter(key = "cucumber.features", value = "src/test/resources/pocketbudget/multiComponents/features")
@ConfigurationParameter(key = "cucumber.glue", value = "pocketbudget.multiComponents.stepDefinitions")
@ConfigurationParameter(key = "cucumber.plugin", value = "pretty")
public class TestRunner {
}
