package com.davivienda.factoraje.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.davivienda.factoraje.domain.model.EntityModel;
import com.davivienda.factoraje.exception.ResourceNotFoundException;
import com.davivienda.factoraje.repository.EntityRepository;

@Service
public class EntityService {

    private final EntityRepository entityRepository;
    private static final Logger log = LoggerFactory.getLogger(EntityService.class);

    public EntityService(EntityRepository entityRepository) {
        this.entityRepository = entityRepository;
        log.info("EntityService initialized");
    }

    public EntityModel createEntity(EntityModel entity) {
        // if (entity == null) {
        //     log.warn("createEntity called with null entity");
        //     throw new IllegalArgumentException("La entidad no puede ser nula");
        // }
        if (entity == null) {
            log.warn("createEntity called with null entity");
            throw new IllegalArgumentException("La entidad no puede ser nula");
        }
        log.debug("Saving entity with code {}", entity.getCode());
        return entityRepository.save(entity);
    }

    public List<EntityModel> getEntities() {
        log.debug("Fetching all entities");
        return entityRepository.findAll();
    }

    public List<EntityModel> getEntitiesByType(boolean isEntityType) {
        log.debug("Filtering entities by type {}", isEntityType);
        return entityRepository.findAll().stream()
                .filter(e -> e.getEntityType() == isEntityType)
                .collect(Collectors.toList());
    }

    public EntityModel findByCode(String code) {
        // if (code == null || code.trim().isEmpty()) {
        //     log.warn("findByCode called with empty code");
        //     throw new IllegalArgumentException("El parámetro 'code' no puede ser vacío");
        // }
        // return entityRepository.findAll().stream()
        //         .filter(e -> code.equals(e.getCode()))
        //         .findFirst()
        //         .orElse(null);

        if (code == null || code.trim().isEmpty()) {
            log.warn("findByCode called with empty code");
            throw new IllegalArgumentException("El parámetro 'code' no puede ser null");
        }

        // 2. Consulta a la base de datos (una sola vez)
        // Se asume que tu repositorio devuelve un Optional<EntityModel>,
        // lo cual es la mejor práctica para evitar NullPointerExceptions.
        java.util.Optional<EntityModel> entityOptional = entityRepository.findByCode(code);

        // 3. Retorno del resultado
        // Usamos orElse(null) para devolver la entidad si se encuentra,
        // o null si el Optional está vacío. Es una forma limpia y segura.
        return entityOptional.orElse(null);
    }

    public EntityModel getEntityById(UUID id) {
        // if (id == null) {
        //     log.warn("getEntityById called with null id");
        //     throw new IllegalArgumentException("El parámetro 'id' no puede ser nulo");
        // }
        // return entityRepository.findById(id)
        //         .orElseThrow(() -> new ResourceNotFoundException("Entidad no encontrada con id=" + id));

        if (id == null) {
            log.warn("getEntityById called with null id");
            throw new IllegalArgumentException("El parámetro 'id' no puede ser nulo");
        }
        return entityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entidad no encontrada con id=" + id));
    }

    public EntityModel updateEntity(UUID id, EntityModel entity) {

        if (id == null) {
            log.warn("getEntityById called with null id");
            throw new IllegalArgumentException("El parámetro 'id' no puede ser nulo");
        }

        if (entity == null) {
            log.warn("updateEntity called with null entity model");
            throw new IllegalArgumentException("El cuerpo de la petición no puede ser nulo");
        }

        EntityModel existing = entityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entidad no encontrada"));

        existing.setCode(entity.getCode());
        existing.setName(entity.getName());
        existing.setNit(entity.getNit());
        existing.setAccountBank(entity.getAccountBank());
        existing.setEmail(entity.getEmail());

        log.debug("Updating entity {}", id);
        return entityRepository.save(existing);
    }

    public void deleteEntity(UUID id) {
        if (id == null) {
            log.warn("deleteEntity called with null id");
            throw new IllegalArgumentException("El parámetro 'id' no puede ser nulo");
        }
        if (!entityRepository.existsById(id)) {
            log.warn("Entity with id {} not found for deletion", id);
            throw new ResourceNotFoundException("Entidad no encontrada");
        }
        log.debug("Deleting entity {}", id);
        entityRepository.deleteById(id);
    }
}
