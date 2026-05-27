package com.wit.salon.sparshlite.mapper;

import com.wit.salon.sparshlite.dto.ServiceDTO;
import com.wit.salon.sparshlite.entity.ServiceEntity;

public class ServiceMapper {

    private ServiceMapper() {
        // Utility class — prevent instantiation
    }

    public static ServiceDTO toDTO(ServiceEntity entity) {
        ServiceDTO dto = new ServiceDTO();
        dto.setServiceId(entity.getServiceId());
        dto.setServiceName(entity.getServiceName());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    public static ServiceEntity toEntity(ServiceDTO dto) {
        ServiceEntity entity = new ServiceEntity();
        entity.setServiceId(dto.getServiceId());
        entity.setServiceName(dto.getServiceName());
        entity.setIsActive(dto.getIsActive());
        return entity;
    }
}
