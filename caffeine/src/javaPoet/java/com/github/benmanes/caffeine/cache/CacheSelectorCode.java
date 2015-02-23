/*
 * Copyright 2015 Ben Manes. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.benmanes.caffeine.cache;

import com.squareup.javapoet.CodeBlock;

/**
 * @author ben.manes@gmail.com (Ben Manes)
 */
public final class CacheSelectorCode {
  private final CodeBlock.Builder name;

  private CacheSelectorCode() {
    name = CodeBlock.builder()
        .addStatement("$T sb = new $T(64)", StringBuilder.class, StringBuilder.class);
  }

  private CacheSelectorCode keys() {
    name.beginControlFlow("if (builder.isStrongKeys())")
            .addStatement("sb.append(\"STRONG_KEYS\")")
        .nextControlFlow("else")
            .addStatement("sb.append(\"WEAK_KEYS\")")
        .endControlFlow();
    return this;
  }

  private CacheSelectorCode values() {
    name.beginControlFlow("if (builder.isStrongValues())")
            .addStatement("sb.append(\"_STRONG_VALUES\")")
        .nextControlFlow("else")
            .addStatement("sb.append(\"_INFIRM_VALUES\")")
        .endControlFlow();
    return this;
  }

  private CacheSelectorCode cacheLoader() {
    name.beginControlFlow("if (cacheLoader != null)")
            .addStatement("sb.append(\"_LOADING\")")
        .endControlFlow();
    return this;
  }

  private CacheSelectorCode removalListener() {
    name.beginControlFlow("if (builder.removalListener != null)")
            .addStatement("sb.append(\"_LISTENING\")")
        .endControlFlow();
    return this;
  }

  private CacheSelectorCode executor() {
    name.beginControlFlow("if (builder.executor != null)")
            .addStatement("sb.append(\"_EXECUTOR\")")
        .endControlFlow();
    return this;
  }

  private CodeBlock build() {
    return name
        .addStatement("LocalCacheFactory factory = valueOf(sb.toString())")
        .addStatement("return factory.create(builder, cacheLoader, async)")
        .build();
  }

  public static CodeBlock get() {
    return new CacheSelectorCode()
        .keys()
        .values()
        .cacheLoader()
        .removalListener()
        .executor()
        .build();
  }
}