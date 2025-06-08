package com.example.lab2.Service;

import com.example.lab2.Cache.InMemoryCache;
import com.example.lab2.Model.Category;
import com.example.lab2.Repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private InMemoryCache cache;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void getCategoryByIdShouldReturnFromCache() {
        Long id = 1L;
        Category mockCategory = mock(Category.class);
        when(cache.containsCategory(id)).thenReturn(true);
        when(cache.getCategory(id)).thenReturn(mockCategory);

        Category result = categoryService.getCategoryById(id);

        assertNotNull(result);
        verify(cache).containsCategory(id);
        verify(cache).getCategory(id);
        verify(categoryRepository, never()).findById(anyLong());
    }

    @Test
    void getCategoryByIdShouldFetchFromRepoAndCache() {
        Long id = 1L;
        Category mockCategory = mock(Category.class);
        when(cache.containsCategory(id)).thenReturn(false);
        when(categoryRepository.findById(id)).thenReturn(Optional.of(mockCategory));
        when(mockCategory.getId()).thenReturn(id);

        Category result = categoryService.getCategoryById(id);

        assertNotNull(result);
        verify(cache).containsCategory(id);
        verify(categoryRepository).findById(id);
        verify(cache).putCategory(id, mockCategory);
    }

    @Test
    void createCategoryShouldSaveAndCache() {
        Category mockCategory = mock(Category.class);
        when(categoryRepository.save(mockCategory)).thenReturn(mockCategory);
        when(mockCategory.getId()).thenReturn(1L);

        Category result = categoryService.createCategory(mockCategory);

        assertNotNull(result);
        verify(categoryRepository).save(mockCategory);
        verify(cache).putCategory(1L, mockCategory);
    }

    @Test
    void updateCategoryShouldUpdateAndCache() {
        Long id = 1L;
        Category mockExistingCategory = mock(Category.class);
        Category mockUpdatedDetails = mock(Category.class);

        when(cache.containsCategory(id)).thenReturn(true);
        when(cache.getCategory(id)).thenReturn(mockExistingCategory);
        when(categoryRepository.save(mockExistingCategory)).thenReturn(mockExistingCategory);
        when(mockUpdatedDetails.getName()).thenReturn("new name");

        Category result = categoryService.updateCategory(id, mockUpdatedDetails);

        assertNotNull(result);
        verify(mockExistingCategory).setName("new name");
        verify(categoryRepository).save(mockExistingCategory);
        verify(cache).putCategory(id, mockExistingCategory);
    }

    @Test
    void deleteCategoryShouldRemoveFromRepoAndCache() {
        Long id = 1L;

        categoryService.deleteCategory(id);

        verify(categoryRepository).deleteById(id);
        verify(cache).removeCategory(id);
    }
}