package top.ivan.simple.gateway.core.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.stream.Stream;

/**
 * @author Ivan
 * @since 2021/10/09 17:10
 */
@Slf4j
public class PropertiesUtil {
    private PropertiesUtil() {
    }

    private static final YamlPropertyLoaderFactory factory = new YamlPropertyLoaderFactory();


    public static <T> T loadResource(String resourceLocation, String resourceName, Class<T> type) throws IOException {
        Path basePath = Paths.get(resourceLocation);
        Resource resource = null;
        if (Files.exists(basePath)) {
            try (Stream<Path> stream = Files.find(Paths.get(resourceLocation), 1, (p, attr) ->
                    p.toFile().getName().matches(resourceName + "(\\.(yml|yaml|properties))*"))) {
                resource = stream.findFirst().map(f -> new FileSystemResource(f.toFile().getAbsolutePath())).orElse(null);
            }
        }
        if (resource == null) {
            String prefix = resourceLocation + "/" + resourceName;
            resource = new ClassPathResource(prefix);
            if (!resource.exists()) {
                resource = new ClassPathResource(prefix + ".properties");
                if (!resource.exists()) {
                    resource = new ClassPathResource(prefix + ".yml");
                    if (!resource.exists()) {
                        resource = new ClassPathResource(prefix + ".yaml");
                        if (!resource.exists()) {
                            throw new IOException("resource '" + resourceName + "' not found");
                        }
                    }
                }
            }
        }

        File file = resource.getFile();
        String fn = file.getName();
        if (fn.endsWith("properties")) {
            return loadFromProperties(file, type);
        } else if (fn.endsWith("yml") || fn.endsWith("yaml")) {
            return loadFromYaml(file, type);
        }
        throw new IOException("unsupported resource");
    }

    public static <T> T loadFromYaml(File file, Class<T> type) throws IOException {
        log.info("load resource from '{}'", file.getAbsolutePath());

        PropertySource<?> ps = factory.createPropertySource("", new EncodedResource(new FileSystemResource(file)));
        Binder binder = new Binder(ConfigurationPropertySources.from(ps));
        return binder.bind("", type).get();
    }

    public static <T> T loadFromProperties(File file, Class<T> type) throws IOException {
        log.info("load resource from '{}'", file.getAbsolutePath());

        try (FileReader reader = new FileReader(file)) {
            Properties prop = new Properties();
            prop.load(reader);
            Binder binder = new Binder(ConfigurationPropertySources.from(new PropertiesPropertySource(file.getName(), prop)));
            return binder.bind("", type).get();
        }
    }

}
