package chimeracardsplus.actions;

import chimeracardsplus.powers.DoomPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

public class SwipeDoomAction extends AbstractGameAction {
    private final DamageInfo info;

    public SwipeDoomAction(AbstractMonster target, DamageInfo info) {
        this.info = info;
        setValues(target, info);
        actionType = ActionType.DAMAGE;
        attackEffect = AttackEffect.SLASH_VERTICAL;
    }

    @Override
    public void update() {
        if (target == null || target.currentHealth <= 0 || !target.hasPower(DoomPower.POWER_ID) || info.type != DamageType.THORNS && info.owner.isDying) {
            isDone = true;
            return;
        }
        AbstractDungeon.effectList.add(new FlashAtkImgEffect(target.hb.cX, target.hb.cY, attackEffect));
        target.damage(info);
        if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            AbstractDungeon.actionManager.clearPostCombatActions();
        }
        isDone = true;
    }
}
