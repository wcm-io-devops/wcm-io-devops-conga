/*-
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2015 - 2018 wcm.io DevOps
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
package io.wcm.devops.conga.generator.plugins.handlebars.helper;

import com.google.common.collect.ImmutableList;
import io.wcm.devops.conga.generator.spi.handlebars.HelperPlugin;
import io.wcm.devops.conga.generator.util.PluginManagerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.wcm.devops.conga.generator.plugins.handlebars.helper.TestUtils.assertHelper;

public class ReplaceHelperTest {

  private HelperPlugin<Object> helper;

  @SuppressWarnings("unchecked")
  @BeforeEach
  public void setUp() {
    helper = new PluginManagerImpl().get(ReplaceHelper.NAME, HelperPlugin.class);
  }

  @Test
  public void testApply() throws Exception {
    assertHelper("subdomain_domain_tld", helper, "subdomain.domain.tld", new MockOptions(".", "_"));
  }

}
