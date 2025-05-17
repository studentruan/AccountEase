package detectorTools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeSensitiveAdjuster {
    // 自定义节日/纪念日集合（月日格式，如"05-18"）
    private final Set<MonthDay> customHolidays = new HashSet<>();
    private static final Pattern MM_DD_PATTERN = Pattern.compile("^(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$");
    // 灵敏度调整系数
    private static final double WEEKEND_ADJUSTMENT = 0.8;  // 周末灵敏度降低20%
    private static final double HOLIDAY_ADJUSTMENT = 0.6;  // 节日灵敏度降低40%
    private static final Logger logger = LoggerFactory.getLogger(TimeSensitiveAdjuster.class);
    // 添加自定义节日（MM-dd格式）
    public void addCustomHoliday(String monthDayStr) {
        if (!monthDayStr.matches(MM_DD_PATTERN.pattern())) {
            throw new DateTimeParseException("Invalid format", monthDayStr, 0);
        }


        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
            MonthDay holiday = MonthDay.parse(monthDayStr, formatter);
            customHolidays.add(holiday);
        } catch (DateTimeParseException e) {
            System.err.println("无效节假日格式: " + monthDayStr + "，错误原因: " + e.getMessage());
        }
    }

    public void loadHolidaysFromList(List<String> holidaysList) {
        holidaysList.stream()
                .map(line -> {
                    try {
                        // 兼容键值对或纯值格式（如 "holiday=05-01" 或 "05-01"）
                        return line.contains("=") ? line.split("=")[1].trim() : line.trim();
                    } catch (ArrayIndexOutOfBoundsException e) {
                        logger.error("配置项格式错误，需为 key=MM-DD 或 MM-DD 形式: {}", line);
                        return null; // 标记无效行
                    }
                })
                .filter(Objects::nonNull ) // 过滤空值
                .filter(dateStr -> MM_DD_PATTERN.matcher(dateStr).matches()) // 验证格式
                .forEach(this::addCustomHoliday); // 添加到节假日集合
    }

    // 核心调整逻辑（整合网页4、网页6、网页9方法）
    public double adjustThreshold(double base, LocalDateTime timestamp) {
        // 节假日优先逻辑（空集合不影响判断）
        if (customHolidays.contains(MonthDay.from(timestamp))) {
            return base * HOLIDAY_ADJUSTMENT;
        }
        // 周末判断
        return isWeekend(timestamp) ? base * WEEKEND_ADJUSTMENT : base;
    }

    /*
    public static double adjustThreshold(double threshold, LocalDateTime timestamp) {
        return threshold;  // 时间参数无实际作用，仅保持方法签名兼容
    }
    */
    // 判断是否为周末（网页6方法优化）
    private boolean isWeekend(LocalDateTime dateTime) {
        return dateTime.getDayOfWeek().getValue() >= 6;  // 6=周六，7=周日
    }

    public boolean isHolidayEmpty() {
        // 双重校验：集合实例存在性检查 + 空元素检查
        return this.customHolidays == null || this.customHolidays.isEmpty();
    }

}
