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
package io.wcm.devops.conga.model.util;

import static io.wcm.devops.conga.model.util.MapMerger.LIST_MERGE_ENTRY;
import static io.wcm.devops.conga.model.util.MapMerger.merge;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class MapMergerTest {

  @Test
  public void testEmpty() {
    assertEquals(map(), merge(null, null));
    assertEquals(map(), merge(map(), null));
    assertEquals(map(), merge(null, map()));
    assertEquals(map(), merge(map(), map()));
  }

  @Test
  public void testSimple() {
    assertEquals(map("k1", "v1"), merge(map("k1", "v1"), null));
    assertEquals(map("k1", "v1"), merge(null, map("k1", "v1")));
    assertEquals(map("k1", "v1"), merge(map("k1", "v1"), map()));
    assertEquals(map("k1", "v1"), merge(map(), map("k1", "v1")));
  }

  @Test
  public void testMerge() {
    assertEquals(map("k1", "v1", "k2", "v2"),
        merge(map("k1", "v1"), map("k2", "v2")));
  }

  @Test
  public void testMergeOverride() {
    assertEquals(map("k1", "v1"),
        merge(map("k1", "v1"), map("k1", "v2")));
  }

  @Test
  public void testMergeDeep2() {
    assertEquals(map("k1", map("k11", "v11", "k12", "v12")),
        merge(map("k1", map("k11", "v11")), map("k1", map("k12", "v12"))));
  }

  @Test
  public void testMergeDeep3() {
    assertEquals(map("k1", map("k11", "v11", "k12", map("k111", "v111", "k112", "v112"))),
        merge(map("k1", map("k11", "v11", "k12", map("k111", "v111"))),
            map("k1", map("k12", map("k112", "v112")))));
  }

  @Test
  public void testMergeList() {
    assertEquals(map("k1", list(map("k11", "v11"), "v12")),
        merge(map("k1", list(map("k11", "v11"), "v12")),
            map("k1", list(map("k11", "v12", "k21", "v21"), "v13"))));
  }

  @Test
  public void testMergeList_MergeLeft_PosStart() {
    assertEquals(map("k1", list(map("k11", "v12", "k21", "v21"), "v13", map("k11", "v11"), "v12")),
        merge(map("k1", list(LIST_MERGE_ENTRY, map("k11", "v11"), "v12")),
            map("k1", list(map("k11", "v12", "k21", "v21"), "v13"))));
  }

  @Test
  public void testMergeList_MergeLeft_PosMiddle() {
    assertEquals(map("k1", list(map("k11", "v11"), map("k11", "v12", "k21", "v21"), "v13", "v12")),
        merge(map("k1", list(map("k11", "v11"), LIST_MERGE_ENTRY, "v12")),
            map("k1", list(map("k11", "v12", "k21", "v21"), "v13"))));
  }

  @Test
  public void testMergeList_MergeLeft_PosEnd() {
    assertEquals(map("k1", list(map("k11", "v11"), "v12", map("k11", "v12", "k21", "v21"), "v13")),
        merge(map("k1", list(map("k11", "v11"), "v12", LIST_MERGE_ENTRY)),
            map("k1", list(map("k11", "v12", "k21", "v21"), "v13"))));
  }

  @Test
  public void testMergeList_MergeRight_PosStart() {
    assertEquals(map("k1", list(map("k11", "v11"), "v12", map("k11", "v12", "k21", "v21"), "v13")),
        merge(map("k1", list(map("k11", "v11"), "v12")),
            map("k1", list(LIST_MERGE_ENTRY, map("k11", "v12", "k21", "v21"), "v13"))));
  }

  @Test
  public void testMergeList_MergeRight_PosMiddle() {
    assertEquals(map("k1", list(map("k11", "v12", "k21", "v21"), map("k11", "v11"), "v12", "v13")),
        merge(map("k1", list(map("k11", "v11"), "v12")),
            map("k1", list(map("k11", "v12", "k21", "v21"), LIST_MERGE_ENTRY, "v13"))));
  }

  @Test
  public void testMergeList_MergeRight_PosEnd() {
    assertEquals(map("k1", list(map("k11", "v12", "k21", "v21"), "v13", map("k11", "v11"), "v12")),
        merge(map("k1", list(map("k11", "v11"), "v12")),
            map("k1", list(map("k11", "v12", "k21", "v21"), "v13", LIST_MERGE_ENTRY))));
  }

  @Test
  public void testMergeList_MergeBoth_PosStart() {
    assertEquals(map("k1", list(map("k11", "v12", "k21", "v21"), "v13", map("k11", "v11"), "v12")),
        merge(map("k1", list(LIST_MERGE_ENTRY, map("k11", "v11"), "v12")),
            map("k1", list(LIST_MERGE_ENTRY, map("k11", "v12", "k21", "v21"), "v13"))));
  }

  @Test
  public void testMergeList_MergeBoth_PosMiddle() {
    assertEquals(map("k1", list(map("k11", "v11"), map("k11", "v12", "k21", "v21"), "v13", "v12")),
        merge(map("k1", list(map("k11", "v11"), LIST_MERGE_ENTRY, "v12")),
            map("k1", list(map("k11", "v12", "k21", "v21"), "v13", LIST_MERGE_ENTRY))));
  }

  @Test
  public void testMergeList_MergeBoth_PosEnd() {
    assertEquals(map("k1", list(map("k11", "v11"), "v12", map("k11", "v12", "k21", "v21"), "v13")),
        merge(map("k1", list(map("k11", "v11"), "v12", LIST_MERGE_ENTRY)),
            map("k1", list(LIST_MERGE_ENTRY, map("k11", "v12", "k21", "v21"), "v13"))));
  }

  @Test
  public void testMergeList_MultiLevel0() {
    List<Object> l1 = list("e1", "e2");
    List<Object> l2 = list("e3", "e4");
    List<Object> l3 = list("e5", "e6");

    Map<String, Object> result = MapMerger.merge(map("k1", l2), map("k1", l1));
    result = MapMerger.merge(map("k1", l3), result);

    assertEquals(map("k1", list("e5", "e6")), result);
  }

  @Test
  public void testMergeList_MultiLevel1() {
    List<Object> l1 = list(LIST_MERGE_ENTRY, "e1", "e2");
    List<Object> l2 = list("e3", "e4");
    List<Object> l3 = list("e5", "e6");

    Map<String, Object> result = MapMerger.merge(map("k1", l2), map("k1", l1));
    result = MapMerger.merge(map("k1", l3), result);

    assertEquals(map("k1", list("e5", "e6", "e3", "e4", "e1", "e2")), result);
  }

  @Test
  public void testMergeList_MultiLevel2() {
    List<Object> l1 = list("e1", "e2");
    List<Object> l2 = list(LIST_MERGE_ENTRY, "e3", "e4");
    List<Object> l3 = list("e5", "e6");

    Map<String, Object> result = MapMerger.merge(map("k1", l2), map("k1", l1));
    result = MapMerger.merge(map("k1", l3), result);

    assertEquals(map("k1", list("e5", "e6", "e1", "e2", "e3", "e4")), result);
  }

  @Test
  public void testMergeList_MultiLevel3() {
    List<Object> l1 = list("e1", "e2");
    List<Object> l2 = list("e3", "e4");
    List<Object> l3 = list(LIST_MERGE_ENTRY, "e5", "e6");

    Map<String, Object> result = MapMerger.merge(map("k1", l2), map("k1", l1));
    result = MapMerger.merge(map("k1", l3), result);

    assertEquals(map("k1", list("e3", "e4", "e5", "e6")), result);
  }

  @Test
  public void testMergeList_MultiLevel1_MultiMerge() {
    List<Object> l1 = list(LIST_MERGE_ENTRY, "e1", "e2");
    List<Object> l2 = list("e3", "e4");
    List<Object> l3 = list("e5", "e6");

    Map<String, Object> result = MapMerger.merge(map("k1", l2), map("k1", l1));
    result = MapMerger.merge(map("k1", l1), result);
    result = MapMerger.merge(map("k1", l2), result);
    result = MapMerger.merge(map("k1", l3), result);
    result = MapMerger.merge(map("k1", l3), result);

    assertEquals(map("k1", list("e5", "e6", "e3", "e4", "e1", "e2")), result);
  }

  @Test
  public void testMergeList_MultiLevel1_MultiMerge_Objects() {
    List<Object> l1 = list(LIST_MERGE_ENTRY, "e1", map("k2", "e2"));
    List<Object> l2 = list("e3", map("k2", "e4"));
    List<Object> l3 = list("e5", map("k2", "e6"));

    Map<String, Object> result = MapMerger.merge(map("k1", l2), map("k1", l1));
    result = MapMerger.merge(map("k1", l1), result);
    result = MapMerger.merge(map("k1", l2), result);
    result = MapMerger.merge(map("k1", l3), result);
    result = MapMerger.merge(map("k1", l3), result);

    assertEquals(
        map("k1", list("e5", map("k2", "e6"), "e3", map("k2", "e4"), "e1", map("k2", "e2"))),
        result);
  }

  private static List<Object> list(Object... items) {
    return ImmutableList.copyOf(items);
  }

  private static Map<String, Object> map(Object... items) {
    Map<String, Object> map = new HashMap<>();
    if (items.length % 2 != 0) {
      throw new IllegalArgumentException("Expected even number of arguments.");
    }
    for (int i = 0; i < items.length - 1; i = i + 2) {
      map.put(items[i].toString(), items[i + 1]);
    }
    return ImmutableMap.copyOf(map);
  }

}
