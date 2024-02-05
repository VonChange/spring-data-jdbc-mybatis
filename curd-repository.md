 ## first We don't need global updates, and this is a dangerous operation
 
 ```
    long count();
    void deleteAll();
    Iterable<T> findAll();
 ```

## deleteById is ok 

```
void delete(T entity);
void deleteAll(Iterable<? extends T> entities);
```

## add methods return update num
```
<S extends T> int saveAll(Iterable<S> entities,int batchSize);
<S extends T> int  update(S entity);
<S extends T> int  updateAllField(S entity);
```