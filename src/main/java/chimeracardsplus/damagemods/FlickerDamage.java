package chimeracardsplus.damagemods;

import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class FlickerDamage extends AbstractDamageModifier {
    private final AbstractCard hiddenCard;

    public FlickerDamage(AbstractCard card) {
        priority = 32767;
        hiddenCard = card;
    }

    @Override
    public void onLastDamageTakenUpdate(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature target) {
        if (target instanceof AbstractMonster && DamageModifierManager.getInstigator(info) instanceof AbstractCard
                && target.currentHealth > 0
                && target.currentHealth - lastDamageTaken <= 0) {
            hiddenCard.returnToHand = true;
        }
    }

    @Override
    public boolean isInherent() {
        return true;
    }

    @Override
    public AbstractDamageModifier makeCopy() {
        return new FlickerDamage(hiddenCard);
    }
}
