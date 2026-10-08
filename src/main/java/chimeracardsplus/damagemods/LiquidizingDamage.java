package chimeracardsplus.damagemods;

import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.megacrit.cardcrawl.actions.AbstractGameAction.ActionType;
import com.megacrit.cardcrawl.actions.common.ObtainPotionAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class LiquidizingDamage extends CardModifierDamageModifier {
    @Override
    public void onLastDamageTakenUpdate(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature target) {
        if (!dealtFatalDamage(info, lastDamageTaken, overkillAmount, target)) {
            return;
        }
        ObtainPotionAction action = new ObtainPotionAction(AbstractDungeon.returnRandomPotion(true));
        action.actionType = ActionType.DAMAGE;
        addToBot(action);
    }

    @Override
    public AbstractDamageModifier makeCopy() {
        return new LiquidizingDamage();
    }
}
