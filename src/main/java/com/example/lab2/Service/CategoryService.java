package com.example.lab2.Service;

import com.example.lab2.Cache.InMemoryCache;
import com.example.lab2.Model.Category;
import com.example.lab2.Repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final InMemoryCache cache;
    private final RequestCounterService requestCounterService;

    public List<Category> getAllCategories() {
        requestCounterService.increment();
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        requestCounterService.increment();
        if (cache.containsCategory(id)) {
            return cache.getCategory(id);
        }
        Category cat = categoryRepository.findById(id).orElseThrow();
        cache.putCategory(id, cat);
        return cat;
    }

    public Category createCategory(Category category) {
        requestCounterService.increment();
        Category saved = categoryRepository.save(category);
        cache.putCategory(saved.getId(), saved);
        return saved;
    }

    public Category updateCategory(Long id, Category categoryDetails) {
        requestCounterService.increment();
        Category category = getCategoryById(id);
        category.setName(categoryDetails.getName());
        Category updated = categoryRepository.save(category);
        cache.putCategory(updated.getId(), updated);
        return updated;
    }

    public void deleteCategory(Long id) {
        requestCounterService.increment();
        categoryRepository.deleteById(id);
        cache.removeCategory(id);
    }
}
