package com.example.lab2.Service;

import com.example.lab2.Cache.InMemoryCache;
import com.example.lab2.Dto.WikipediaDto;
import com.example.lab2.Model.Category;
import com.example.lab2.Model.WikiPage;
import com.example.lab2.Repository.WikiPageRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WikiServiceTest {

    @Mock
    private WikiPageRepository wikiPageRepository;

    @Mock
    private InMemoryCache cache;

    @InjectMocks
    private WikiService wikiService;

    @Test
    void fetchAndSaveWikiPageShouldSaveAndCachePage() {
        String term = "test";
        Long categoryId = 1L;
        WikipediaDto mockDto = mock(WikipediaDto.class);
        WikiPage mockPage = mock(WikiPage.class);
        Category mockCategory = mock(Category.class);

        when(mockDto.getTerm()).thenReturn(term);
        when(mockDto.getSummary()).thenReturn("summary");
        when(wikiPageRepository.save(any(WikiPage.class))).thenReturn(mockPage);
        when(mockPage.getId()).thenReturn(1L);
        when(mockPage.getCategory()).thenReturn(mockCategory);
        when(mockCategory.getId()).thenReturn(categoryId);

        WikiPage result = wikiService.fetchAndSaveWikiPage(term, categoryId);

        assertNotNull(result);
        verify(wikiPageRepository).save(any(WikiPage.class));
        verify(cache).putWikiPage(anyLong(), eq(mockPage));
        verify(cache).removeWikiPagesByCategory(categoryId);
    }

    @Test
    void findAllWikiPagesShouldReturnAllPages() {
        WikiPage mockPage1 = mock(WikiPage.class);
        WikiPage mockPage2 = mock(WikiPage.class);
        List<WikiPage> mockPages = Arrays.asList(mockPage1, mockPage2);
        when(wikiPageRepository.findAll()).thenReturn(mockPages);

        List<WikiPage> result = wikiService.findAllWikiPages();

        assertEquals(2, result.size());
        verify(wikiPageRepository).findAll();
    }

    @Test
    void findWikiPageByIdShouldReturnFromCache() {
        Long id = 1L;
        WikiPage mockPage = mock(WikiPage.class);
        when(cache.containsWikiPage(id)).thenReturn(true);
        when(cache.getWikiPage(id)).thenReturn(mockPage);

        WikiPage result = wikiService.findWikiPageById(id);

        assertNotNull(result);
        verify(cache).containsWikiPage(id);
        verify(cache).getWikiPage(id);
        verify(wikiPageRepository, never()).findById(anyLong());
    }

    @Test
    void findWikiPageByIdShouldFetchFromRepoAndCache() {
        Long id = 1L;
        WikiPage mockPage = mock(WikiPage.class);
        when(cache.containsWikiPage(id)).thenReturn(false);
        when(wikiPageRepository.findById(id)).thenReturn(Optional.of(mockPage));
        when(mockPage.getId()).thenReturn(id);

        WikiPage result = wikiService.findWikiPageById(id);

        assertNotNull(result);
        verify(cache).containsWikiPage(id);
        verify(wikiPageRepository).findById(id);
        verify(cache).putWikiPage(id, mockPage);
    }

    @Test
    void saveWikiPageShouldSaveAndCache() {
        WikiPage mockPage = mock(WikiPage.class);
        Category mockCategory = mock(Category.class);
        when(wikiPageRepository.save(mockPage)).thenReturn(mockPage);
        when(mockPage.getId()).thenReturn(1L);
        when(mockPage.getCategory()).thenReturn(mockCategory);
        when(mockCategory.getId()).thenReturn(1L);

        WikiPage result = wikiService.saveWikiPage(mockPage);

        assertNotNull(result);
        verify(wikiPageRepository).save(mockPage);
        verify(cache).putWikiPage(anyLong(), eq(mockPage));
        verify(cache).removeWikiPagesByCategory(1L);
    }

    @Test
    void bulkSaveWikiPagesShouldSaveAllPages() {
        WikipediaDto mockDto1 = mock(WikipediaDto.class);
        WikipediaDto mockDto2 = mock(WikipediaDto.class);
        WikiPage mockPage1 = mock(WikiPage.class);
        WikiPage mockPage2 = mock(WikiPage.class);

        when(mockDto1.getTerm()).thenReturn("term1");
        when(mockDto1.getSummary()).thenReturn("summary1");
        when(mockDto2.getTerm()).thenReturn("term2");
        when(mockDto2.getSummary()).thenReturn("summary2");
        when(wikiPageRepository.save(any(WikiPage.class)))
                .thenReturn(mockPage1)
                .thenReturn(mockPage2);

        List<WikiPage> result = wikiService.bulkSaveWikiPages(Arrays.asList(mockDto1, mockDto2));

        assertEquals(2, result.size());
        verify(wikiPageRepository, times(2)).save(any(WikiPage.class));
    }

    @Test
    void updateWikiPageShouldUpdateAndCache() {
        Long id = 1L;
        WikiPage mockExistingPage = mock(WikiPage.class);
        WikiPage mockUpdatedDetails = mock(WikiPage.class);
        Category mockCategory = mock(Category.class);

        when(cache.containsWikiPage(id)).thenReturn(true);
        when(cache.getWikiPage(id)).thenReturn(mockExistingPage);
        when(wikiPageRepository.save(mockExistingPage)).thenReturn(mockExistingPage);
        when(mockExistingPage.getCategory()).thenReturn(mockCategory);
        when(mockCategory.getId()).thenReturn(1L);

        WikiPage result = wikiService.updateWikiPage(id, mockUpdatedDetails);

        assertNotNull(result);
        verify(mockExistingPage).setTerm(anyString());
        verify(mockExistingPage).setSummary(anyString());
        verify(wikiPageRepository).save(mockExistingPage);
        verify(cache).putWikiPage(id, mockExistingPage);
        verify(cache).removeWikiPagesByCategory(1L);
    }

    @Test
    void deleteWikiPageShouldRemoveFromRepoAndCache() {
        Long id = 1L;
        WikiPage mockPage = mock(WikiPage.class);
        Category mockCategory = mock(Category.class);

        when(cache.containsWikiPage(id)).thenReturn(true);
        when(cache.getWikiPage(id)).thenReturn(mockPage);
        when(mockPage.getCategory()).thenReturn(mockCategory);
        when(mockCategory.getId()).thenReturn(1L);

        wikiService.deleteWikiPage(id);

        verify(wikiPageRepository).deleteById(id);
        verify(cache).removeWikiPage(id);
        verify(cache).removeWikiPagesByCategory(1L);
    }

    @Test
    void findWikiPagesByCategoryShouldReturnFromCache() {
        Long categoryId = 1L;
        WikiPage mockPage = mock(WikiPage.class);
        List<WikiPage> mockPages = Collections.singletonList(mockPage);

        when(cache.containsWikiPagesByCategory(categoryId)).thenReturn(true);
        when(cache.getWikiPagesByCategory(categoryId)).thenReturn(mockPages);

        List<WikiPage> result = wikiService.findWikiPagesByCategory(categoryId);

        assertEquals(1, result.size());
        verify(cache).containsWikiPagesByCategory(categoryId);
        verify(cache).getWikiPagesByCategory(categoryId);
        verify(wikiPageRepository, never()).findByCategoryId(anyLong());
    }
}