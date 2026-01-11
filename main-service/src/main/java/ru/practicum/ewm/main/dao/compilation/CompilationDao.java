package ru.practicum.ewm.main.dao.compilation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.main.model.Compilation;

@Repository
public interface CompilationDao {
    Compilation save(Compilation compilation);

    void deleteById(Long id);

    Compilation findById(Long id);

    Page<Compilation> findAll(Pageable pageable);
}
