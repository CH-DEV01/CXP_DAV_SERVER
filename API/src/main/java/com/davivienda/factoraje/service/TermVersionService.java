package com.davivienda.factoraje.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.davivienda.factoraje.domain.dto.termVersion.TermVersionDTORequest;
import com.davivienda.factoraje.domain.dto.termVersion.TermVersionDTOResponse;
import com.davivienda.factoraje.domain.model.TermTypeModel;
import com.davivienda.factoraje.domain.model.TermVersionModel;
import com.davivienda.factoraje.exception.ResourceNotFoundException;
import com.davivienda.factoraje.repository.TermVersionRepository;

@Service
public class TermVersionService {

    private final TermVersionRepository termVersionRepository;
    private final TermTypeService termTypeService;

    public TermVersionService(TermVersionRepository termVersionRepository, TermTypeService termTypeService){
        this.termVersionRepository = termVersionRepository;
        this.termTypeService = termTypeService;
    }

    // CREATE
    @Transactional
    public TermVersionDTOResponse create(TermVersionDTORequest dto){

        TermVersionModel termVersionToSave = new TermVersionModel();
        TermTypeModel termVersion = termTypeService.findByIdEntity(dto.getTermTypeId());
        
        termVersionToSave.setTermType(termVersion);
        termVersionToSave.setVersionNumber(dto.getVersionNumber());
        termVersionToSave.setLegalText(dto.getLegalText());
        termVersionToSave.setIsActive(dto.getIsActive());
        termVersionToSave.setPublicationDate(Instant.now());

        TermVersionModel termVersionSaved = termVersionRepository.save(termVersionToSave);

        TermVersionDTOResponse response = new TermVersionDTOResponse();
        response.setId(termVersionSaved.getId());
        response.setIsActive(termVersionSaved.getIsActive());
        response.setLegalText(termVersionSaved.getLegalText());
        response.setTermTypeId(termVersionSaved.getTermType().getId());
        response.setVersionNumber(termVersionSaved.getVersionNumber());

        return response;

    }

    // READ ALL 
    public List<TermVersionDTOResponse> getAllTermVersions(){

        List<TermVersionModel> termVersions = termVersionRepository.findAll();

        List<TermVersionDTOResponse> response = termVersions.stream()
            .map(termVersion -> {
                TermVersionDTOResponse dto = new TermVersionDTOResponse();
                dto.setId(termVersion.getId());
                dto.setIsActive(termVersion.getIsActive());
                dto.setLegalText(termVersion.getLegalText());
                dto.setTermTypeId(termVersion.getTermType().getId());
                dto.setVersionNumber(termVersion.getVersionNumber());
                return dto;
            })
            .collect(Collectors.toList());
        return response;
    }

    // READ ONE
    public TermVersionDTOResponse findById(UUID id) {

        TermVersionModel termVersion = termVersionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de término no encontrado"));

        TermVersionDTOResponse response = new TermVersionDTOResponse();
        response.setId(termVersion.getId());
        response.setIsActive(termVersion.getIsActive());
        response.setLegalText(termVersion.getLegalText());
        response.setTermTypeId(termVersion.getTermType().getId());
        response.setVersionNumber(termVersion.getVersionNumber());

        return response;
    }

    public TermVersionModel findByIdEntity(UUID id) {

        TermVersionModel termVersion = termVersionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de término no encontrado"));

        return termVersion;
    }

    // UPDATE
    @Transactional
    public TermVersionDTOResponse update(UUID id, TermVersionDTORequest dto) {

        TermVersionModel termVersion = termVersionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: ID no existe"));
        

        TermTypeModel termType = termTypeService.findByIdEntity(dto.getTermTypeId());
        
        termVersion.setIsActive(dto.getIsActive());
        termVersion.setLegalText(dto.getLegalText());
        termVersion.setTermType(termType);

        TermVersionModel termVersionUpdated = termVersionRepository.save(termVersion);

        TermVersionDTOResponse response = new TermVersionDTOResponse();
        response.setId(termVersionUpdated.getId());
        response.setIsActive(termVersionUpdated.getIsActive());
        response.setLegalText(termVersionUpdated.getLegalText());
        response.setTermTypeId(termVersionUpdated.getTermType().getId());
        response.setVersionNumber(termVersionUpdated.getVersionNumber());
        
        return response;
    } 

    // DELETE
    public void delete(UUID id) {
        if (!termVersionRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: ID no existe");
        }
        termVersionRepository.deleteById(id);
    }
}
