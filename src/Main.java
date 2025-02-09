import com.l1akr.puzzle.ui.game.GameJFrame;
import com.l1akr.ui.LoginJFrame;

import java.io.IOException;

//TIP 要<b>运行</b>代码，请按 <shortcut actionId="Run"/> 或
// 点击装订区域中的 <icon src="AllIcons.Actions.Execute"/> 图标。
public class Main {
    public static void main(String[] args) throws IOException {
        new LoginJFrame();
//        new RegisterJFrame();
        new GameJFrame();
    }
}