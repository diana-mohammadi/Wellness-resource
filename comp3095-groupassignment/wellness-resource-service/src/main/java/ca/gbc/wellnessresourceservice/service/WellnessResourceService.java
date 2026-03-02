package ca.gbc.wellnessresourceservice.service;

import ca.gbc.wellnessresourceservice.dto.WellnessResourceRequest;
import ca.gbc.wellnessresourceservice.dto.WellnessResourceResponse;
import ca.gbc.wellnessresourceservice.model.WellnessResource;
import ca.gbc.wellnessresourceservice.repository.WellnessResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WellnessResourceService {

    private final WellnessResourceRepository repository;

    @Transactional
    @CacheEvict(value = {"resources", "resourcesByCategory"}, allEntries = true)
    public WellnessResourceResponse createResource(WellnessResourceRequest request) {
        WellnessResource resource = new WellnessResource();
        resource.setTitle(request.getTitle());
        resource.setDescription(request.getDescription());
        resource.setCategory(request.getCategory());
        resource.setUrl(request.getUrl());

        WellnessResource saved = repository.save(resource);
        log.info("Created resource with ID: {}", saved.getResourceId());

        return mapToResponse(saved);
    }

    @Cacheable(value = "resources")
    public List<WellnessResourceResponse> getAllResources() {
        log.info("Fetching all resources from database");
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "resourceById", key = "#id")
    public WellnessResourceResponse getResourceById(Long id) {
        log.info("Fetching resource with ID: {}", id);
        WellnessResource resource = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + id));
        return mapToResponse(resource);
    }

    @Cacheable(value = "resourcesByCategory", key = "#category")
    public List<WellnessResourceResponse> getResourcesByCategory(String category) {
        log.info("Fetching resources for category: {}", category);
        return repository.findByCategory(category).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<WellnessResourceResponse> searchResources(String keyword) {
        log.info("Searching resources with keyword: {}", keyword);
        return repository.searchByKeyword(keyword).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @CachePut(value = "resourceById", key = "#id")
    @CacheEvict(value = {"resources", "resourcesByCategory"}, allEntries = true)
    public WellnessResourceResponse updateResource(Long id, WellnessResourceRequest request) {
        WellnessResource resource = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + id));

        resource.setTitle(request.getTitle());
        resource.setDescription(request.getDescription());
        resource.setCategory(request.getCategory());
        resource.setUrl(request.getUrl());

        WellnessResource updated = repository.save(resource);
        log.info("Updated resource with ID: {}", id);

        return mapToResponse(updated);
    }

    @Transactional
    @CacheEvict(value = {"resources", "resourceById", "resourcesByCategory"}, allEntries = true)
    public void deleteResource(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Resource not found with id: " + id);
        }
        repository.deleteById(id);
        log.info("Deleted resource with ID: {}", id);
    }

    private WellnessResourceResponse mapToResponse(WellnessResource resource) {
        return WellnessResourceResponse.builder()
                .resourceId(resource.getResourceId())
                .title(resource.getTitle())
                .description(resource.getDescription())
                .category(resource.getCategory())
                .url(resource.getUrl())
                .createdAt(resource.getCreatedAt())
                .updatedAt(resource.getUpdatedAt())
                .build();
    }
}
