package chimeracardsplus.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect.ShockWaveType;

public class ShriekAction extends AbstractGameAction {
    public ShriekAction(int amount) {
        this.amount = amount;
        actionType = ActionType.DEBUFF;
    }

    @Override
    public void update() {
        for (int i = AbstractDungeon.getMonsters().monsters.size() - 1; i >= 0; --i) {
            AbstractMonster mo = AbstractDungeon.getMonsters().monsters.get(i);
            if (!mo.hasPower("Artifact")) {
                addToTop(new ApplyPowerAction(mo, AbstractDungeon.player, new GainStrengthPower(mo, amount), amount, true, AttackEffect.NONE));
            }
        }
        for (int i = AbstractDungeon.getMonsters().monsters.size() - 1; i >= 0; --i) {
            AbstractMonster mo = AbstractDungeon.getMonsters().monsters.get(i);
            addToTop(new ApplyPowerAction(mo, AbstractDungeon.player, new StrengthPower(mo, -amount), -amount, true, AttackEffect.NONE));
        }
        if (Settings.FAST_MODE) {
            addToTop(new VFXAction(AbstractDungeon.player, new ShockWaveEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, Settings.GREEN_TEXT_COLOR, ShockWaveType.CHAOTIC), 0.3F));
        } else {
            addToTop(new VFXAction(AbstractDungeon.player, new ShockWaveEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, Settings.GREEN_TEXT_COLOR, ShockWaveType.CHAOTIC), 1.5F));
        }
        addToTop(new SFXAction("ATTACK_PIERCING_WAIL"));
        isDone = true;
    }
}
