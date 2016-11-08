/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2015 wcm.io
 * %%
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
 * #L%
 */
package io.wcm.devops.conga.generator.spi.context;

import java.io.File;

/**
 * Context for {@link io.wcm.devops.conga.generator.spi.UrlFilePlugin}.
 */
public final class UrlFilePluginContext extends AbstractPluginContext<UrlFilePluginContext> {

  private File baseDir;
  private Object containerContext;

  /**
   * @return Base directory for resolving relative files in filesystem
   */
  public File getBaseDir() {
    return baseDir;
  }

  /**
   * @param value Base directory for resolving relative files in filesystem
   * @return this
   */
  public UrlFilePluginContext baseDir(File value) {
    baseDir = value;
    return this;
  }

  /**
   * @return Container-specific context object
   */
  public Object getContainerContext() {
    return containerContext;
  }

  /**
   * @param value Container-specific context object
   * @return this
   */
  public UrlFilePluginContext containerContext(Object value) {
    containerContext = value;
    return this;
  }

}
