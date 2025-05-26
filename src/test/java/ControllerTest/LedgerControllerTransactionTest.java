package ControllerTest;

import Backend.FinanceDataProcessor;
import Backend.TransactionXmlMerger;
import com.myapp.controller.LedgerController;
import AIUtilities.classification.TransactionClassifier;
import javafx.application.Platform;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;

import java.lang.reflect.Field;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class LedgerControllerTransactionTest extends ApplicationTest {

    private TransactionClassifier classifier;
    private LedgerController controller;
    private Path tempDir;
    private final String testLedgerId = "test-ledger-001";

    @BeforeAll
    public static void setupJavaFX() {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("java.awt.headless", "true");
        Platform.startup(() -> {});
    }

    @BeforeEach
    void setUp() throws Exception {
        // 初始化分类器
        Path tokenizerDir = Paths.get("src/main/resources/Tokenizer");
        String modelPath = "src/main/resources/bert_transaction_categorization.onnx";
        Path descriptionPath = Paths.get("src/main/resources/counterparty_description.json");
        classifier = new TransactionClassifier(tokenizerDir, modelPath, descriptionPath);

        // 在JavaFX线程中初始化控制器
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller = new LedgerController();
            latch.countDown();
        });
        latch.await(5, TimeUnit.SECONDS);

        // 创建临时测试目录
        tempDir = Files.createTempDirectory("transaction-test");
        Files.createDirectories(tempDir.resolve("fourthlevel_xml/"+testLedgerId));
        Files.createDirectories(tempDir.resolve("thirdlevel_json"));

        // 设置测试账本ID和基础目录
        setFieldIfExists(controller, "ledgerId", testLedgerId);
        setFieldIfExists(controller, "baseDir", tempDir.toString());
    }

    @Test
    void testNormalTransactionProcessing() throws Exception {
        // 准备测试数据
        LedgerController.TransactionData transaction = new LedgerController.TransactionData(
                "txn-001",
                "京东",
                "手机",
                "3990.00"
        );

        // 在JavaFX线程中执行测试
        CountDownLatch testLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                // 直接使用分类器（不通过注入）
                Map<String, String> classification = classifier.classify(
                        transaction.getDescription() + " " + transaction.getCounterparty()
                );

                // 处理交易
                controller.processTransaction(transaction);

                // 验证文件生成
                assertTrue(Files.list(tempDir.resolve("fourthlevel_xml/"+testLedgerId))
                        .findFirst().isPresent());

                testLatch.countDown();
            } catch (Exception e) {
                fail("Test failed", e);
            }
        });
        assertTrue(testLatch.await(10, TimeUnit.SECONDS), "Test timed out");
    }

    // 安全的字段设置方法
    private void setFieldIfExists(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException e) {
            System.out.println("Field '" + fieldName + "' not found, skipping injection");
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field " + fieldName, e);
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        // 清理测试目录
        if (tempDir != null && Files.exists(tempDir)) {
            Files.walk(tempDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try { Files.deleteIfExists(path); }
                        catch (Exception e) { /* ignore */ }
                    });
        }

        // 关闭分类器资源
        if (classifier != null) {
            try {
                classifier.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}