package swu.xl.linkgame.Model;

/**
 * 关卡类
 */
public class XLLevel {
    //关卡号
    private int l_id;
    //闯关时间
    private int l_time;
    //关卡模式 1：简单 2：普通 3：困难
    private char l_mode;
    //闯关状态 0：没有闯关 1：1星 2：2星 3：3星
    private char l_new;
    //闯关获得的金币数
    private int l_money;

    //setter、getter方法
    public int getL_id() {
        return l_id;
    }

    public void setL_id(int l_id) {
        this.l_id = l_id;
    }

    public int getL_time() {
        return l_time;
    }

    public void setL_time(int l_time) {
        this.l_time = l_time;
    }

    public char getL_mode() {
        return l_mode;
    }

    public void setL_mode(char l_mode) {
        this.l_mode = l_mode;
    }

    public char getL_new() {
        return l_new;
    }

    public void setL_new(char l_new) {
        this.l_new = l_new;
    }

    public int getL_money() {
        return l_money;
    }

    public void setL_money(int l_money) {
        this.l_money = l_money;
    }
}