package se.datahamstern.domain;

import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONFormatter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author kalle
 * @since 2012-04-05 18:50
 */
public class DomainStoreJsonServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setContentType("application/json");
    functions.get(request.getParameter("function")).doGet(request, response);
  }

  private Map<String, Function> functions;

  @Override
  public void init() throws ServletException {
    functions = new HashMap<String, Function>();
    addFunction(new GetPrimaryEntityFunction("getGata", DomainStore.getInstance().getGator()));
    addFunction(new GetPrimaryEntityFunction("getGatuadresser", DomainStore.getInstance().getGatuadresser()));
    addFunction(new GetPrimaryEntityFunction("getKommun", DomainStore.getInstance().getKommuner()));
    addFunction(new GetPrimaryEntityFunction("getLän", DomainStore.getInstance().getLän()));
    addFunction(new GetPrimaryEntityFunction("getOrt", DomainStore.getInstance().getOrter()));
    addFunction(new GetPrimaryEntityFunction("getPostort", DomainStore.getInstance().getPostorter()));
    addFunction(new GetPrimaryEntityFunction("getPostnummer", DomainStore.getInstance().getPostnummer()));
    addFunction(new GetPrimaryEntityFunction("getOrganisation", DomainStore.getInstance().getOrganisationer()));

    addFunction(new PrimaryIndexEntityCursorFunction("getGataCursor", DomainStore.getInstance().getGator()));
    addFunction(new PrimaryIndexEntityCursorFunction("getGatuadressCursor", DomainStore.getInstance().getGatuadresser()));
    addFunction(new PrimaryIndexEntityCursorFunction("getKommunCursor", DomainStore.getInstance().getKommuner()));
    addFunction(new PrimaryIndexEntityCursorFunction("getLänCursor", DomainStore.getInstance().getLän()));
    addFunction(new PrimaryIndexEntityCursorFunction("getOrtCursor", DomainStore.getInstance().getOrter()));
    addFunction(new PrimaryIndexEntityCursorFunction("getPostortCursor", DomainStore.getInstance().getPostorter()));
    addFunction(new PrimaryIndexEntityCursorFunction("getPostnummerCursor", DomainStore.getInstance().getPostnummer()));
    addFunction(new PrimaryIndexEntityCursorFunction("getOrganisationCursor", DomainStore.getInstance().getOrganisationer()));

    addFunction(new GetSecondaryEntityFunction("getLänByAlfakod", DomainStore.getInstance().getLänByAlfakod()));
    addFunction(new GetSecondaryEntityFunction("getLänByNummerkod", DomainStore.getInstance().getLänByNummerkod()));
    addFunction(new GetSecondaryEntityFunction("getLänByNamn", DomainStore.getInstance().getLänByNamn()));
    addFunction(new GetSecondaryEntityFunction("getKommunByNummerkod", DomainStore.getInstance().getKommunByNummerkod()));
    addFunction(new GetSecondaryEntityFunction("getKommunByNamn", DomainStore.getInstance().getKommunByNamn()));
    addFunction(new GetSecondaryEntityFunction("getOrtByTätortskod", DomainStore.getInstance().getOrtByTätortskod()));
    addFunction(new GetSecondaryEntityFunction("getPostortByNamn", DomainStore.getInstance().getPostortByNamn()));
    addFunction(new GetSecondaryEntityFunction("getPostnummerByPostnummer", DomainStore.getInstance().getPostnummerByPostnummer()));
    addFunction(new GetSecondaryEntityFunction("getOrganisationByNummer", DomainStore.getInstance().getOrganisationByNummer()));

    addFunction(new SecondaryIndexEntityCursorFunction("getOrganisationerByLänCursor", DomainStore.getInstance().getOrganisationerByLän()));
    addFunction(new SecondaryIndexEntityCursorFunction("getKommunerByLänCursor", DomainStore.getInstance().getKommunerByLän()));
    addFunction(new SecondaryIndexEntityCursorFunction("getOrterByKommunCursor", DomainStore.getInstance().getOrterByKommun()));
    addFunction(new SecondaryIndexEntityCursorFunction("getGatorByNamnAndPostortCursor", DomainStore.getInstance().getGatorByNamnAndPostort()));
    addFunction(new SecondaryIndexEntityCursorFunction("getGatorByPostnummerCursor", DomainStore.getInstance().getGatorByPostnummer()));
    addFunction(new SecondaryIndexEntityCursorFunction("getGatorByPostortCursor", DomainStore.getInstance().getGatorByPostort()));
    addFunction(new SecondaryIndexEntityCursorFunction("getGatuadressByGataAndGatunummerCursor", DomainStore.getInstance().getGatuadressByGataAndGatunummer()));
    addFunction(new SecondaryIndexEntityCursorFunction("getGatuadresserByGataCursor", DomainStore.getInstance().getGatuadresserByGata()));
    addFunction(new SecondaryIndexEntityCursorFunction("getGatuadresserByPostnummerCursor", DomainStore.getInstance().getGatuadresserByPostnummer()));
    addFunction(new SecondaryIndexEntityCursorFunction("getPostnummerByPostortCursor", DomainStore.getInstance().getPostnummerByPostort()));

  }

  private void addFunction(Function function) {
    functions.put(function.getFunctionName(), function);
  }

  public static <SK, PK, E> SK getSecondaryKey(SecondaryIndex<SK, PK, E> secondaryIndex, HttpServletRequest request, String parameterName)  throws Exception {
    SK secondaryKey;
    String secondaryKeyString = request.getParameter(parameterName);
    if (secondaryIndex.getKeyClass() == String.class) {
      if (secondaryKeyString == null) {
        return null;
      }
      secondaryKey = (SK) secondaryKeyString;
    } else if (secondaryIndex.getKeyClass() == Boolean.class) {
      if (secondaryKeyString == null) {
        return null;
      }
      secondaryKey = (SK) Boolean.valueOf(secondaryKeyString);
    } else if (secondaryIndex.getKeyClass() == Byte.class) {
      if (secondaryKeyString == null) {
        return null;
      }
      secondaryKey = (SK) Byte.valueOf(secondaryKeyString);
    } else if (secondaryIndex.getKeyClass() == Short.class) {
      if (secondaryKeyString == null) {
        return null;
      }
      secondaryKey = (SK) Short.valueOf(secondaryKeyString);
    } else if (secondaryIndex.getKeyClass() == Integer.class) {
      if (secondaryKeyString == null) {
        return null;
      }
      secondaryKey = (SK) Integer.valueOf(secondaryKeyString);
    } else if (secondaryIndex.getKeyClass() == Long.class) {
      if (secondaryKeyString == null) {
        return null;
      }
      secondaryKey = (SK) Long.valueOf(secondaryKeyString);
    } else if (secondaryIndex.getKeyClass() == Float.class) {
      if (secondaryKeyString == null) {
        return null;
      }
      secondaryKey = (SK) Float.valueOf(secondaryKeyString);
    } else if (secondaryIndex.getKeyClass() == Double.class) {
      if (secondaryKeyString == null) {
        return null;
      }
      secondaryKey = (SK) Double.valueOf(secondaryKeyString);
    } else if (secondaryIndex.getKeyClass() == Character.class) {
      if (secondaryKeyString == null) {
        return null;
      }
      if (secondaryKeyString.length() != 1) {
        throw new RuntimeException("Secondary key must contain a single character! Was: " + secondaryKeyString);
      }
      secondaryKey = (SK) new Character(secondaryKeyString.charAt(0));
    } else {


      secondaryKey = secondaryIndex.getKeyClass().newInstance();


      for (String parameter : (Set<String>)request.getParameterMap().keySet()) {
        if (parameter.startsWith(parameterName + ".")) {
          String fieldName = parameter.substring(parameter.indexOf("."));
          String stringValue = request.getParameter(parameter);

          Field field = secondaryIndex.getKeyClass().getDeclaredField(fieldName);

          StringBuilder setterNameFactory = new StringBuilder(field.getName().length()  +3);
          setterNameFactory.append("set");
          setterNameFactory.append(field.getName().substring(0, 1).toUpperCase());
          if (field.getName().length()  > 1) {
            setterNameFactory.append(field.getName().substring(1));
          }
          String setterName = setterNameFactory.toString();
          Method setter;

          if (field.getType() == boolean.class) {
            setter = secondaryIndex.getKeyClass().getDeclaredMethod(setterName, boolean.class);
            setter.invoke(secondaryKey, Boolean.valueOf(stringValue));
          } else if (field.getType() == byte.class) {
            setter = secondaryIndex.getKeyClass().getDeclaredMethod(setterName, byte.class);
            setter.invoke(secondaryKey, Byte.valueOf(stringValue));
          } else if (field.getType() == short.class) {
            setter = secondaryIndex.getKeyClass().getDeclaredMethod(setterName, short.class);
            setter.invoke(secondaryKey, Short.valueOf(stringValue));
          } else if (field.getType() == int.class) {
            setter = secondaryIndex.getKeyClass().getDeclaredMethod(setterName, int.class);
            setter.invoke(secondaryKey, Integer.valueOf(stringValue));
          } else if (field.getType() == long.class) {
            setter = secondaryIndex.getKeyClass().getDeclaredMethod(setterName, long.class);
            setter.invoke(secondaryKey, Long.valueOf(stringValue));
          } else if (field.getType() == float.class) {
            setter = secondaryIndex.getKeyClass().getDeclaredMethod(setterName, float.class);
            setter.invoke(secondaryKey, Float.valueOf(stringValue));
          } else if (field.getType() == double.class) {
            setter = secondaryIndex.getKeyClass().getDeclaredMethod(setterName, double.class);
            setter.invoke(secondaryKey, Double.valueOf(stringValue));
          } else if (field.getType() == char.class) {
            setter = secondaryIndex.getKeyClass().getDeclaredMethod(setterName, char.class);
            if (stringValue.length() != 1) {
              throw new RuntimeException("Character value string must be a single character but was: " + stringValue);
            }
            setter.invoke(secondaryKey, stringValue.charAt(0));
          } else if (field.getType() == Boolean.class) {
            setter = secondaryIndex.getKeyClass().getDeclaredMethod(setterName, Boolean.class);
            setter.invoke(secondaryKey, Boolean.valueOf(stringValue));
          } else if (field.getType() == Short.class) {
            setter = secondaryIndex.getKeyClass().getDeclaredMethod(setterName, Short.class);
            setter.invoke(secondaryKey, Short.valueOf(stringValue));
          } else if (field.getType() == Integer.class) {
            setter = secondaryIndex.getKeyClass().getDeclaredMethod(setterName, Integer.class);
            setter.invoke(secondaryKey, Integer.valueOf(stringValue));
          } else if (field.getType() == Long.class) {
            setter = secondaryIndex.getKeyClass().getDeclaredMethod(setterName, Long.class);
            setter.invoke(secondaryKey, Long.valueOf(stringValue));
          } else if (field.getType() == Float.class) {
            setter = secondaryIndex.getKeyClass().getDeclaredMethod(setterName, Float.class);
            setter.invoke(secondaryKey, Float.valueOf(stringValue));
          } else if (field.getType() == Double.class) {
            setter = secondaryIndex.getKeyClass().getDeclaredMethod(setterName, Double.class);
            setter.invoke(secondaryKey, Double.valueOf(stringValue));
          } else if (field.getType() == Character.class) {
            setter = secondaryIndex.getKeyClass().getDeclaredMethod(setterName, Character.class);
            if (stringValue.length() != 1) {
              throw new RuntimeException("Character value string must be a single character but was: " + stringValue);
            }
            setter.invoke(secondaryKey, new Character(stringValue.charAt(0)));
          } else if (field.getType() == String.class) {
            setter = secondaryIndex.getKeyClass().getDeclaredMethod(setterName, String.class);
            setter.invoke(secondaryKey, stringValue);
          } else {
            throw new RuntimeException("Unsupported field type: " + field.getType());
          }

          // todo setter.execute(secondaryKey, );

        }
      }

    }
    return secondaryKey;
  }


  public abstract static class Function {
    private String functionName;

    private Function(String functionName) {
      this.functionName = functionName;
    }

    public String getFunctionName() {
      return functionName;
    }

    public void setFunctionName(String functionName) {
      this.functionName = functionName;
    }

    protected abstract void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

  }

  public abstract static class PrimaryIndexFunction<PK, E extends DomainEntityObject> extends Function {
    private PrimaryIndex<PK, E> primaryIndex;

    public PrimaryIndexFunction(String functionName, PrimaryIndex<PK, E> primaryIndex) {
      super(functionName);
      this.primaryIndex = primaryIndex;
    }

    public PrimaryIndex<PK, E> getPrimaryIndex() {
      return primaryIndex;
    }

    public void setPrimaryIndex(PrimaryIndex<PK, E> primaryIndex) {
      this.primaryIndex = primaryIndex;
    }
  }

  public static class GetPrimaryEntityFunction<PK, E extends DomainEntityObject> extends PrimaryIndexFunction<PK, E> {

    protected GetPrimaryEntityFunction(String functionName, PrimaryIndex<PK, E> primaryIndex) {
      super(functionName, primaryIndex);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      try {
        E entity = getPrimaryIndex().get((PK) request.getParameter("identity"));
        if (entity == null) {
          response.getWriter().write("{ \"request\" : { \"success\" : true }, \"response\" : null }");
        } else {
          StringWriter buffer = new StringWriter(49152);
          entity.accept(new DomainEntityObjectJsonSerializer(buffer, request.getParameterMap().containsKey("includeMetadata")));
          response.getWriter().write("{ \"request\" : { \"success\" : true }, \"response\" : ");
          response.getWriter().write(new JSONFormatter().format(buffer.toString()));
          response.getWriter().write(" }");
        }
      } catch (Exception e) {
        jsonException(response, e);
      }
    }
  }

  public static class GetSecondaryEntityFunction<SK, PK, E extends DomainEntityObject> extends Function {

    private SecondaryIndex<SK, PK, E> secondaryIndex;

    protected GetSecondaryEntityFunction(String functionName, SecondaryIndex<SK, PK, E> secondaryIndex) {
      super(functionName);
      this.secondaryIndex = secondaryIndex;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      try {
        SK secondaryKey = getSecondaryKey(secondaryIndex, request, "secondaryKey");
        E entity = secondaryIndex.get(secondaryKey);
        if (entity == null) {
          response.getWriter().write("{ \"request\" : { \"success\" : true }, \"response\" : null }");
        } else {
          StringWriter buffer = new StringWriter(49152);
          entity.accept(new DomainEntityObjectJsonSerializer(buffer, request.getParameterMap().containsKey("includeMetadata")));
          response.getWriter().write("{ \"request\" : { \"success\" : true }, \"response\" : ");
          response.getWriter().write(new JSONFormatter().format(buffer.toString()));
          response.getWriter().write(" }");
        }
      } catch (Exception e) {
        jsonException(response, e);
      }
    }

  }

  public abstract static class GetEntityCursorFunction<E extends DomainEntityObject> extends Function {

    public GetEntityCursorFunction(String functionName) {
      super(functionName);
    }

    protected abstract EntityCursor<E> cursorFactory(HttpServletRequest request, HttpServletResponse response) throws Exception;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      try {

        String cursorIdentity = request.getParameter("cursorIdentity");
        if (cursorIdentity == null) {
          cursorIdentity = UUID.randomUUID().toString();
          // todo make sure this is used again within one minute or close it!
        }

        EntityCursor<E> cursor = (EntityCursor<E>) request.getSession().getAttribute("cursor." + cursorIdentity);
        if (cursor == null) {
          cursor = cursorFactory(request, response);
          request.getSession().setAttribute("cursor." + cursorIdentity, cursor);
          response.getWriter().write("{ \"request\" : { \"success\" : true }, \"response\" : { \"cursorIdentity\" : \"" + JSONObject.escape(cursorIdentity) + "\" } }");

        } else if (request.getParameterMap().containsKey("close")) {
          cursor.close();
          response.getWriter().write("{ \"request\" : { \"success\" : true } }");
        } else if (request.getParameterMap().containsKey("next")) {
          DomainEntityObject next = cursor.next();
          if (next == null) {
            response.getWriter().write("{ \"request\" : { \"success\" : true }, \"response\" : null }");
          } else {
            StringWriter buffer = new StringWriter(49152);
            try {
              next.accept(new DomainEntityObjectJsonSerializer(buffer, request.getParameterMap().containsKey("includeMetadata")));
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
            response.getWriter().write("{ \"request\" : { \"success\" : true }, \"response\" : ");
            response.getWriter().write(new JSONFormatter().format(buffer.toString()));
            response.getWriter().write("}");
          }
        } else {
          response.getWriter().write("{ \"request\" : { \"success\" : false,  \"error\" : \"one parameter expected\", \"parameters\" : [\"next\", \"close\"] } }");
        }

      } catch (Exception e) {
        jsonException(response, e);
      }

    }

  }

  public static class SecondaryIndexEntityCursorFunction<SK, PK, E extends DomainEntityObject> extends GetEntityCursorFunction<E> {

    private SecondaryIndex<SK, PK, E> secondaryIndex;

    public SecondaryIndexEntityCursorFunction(String functionName, SecondaryIndex<SK, PK, E> secondaryIndex) {
      super(functionName);
      this.secondaryIndex = secondaryIndex;
    }

    @Override
    protected EntityCursor<E> cursorFactory(HttpServletRequest request, HttpServletResponse response) throws Exception {
      boolean fromKeyInclude = request.getParameterMap().containsKey("fromKeyInclude") ? Boolean.valueOf(request.getParameter("fromKeyInclude")) : true;
      SK fromKey = getSecondaryKey(secondaryIndex, request, "fromKey");

      boolean toKeyInclude = request.getParameterMap().containsKey("toKeyInclude") ? Boolean.valueOf(request.getParameter("toKeyInclude")) : true;
      SK toKey = getSecondaryKey(secondaryIndex, request, "toKey");

      return secondaryIndex.entities(fromKey, fromKeyInclude, toKey, toKeyInclude);
    }
  }

  public static class PrimaryIndexEntityCursorFunction<PK, E extends DomainEntityObject> extends GetEntityCursorFunction<E> {

    private PrimaryIndex<PK, E> primaryIndex;

    public PrimaryIndexEntityCursorFunction(String functionName, PrimaryIndex<PK, E> primaryIndex) {
      super(functionName);
      this.primaryIndex = primaryIndex;
    }

    public static String defaultFromKey;
    public static String defaultToKey;

    static {
      // todo this depends on using this UUID algorithm! people might not be using an uuid at all!!!
      char[] chars = new char[UUID.randomUUID().toString().length()];
      defaultFromKey = "";
      for (int i = 0; i < chars.length; i++) {
        chars[i] = Character.MAX_VALUE;
      }
      defaultToKey = new String(chars);
    }

    @Override
    protected EntityCursor<E> cursorFactory(HttpServletRequest request, HttpServletResponse response) throws Exception {
      PK fromKey = request.getParameterMap().containsKey("fromKey") ? (PK) request.getParameter("fromKey") : (PK) defaultFromKey;
      boolean fromKeyInclude = request.getParameterMap().containsKey("fromKeyInclude") ? Boolean.valueOf(request.getParameter("fromKeyInclude")) : true;
      PK toKey = request.getParameterMap().containsKey("toKey") ? (PK) request.getParameter("toKey") : (PK) defaultToKey;
      boolean toKeyInclude = request.getParameterMap().containsKey("toKeyInclude") ? Boolean.valueOf(request.getParameter("toKeyInclude")) : true;
      return primaryIndex.entities(fromKey, fromKeyInclude, toKey, toKeyInclude);
    }
  }

  private static void jsonException(HttpServletResponse response, Exception e) throws IOException {
    response.getWriter().write("{ \"request\" : { \"success\" : false, \"error\" : \"caught exception\", \"exception\" : ");
    // todo tojson
    response.getWriter().write('"');
    if (e.getMessage() != null) {
      response.getWriter().write(e.getMessage());
    }
    response.getWriter().write('"');
    response.getWriter().write(" } }");
    e.printStackTrace();
  }
}
