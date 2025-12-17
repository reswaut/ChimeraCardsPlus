package chimeracardsplus.uis;

import basemod.IUIElement;
import basemod.patches.com.megacrit.cardcrawl.helpers.TipHelper.HeaderlessTip;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.options.DropdownMenu;
import com.megacrit.cardcrawl.screens.options.DropdownMenuListener;

import java.util.ArrayList;

public class LabeledDropdownMenu implements IUIElement {
    private final DropdownMenu dropdownMenu;
    private final ArrayList<String> descriptions;
    private float x, y;

    public LabeledDropdownMenu(DropdownMenuListener listener, ArrayList<String> options, ArrayList<String> descriptions, float x, float y, int defaultRow) {
        dropdownMenu = new DropdownMenu(listener, options, FontHelper.tipBodyFont, Settings.CREAM_COLOR);
        dropdownMenu.setSelectedIndex(defaultRow);
        this.descriptions = new ArrayList<>(descriptions);
        this.x = x;
        this.y = y;
    }

    @Override
    public void render(SpriteBatch sb) {
        dropdownMenu.render(sb, x * Settings.scale, y * Settings.scale);
        if (!dropdownMenu.isOpen && dropdownMenu.getHitbox().hovered) {
            HeaderlessTip.renderHeaderlessTip(InputHelper.mX + 60.0F * Settings.scale, InputHelper.mY - 50.0F * Settings.scale, descriptions.get(dropdownMenu.getSelectedIndex()));
        }
    }

    @Override
    public void update() {
        dropdownMenu.update();
    }

    @Override
    public int renderLayer() {
        return 3;
    }

    @Override
    public int updateOrder() {
        return 0;
    }

    @Override
    public void set(float xPos, float yPos) {
        x = xPos;
        y = yPos;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public void setX(float xPos) {
        x = xPos;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public void setY(float yPos) {
        y = yPos;
    }
}
