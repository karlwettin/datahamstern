package se.datahamstern.xml;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kalle
 * @since 2012-03-02 00:09
 */
public class DomUtils {

  /**
   * replace &NBSP; (char)160 with normal whitespace
   * Pattern \\s does not match (char)160
   */
  public static String replaceNonBreakingSpaces(String text) {
    //
    return text.replaceAll(new String(new char[]{160}), " ");
  }

  public static String normalizeText(Node node) {
    return normalizeText(node.getTextContent());
  }

  public static String normalizeText(String text) {
    // replace &NBSP; with normal whitespace
    text = replaceNonBreakingSpaces(text);
    text = text.replaceAll("\\s+", " ");
    text = text.trim();
    return text;
  }


  public static String getAttributeValue(Node node, String attribute) {
    if (node.getAttributes() == null) {
      return null;
    }
    Node attributeNode = node.getAttributes().getNamedItem(attribute);
    if (attributeNode == null) {
      return null;
    }
    return attributeNode.getTextContent();
  }

  public static String getAttributeValue(Node node, String attribute, boolean caseSensitive) {
    if (caseSensitive) {
      return getAttributeValue(node, attribute);
    }

    if (node.getAttributes() == null) {
      return null;
    }
    NamedNodeMap attributeNodes = node.getAttributes();
    Map<String, String> matches = new HashMap<String, String>();
    for (int i=0; i<attributeNodes.getLength(); i++) {
      Node attributeNode = attributeNodes.item(i);
      if (attributeNode.getNodeName().equalsIgnoreCase(attribute)) {
        matches.put(attributeNode.getNodeName(), attributeNode.getTextContent());
      }
    }
    if (matches.size() == 0) {
      return null;
    } else if (matches.size() == 1) {
      return matches.entrySet().iterator().next().getValue();
    } else {
      throw new RuntimeException("Multiple case insensitive matches!");
    }
  }


  public static String toText(Node node) {
    StringWriter out = new StringWriter(1024);
    try {
      writeText(node, out);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return out.toString();
  }


  public static void writeText(Node node, final Writer out) throws IOException {
    if (isTextNode(node)) {
      out.write(normalizeText(node));
      out.write("\n");
    } else {
      NodeList children = node.getChildNodes();
      if (children != null) {
        for (int i = 0; i < children.getLength(); i++) {
          writeText(children.item(i), out);
        }
      }
    }

  }

  public static boolean isTextNode(Node node) {
    return node.getParentNode() != null
//        && node.getParentNode().getNodeName().equals("#comment")    // todo why is this commented out?
        && "#text".equalsIgnoreCase(node.getNodeName())
        && !"STYLE".equalsIgnoreCase(node.getParentNode().getNodeName())
        && !"SCRIPT".equalsIgnoreCase(node.getParentNode().getNodeName());
  }


}
