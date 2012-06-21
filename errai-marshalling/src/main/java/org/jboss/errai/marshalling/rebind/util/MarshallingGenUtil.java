/*
 * Copyright 2011 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.marshalling.rebind.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.meta.MetaParameterizedType;
import org.jboss.errai.codegen.meta.MetaType;
import org.jboss.errai.codegen.util.GenUtil;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public class MarshallingGenUtil {
  @Deprecated
  /**
   * Use 'errai.marshalling.serializableTypes' now.
   */

  public static String getVarName(final MetaClass clazz) {
    return clazz.isArray()
            ? getArrayVarName(clazz.getOuterComponentType().getFullyQualifiedName())
            + "_D" + GenUtil.getArrayDimensions(clazz)
            : getVarName(clazz.asBoxed().getFullyQualifiedName());
  }

  public static String getVarName(final Class<?> clazz) {
    return getVarName(MetaClassFactory.get(clazz));
  }

  private static final String ARRAY_VAR_PREFIX = "arrayOf_";

  public static String getArrayVarName(final String clazz) {
    final char[] newName = new char[clazz.length() + ARRAY_VAR_PREFIX.length()];
    _replaceAllDotsWithUnderscores(ARRAY_VAR_PREFIX, newName, 0);
    _replaceAllDotsWithUnderscores(clazz, newName, ARRAY_VAR_PREFIX.length());
    return new String(newName);
  }

  public static String getVarName(String clazz) {
    final char[] newName = new char[clazz.length()];
    _replaceAllDotsWithUnderscores(clazz, newName, 0);
    return new String(newName);
  }
  
  private static void _replaceAllDotsWithUnderscores(String sourceString, char[] destArray, int offset) {
    char c;
    for (int i = 0; i < sourceString.length(); i++) {
      if ((c = sourceString.charAt(i)) == '.') {
        destArray[i + offset] = '_';
      }
      else {
        destArray[i + offset] = c;
      }
    }
  }
  
  public static MetaMethod findGetterMethod(MetaClass cls, String key) {
    MetaMethod metaMethod = _findGetterMethod("get", cls, key);
    if (metaMethod != null) return metaMethod;
    metaMethod = _findGetterMethod("is", cls, key);
     return metaMethod;
  }
  
  private static MetaMethod _findGetterMethod(String prefix, MetaClass cls, String key) {
    key = (prefix + key).toUpperCase();
    for (MetaMethod m : cls.getDeclaredMethods()) {
      if (m.getName().toUpperCase().equals(key) && m.getParameters().length == 0) {
        return m;
      }
    }
    return null;
  }
  
  /**
   * Returns the element type of the given metaclass under the following conditions:
   * <ul>
   * <li>toType is a collection type
   * <li>toType has a single type parameter
   * <li>toType's type parameter is not a wildcard
   * <li>toType's type parameter is a non-abstract (concrete) type
   * <li>toType's type parameter is not java.lang.Object
   * </ul>
   * 
   * @param toType
   *          The type to check for a known concrete collection element type.
   * @return The concrete element type meeting all above-mentioned criteria, or null if one or more of the criteria
   *         fails.
   */
  public static MetaClass getConcreteCollectionElementType(MetaClass toType) {
    if (toType.isAssignableTo(Collection.class)) {
      return getConcreteElementType(toType);
    }
    return null;
  }
  
  /**
   * Returns the element type of the given metaclass under the following conditions:
   * <ul>
   * <li>toType has a single type parameter
   * <li>toType's type parameter is not a wildcard
   * <li>toType's type parameter is a non-abstract (concrete) type
   * <li>toType's type parameter is not java.lang.Object
   * </ul>
   * 
   * @param toType
   *          The type to check for a known concrete collection element type.
   * @return The concrete element type meeting all above-mentioned criteria, or null if one or more of the criteria
   *         fails.
   */
  public static MetaClass getConcreteElementType(MetaClass toType) {
    if (toType.getParameterizedType() != null) {
      MetaType[] typeParms = toType.getParameterizedType().getTypeParameters();
      if (typeParms != null && typeParms.length == 1) {

        MetaClass typeParameter = null;
        if (typeParms[0] instanceof MetaParameterizedType) {
          MetaParameterizedType parameterizedTypeParemeter = (MetaParameterizedType) typeParms[0];
          typeParameter = (MetaClass) parameterizedTypeParemeter.getRawType();
        }
        else if (typeParms[0] instanceof MetaClass) {
          typeParameter = (MetaClass) typeParms[0];
        }      

        if (!MetaClassFactory.get(Object.class).equals(typeParameter))
          return typeParameter;
      }
    }
    return null;
  }

  public static Collection<MetaClass> getDefaultArrayMarshallers() {
    final List<MetaClass> l = new ArrayList<MetaClass>();
    
    l.add(MetaClassFactory.get(Object[].class));
    l.add(MetaClassFactory.get(String[].class));
    l.add(MetaClassFactory.get(int[].class));
    l.add(MetaClassFactory.get(long[].class));
    l.add(MetaClassFactory.get(double[].class));
    l.add(MetaClassFactory.get(float[].class));
    l.add(MetaClassFactory.get(short[].class));
    l.add(MetaClassFactory.get(boolean[].class));
    l.add(MetaClassFactory.get(byte[].class));
    l.add(MetaClassFactory.get(char[].class));

    l.add(MetaClassFactory.get(Integer[].class));
    l.add(MetaClassFactory.get(Long[].class));
    l.add(MetaClassFactory.get(Double[].class));
    l.add(MetaClassFactory.get(Float[].class));
    l.add(MetaClassFactory.get(Short[].class));
    l.add(MetaClassFactory.get(Boolean[].class));
    l.add(MetaClassFactory.get(Byte[].class));
    l.add(MetaClassFactory.get(Character[].class));


    return Collections.unmodifiableCollection(l);
  }
}
