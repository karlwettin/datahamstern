package se.datahamstern.domain;

import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.PrimaryIndex;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONFormatter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
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
    addFunction(new GetEntityFunction("getGata", DomainStore.getInstance().getGator()));
    addFunction(new GetEntityFunction("getGatuadresser", DomainStore.getInstance().getGatuadresser()));
    addFunction(new GetEntityFunction("getKommun", DomainStore.getInstance().getKommuner()));
    addFunction(new GetEntityFunction("getLän", DomainStore.getInstance().getLän()));
    addFunction(new GetEntityFunction("getOrt", DomainStore.getInstance().getOrter()));
    addFunction(new GetEntityFunction("getPostort", DomainStore.getInstance().getPostorter()));
    addFunction(new GetEntityFunction("getPostnummer", DomainStore.getInstance().getPostnummer()));
    addFunction(new GetEntityFunction("getOrganisation", DomainStore.getInstance().getOrganisationer()));

    addFunction(new PrimaryIndexEntityCursorFunction("getGataCursor", DomainStore.getInstance().getGator()));
    addFunction(new PrimaryIndexEntityCursorFunction("getGatuadressCursor", DomainStore.getInstance().getGatuadresser()));
    addFunction(new PrimaryIndexEntityCursorFunction("getKommunCursor", DomainStore.getInstance().getKommuner()));
    addFunction(new PrimaryIndexEntityCursorFunction("getLänCursor", DomainStore.getInstance().getLän()));
    addFunction(new PrimaryIndexEntityCursorFunction("getOrtCursor", DomainStore.getInstance().getOrter()));
    addFunction(new PrimaryIndexEntityCursorFunction("getPostortCursor", DomainStore.getInstance().getPostorter()));
    addFunction(new PrimaryIndexEntityCursorFunction("getPostnummerCursor", DomainStore.getInstance().getPostnummer()));
    addFunction(new PrimaryIndexEntityCursorFunction("getOrganisationCursor", DomainStore.getInstance().getOrganisationer()));
  }

  private void addFunction(Function function) {
    functions.put(function.getFunctionName(), function);
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

  public static class GetEntityFunction<PK, E extends DomainEntityObject> extends PrimaryIndexFunction<PK, E> {

    protected GetEntityFunction(String functionName, PrimaryIndex<PK, E> primaryIndex) {
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

  public abstract static class GetEntityCursorFunction<E extends DomainEntityObject> extends Function {

    public GetEntityCursorFunction(String functionName) {
      super(functionName);
    }

    protected abstract EntityCursor<E> cursorFactory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      try {

        String cursorIdentity = request.getParameter("identity");
        if (cursorIdentity == null) {
          cursorIdentity = UUID.randomUUID().toString();
          // todo make sure this is used within one minute or close it!
        }

        EntityCursor<E> cursor = (EntityCursor<E>) request.getSession().getAttribute("cursor." + cursorIdentity);
        if (cursor == null) {
          cursor = cursorFactory(request, response);
          request.getSession().setAttribute("cursor." + cursorIdentity, cursor);
          response.getWriter().write("{ \"request\" : { \"success\" : true }, \"response\" : { \"cursorName\" : \"" + JSONObject.escape(cursorIdentity) + "\" } }");

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
    protected EntityCursor<E> cursorFactory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
