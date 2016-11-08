/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2016 wcm.io
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
package io.wcm.devops.conga.generator.plugins.urlfile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;

import io.wcm.devops.conga.generator.spi.UrlFilePlugin;
import io.wcm.devops.conga.generator.spi.context.UrlFilePluginContext;
import io.wcm.devops.conga.generator.util.FileUtil;

/**
 * Get file from local filesystem.
 */
public class FilesystemUrlFilePlugin implements UrlFilePlugin {

  /**
   * Plugin name
   */
  public static final String NAME = "filesystem";

  private static final String PREFIX = "file:";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean accepts(String url, UrlFilePluginContext context) {
    return StringUtils.startsWith(url, PREFIX);
  }

  @Override
  public InputStream getFile(String url, UrlFilePluginContext context) throws IOException {
    File file;
    if (StringUtils.startsWith(url, PREFIX)) {
      String absoultePath = StringUtils.substringAfter(url, PREFIX);
      file = new File(absoultePath);
    }
    else {
      String revaltivePath = url;
      file = new File(context.getBaseDir(), revaltivePath);
    }
    if (!file.exists()) {
      throw new FileNotFoundException("File does not exist: " + FileUtil.getCanonicalPath(file));
    }
    return new FileInputStream(file);
  }

}
