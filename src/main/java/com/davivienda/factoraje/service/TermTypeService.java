package com.davivienda.factoraje.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.davivienda.factoraje.domain.dto.termType.TermTypeDTORequest;
import com.davivienda.factoraje.domain.dto.termType.TermTypeDTOResponse;
import com.davivienda.factoraje.domain.model.TermTypeModel;
import com.davivienda.factoraje.exception.ResourceNotFoundException;
import com.davivienda.factoraje.repository.TermTypeRepository;

@Service
public class TermTypeService {

    private final TermTypeRepository termTypeRepository;

    public TermTypeService(TermTypeRepository termTypeRepository){
        this.termTypeRepository = termTypeRepository;
    }
    
    // CREATE
    @Transactional
    public TermTypeDTOResponse create(TermTypeDTORequest dto){

        TermTypeModel termTypeToSave = new TermTypeModel();
        termTypeToSave.setDocumentName(dto.getDocumentName());
        termTypeToSave.setUniqueCode(dto.getUnique_code());

        TermTypeModel termTypeSaved = termTypeRepository.save(termTypeToSave);

        TermTypeDTOResponse response = new TermTypeDTOResponse();
        response.setDocumentName(termTypeSaved.getDocumentName());
        response.setUnique_code(termTypeSaved.getUniqueCode());

        return response;

    }

    // READ ALL 
    public List<TermTypeDTOResponse> getAllTermTypes(){

        List<TermTypeModel> termTypes = termTypeRepository.findAll();

        List<TermTypeDTOResponse> response = termTypes.stream()
            .map(termType -> {
                TermTypeDTOResponse dto = new TermTypeDTOResponse();
                dto.setDocumentName(termType.getDocumentName());
                dto.setUnique_code(termType.getUniqueCode());
                return dto;
            })
            .collect(Collectors.toList());
        return response;
    }

    // READ ONE
    public TermTypeDTOResponse findById(UUID id) {

        TermTypeModel termType = termTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de término no encontrado"));

        TermTypeDTOResponse response = new TermTypeDTOResponse();
        response.setDocumentName(termType.getDocumentName());
        response.setUnique_code(termType.getUniqueCode());

        return response;
    }

    public TermTypeModel findByIdEntity(UUID id) {

        TermTypeModel termType = termTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de término no encontrado"));

        return termType;
    }

    // UPDATE
    public TermTypeDTOResponse update(UUID id, TermTypeDTORequest dto) {

        TermTypeModel termType = termTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: ID no existe"));
        
        termType.setDocumentName(dto.getDocumentName());
        termType.setUniqueCode(dto.getUnique_code());

        TermTypeModel termTypeUpdated = termTypeRepository.save(termType);

        TermTypeDTOResponse response = new TermTypeDTOResponse();
        response.setDocumentName(termTypeUpdated.getDocumentName());
        response.setUnique_code(termTypeUpdated.getUniqueCode());
        
        return response;
    } 

    // DELETE
    public void delete(UUID id) {
        if (!termTypeRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: ID no existe");
        }
        termTypeRepository.deleteById(id);
    }
    
}
