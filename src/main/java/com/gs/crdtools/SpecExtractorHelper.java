package com.gs.crdtools;

import com.resare.nryaml.YAMLMapping;
import com.resare.nryaml.YAMLSequence;
import com.resare.nryaml.YAMLValue;
import io.vavr.Tuple;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;

/**
 * A helper class to extract the openAPI specs from a given yaml file in the format of a string.
 */
public class SpecExtractorHelper {

    /**
     * Pull out the openapi specs according to the "kind" attribute in the inputted yaml file.
     * @param allTheYamls A list containing the previously extracted crds.
     * @return The extracted specs.
     */
     static HashMap<Object, HashMap<String, Object>> pullOpenapiSpecs(List<YAMLValue> allTheYamls) {
         return HashMap.ofEntries(allTheYamls.map(y -> {
             var kind = y.asMapping().get("spec")
                     .asMapping().get("names")
                     .asMapping().get("kind")
                     .asString();

             var group = y.asMapping().get("spec").asMapping().get("group").asString();

             // Get the latest version of the current CDR and read its openAPIV3Schema
             var latestVersion = getLatestVersion(y.asMapping().get("spec").asMapping().get("versions").asSequence());
             var version = latestVersion.asMapping().get("name").asString();
             var schema = latestVersion.get("schema").asMapping().get("openAPIV3Schema").asMapping();

             //noinspection unchecked
             var properties = (java.util.Map)schema.get("properties").asMapping().toBareObject();

             properties.put("metadata", makeSpec("V1ObjectMeta"));
             if (!properties.containsKey("kind")) {
                 properties.put("kind", makeSpec("string"));
             }
             if (!properties.containsKey("apiVersion")) {
                 properties.put("apiVersion", makeSpec("string"));
             }

             var bareSchema = io.vavr.collection.HashMap.ofAll((java.util.Map<String,Object>)schema.toBareObject());
             bareSchema = bareSchema.put("properties", properties);

             var additionalInfo = List.of(
                     group,
                     version,
                     kind
             );

             return Tuple.of(additionalInfo, HashMap.of("type", (Object) "object").merge(bareSchema));
        }));
    }

    /**
     * Get the latest version of the current CDR.
     * @param versions A list of all the versions (in the format of maps) of the current CDR.
     * @return The latest version of the current CDR.
     */
    static YAMLMapping getLatestVersion(YAMLSequence versions) {
        for (var v : versions.stream().map(YAMLValue::asMapping).toList()) {
            if (v.asMapping().get("storage").asBoolean()) {
                return v;
            }
        }

        throw new IllegalStateException("No version found with storage=true");
    }

    private static java.util.Map<String, String> makeSpec(String qualifiedType) {
         return HashMap.of("type", qualifiedType).toJavaMap();
    }

}
