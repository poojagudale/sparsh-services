package com.wit.salon.sparshlite.service;

import com.wit.salon.sparshlite.dto.CsvUploadResponse;
import com.wit.salon.sparshlite.entity.ServiceEntity;
import com.wit.salon.sparshlite.repository.ServiceRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvUploadService {

    private final ServiceRepository serviceRepository;

    public CsvUploadService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    /**
     * Reads a CSV file uploaded by the user, validates each row,
     * and saves valid rows into the SERVICES table.
     *
     * Expected CSV columns: SERVICE_NAME, IS_ACTIVE
     *
     * @param file the uploaded CSV file
     * @return CsvUploadResponse with success/failure details
     */
    public CsvUploadResponse uploadCsvFile(MultipartFile file) {

        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int totalRecords = 0;

        // Step 1: Validate that the file is a CSV
        validateCsvFile(file);

        // Step 2: Parse the CSV file and process each row
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));

             CSVParser csvParser = new CSVParser(reader,
                     CSVFormat.DEFAULT
                             .builder()
                             .setHeader("SERVICE_NAME", "IS_ACTIVE")  // expected headers
                             .setIgnoreHeaderCase(true)               // case-insensitive headers
                             .setTrim(true)                           // trim whitespace
                             .setSkipHeaderRecord(true)               // skip the header row
                             .build())) {

            for (CSVRecord csvRecord : csvParser) {
                totalRecords++;
                long rowNumber = csvRecord.getRecordNumber();

                try {
                    // Step 3: Read values from the CSV row
                    String serviceName = csvRecord.get("SERVICE_NAME");
                    String isActiveStr = csvRecord.get("IS_ACTIVE");

                    // Step 4: Validate each field
                    validateRow(serviceName, isActiveStr, rowNumber, errors);

                    // Step 5: Convert IS_ACTIVE string to Boolean
                    Boolean isActive = parseIsActive(isActiveStr);

                    // Step 6: Create entity and save to database
                    ServiceEntity entity = new ServiceEntity();
                    entity.setServiceName(serviceName);
                    entity.setIsActive(isActive);

                    serviceRepository.save(entity);
                    successCount++;

                } catch (IllegalArgumentException ex) {
                    // Validation error — already added to errors list inside validateRow()
                    // Just skip this row and continue
                } catch (Exception ex) {
                    errors.add("Row " + rowNumber + ": Unexpected error — " + ex.getMessage());
                }
            }

        } catch (Exception ex) {
            throw new RuntimeException("Failed to parse CSV file: " + ex.getMessage(), ex);
        }

        // Step 7: Build and return the response
        int failureCount = totalRecords - successCount;
        String message = (failureCount == 0)
                ? "All records imported successfully!"
                : successCount + " records imported, " + failureCount + " records failed.";

        return new CsvUploadResponse(message, totalRecords, successCount, failureCount, errors);
    }

    /**
     * Validates that the uploaded file is not empty and has a CSV content type.
     */
    private void validateCsvFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("Uploaded file is empty. Please upload a valid CSV file.");
        }

        String contentType = file.getContentType();
        if (contentType != null
                && !contentType.equals("text/csv")
                && !contentType.equals("application/vnd.ms-excel")) {
            throw new RuntimeException("Invalid file type: " + contentType
                    + ". Only CSV files are allowed.");
        }
    }

    /**
     * Validates the values from a single CSV row.
     * Throws IllegalArgumentException if validation fails.
     */
    private void validateRow(String serviceName, String isActiveStr,
                             long rowNumber, List<String> errors) {

        boolean hasError = false;

        if (serviceName == null || serviceName.isBlank())
        {
            errors.add("Row " + rowNumber + ": SERVICE_NAME is empty.");
            hasError = true;
        } else if (serviceName.length() > 100)
        {
            errors.add("Row " + rowNumber + ": SERVICE_NAME exceeds 100 characters.");
            hasError = true;
        }

        if (isActiveStr == null || isActiveStr.isBlank()) {
            errors.add("Row " + rowNumber + ": IS_ACTIVE is empty.");
            hasError = true;
        } else if (!isActiveStr.equals("0") && !isActiveStr.equals("1")
                && !isActiveStr.equalsIgnoreCase("true")
                && !isActiveStr.equalsIgnoreCase("false")) {
            errors.add("Row " + rowNumber + ": IS_ACTIVE must be 0, 1, true, or false. Found: '" + isActiveStr + "'.");
            hasError = true;
        }

        if (hasError) {
            throw new IllegalArgumentException("Validation failed for row " + rowNumber);
        }
    }

    /**
     * Converts IS_ACTIVE string value to a Boolean.
     * Accepts: "1", "true" → true | "0", "false" → false
     */
    private Boolean parseIsActive(String value) {
        if ("1".equals(value) || "true".equalsIgnoreCase(value)) {
            return true;
        }
        return false;
    }
}
