/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.model.internal.manage.schema.store;

import net.jcip.annotations.ThreadSafe;
import org.gradle.model.internal.manage.schema.ModelSchema;

import java.util.Collections;
import java.util.List;

@ThreadSafe
public class ModelSchemaExtractionResult<T> {

    private ModelSchema<T> schema;

    private List<? extends ModelSchemaExtractionContext> dependencies;

    public ModelSchemaExtractionResult(ModelSchema<T> schema) {
        this(schema, Collections.<ModelSchemaExtractionContext>emptyList());
    }

    public ModelSchemaExtractionResult(ModelSchema<T> schema, List<? extends ModelSchemaExtractionContext> dependencies) {
        this.schema = schema;
        this.dependencies = dependencies;
    }
    public ModelSchema<T> getSchema() {
        return schema;
    }

    public List<? extends ModelSchemaExtractionContext> getDependencies() {
        return dependencies;
    }
}