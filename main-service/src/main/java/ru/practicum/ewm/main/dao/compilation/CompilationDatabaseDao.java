package ru.practicum.ewm.main.dao.compilation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.main.model.Compilation;

import java.util.Optional;

public interface CompilationDatabaseDao extends CompilationDao, JpaRepository<Compilation, Long> {

    Optional<Compilation> findByTitle(String title);

    @Override
    Page<Compilation> findAll(Pageable pageable);

    @Query("SELECT c FROM Compilation c WHERE c.pinned = :pinned")
    Page<Compilation> findAllByPinned(@Param("pinned") Boolean pinned, Pageable pageable);
}
