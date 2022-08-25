package com.gs.crdtools;

import io.swagger.codegen.v3.templates.CodegenTemplateLoader;

import java.nio.file.Path;

/**
 * A CodegenTemplateLoader that for each attempt to resolve a template will check
 * for an overloaded version of the template in the classpath and return a reference
 * to that if it exists, otherwise fall back to the original behaviour.
 */
class OverrideTemplateLoader extends CodegenTemplateLoader {
    private static final String OVERRIDE_CLASSPATH_PREFIX = "swaggerTemplateOverloads";

    @Override
    public String resolve(String uri) {
        if (!uri.endsWith(this.getSuffix())) {
            uri = uri + this.getSuffix();
        }

        var overridePath = Path.of(OVERRIDE_CLASSPATH_PREFIX, uri).toString();
        var resource = this.getClass().getClassLoader().getResource(overridePath);
        if (resource != null) {
            return '/' + overridePath;
        }

        return super.resolve(uri);
    }

}
