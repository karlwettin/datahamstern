package se.datahamstern.external.osm;

import java.util.List;
import java.util.Set;

/**
 * @author kalle
 * @since 2012-05-24 12:51
 */
public class Way {

  private int id;

  private String name;
  private List<Node> nodes;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Way way = (Way) o;

    if (id != way.id) return false;

    return true;
  }

  @Override
  public String toString() {
    return "Way{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", nodes=" + nodes +
        '}';
  }

  @Override
  public int hashCode() {
    return id;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Node> getNodes() {
    return nodes;
  }

  public void setNodes(List<Node> nodes) {
    this.nodes = nodes;
  }
}
