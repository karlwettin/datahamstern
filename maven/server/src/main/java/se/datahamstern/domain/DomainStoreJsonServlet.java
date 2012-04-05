package se.datahamstern.domain;

import com.sleepycat.persist.PrimaryIndex;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kalle
 * @since 2012-04-05 18:50
 */
public class DomainStoreJsonServlet extends HttpServlet {


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
      E entity = getPrimaryIndex().get((PK) request.getParameter("identity"));
      if (entity == null) {
        response.getWriter().print("null");
      } else {
        try {
          entity.accept(new DomainEntityObjectJsonSerializer(response.getWriter()));
        } catch (IOException e) {
          throw e;
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    }
  }


  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setContentType("application/json");
    functions.get(request.getParameter("function")).doGet(request, response);
  }
}
