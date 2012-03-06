package se.datahamstern.util;

import java.io.*;

/**
 * @author kalle
 * @since 2012-03-06 03:55
 */
public class CloneUtils {

  public static int defaultDeepCloneBufferStartingSize = 49152;

  public static <T extends Serializable> T deepClone(T object)  throws Exception {
    return deepClone(object, defaultDeepCloneBufferStartingSize);
  }

  public static <T extends Serializable> T deepClone(T object, int bufferStartingSize) throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream(bufferStartingSize);
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(object);
    oos.close();
    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
    return (T)ois.readObject();
  }
}
