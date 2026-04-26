package com.davivienda.factoraje.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.davivienda.factoraje.domain.dto.parameter.ParameterDTO;
import com.davivienda.factoraje.domain.model.ParameterModel;
import com.davivienda.factoraje.exception.ResourceNotFoundException;
import com.davivienda.factoraje.repository.ParameterRepository;

@Service
public class ParameterService {

    private final ParameterRepository parameterRepository;

    public ParameterService(ParameterRepository parameterRepository) {
        this.parameterRepository = parameterRepository;
    }

    public ParameterModel getParameterById(UUID parameterId) {
        return parameterRepository.findById(parameterId)
                .orElseThrow(() -> new ResourceNotFoundException("Parámetro no encontrado"));
    }

    // CREATE
    @Transactional
    public ParameterDTO.response create(ParameterDTO.request dto){

        ParameterModel parameterToSave = new ParameterModel();
        parameterToSave.setKey(dto.getParam_key());
        parameterToSave.setValue(dto.getParam_value());

        ParameterModel parameterSaved = parameterRepository.save(parameterToSave);

        ParameterDTO.response response = new ParameterDTO.response();
        response.setParam_key(parameterSaved.getKey());
        response.setParam_value(parameterSaved.getValue());

        return response;

    }

    // READ ALL 
    public List<ParameterDTO.response> getAllParameters(){

        List<ParameterModel> parameters = parameterRepository.findAll();

        List<ParameterDTO.response> response = parameters.stream()
            .map(parameter -> {
                ParameterDTO.response dto = new ParameterDTO.response();
                dto.setId(parameter.getId());
                dto.setParam_key(parameter.getKey());
                dto.setParam_value(parameter.getValue());
                dto.setDescription("");
                dto.setCategory("all");
                return dto;
            })  
            .collect(Collectors.toList());
        return response;
    }

    // READ ONE
    public ParameterDTO.response findById(UUID id) {

        ParameterModel parameter = parameterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parámetro no encontrado"));

        ParameterDTO.response response = new ParameterDTO.response();
        response.setParam_key(parameter.getKey());
        response.setParam_value(parameter.getValue());

        return response;
    }

    // UPDATE
    public ParameterDTO.response update(UUID id, ParameterDTO.request dto) {
        ParameterModel parameter = parameterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: ID no existe"));
        
        parameter.setKey(dto.getParam_key());
        parameter.setValue(dto.getParam_value());

        ParameterModel parameterUpdated = new ParameterModel();
        parameterUpdated = parameterRepository.save(parameter);

        ParameterDTO.response response = new ParameterDTO.response();
        response.setParam_key(parameterUpdated.getKey());
        response.setParam_value(parameterUpdated.getValue());
        
        return response;
    } 

    // DELETE
    public void delete(UUID id) {
        if (!parameterRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: ID no existe");
        }
        parameterRepository.deleteById(id);
    }

    // ACTUALIZAR CACHÉ DE PARAMETROS
    @Cacheable(value = "parameters", key = "#key")
    public String getValueByKey(String key) {
        System.out.println("--- BUSCANDO EN BD EL PARÁMETRO CON KEY: " + key + " ---");

        return parameterRepository.findByKey(key)
                .map(ParameterModel::getValue)
                .orElseThrow(() -> new ResourceNotFoundException("Parámetro no encontrado: " + key));
    }
}
