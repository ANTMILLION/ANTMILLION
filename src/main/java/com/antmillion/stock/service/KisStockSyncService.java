package com.antmillion.stock.service;

import com.antmillion.stock.dto.StockDTO;
import com.antmillion.stock.dto.SyncResult;
import com.antmillion.stock.mapper.StockSyncMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class KisStockSyncService {

    private static final Charset CP949 = Charset.forName("CP949");
    private static final String KOSPI_URL = "https://new.real.download.dws.co.kr/common/master/kospi_code.mst.zip";
    private static final String KOSDAQ_URL = "https://new.real.download.dws.co.kr/common/master/kosdaq_code.mst.zip";

    private final StockSyncMapper stockSyncMapper;

    public List<StockDTO> loadKospiStocks(String baseDir) throws Exception {

        // 1. zip 다운로드
        File zipFile = new File(baseDir, "kospi_code.zip");
        try (InputStream in = new URL(KOSPI_URL).openStream();
             FileOutputStream out = new FileOutputStream(zipFile)) {
            in.transferTo(out);
        }

        // 2. zip 해제
        File mstFile = new File(baseDir, "kospi_code.mst");
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry = zis.getNextEntry();
            try (FileOutputStream fos = new FileOutputStream(mstFile)) {
                zis.transferTo(fos);
            }
        }
        zipFile.delete();

        // 3. mst 파싱
        List<StockDTO> result = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(mstFile), Charset.forName("CP949")))) {

            String line;
            while ((line = br.readLine()) != null) {

                // ----- part1 -----
                String shortCode = line.substring(0, 9).trim();
                String koreanName = line.substring(21, line.length() - 228).trim();

                // ----- part2 -----
                String part2 = line.substring(line.length() - 228);

                // 단축코드 숫자 6자리만 허용
                if (!shortCode.matches("\\d{6}")) {
                    continue;
                }

                String groupCode = part2.substring(0, 3).trim();
                if (!"ST".equals(groupCode)) {
                    continue;
                }

                result.add(StockDTO.builder().stockCode(shortCode).stockName(koreanName).build());
            }
        }
        log.info("Kospi stocks loaded successfully | {} added",  result.size());
        return result;
    }

    public List<StockDTO> loadKosdaqStocks(String baseDir) throws Exception {

        // 1. zip 다운로드
        File zipFile = new File(baseDir, "kosdaq_code.zip");
        try (InputStream in = new URL(KOSDAQ_URL).openStream();
             FileOutputStream out = new FileOutputStream(zipFile)) {
            in.transferTo(out);
        }

        // 2. zip 해제
        File mstFile = new File(baseDir, "kosdaq_code.mst");
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry = zis.getNextEntry();
            try (FileOutputStream fos = new FileOutputStream(mstFile)) {
                zis.transferTo(fos);
            }
        }
        zipFile.delete();

        // 3. mst 파싱
        List<StockDTO> result = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(mstFile), Charset.forName("CP949")))) {

            String line;
            while ((line = br.readLine()) != null) {

                // ----- part1 -----
                String shortCode = line.substring(0, 9).trim();
                String koreanName = line.substring(21, line.length() - 228).trim();

                // ----- part2 -----
                String part2 = line.substring(line.length() - 228);

                // 단축코드 숫자 6자리만 허용
                if (!shortCode.matches("\\d{6}")) {
                    continue;
                }

                if (koreanName.contains("스팩") || koreanName.contains("SPAC")) {
                    continue;
                }

                result.add(StockDTO.builder().stockCode(shortCode).stockName(koreanName).build());
            }
        }
        log.info("Kosdaq stocks loaded successfully | {} added",  result.size());
        return result;
    }
}
