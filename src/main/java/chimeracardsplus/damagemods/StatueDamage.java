package chimeracardsplus.damagemods;

import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.megacrit.cardcrawl.actions.common.ObtainPotionAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class StatueDamage extends AbstractDamageModifier {
    public StatueDamage() {
        this.priority = 32767;
    }

    public void onLastDamageTakenUpdate(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature targetHit) {
        if (DamageModifierManager.getInstigator(info) instanceof AbstractCard
                && targetHit.currentHealth > 0
                && targetHit.currentHealth - lastDamageTaken <= 0) {
            this.addToBot(new ObtainPotionAction(AbstractDungeon.returnRandomPotion(true)));
        }
    }

    public boolean isInherent() {
        return true;
    }

    public AbstractDamageModifier makeCopy() {
        return new StatueDamage();
    }
}
