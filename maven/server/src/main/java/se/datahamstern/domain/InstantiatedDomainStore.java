package se.datahamstern.domain;


import com.sleepycat.bind.EntityBinding;
import com.sleepycat.bind.EntryBinding;
import com.sleepycat.je.*;
import com.sleepycat.persist.EntityIndex;
import com.sleepycat.persist.StoreConfig;
import com.sleepycat.persist.evolve.EvolveConfig;
import com.sleepycat.persist.evolve.EvolveStats;
import com.sleepycat.persist.evolve.Mutations;
import com.sleepycat.persist.model.EntityModel;
import se.datahamstern.util.CloneUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * todo @Entity must currently be serializable!
 * todo @PrimaryKey must currently be private String identity;
 * todo no support for constraints in @SecondaryKey!
 * todo PK nor SK compares well to it self. This should use the same algorithm as in BDB JE!
 *
 * @author kalle
 * @since 2012-04-03 01:08
 */
public class InstantiatedDomainStore {

  private Map<Class, PrimaryIndex> primaryIndices;

  public InstantiatedDomainStore() {
    primaryIndices = new HashMap<Class, PrimaryIndex>();
  }

  public class EntityStore extends com.sleepycat.persist.EntityStore {

    public EntityStore() {
    }

    private String storeName;

    public EntityStore(String storeName) {
      this.storeName = storeName;
    }

    @Override
    public <PK, E> com.sleepycat.persist.PrimaryIndex<PK, E> getPrimaryIndex(Class<PK> primaryKeyClass, Class<E> entityClass) throws DatabaseException {
      PrimaryIndex primaryIndex = primaryIndices.get(entityClass);
      if (primaryIndex == null) {
        primaryIndex = new PrimaryIndex(primaryKeyClass, entityClass);
        primaryIndices.put(entityClass, primaryIndex);
      }
      return primaryIndex;
    }

    @Override
    public <SK, PK, E > SecondaryIndex<SK, PK, E> getSecondaryIndex(com.sleepycat.persist.PrimaryIndex<PK, E> primaryIndex, Class<SK> keyClass, String keyName) throws DatabaseException {
      PrimaryIndex instantiatedPrimaryIndex = (PrimaryIndex) primaryIndex;
      SecondaryIndex secondaryIndex = (SecondaryIndex) instantiatedPrimaryIndex.secondaryIndices.get(keyName);
      if (secondaryIndex == null) {
        secondaryIndex = new SecondaryIndex(instantiatedPrimaryIndex, keyClass, keyName);
        instantiatedPrimaryIndex.secondaryIndices.put(keyName, secondaryIndex);
      }
      return secondaryIndex;
    }

    @Override
    public Environment getEnvironment() {
      throw new UnsupportedOperationException();
    }

    @Override
    public StoreConfig getConfig() {
      throw new UnsupportedOperationException();
    }

    @Override
    public String getStoreName() {
      return storeName;
    }

    @Override
    public boolean isReplicaUpgradeMode() {
      throw new UnsupportedOperationException();
    }

    @Override
    public EntityModel getModel() {
      throw new UnsupportedOperationException();
    }

    @Override
    public Mutations getMutations() {
      throw new UnsupportedOperationException();
    }

    @Override
    public <SK, PK, E1, E2 extends E1> com.sleepycat.persist.SecondaryIndex<SK, PK, E2> getSubclassIndex(com.sleepycat.persist.PrimaryIndex<PK, E1> primaryIndex, Class<E2> entitySubclass, Class<SK> keyClass, String keyName) throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public EvolveStats evolve(EvolveConfig config) throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public void truncateClass(Class entityClass) throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public void truncateClass(Transaction txn, Class entityClass) throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public void sync() throws DatabaseException {

    }

    @Override
    public void closeClass(Class entityClass) throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws DatabaseException {

    }

    @Override
    public Sequence getSequence(String name) throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public SequenceConfig getSequenceConfig(String name) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setSequenceConfig(String name, SequenceConfig config) {
      throw new UnsupportedOperationException();
    }

    @Override
    public DatabaseConfig getPrimaryConfig(Class entityClass) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setPrimaryConfig(Class entityClass, DatabaseConfig config) {
      throw new UnsupportedOperationException();
    }

    @Override
    public SecondaryConfig getSecondaryConfig(Class entityClass, String keyName) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setSecondaryConfig(Class entityClass, String keyName, SecondaryConfig config) {
      throw new UnsupportedOperationException();
    }
  }

  public class PrimaryIndex<PK, E> extends com.sleepycat.persist.PrimaryIndex<PK, E> {

    private Map<String, SecondaryIndex> secondaryIndices;

    private Class<PK> primaryKeyClass;
    private Class<E> entityClass;
    private Map<PK, E> index;

    public PrimaryIndex(Class<PK> primaryKeyClass, Class<E> entityClass) throws DatabaseException {
      this.primaryKeyClass = primaryKeyClass;
      this.entityClass = entityClass;
      index = new HashMap<PK, E>();
      secondaryIndices = new HashMap<String, SecondaryIndex>();
    }

    public int compare(PK key1, PK key2) {

      if (key1 == null && key2 == null) {
        return 0;
      } else if (key1 == null) {
        return -1;
      } else if (key2 == null) {
        return 1;
      } else {
        // todo @PrimaryKey must currently be private String identity;
        // todo rather implement as strategies created on init based on primaryKeyClass
        // todo keyfields if persistent
        // todo hashcode if not comparable
        return ((Comparable) key1).compareTo(key2);
      }
    }

    @Override
    public long count() throws DatabaseException {
      return index.size();
    }

    @Override
    public Map<PK, E> map() {
      return index;
    }

    @Override
    public boolean contains(PK key) throws DatabaseException {
      return index.containsKey(key);
    }
    
    

    private transient Method primaryKeyGetter;

    public PK getPrimaryKey(E entity) throws DatabaseException {
      try {
        if (primaryKeyGetter == null) {
          // todo @PrimaryKey must currently be private String identity;
          Field field = entity.getClass().getDeclaredField("identity");
          StringBuilder sb = new StringBuilder(field.getName().length() + 3);
          if (field.getType() == boolean.class) {
            sb.append("is");
          } else {
            sb.append("get");
          }
          sb.append(Character.toUpperCase(field.getName().charAt(0)));
          if (field.getName().length() > 1) {
            sb.append(field.getName().substring(1));
          }
          primaryKeyGetter = entity.getClass().getDeclaredMethod(sb.toString());
        }
        return (PK) primaryKeyGetter.invoke(entity);
      } catch (Exception e) {
        throw new InstantiatedDatabaseException(e);
      }
    }

    private transient Method primaryKeySetter;

    public void setPrimaryKey(E entity, PK primaryKey) throws DatabaseException {
      try {
        if (primaryKeySetter == null) {
          // todo @PrimaryKey must currently be private String identity;
          Field field = entity.getClass().getDeclaredField("identity");
          StringBuilder sb = new StringBuilder(field.getName().length() + 3);
          sb.append("set");
          sb.append(Character.toUpperCase(field.getName().charAt(0)));
          if (field.getName().length() > 1) {
            sb.append(field.getName().substring(1));
          }
          primaryKeySetter = entity.getClass().getDeclaredMethod(sb.toString());
        }
        primaryKeySetter.invoke(entity, primaryKey);
      } catch (Exception e) {
        throw new InstantiatedDatabaseException(e);
      }
    }


    @Override
    public E put(E entity) throws DatabaseException {


      PK primaryKey = getPrimaryKey(entity);
      if (primaryKey == null) {
        // todo @PrimaryKey must currently be private String identity;
        primaryKey = (PK) UUID.randomUUID().toString();
        setPrimaryKey(entity, primaryKey);
      }

      E toset;
      try {
        // todo @Entity must currently be serializable!
        toset = (E)CloneUtils.deepClone((Serializable)entity);
      } catch (Exception e) {
        throw new InstantiatedDatabaseException("Could not clone entity " + entity, e);
      }
      E previous = index.put(primaryKey, toset);

      for (SecondaryIndex secondaryIndex : secondaryIndices.values()) {
        Object secondaryKey = secondaryIndex.getSecondaryKey(toset);
        secondaryIndex.put(primaryKey, secondaryKey, toset);
      }
      return previous;
    }

    @Override
    public E get(PK key) throws DatabaseException {
      return index.get(key);
    }

    @Override
    public boolean delete(PK key) throws DatabaseException {
      E previous = index.remove(key);
      for (SecondaryIndex secondaryIndex : secondaryIndices.values()) {
        secondaryIndex.index.remove(secondaryIndex.getSecondaryKey(previous));
      }
      return previous != null;
    }

    private Comparator primaryKeyComparator = new Comparator() {
      @Override
      public int compare(Object o1, Object o2) {
        if (o1 instanceof Map.Entry) {
          return PrimaryIndex.this.compare(((Map.Entry<PK, E>) o1).getKey(), (PK) o2);
        } else {
          return PrimaryIndex.this.compare((PK) o1, ((Map.Entry<PK, E>) o2).getKey());
        }
      }
    };

    @Override
    public com.sleepycat.persist.EntityCursor<E> entities(PK fromKey, boolean fromInclusive, PK toKey, boolean toInclusive) throws DatabaseException {
      List<Map.Entry<PK, E>> entries = new ArrayList<Map.Entry<PK, E>>(index.entrySet());
      Collections.sort(entries, primaryKeyComparator);

      int start = Collections.binarySearch(entries, fromKey, primaryKeyComparator);
      if (start < 0) {
        start *= -1;
      } else if (!fromInclusive) {
        start++;
        if (start == entries.size()) {
          return new EntityCursor<E>(Collections.<E>emptyList());
        }
      }

      int end = Collections.binarySearch(entries, toKey, primaryKeyComparator);
      if (end < 0) {
        end *= -1;
        end--;
      } else if (!toInclusive) {
        end--;
      }

      List<E> entities = new ArrayList<E>(end - start);
      for (int i = start; i < end; i++) {
        entities.add(entries.get(i).getValue());
      }
      return new EntityCursor<E>(entities);

    }

    @Override
    public Class<PK> getKeyClass() {
      return primaryKeyClass;
    }

    @Override
    public Class<E> getEntityClass() {
      return entityClass;
    }

    @Override
    public Database getDatabase() {
      throw new UnsupportedOperationException();
    }

    @Override
    public EntryBinding<PK> getKeyBinding() {
      throw new UnsupportedOperationException();
    }

    @Override
    public EntityBinding<E> getEntityBinding() {
      throw new UnsupportedOperationException();
    }

    @Override
    public E put(Transaction txn, E entity) throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public void putNoReturn(E entity) throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public void putNoReturn(Transaction txn, E entity) throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean putNoOverwrite(E entity) throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean putNoOverwrite(Transaction txn, E entity) throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public E get(Transaction txn, PK key, LockMode lockMode) throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public SortedMap<PK, E> sortedMap() {
      throw new UnsupportedOperationException();
    }

    @Override
    protected TransactionConfig getAutoCommitTransactionConfig() {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Transaction txn, PK key, LockMode lockMode) throws DatabaseException {
      return contains(key);
    }

    @Override
    public boolean delete(Transaction txn, PK key) throws DatabaseException {
      return delete(key);
    }

    @Override
    public com.sleepycat.persist.EntityCursor<PK> keys() throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public com.sleepycat.persist.EntityCursor<PK> keys(Transaction txn, CursorConfig config) throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public com.sleepycat.persist.EntityCursor<E> entities() throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public com.sleepycat.persist.EntityCursor<E> entities(Transaction txn, CursorConfig config) throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public com.sleepycat.persist.EntityCursor<PK> keys(PK fromKey, boolean fromInclusive, PK toKey, boolean toInclusive) throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public com.sleepycat.persist.EntityCursor<PK> keys(Transaction txn, PK fromKey, boolean fromInclusive, PK toKey, boolean toInclusive, CursorConfig config) throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public com.sleepycat.persist.EntityCursor<E> entities(Transaction txn, PK fromKey, boolean fromInclusive, PK toKey, boolean toInclusive, CursorConfig config) throws DatabaseException {
      return entities(fromKey, fromInclusive, toKey, toInclusive);
    }
  }

  public class SecondaryIndex<SK, PK, E> extends com.sleepycat.persist.SecondaryIndex<SK, PK, E> {
    public SecondaryIndex(PrimaryIndex<PK, E> primaryIndex, Class<SK> secondaryKeyClass, String secondaryKeyName) throws DatabaseException {
      this.primaryIndex = primaryIndex;
      this.secondaryKeyClass = secondaryKeyClass;
      secondaryKeyFieldName = secondaryKeyName;
    }

    private Class<SK> secondaryKeyClass;
    private String secondaryKeyFieldName;
    private PrimaryIndex<PK, E> primaryIndex;
    private Map<SK, Map<PK, E>> index = new HashMap<SK, Map<PK, E>>();

    public int compare(SK key1, SK key2) {
      if (key1 == null && key2 == null) {
        return 0;
      } else if (key1 == null) {
        return -1;
      } else if (key2 == null) {
        return 1;
      } else {

        // todo as strategies created on init based on secondaryKeyClass
        // todo use Comparable, look for key fields, etc
        return key1.hashCode() - key2.hashCode();
      }
    }

    public E put(PK primaryKey, SK secondaryKey, E entity) throws DatabaseException {
      Map<PK, E> values = index.get(secondaryKey);
      if (values == null) {
        values = new HashMap<PK, E>();
        index.put(secondaryKey, values);
      }
      E previous = values.put(primaryKey, entity);
      return previous;
    }


    @Override
    public E get(SK key) throws DatabaseException {
      Map<PK, E> values = index.get(key);
      return values == null || values.isEmpty() ? null : values.values().iterator().next();
    }

    private transient Method secondaryKeyGetter;

    public SK getSecondaryKey(E entity) throws DatabaseException {
      try {
        if (secondaryKeyGetter == null) {
          Field field = entity.getClass().getDeclaredField(secondaryKeyFieldName);
          StringBuilder sb = new StringBuilder(field.getName().length() + 3);
          if (field.getType() == boolean.class) {
            sb.append("is");
          } else {
            sb.append("get");
          }
          sb.append(Character.toUpperCase(field.getName().charAt(0)));
          if (field.getName().length() > 1) {
            sb.append(field.getName().substring(1));
          }
          secondaryKeyGetter = entity.getClass().getDeclaredMethod(sb.toString());
        }
        return (SK) secondaryKeyGetter.invoke(entity);
      } catch (Exception e) {
        throw new InstantiatedDatabaseException(e);
      }
    }

    /**
     * compares a primary key with Map.Entry<PK, E> or SecondaryIndex.Entity
     * future use for PrimaryKeys.entities(
     */
    private Comparator primaryKeyComparator = new Comparator() {
      @Override
      public int compare(Object o1, Object o2) {
        if (o1 instanceof SecondaryIndex.Entity) {
          return SecondaryIndex.this.primaryIndex.compare(((Entity) o1).primaryKey, (PK) o2);
        } else if (o2 instanceof SecondaryIndex.Entity) {
          return SecondaryIndex.this.primaryIndex.compare((PK) o1, ((Entity) o2).primaryKey);
        } else if (o1 instanceof Map.Entry) {
          return SecondaryIndex.this.primaryIndex.compare((PK) ((Map.Entry) o1).getKey(), (PK) o2);
        } else {
          return SecondaryIndex.this.primaryIndex.compare((PK) o1, (PK) ((Map.Entry) o2).getKey());
        }
      }
    };

    /**
     * compares entity with secondary key.
     */
    private Comparator secondaryKeyComparator = new Comparator() {
      @Override
      public int compare(Object o1, Object o2) {
        if (o1 == null && o2 == null) {
          return 0;
        } else if (o1 == null) {
          return -1;
        } else if (o2 == null) {
          return 1;
//        } else if (o1 instanceof DomainEntityObject) {
//          return getSecondaryKey((E) o1).compareTo((SK) o2);
//        } else if (o2 instanceof DomainEntityObject) {
//          return ((SK) o1).compareTo(getSecondaryKey((E) o2));
        } else if (o1 instanceof InstantiatedDomainStore.SecondaryIndex.Entity) {
          return SecondaryIndex.this.compare(((Entity) o1).getSecondaryKey(), (SK) o2);
        } else if (o2 instanceof InstantiatedDomainStore.SecondaryIndex.Entity) {
          return SecondaryIndex.this.compare((SK) o1, ((Entity) o2).getSecondaryKey());
        } else {
          throw new RuntimeException();
        }
      }
    };

    public class Entity {
      private PK primaryKey;
      private SK secondaryKey;
      private E entity;

      public Entity() {
      }

      public Entity(E entity) {
        this(SecondaryIndex.this.getSecondaryKey(entity), SecondaryIndex.this.primaryIndex.getPrimaryKey(entity), entity);
      }

      public Entity(SK secondaryKey, PK primaryKey, E entity) {
        this.secondaryKey = secondaryKey;
        this.primaryKey = primaryKey;
        this.entity = entity;
      }

      public SK getSecondaryKey() {
        return secondaryKey;
      }

      public void setSecondaryKey(SK secondaryKey) {
        this.secondaryKey = secondaryKey;
      }

      public E getEntity() {
        return entity;
      }

      public void setEntity(E entity) {
        this.entity = entity;
      }
    }

    @Override
    public EntityCursor<E> entities(SK fromKey, boolean fromInclusive, SK toKey, boolean toInclusive) throws DatabaseException {

      List<Entity> orderedEntities = new ArrayList<Entity>(primaryIndex.index.size());
      for (Map.Entry<SK, Map<PK, E>> skEntry : index.entrySet()) {
        for (Map.Entry<PK, E> pkEntry : skEntry.getValue().entrySet()) {
          orderedEntities.add(new Entity(skEntry.getKey(), pkEntry.getKey(), pkEntry.getValue()));
        }
      }
      Collections.sort(orderedEntities, secondaryKeyComparator);

      int start = Collections.binarySearch(orderedEntities, fromKey, secondaryKeyComparator);
      if (start < 0) {
        start *= -1;
      } else if (!fromInclusive) {
        while (orderedEntities.get(start).secondaryKey.equals(fromKey)) {
          start++;
          if (start == orderedEntities.size()) {
            return new EntityCursor<E>(Collections.<E>emptyList());
          }
        }
      }
      int end = Collections.binarySearch(orderedEntities, toKey, secondaryKeyComparator);
      if (end < 0) {
        end *= -1;
      } else if (toInclusive) {
        while (orderedEntities.get(end).secondaryKey.equals(toKey)) {
          end--;
          if (end == start) {
            break;
          }
        }
      }

      List<E> entities = new ArrayList<E>();
      for (int index = start; index < orderedEntities.size() && index < end; index++) {
        Entity entity = orderedEntities.get(index);
        if (toKey.equals(entity.getSecondaryKey())) {
          entities.add(entity.getEntity());
        }
      }

      return new EntityCursor<E>(entities);

    }

    @Override
    public SecondaryDatabase getDatabase() {
      throw new UnsupportedOperationException();
    }

    @Override
    public Database getKeysDatabase() {
      throw new UnsupportedOperationException();
    }

    @Override
    public com.sleepycat.persist.PrimaryIndex<PK, E> getPrimaryIndex() {
      return primaryIndex;
    }

    @Override
    public EntryBinding<SK> getKeyBinding() {
      throw new UnsupportedOperationException();
    }

    @Override
    public EntityIndex<SK, PK> keysIndex() throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public EntityIndex<PK, E> subIndex(SK key) throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public E get(Transaction txn, SK key, LockMode lockMode) throws DatabaseException {
      return get(key);
    }

    @Override
    public Map<SK, E> map() {
      throw new UnsupportedOperationException();
    }

    @Override
    public SortedMap<SK, E> sortedMap() {
      throw new UnsupportedOperationException();
    }

    @Override
    protected TransactionConfig getAutoCommitTransactionConfig() {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(SK key) throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Transaction txn, SK key, LockMode lockMode) throws DatabaseException {
      return contains(key);
    }

    @Override
    public long count() throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean delete(SK key) throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean delete(Transaction txn, SK key) throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public com.sleepycat.persist.EntityCursor<SK> keys() throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public com.sleepycat.persist.EntityCursor<SK> keys(Transaction txn, CursorConfig config) throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public com.sleepycat.persist.EntityCursor<E> entities() throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public com.sleepycat.persist.EntityCursor<E> entities(Transaction txn, CursorConfig config) throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public com.sleepycat.persist.EntityCursor<SK> keys(SK fromKey, boolean fromInclusive, SK toKey, boolean toInclusive) throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public com.sleepycat.persist.EntityCursor<SK> keys(Transaction txn, SK fromKey, boolean fromInclusive, SK toKey, boolean toInclusive, CursorConfig config) throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public com.sleepycat.persist.EntityCursor<E> entities(Transaction txn, SK fromKey, boolean fromInclusive, SK toKey, boolean toInclusive, CursorConfig config) throws DatabaseException {
      throw new UnsupportedOperationException();
    }
  }


  // todo implement PrimaryEntityCursor and SecondaryEntityCursor
  // todo and set dup-methods abstract in here

  public class EntityCursor<V> implements com.sleepycat.persist.EntityCursor<V> {

    private List<V> entites;
    private int index;

    public EntityCursor(List<V> entites) {
      this.entites = entites;
      this.index = -1;
    }

    @Override
    public V first() throws DatabaseException {
      return entites.isEmpty() ? null : entites.get(0);
    }

    @Override
    public V first(LockMode lockMode) throws DatabaseException {
      return first();
    }

    @Override
    public V last() throws DatabaseException {
      return entites.isEmpty() ? null : entites.get(entites.size() - 1);
    }

    @Override
    public V last(LockMode lockMode) throws DatabaseException {
      return last();
    }

    @Override
    public V next() throws DatabaseException {
      if (entites.isEmpty()) {
        return null;
      }
      index++;
      if (index > entites.size() - 1) {
        return null;
      }
      return entites.get(index);
    }

    @Override
    public V next(LockMode lockMode) throws DatabaseException {
      return next();
    }

    @Override
    public V nextDup() throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public V nextDup(LockMode lockMode) throws DatabaseException {
      return nextDup();
    }

    @Override
    public V nextNoDup() throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public V nextNoDup(LockMode lockMode) throws DatabaseException {
      return nextNoDup();
    }

    @Override
    public V prev() throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public V prev(LockMode lockMode) throws DatabaseException {
      return prev();
    }

    @Override
    public V prevDup() throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public V prevDup(LockMode lockMode) throws DatabaseException {
      return prevDup();
    }

    @Override
    public V prevNoDup() throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public V prevNoDup(LockMode lockMode) throws DatabaseException {
      return prevNoDup();
    }

    @Override
    public V current() throws DatabaseException {
      if (index == -1) {
        throw new NoSuchElementException();
      }
      return entites.get(index);
    }

    @Override
    public V current(LockMode lockMode) throws DatabaseException {
      return current();
    }

    @Override
    public int count() throws DatabaseException {
      return entites.size();
    }

    @Override
    public long countEstimate() throws DatabaseException {
      return count();
    }


    @Override
    public Iterator<V> iterator() {
      throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<V> iterator(LockMode lockMode) {
      return iterator();
    }

    @Override
    public boolean update(V entity) throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean delete() throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public com.sleepycat.persist.EntityCursor<V> dup() throws DatabaseException {
      throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws DatabaseException {

    }

    @Override
    public void setCacheMode(CacheMode cacheMode) {

    }

    @Override
    public CacheMode getCacheMode() {
      return null;
    }
  }

  public static class InstantiatedDatabaseException extends DatabaseException {
    public InstantiatedDatabaseException(Throwable t) {
      super(t);
    }

    public InstantiatedDatabaseException(String message) {
      super(message);
    }

    public InstantiatedDatabaseException(String message, Throwable t) {
      super(message, t);
    }
  }

}
