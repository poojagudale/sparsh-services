package com.wit.salon.sparshlite.service;

import com.wit.salon.sparshlite.dto.ServiceDTO;

import java.util.List;

public interface ServiceService {

    ServiceDTO createService(ServiceDTO serviceDTO);

    List<ServiceDTO> getAllServices();

    ServiceDTO getServiceById(Long id);

    ServiceDTO updateService(Long id, ServiceDTO serviceDTO);

    void deleteService(Long id);
}
