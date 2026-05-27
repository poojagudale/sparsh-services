package com.wit.salon.sparshlite.service.impl;

import com.wit.salon.sparshlite.dto.ServiceDTO;
import com.wit.salon.sparshlite.entity.ServiceEntity;
import com.wit.salon.sparshlite.exception.ResourceNotFoundException;
import com.wit.salon.sparshlite.mapper.ServiceMapper;
import com.wit.salon.sparshlite.repository.ServiceRepository;
import com.wit.salon.sparshlite.service.ServiceService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository serviceRepository;

    public ServiceServiceImpl(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    public ServiceDTO createService(ServiceDTO serviceDTO) {
        ServiceEntity entity = ServiceMapper.toEntity(serviceDTO);
        ServiceEntity savedEntity = serviceRepository.save(entity);
        return ServiceMapper.toDTO(savedEntity);
    }

    @Override
    public List<ServiceDTO> getAllServices() {
        List<ServiceEntity> entities = serviceRepository.findAll();
        return entities.stream()
                .map(ServiceMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ServiceDTO getServiceById(Long id) {
        ServiceEntity entity = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", "serviceId", id));
        return ServiceMapper.toDTO(entity);
    }

    @Override
    public ServiceDTO updateService(Long id, ServiceDTO serviceDTO) {
        ServiceEntity existingEntity = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", "serviceId", id));

        existingEntity.setServiceName(serviceDTO.getServiceName());
        existingEntity.setIsActive(serviceDTO.getIsActive());

        ServiceEntity updatedEntity = serviceRepository.save(existingEntity);
        return ServiceMapper.toDTO(updatedEntity);
    }

    @Override
    public void deleteService(Long id) {
        ServiceEntity entity = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", "serviceId", id));
        serviceRepository.delete(entity);
    }
}
