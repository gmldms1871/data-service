package com.example.demo.service;

import com.example.demo.domain.Products;
import com.example.demo.domain.UploadRecord;
import com.example.demo.domain.UploadRecord.Badge;
import com.example.demo.repository.ProductsRepository;
import com.example.demo.repository.UploadRecordRepository;
import com.opencsv.CSVReader;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import jakarta.servlet.http.HttpSession;
import static org.springframework.http.HttpStatus.*;

import java.io.InputStreamReader;
import java.util.*;

@Service
public class UploadService {
    private final UploadRecordRepository uploadRepo;
    private final ProductsRepository productRepo;

    public UploadService(UploadRecordRepository uploadRepo, ProductsRepository productRepo) {
        this.uploadRepo = uploadRepo;
        this.productRepo = productRepo;
    }

    public UploadRecord handleUpload(String productId, MultipartFile file, HttpSession session) {
        // 1) 세션에서 loginCompanyId 확인
        String sessionCompanyId = (String) session.getAttribute("loginCompanyId");
        if (sessionCompanyId == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "로그인 세션이 없습니다.");
        }
        Products product = productRepo.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Product not found"));
        if (!Objects.equals(product.getCompanyId(), sessionCompanyId)) {
            throw new ResponseStatusException(FORBIDDEN,
                    String.format("Not allowed to upload: sessionCompanyId=%s, productCompanyId=%s",
                            sessionCompanyId, product.getCompanyId()));
        }

        // 2) CSV 검증 & 메트릭 계산
        String fn = file.getOriginalFilename();
        if (fn == null || !fn.toLowerCase().endsWith(".csv")) {
            throw new ResponseStatusException(UNSUPPORTED_MEDIA_TYPE, "CSV 파일만 업로드 가능합니다.");
        }

        List<String[]> rows;
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            rows = reader.readAll();
        } catch (Exception e) {
            throw new ResponseStatusException(BAD_REQUEST, "CSV 파싱 중 오류 발생");
        }
        if (rows.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "CSV에 데이터가 없습니다.");
        }

        // 헤더 제외
        rows.remove(0);
        int totalRows = rows.size();
        int totalCols = rows.get(0).length;

        // 결측치 비율
        int totalCells = totalRows * totalCols;
        long missing = rows.stream()
                .flatMap(Arrays::stream)
                .filter(cell -> cell == null || cell.trim().isEmpty())
                .count();
        double missingRate = (double) missing / totalCells;

        // 중복 비율
        Set<String> seen = new HashSet<>();
        long dupCount = rows.stream()
                .map(r -> String.join("¶", r))
                .filter(rowStr -> !seen.add(rowStr))
                .count();
        double duplicationRate = (double) dupCount / totalRows;

        // 이상치 비율 (Z-score ±3)
        List<Double> numeric = new ArrayList<>();
        for (String[] r : rows) {
            for (String cell : r) {
                try { numeric.add(Double.parseDouble(cell)); }
                catch (NumberFormatException ignored) {}
            }
        }
        DescriptiveStatistics stats = new DescriptiveStatistics();
        numeric.forEach(stats::addValue);
        long outliers = numeric.stream()
                .filter(v -> Math.abs((v - stats.getMean()) / stats.getStandardDeviation()) > 3)
                .count();
        double outlierRate = (double) outliers / totalRows;

        // 3) 기존 레코드 조회 및 저장 (upsert)
        UploadRecord rec = uploadRepo.findByProductId(productId)
                .map(existing -> {
                    existing.setFilename(fn);
                    existing.setMissingRate(missingRate);
                    existing.setDuplicationRate(duplicationRate);
                    existing.setOutlierRate(outlierRate);
                    existing.setBadge(determineBadge(missingRate, duplicationRate, outlierRate));
                    return existing;
                })
                .orElseGet(() -> UploadRecord.builder()
                        .productId(productId)
                        .filename(fn)
                        .missingRate(missingRate)
                        .duplicationRate(duplicationRate)
                        .outlierRate(outlierRate)
                        .badge(determineBadge(missingRate, duplicationRate, outlierRate))
                        .build());

        return uploadRepo.save(rec);
    }

    private Badge determineBadge(double missingRate, double duplicationRate, double outlierRate) {
        return (missingRate <= 0.05
                && duplicationRate <= 0.03
                && outlierRate <= 0.02)
                ? Badge.Cleaned
                : Badge.Raw;
    }
}