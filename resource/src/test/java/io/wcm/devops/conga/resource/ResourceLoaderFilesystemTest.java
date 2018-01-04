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
package io.wcm.devops.conga.resource;

import static io.wcm.devops.conga.resource.ResourceLoader.FILE_PREFIX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class ResourceLoaderFilesystemTest {

  private static final String ROOT = "src/test/resources/test-files";

  private ResourceLoader underTest;

  @Before
  public void setUp() {
    underTest = new ResourceLoader();
  }

  @Test
  public void testResource() throws Exception {
    Resource resource = underTest.getResource(FILE_PREFIX + ROOT + "/folder1/file1.txt");

    assertTrue(resource.exists());
    assertEquals("file1.txt", resource.getName());
    assertEquals("txt", resource.getFileExtension());
    assertEquals(ROOT + "/folder1/file1.txt", unifySlashes(resource.getPath()));
    assertTrue("Canonical path " + unifySlashes(resource.getCanonicalPath()) + " does not end with /" + ROOT + "/folder1/file1.txt",
        StringUtils.endsWith(unifySlashes(resource.getCanonicalPath()), "/" + ROOT + "/folder1/file1.txt"));

    assertTrue(resource.getLastModified() > 0);

    try (InputStream is = resource.getInputStream()) {
      assertEquals("File 1", IOUtils.toString(is, StandardCharsets.UTF_8));
    }
  }

  @Test
  public void testResourceCollection() throws Exception {
    ResourceCollection col = underTest.getResourceCollection(FILE_PREFIX + ROOT + "/folder1");

    assertTrue(col.exists());
    assertEquals("folder1", col.getName());
    assertEquals(ROOT + "/folder1", unifySlashes(col.getPath()));
    assertTrue("Canonical path " + unifySlashes(col.getCanonicalPath()) + " does not end with /" + ROOT + "/folder1",
        StringUtils.endsWith(unifySlashes(col.getCanonicalPath()), "/" + ROOT + "/folder1"));

    List<Resource> resources = ImmutableList.copyOf(col.getResources());
    assertEquals(2, resources.size());
    assertEquals("file1.txt", resources.get(0).getName());
    assertEquals("file2.txt", resources.get(1).getName());

    List<ResourceCollection> resourceCollections = ImmutableList.copyOf(col.getResourceCollections());
    assertEquals(1, resourceCollections.size());
    assertEquals("folder2", resourceCollections.get(0).getName());

    List<Resource> folder2Resources = ImmutableList.copyOf(resourceCollections.get(0).getResources());
    assertEquals(1, folder2Resources.size());
    assertEquals("file3.txt", folder2Resources.get(0).getName());
  }

  @Test
  public void testResourceAutoDetect() throws Exception {
    Resource resource = underTest.getResource(ROOT + "/folder1/file1.txt");
    assertTrue(resource.exists());
    assertEquals("file1.txt", resource.getName());
  }

  @Test
  public void testNonExistingResource() throws Exception {
    Resource resource = underTest.getResource(FILE_PREFIX + ROOT + "/folder1/invalid.txt");
    assertFalse(resource.exists());
    assertTrue(resource instanceof FileResourceImpl);
  }

  @Test
  public void testNonExistingResourceAutoDetect() throws Exception {
    Resource resource = underTest.getResource(ROOT + "/folder1/invalid.txt");
    assertFalse(resource.exists());
    assertTrue(resource instanceof FileResourceImpl);
  }

  @Test
  public void testNonExistingResourceCollection() throws Exception {
    ResourceCollection col = underTest.getResourceCollection(FILE_PREFIX + ROOT + "/invalidFolder");
    assertFalse(col.exists());
    assertEquals(ImmutableList.of(), ImmutableList.copyOf(col.getResources()));
    assertEquals(ImmutableList.of(), ImmutableList.copyOf(col.getResourceCollections()));
  }

  @Test
  public void testResourceByParentFolder() throws Exception {
    ResourceCollection col = underTest.getResourceCollection(FILE_PREFIX + ROOT + "/folder1");
    Resource resource = underTest.getResource(col, "folder2/file3.txt");
    assertTrue(resource.exists());
    assertEquals("file3.txt", resource.getName());
  }

  @Test
  public void testResourceCollectionByParentFolder() throws Exception {
    ResourceCollection colParent = underTest.getResourceCollection(FILE_PREFIX + ROOT + "/folder1");
    ResourceCollection col = underTest.getResourceCollection(colParent, "folder2");
    assertTrue(col.exists());
    assertEquals("folder2", col.getName());
  }

  private String unifySlashes(String path) {
    return StringUtils.replace(path, "\\", "/");
  }

}
