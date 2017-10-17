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
package io.wcm.devops.conga.model.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.lang3.CharEncoding;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import io.wcm.devops.conga.model.role.Role;
import io.wcm.devops.conga.model.role.RoleFile;
import io.wcm.devops.conga.model.role.RoleFile.RoleFileVariantMetadata;
import io.wcm.devops.conga.model.role.RoleInherit;
import io.wcm.devops.conga.model.role.RoleVariant;
import io.wcm.devops.conga.model.shared.LineEndings;

public class RoleReaderTest {

  private Role role;

  @Before
  public void setUp() throws IOException {
    RoleReader reader = new RoleReader();
    try (InputStream is = getClass().getResourceAsStream("/role.yaml")) {
      role = reader.read(is);
    }
    assertNotNull(role);
  }

  @Test
  public void testRole() {

    List<RoleInherit> inherits = role.getInherits();
    assertEquals(2, inherits.size());

    List<RoleVariant> variants = role.getVariants();
    assertEquals(2, variants.size());

    assertEquals("tomcat-services", role.getTemplateDir());

    List<RoleFile> files = role.getFiles();
    assertEquals(6, files.size());

    assertEquals(ImmutableMap.of(
        "var1", "value1",
        "tomcat", ImmutableMap.of("port", 8080, "path", "/path/to/tomcat"),
        "jvm", ImmutableMap.of("heapspace", ImmutableMap.of("min", "512m", "max", "2048m"), "permgenspace", ImmutableMap.of("max", "256m")),
        "topologyConnectors", ImmutableList.of("http://localhost:8080/libs/sling/topology/connector")
        ), role.getConfig());
  }

  @Test
  public void testInherit() {
    RoleInherit inherit = role.getInherits().get(0);

    assertEquals("superRole1", inherit.getRole());
  }

  @Test
  public void testVariant() {
    RoleVariant variant = role.getVariants().get(0);

    assertEquals("services", variant.getVariant());
    assertEquals(ImmutableMap.of("var1", "value1_service"), variant.getConfig());
  }

  @Test
  public void testFile() {
    RoleFile file = role.getFiles().get(0);

    assertEquals("systemconfig-importer.txt", file.getFile());
    assertEquals("data/deploy", file.getDir());
    assertEquals("systemconfig-importer.txt.hbs", file.getTemplate());
    assertEquals(ImmutableList.of("importer", "variant2*", "variant3"), file.getVariants());
    assertEquals("${abc}", file.getCondition());
    assertEquals(ImmutableList.of("sling-provisioning-model"), file.getValidators());
    assertEquals(ImmutableMap.of("option1", "value1"), file.getValidatorOptions());
    assertEquals(ImmutableList.of("osgi-config-generator"), file.getPostProcessors());
    assertEquals(ImmutableMap.of("option2", "value2"), file.getPostProcessorOptions());
    assertEquals("sling-provisioning", file.getFileHeader());
    assertEquals("none", file.getMultiply());
    assertEquals(CharEncoding.UTF_8, file.getCharset());
    assertEquals(LineEndings.windows, file.getLineEndings());
    assertEquals("none", file.getEscapingStrategy());

    RoleFile vhostFile = role.getFiles().get(4);
    assertEquals("tenant", vhostFile.getMultiply());
    assertEquals(ImmutableMap.of("roles", ImmutableList.of("website")), vhostFile.getMultiplyOptions());
    assertEquals(LineEndings.unix, vhostFile.getLineEndings());

    List<RoleFileVariantMetadata> variantsMetadata = file.getVariantsMetadata();
    assertEquals("importer", variantsMetadata.get(0).getVariant());
    assertFalse(variantsMetadata.get(0).isMandatory());
    assertEquals("variant2", variantsMetadata.get(1).getVariant());
    assertTrue(variantsMetadata.get(1).isMandatory());
    assertEquals("variant3", variantsMetadata.get(2).getVariant());
    assertFalse(variantsMetadata.get(2).isMandatory());
  }

  @Test
  public void testDownload() {
    RoleFile file = role.getFiles().get(5);

    assertEquals("download", file.getDir());
    assertEquals("classpath://xyz.txt", file.getUrl());
    assertEquals(ImmutableMap.of("modelOption1", "value1"), file.getModelOptions());
  }

}
