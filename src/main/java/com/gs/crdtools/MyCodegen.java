package com.gs.crdtools;

import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.swagger.codegen.v3.CodegenModel;
import io.swagger.codegen.v3.generators.java.SpringCodegen;
import io.swagger.v3.oas.models.media.Schema;
import io.vavr.collection.List;

/**
 * This class defines the specific implementation of the CodeGenerator.
 */
public class MyCodegen extends SpringCodegen {

    /**
     * Create a new instance of a model from existing schemas.
     * @param name The model's name.
     * @param schema The model's schema.
     * @param allSchemas The model's set of schemas.
     * @return The generated model.
     */
    @Override
    public io.swagger.codegen.v3.CodegenModel fromModel(String name, Schema schema,
                                                        java.util.Map<String, Schema> allSchemas) {
        var ret = super.fromModel(name, schema, allSchemas);

        ret.imports.add(BaseObject.class.getSimpleName());

        boolean hasMetadata = List.ofAll(ret.requiredVars)
                .appendAll(ret.optionalVars)
                .find(prop -> prop.name.equals("metadata") && prop.datatype.equals(
                        V1ObjectMeta.class.getCanonicalName()))
                .isDefined();

        if (hasMetadata) {
            generateMetaModel(ret);
        }

        return ret;
    }

    /**
     * Configure CodegenModel with the previously fetched metadata from a given model.
     * @param model The model whose metadata is to be used.
     */
    private void generateMetaModel(CodegenModel model) {
        var metaModel = new CodegenModel();
        metaModel.name = BaseObject.class.getSimpleName();
        metaModel.classname = BaseObject.class.getCanonicalName();
        model.parentModel = metaModel;
        model.parent = BaseObject.class.getSimpleName();
    }

    /**
     * Take a property name and return the getter name for it.
     * This is due to a hateful combination of java primitives and java properties.
     * All the boolean properties are generated as 'Boolean' not 'boolean',
     * since they are all optional. Swagger generates a 'Boolean' getter as 'is<Foo>'.
     * Java properties want all non-'boolean' property getters to be 'get<Foo>'
     * The Yamlifier thus can't find a getter, which means it's not a property it can write.
     * This is fixed in the OpenAPI generator but not the Swagger generator.
     * @param name The property name.
     * @return The getter name.
     */
    @Override
    public String toBooleanGetter(String name) {
        return toGetter(name);
    }
}

