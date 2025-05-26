package ControllerTest;

import com.myapp.controller.LedgerController;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class MemorialDayFormatTest {

    @Test
    public void shouldValidateDateFormat() throws Exception {
        // 获取私有方法
        Method method = LedgerController.class.getDeclaredMethod("validateMemorialDayFormat", String.class);
        method.setAccessible(true);

        LedgerController controller = new LedgerController();

        // 测试有效日期格式
        assertDoesNotThrow(() -> method.invoke(controller, "01-31"));
        assertDoesNotThrow(() -> method.invoke(controller, "12-01"));

        // 测试无效日期格式
        assertThrows(Exception.class, () -> method.invoke(controller, "13-01")); // 无效月份
        assertThrows(Exception.class, () -> method.invoke(controller, "00-15")); // 无效月份
        assertThrows(Exception.class, () -> method.invoke(controller, "05-32")); // 无效日期
        assertThrows(Exception.class, () -> method.invoke(controller, "05-00")); // 无效日期
        assertThrows(Exception.class, () -> method.invoke(controller, "5-15"));  // 缺少前导零
        assertThrows(Exception.class, () -> method.invoke(controller, "05-1"));  // 缺少前导零
        assertThrows(Exception.class, () -> method.invoke(controller, "0515"));  // 缺少分隔符
        assertThrows(Exception.class, () -> method.invoke(controller, "May-15")); // 非数字格式
    }
}
