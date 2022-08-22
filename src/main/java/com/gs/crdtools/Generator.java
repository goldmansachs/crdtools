package com.gs.crdtools;

import com.resare.nryaml.YAMLMapping;
import com.resare.nryaml.YAMLUtil;
import com.resare.nryaml.YAMLValue;
import io.vavr.collection.List;
import io.vavr.collection.Map;

import java.io.IOException;
import java.nio.file.Path;

import static com.gs.crdtools.SourceGenFromSpec.toZip;

public class Generator {
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            throw new IllegalArgumentException("Usage: Generator GENERATED_SRC_ZIP CRD_YAML [CRD_YAML...]");
        }
        var crds = parseCrds(List.of(args).subSequence(1).map(Path::of));
        var result = generate(crds);
        toZip(result, Path.of(args[0]));
    }

    static List<YAMLMapping> parseCrds(List<Path> inputs) {
        return inputs.flatMap(YAMLUtil::allFromPath).map(YAMLValue::asMapping);
    }

    static Map<Path, String> generate(List<YAMLMapping> crds) throws IOException {
        var spec = SpecExtractorHelper.createSpec(crds);
        return SourceGenFromSpec.generateSource(spec);
    }

}
