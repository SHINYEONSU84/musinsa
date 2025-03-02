package org.example.repository;


import org.example.model.Brand;
import org.example.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    Brand findByName(String name);

    @Query("SELECT b FROM Brand b JOIN b.prices p WHERE KEY(p) = :category ORDER BY VALUE(p) ASC")
    List<Brand> findAllByCategoryOrderByPriceAsc(@Param("category") Category category);

    @Query("SELECT b FROM Brand b JOIN b.prices p WHERE KEY(p) = :category ORDER BY VALUE(p) DESC")
    List<Brand> findAllByCategoryOrderByPriceDesc(@Param("category") Category category);
}
