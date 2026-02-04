package io.micronaut.jsonschema.test;

import io.micronaut.core.io.ResourceLoader;
import io.micronaut.json.JsonMapper;
import io.micronaut.jsonschema.generator.user.User;
import io.micronaut.jsonschema.utils.DefaultJsonSchemaClassPathResourceLoader;
import io.micronaut.jsonschema.utils.JsonSchemaClassPathResourceLoader;
import io.micronaut.jsonschema.utils.JsonSchemaConfiguration;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class SchemaGenerationTest {
    @Inject
    ResourceLoader resourceLoader;

    @Inject
    JsonSchemaClassPathResourceLoader jsonSchemaResourceLoader;

    @ParameterizedTest
    @ValueSource(strings = {"llama", "bird", "possum", "possum-environment", "red-winged-blackbird", "salamander" })
    void buildJsonSchema(String name) {
        assertTrue(resourceLoader.getResource("META-INF/schemas/" + name + ".schema.json").isPresent());
    }

    @Test
    void llamaSchema(JsonMapper jsonMapper) throws IOException {
        Optional<InputStream> expectedOptional = resourceLoader.getResourceAsStream("expected-llama.schema.json");
        assertTrue(expectedOptional.isPresent());
        String expected = new String(expectedOptional.get().readAllBytes(), StandardCharsets.UTF_8);
        Optional<InputStream> resultOptional = resourceLoader.getResourceAsStream("META-INF/schemas/llama.schema.json");
        assertTrue(resultOptional.isPresent());
        String result = new String(resultOptional.get().readAllBytes(), StandardCharsets.UTF_8);
        assertEquals(jsonMapper.readValue(expected, Map.class), jsonMapper.readValue(result, Map.class));
    }

    @Test
    void resourcesGenerator() throws IOException {
//        assertEquals("ada", jsonSchemaResourceLoader.jsonSchemaStringForClass(User.class).get());
        Optional<String> schemaPathOptional = DefaultJsonSchemaClassPathResourceLoader
            .jsonSchemaPath(
                User.class,
                JsonSchemaConfiguration.DEFAULT_OUTPUT_LOCATION
            );
        assertTrue(schemaPathOptional.isPresent());
        String schemaPath = schemaPathOptional.get();
        Optional<InputStream> resultOptional = resourceLoader.getResourceAsStream(schemaPath);
        assertTrue(resultOptional.isPresent());
        String result = new String(resultOptional.get().readAllBytes(), StandardCharsets.UTF_8);
        assertEquals("ada", result);
    }
}
