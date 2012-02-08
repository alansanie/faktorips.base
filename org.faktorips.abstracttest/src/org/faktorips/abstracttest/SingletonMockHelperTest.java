/*******************************************************************************
 * Copyright (c) 2005-2012 Faktor Zehn AG und andere.
 * 
 * Alle Rechte vorbehalten.
 * 
 * Dieses Programm und alle mitgelieferten Sachen (Dokumentationen, Beispiele, Konfigurationen,
 * etc.) duerfen nur unter den Bedingungen der Faktor-Zehn-Community Lizenzvereinbarung - Version
 * 0.1 (vor Gruendung Community) genutzt werden, die Bestandteil der Auslieferung ist und auch unter
 * http://www.faktorzehn.org/f10-org:lizenzen:community eingesehen werden kann.
 * 
 * Mitwirkende: Faktor Zehn AG - initial API and implementation - http://www.faktorzehn.de
 *******************************************************************************/

package org.faktorips.abstracttest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

public class SingletonMockHelperTest {

    @Test
    public void testSetSingletonInstance() {
        assertEquals("bar", MySingleton.getInstance().getName());
        MySingleton mockInstance = mock(MySingleton.class);
        when(mockInstance.getName()).thenReturn("foobar");
        SingletonMockHelper singletonMockHelper = new SingletonMockHelper();
        singletonMockHelper.setSingletonInstance(MySingleton.class, mockInstance);
        assertEquals("foobar", MySingleton.getInstance().getName());

        singletonMockHelper.setSingletonInstance(MySingleton.class, new MySingleton2());
        assertEquals("test", MySingleton.getInstance().getName());

        try {
            singletonMockHelper.setSingletonInstance(MySingleton2.class, new MySingleton2());
            fail("Expected IllegalArgumentException because MySingleton2 declares no instance field");
        } catch (IllegalArgumentException e) {
            // expected
        }

        try {
            singletonMockHelper.setSingletonInstance(MyDoubleSingleton.class, new MyDoubleSingleton());
            fail("Expected IllegalArgumentException because MyDoubleSingleton has two instance fields");
        } catch (IllegalArgumentException e) {
            // expected
        }

        try {
            singletonMockHelper.setSingletonInstance(MyNoSingleton.class, new MyNoSingleton());
            fail("Expected IllegalArgumentException because MyNoSingleton has no static instance field");
        } catch (IllegalArgumentException e) {
            // expected
        }

        MyFinalSingleton finalMockInstance = mock(MyFinalSingleton.class);
        try {
            singletonMockHelper.setSingletonInstance(MyFinalSingleton.class, finalMockInstance);
            fail("Expected IllegalArgumentException when trying to set a final field");
        } catch (IllegalArgumentException e) {
            // expected
        }
        singletonMockHelper.reset();
    }

    @Test
    public void testReset() {
        SingletonMockHelper singletonMockHelper = new SingletonMockHelper();

        MySingleton mockInstance = mock(MySingleton.class);
        when(mockInstance.getName()).thenReturn("foobar");
        singletonMockHelper.setSingletonInstance(MySingleton.class, mockInstance);

        when(mockInstance.getName()).thenReturn("foobarXY");
        singletonMockHelper.setSingletonInstance(MySingleton.class, mockInstance);

        singletonMockHelper.reset();
        assertEquals("bar", MySingleton.getInstance().getName());
    }

    public static class MySingleton {
        private static MySingleton instance = new MySingleton();
        private String name = "foo";

        private MySingleton() {
            name = "bar";
        }

        public static MySingleton getInstance() {
            return instance;
        }

        public String getName() {
            return name;
        }
    }

    public static class MySingleton2 extends MySingleton {

        @Override
        public String getName() {
            return "test";
        }
    }

    public static class MyFinalSingleton {
        private static final MyFinalSingleton instance = new MyFinalSingleton();

        public static MyFinalSingleton getInstance() {
            return instance;
        }
    }

    public static class MyNoSingleton {
        public MyNoSingleton instance;
    }

    public static class MyDoubleSingleton {
        private static MyDoubleSingleton instance = new MyDoubleSingleton();

        @SuppressWarnings("unused")
        private static MyDoubleSingleton anotherField;

        public static MyDoubleSingleton getInstance() {
            return instance;
        }
    }

}
