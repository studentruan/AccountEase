package Backend;



// GlobalContext.java
public class GlobalContext {
    private static GlobalContext instance;
    private String currentLedgerId;
    private Ledger currentLedger;

    private GlobalContext() {}  // 私有构造器

    public static synchronized GlobalContext getInstance() {
        if (instance == null) {
            instance = new GlobalContext();
        }
        return instance;
    }

    // 设置当前账本（完整对象）
    public void setCurrentLedger(Ledger ledger) {
        this.currentLedger = ledger;
        this.currentLedgerId = (ledger != null) ? ledger.getId() : null;
    }

    // 获取当前账本ID
    public String getCurrentLedgerId() {
        return this.currentLedgerId;
    }

    // 获取完整账本对象
    public Ledger getCurrentLedger() {
        return this.currentLedger;
    }

    // 清除上下文
    public void clear() {
        this.currentLedger = null;
        this.currentLedgerId = null;
    }
}