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

    @Transactional
    public SyncResult syncAllStocks() throws Exception {
        String tempDir = System.getProperty("user.dir");
        return syncAllStocks(tempDir);
    }

    // 코스피+코스닥 종목 정보를 다운로드하여 DB와 동기화
    @Transactional
    public SyncResult syncAllStocks(String baseDir) throws Exception {
        log.info("=== 종목 동기화 시작 ===");

        // 1. temp_stock 테이블 초기화
        log.info("temp_stock 테이블 초기화 중...");
        stockSyncMapper.truncateTemp();

        // 2. 코스피 종목 다운로드 및 파싱
        log.info("코스피 종목 다운로드 중...");
        List<StockDTO> kospiStocks = loadStocksByMarket("KOSPI", baseDir);

        // 3. 코스닥 종목 다운로드 및 파싱
        log.info("코스닥 종목 다운로드 중...");
        List<StockDTO> kosdaqStocks = loadStocksByMarket("KOSDAQ", baseDir);

        // 4. 모든 종목을 temp_stock에 삽입
        log.info("temp_stock에 종목 삽입 중... (코스피: {}, 코스닥: {})",
                kospiStocks.size(), kosdaqStocks.size());

        int totalInserted = 0;
        for (StockDTO stock : kospiStocks) {
            stockSyncMapper.insertTempStock(stock);
            totalInserted++;
        }
        for (StockDTO stock : kosdaqStocks) {
            stockSyncMapper.insertTempStock(stock);
            totalInserted++;
        }

        log.info("temp_stock에 {} 종목 삽입 완료", totalInserted);

        // 5. stock 테이블과 동기화
        log.info("stock 테이블 동기화 중...");

        // 기존 stock 테이블 종목 수 확인
        int beforeCount = stockSyncMapper.countStock();
        log.info("동기화 전 stock 테이블 종목 수: {}", beforeCount);

        // 새로운 종목 추가
        int insertedCount = stockSyncMapper.insertNewStocks();
        log.info("새로 추가된 종목: {}", insertedCount);

        // 삭제된 종목 제거
        int deletedCount = stockSyncMapper.deleteOldStocks();
        log.info("삭제된 종목: {}", deletedCount);

        // 동기화 후 종목 수 확인
        int afterCount = stockSyncMapper.countStock();
        log.info("동기화 후 stock 테이블 종목 수: {}", afterCount);

        // 결과 반환
        SyncResult result = SyncResult.builder()
                .beforeCount(beforeCount)
                .afterCount(afterCount)
                .insertedCount(insertedCount)
                .deletedCount(deletedCount)
                .totalDownloaded(totalInserted)
                .build();

        log.info("=== 종목 동기화 완료 ===");
        log.info("결과: {}", result);

        return result;
    }

    public List<StockDTO> loadKospiStocks(String baseDir) throws Exception {
        return loadStocksByMarket("KOSPI", baseDir);
    }

    public List<StockDTO> loadKosdaqStocks(String baseDir) throws Exception {
        return loadStocksByMarket("KOSDAQ", baseDir);
    }

    //코스피 또는 코스닥 종목 정보를 다운로드하고 파싱
    private List<StockDTO> loadStocksByMarket(String marketType, String baseDir) throws Exception {
        String url;
        String zipFileName;
        String mstFileName;

        if ("KOSPI".equals(marketType)) {
            url = KOSPI_URL;
            zipFileName = "kospi_code.zip";
            mstFileName = "kospi_code.mst";
        } else if ("KOSDAQ".equals(marketType)) {
            url = KOSDAQ_URL;
            zipFileName = "kosdaq_code.zip";
            mstFileName = "kosdaq_code.mst";
        } else {
            throw new IllegalArgumentException("Invalid market type: " + marketType);
        }

        // 1. zip 다운로드
        File zipFile = new File(baseDir, zipFileName);
        try (InputStream in = new URL(url).openStream();
             FileOutputStream out = new FileOutputStream(zipFile)) {
            in.transferTo(out);
        }

        // 2. zip 해제
        File mstFile = new File(baseDir, mstFileName);
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
                new InputStreamReader(new FileInputStream(mstFile), CP949))) {

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

                // 코스피: ST 그룹만 허용
                if ("KOSPI".equals(marketType)) {
                    String groupCode = part2.substring(0, 3).trim();
                    if (!"ST".equals(groupCode)) {
                        continue;
                    }
                }

                // 코스닥: 스팩 제외
                if ("KOSDAQ".equals(marketType)) {
                    if (koreanName.contains("스팩") || koreanName.contains("SPAC")) {
                        continue;
                    }
                }

                result.add(StockDTO.builder()
                        .stockCode(shortCode)
                        .stockName(koreanName)
                        .build());
            }
        }

        // 파일 정리
        mstFile.delete();

        log.info("{} stocks loaded successfully | {} stocks", marketType, result.size());
        return result;
    }
}
