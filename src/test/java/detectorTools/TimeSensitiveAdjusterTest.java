package detectorTools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TimeSensitiveAdjusterTest {
    private TimeSensitiveAdjuster adjuster;

    @BeforeEach
    void setUp() {
        adjuster = new TimeSensitiveAdjuster();
    }
    @Test
    void testAddCustomHoliday() {
        // 正常日期
        adjuster.addCustomHoliday("05-18");
        LocalDateTime testDate = LocalDateTime.of(2025, 5, 18, 10, 0);
        assertEquals(1.8, adjuster.adjustThreshold(1.0, testDate));

    }
    @Test
    void testLoadHolidaysFromList() {
        // 混合有效/无效数据
        List<String> testData = List.of(
                "holiday=05-01",  // 键值对格式
                "12-25",          // 纯值格式
                "invalid_format",
                "key=2025-12-31" // 错误格式
        );

        adjuster.loadHolidaysFromList(testData);

        LocalDateTime christmas = LocalDateTime.of(2025, 12, 25, 0, 0);
        assertEquals(1.8, adjuster.adjustThreshold(1.0, christmas));
    }
    @Test
    void testAdjustmentPriority() {
        // 既是节假日又是周末
        adjuster.addCustomHoliday("05-18"); // 2025.5.18是周日
        LocalDateTime date = LocalDateTime.of(2025, 5, 18, 10, 0);
        assertEquals(1.8, adjuster.adjustThreshold(1.0, date)); // 节日优先级更高
    }
    @Test
    void testWeekendAdjustment() {
        // 周六测试
        LocalDateTime saturday = LocalDateTime.of(2025, 5, 17, 15, 0);
        assertEquals(1.2, adjuster.adjustThreshold(1.0, saturday));

        // 周日测试
        LocalDateTime sunday = LocalDateTime.of(2025, 5, 18, 9, 0);
        assertEquals(1.2, adjuster.adjustThreshold(1.0, sunday));

        // 工作日测试
        LocalDateTime monday = LocalDateTime.of(2025, 5, 19, 9, 0);
        assertEquals(1.0, adjuster.adjustThreshold(1.0, monday));
    }
    @Test
    void testLeapYearHandling() {
        // 闰月日期
        adjuster.addCustomHoliday("02-29");
        LocalDateTime leapDay = LocalDateTime.of(2024, 2, 29, 0, 0);
        assertEquals(1.8, adjuster.adjustThreshold(1.0, leapDay));
    }
    @Test
    void testInvalidInputHandling() {
        // 无效日期格式
        assertThrows(DateTimeParseException.class,
                () -> adjuster.addCustomHoliday("May-18"));

        // 空值处理
        // 预期：不会抛出异常且不影响已有配置
        assertTrue(adjuster.isHolidayEmpty());
    }
}