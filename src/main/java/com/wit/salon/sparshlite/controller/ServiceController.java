package com.wit.salon.sparshlite.controller;

import com.wit.salon.sparshlite.dto.CsvUploadResponse;
import com.wit.salon.sparshlite.dto.ServiceDTO;
import com.wit.salon.sparshlite.service.CsvUploadService;
import com.wit.salon.sparshlite.service.ServiceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private final ServiceService serviceService;
    private final CsvUploadService csvUploadService;

    public ServiceController(ServiceService serviceService,
                             CsvUploadService csvUploadService) {
        this.serviceService = serviceService;
        this.csvUploadService = csvUploadService;
    }

    // POST /api/services — Create a new service
    @PostMapping
    public ResponseEntity<ServiceDTO> createService(@Valid @RequestBody ServiceDTO serviceDTO) {
        ServiceDTO created = serviceService.createService(serviceDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // GET /api/services — Get all services
    @GetMapping
    public ResponseEntity<List<ServiceDTO>> getAllServices() {
        List<ServiceDTO> services = serviceService.getAllServices();
        return ResponseEntity.ok(services);
    }

    // GET /api/services/{id} — Get a service by ID
    @GetMapping("/{id}")
    public ResponseEntity<ServiceDTO> getServiceById(@PathVariable Long id) {
        ServiceDTO service = serviceService.getServiceById(id);
        return ResponseEntity.ok(service);
    }

    // PUT /api/services/{id} — Update a service
    @PutMapping("/{id}")
    public ResponseEntity<ServiceDTO> updateService(
            @PathVariable Long id,
            @Valid @RequestBody ServiceDTO serviceDTO) {
        ServiceDTO updated = serviceService.updateService(id, serviceDTO);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/services/{id} — Delete a service
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteService(@PathVariable Long id) {
        serviceService.deleteService(id);
        return ResponseEntity.ok("Service deleted successfully with id: " + id);
    }

    // POST /api/services/upload — Upload CSV file to import services
    @PostMapping("/upload")
    public ResponseEntity<CsvUploadResponse> uploadCsvFile(
            @RequestParam("file") MultipartFile file) {

        CsvUploadResponse response = csvUploadService.uploadCsvFile(file);

        // Return 207 Multi-Status if some rows failed, else 200 OK
        if (response.getFailureCount() > 0) {
            return new ResponseEntity<>(response, HttpStatus.MULTI_STATUS);
        }
        return ResponseEntity.ok(response);
    }
}
