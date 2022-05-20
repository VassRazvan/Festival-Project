package repository;

public interface Repository<T, Tid> {
    void add(T elem) throws Exception;
    void delete(Integer id);
    void update(T elem);
    T findById(Tid id);
    Iterable<T> findAll();
}