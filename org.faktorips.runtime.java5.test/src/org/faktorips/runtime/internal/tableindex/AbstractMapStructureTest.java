/*******************************************************************************
 * Copyright (c) 2005-2012 Faktor Zehn AG und andere.
 * 
 * Alle Rechte vorbehalten.
 * 
 * Dieses Programm und alle mitgelieferten Sachen (Dokumentationen, Beispiele, Konfigurationen,
 * etc.) duerfen nur unter den Bedingungen der Faktor-Zehn-Community Lizenzvereinbarung - Version
 * 0.1 (vor Gruendung Community) genutzt werden, die Bestandteil der Auslieferung ist und auch unter
 * http://www.faktorzehn.org/fips:lizenz eingesehen werden kann.
 * 
 * Mitwirkende: Faktor Zehn AG - initial API and implementation - http://www.faktorzehn.de
 *******************************************************************************/

package org.faktorips.runtime.internal.tableindex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AbstractMapStructureTest {

    private final Map<String, ResultStructure<Integer>> map = new HashMap<String, ResultStructure<Integer>>();

    private final Map<String, ResultStructure<Integer>> map2 = new HashMap<String, ResultStructure<Integer>>();

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private AbstractMapStructure<String, ResultStructure<Integer>, Integer> abstractMapStructure;

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private AbstractMapStructure<String, ResultStructure<Integer>, Integer> abstractMapStructure2;

    @Before
    public void setUpStructure() {
        when(abstractMapStructure.getMap()).thenReturn(map);
        when(abstractMapStructure2.getMap()).thenReturn(map2);
    }

    @Test
    public void testPut() throws Exception {
        abstractMapStructure.put("abc", new ResultStructure<Integer>(123));

        assertEquals(123, abstractMapStructure.getUnique().intValue());
        assertEquals(1, abstractMapStructure.getMap().keySet().size());
        assertThat(abstractMapStructure.getMap().keySet(), hasItem("abc"));
        assertThat(abstractMapStructure.getMap().values(), hasItem(new ResultStructure<Integer>(123)));
    }

    @Test
    public void testPut_multipleSameKey() throws Exception {
        abstractMapStructure.put("abc", new ResultStructure<Integer>(123));
        abstractMapStructure.put("abc", new ResultStructure<Integer>(321));
        ResultStructure<Integer> resultSet = createResultSet(123, 321);

        assertThat(abstractMapStructure.get(), hasItem(123));
        assertThat(abstractMapStructure.get(), hasItem(321));
        assertEquals(1, abstractMapStructure.getMap().keySet().size());
        assertThat(abstractMapStructure.getMap().keySet(), hasItem("abc"));
        assertThat(abstractMapStructure.getMap().values(), hasItem(resultSet));
    }

    @Test
    public void testPut_multipleDifferentKeys() throws Exception {
        abstractMapStructure.put("abc", new ResultStructure<Integer>(123));
        abstractMapStructure.put("xyz", new ResultStructure<Integer>(321));

        assertThat(abstractMapStructure.get(), hasItem(123));
        assertThat(abstractMapStructure.get(), hasItem(321));
        assertEquals(2, abstractMapStructure.getMap().keySet().size());
        assertThat(abstractMapStructure.getMap().keySet(), hasItem("abc"));
        assertThat(abstractMapStructure.getMap().keySet(), hasItem("xyz"));
        assertEquals(123, abstractMapStructure.getMap().get("abc").getUnique().intValue());
        assertEquals(321, abstractMapStructure.getMap().get("xyz").getUnique().intValue());
    }

    @Test
    public void testMerge_sameKey() throws Exception {
        map.put("abc", new ResultStructure<Integer>(123));
        map2.put("abc", new ResultStructure<Integer>(321));

        abstractMapStructure.merge(abstractMapStructure2);

        Map<String, ResultStructure<Integer>> resultMap = abstractMapStructure.getMap();
        assertEquals(1, map.size());
        assertEquals(1, map2.size());
        assertEquals(1, resultMap.size());
        assertThat(resultMap.keySet(), hasItem("abc"));
        assertThat(resultMap.values(), hasItem(createResultSet(123, 321)));
    }

    @Test
    public void testMerge_differentKeys() throws Exception {
        map.put("abc", new ResultStructure<Integer>(123));
        map2.put("xyz", new ResultStructure<Integer>(321));

        abstractMapStructure.merge(abstractMapStructure2);

        Map<String, ResultStructure<Integer>> resultMap = abstractMapStructure.getMap();
        assertEquals(2, resultMap.size());
        assertThat(resultMap.keySet(), hasItem("abc"));
        assertThat(resultMap.keySet(), hasItem("xyz"));
        assertEquals(createResultSet(123), resultMap.get("abc"));
        assertEquals(createResultSet(321), resultMap.get("xyz"));
    }

    private ResultStructure<Integer> createResultSet(Integer... values) {
        HashSet<Integer> set = new HashSet<Integer>(Arrays.asList(values));
        ResultStructure<Integer> resultSet = new ResultStructure<Integer>(set);
        return resultSet;
    }

    @Test
    public void testGet_multipleSameKey() throws Exception {
        abstractMapStructure.put("abc", new ResultStructure<Integer>(123));
        abstractMapStructure.put("abc", new ResultStructure<Integer>(321));

        assertThat(abstractMapStructure.get(), hasItem(123));
        assertThat(abstractMapStructure.get(), hasItem(321));
    }

    @Test
    public void testGet_multipleDifferentKeys() throws Exception {
        abstractMapStructure.put("abc", new ResultStructure<Integer>(123));
        abstractMapStructure.put("xyz", new ResultStructure<Integer>(321));

        assertThat(abstractMapStructure.get(), hasItem(123));
        assertThat(abstractMapStructure.get(), hasItem(321));
    }

}
