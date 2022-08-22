package com.gs.crdtools;

import io.kubernetes.client.common.KubernetesObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ToYaml {

    public static <T extends KubernetesObject> String generateYamlString(Class<T> clazz, String name) {
        var kccObject = Yamlifier.base(clazz, name);
        var kccObjectBytes = Yamlifier.yamlify(kccObject);

        return new String(kccObjectBytes);
    }

    public static void generateYamlFile(String outputPath, byte[] kccBytes) throws IOException {
        File outputFile = new File(outputPath);
        Files.write(outputFile.toPath(), kccBytes);
    }
}
