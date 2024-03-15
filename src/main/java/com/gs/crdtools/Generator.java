package com.gs.crdtools;

import com.nryaml.YAMLMapping;
import com.nryaml.YAMLUtil;
import com.nryaml.YAMLValue;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.gs.crdtools.SourceGenFromSpec.toZip;

@CommandLine.Command(name = "generator", version = "crdtools 1.0", mixinStandardHelpOptions = true,
        description = "generate one or more compile-able java classes for each set of CRDs")
public class Generator implements Runnable {

    @CommandLine.Option(names = {"-p", "--package"}, description = "Model package")
    String targetPackage = "com.gs.crdtools.generated";

    @CommandLine.Option(names = {"-o", "--out", "--output"}, description = "Output file")
    String output = "generated.srcjar";

    @CommandLine.Option(
            names = {"--spec-output-path"},
            description = "Output the intermediate openapi spec to this path")
    private String specOutput;

    @CommandLine.Parameters(arity = "1..*", description = "One or more CRD(s) input")
    String[] inputCrds;

    public void run() {
        try {
            Generator.generateAndZip(inputCrds, targetPackage, output, Option.of(specOutput));
        } catch (IOException e) {
            throw new RuntimeException("An unexpected error occurred. " +
                    "Make sure to follow the usage instructions for this tool.");
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Generator()).execute(args);
        System.exit(exitCode);
    }

    static void generateAndZip(
            String[] inputCrds,
            String packageName,
            String outputPath,
            Option<String> specOutputPath) throws IOException {
        var crdsList = parseCrds(List.of(inputCrds).map(Path::of));
        var result = generate(crdsList, packageName, specOutputPath);
        toZip(result, Path.of(outputPath));
    }

    static List<YAMLMapping> parseCrds(List<Path> inputs) {
        return inputs.flatMap(YAMLUtil::allFromPath).map(YAMLValue::asMapping);
    }

    static Map<Path, String> generate(List<YAMLMapping> crds, String packageName, Option<String> specOutputPath) throws IOException {
        var spec = SpecExtractorHelper.createSpec(crds);
        if (specOutputPath.isDefined()) {
            Files.writeString(Path.of(specOutputPath.get()), spec.openapiSpec());
        }
        return SourceGenFromSpec.generateSource(spec, packageName);
    }

    String getPackage() {
        return this.targetPackage;
    }

    String getOutput() {
        return this.output;
    }

    String[] getInputCdrs() {
        return this.inputCrds;
    }
}
