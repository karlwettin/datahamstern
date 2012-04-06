package se.datahamstern.domain;

import org.json.simple.serialization.CodecRegistry;
import org.json.simple.serialization.collections.ArrayCodec;
import org.json.simple.serialization.collections.CollectionCodec;
import org.json.simple.serialization.collections.MapCodec;
import se.datahamstern.util.ListMap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author kalle
 * @since 2012-04-06 15:13
 */
public abstract class ReflectiveDomainEntityObjectVisitor extends AllDomainEntityObjectVisitor {

  public static class BeanReflector<E> {

    private Class<E> beanClass;
    private E defaultInstance;
    private Map<String, Field> fieldsByName = new LinkedHashMap<String, Field>();

    private ListMap<String, Field> primitiveFields = new ListMap<String, Field>();
    private ListMap<String, Field> primitiveArrayFields = new ListMap<String, Field>();
    private ListMap<String, Field> objectFields = new ListMap<String, Field>();
    private ListMap<String, Field> objectArrayFields = new ListMap<String, Field>();
    private ListMap<String, Field> listFields = new ListMap<String, Field>();
    private ListMap<String, Field> setFields = new ListMap<String, Field>();
    private ListMap<String, Field> mapFields = new ListMap<String, Field>();

    public static void findSuperClasses(Set<Class> classes, Class current) {
      classes.add(current);
      if (current.getSuperclass() != null && classes.add(current.getSuperclass())) {
        findSuperClasses(classes, current.getSuperclass());
      }
      for (Class _class : current.getInterfaces()) {
        if (classes.add(_class)) {
          findSuperClasses(classes, _class);
        }
      }
    }

    public void resolve(Class<E> beanClass) throws IllegalAccessException, InstantiationException {

      System.out.println("Debug: Reflecting " + beanClass.getName());

      if (!Modifier.isAbstract(beanClass.getModifiers())
          && !Modifier.isInterface(beanClass.getModifiers())
          && !beanClass.isEnum()) {
        defaultInstance = beanClass.newInstance();
      }

      this.beanClass = beanClass;

      Set<Class> allClasses = new HashSet<Class>();
      findSuperClasses(allClasses, beanClass);

      List<Field> allFields = new ArrayList<Field>();

      for (Class _class : allClasses) {
        allFields.addAll(Arrays.asList(_class.getDeclaredFields()));
      }


      for (Field field : allFields) {
        System.out.println("Debug: Reflecting " + beanClass.getName() + "#" + field.getName());
        // all static fields are considered transient
        if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
          continue;
        }

        String attribute = field.getName();


//        JSON annotation = field.getAnnotation(JSON.class);
//        if (annotation != null) {
//          if (annotation.attribute() != null) {
//            attribute = annotation.attribute();
//          }
//        }
        fieldsByName.put(attribute, field);


      }

      Set<Class> resolved = new HashSet<Class>();
      if (resolved.add(beanClass)) {
        for (Field f : fieldsByName.values()) {
          if (resolved.add(f.getType())) {


            if (f.getType().isArray()) {
              Class genericType = f.getType().getComponentType();
              if (genericType.isPrimitive()) {
                primitiveArrayFields.listAdd(f.getName(), f);
              } else {
                objectArrayFields.listAdd(f.getName(), f);
              }

            } else if (List.class.isAssignableFrom(f.getType())) {
              listFields.listAdd(f.getName(), f);
//              Class genericType = getPrimitiveGenericType(f);

            } else if (Set.class.isAssignableFrom(f.getType())) {
              setFields.listAdd(f.getName(), f);
//              Class genericType = getPrimitiveGenericType(f);

            } else if (Map.class.isAssignableFrom(f.getType())) {
              mapFields.listAdd(f.getName(), f);
//              Class[] genericTypes = getGenericTypes(f);
//              if (genericTypes != null) {
//                return new MapCodec(this, genericTypes[0], genericTypes[1]);
//              } else {
//                return new MapCodec(this, Object.class, Object.class);
//              }
            }

          }

        }
      }
    }


  }




  public <E extends DomainEntityObject> void visitPrimitive(Class<E> type, E object) throws Exception {
  }

  public <E extends DomainEntityObject> void visitPrimitiveArray(Class<E> type, E object) throws Exception {
  }

  public <E extends DomainEntityObject> void visitObject(Class<E> type, E object) throws Exception {
  }

  public <E extends DomainEntityObject> void visitObjectArray(Class<E> type, E object) throws Exception {
  }

  public <E extends DomainEntityObject> void visitList(Class<E> type, E object) throws Exception {
  }

  public <E extends DomainEntityObject> void visitSet(Class<E> type, E object) throws Exception {
  }

  public <E extends DomainEntityObject> void visitMap(Class<E> type, E object) throws Exception {
  }





  @Override
  public <E extends DomainEntityObject> void visit(Class<E> type, E object) {

  }
}
