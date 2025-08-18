package chimeracardsplus.damagemods;

import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.MinionPower;
import com.megacrit.cardcrawl.powers.UnawakenedPower;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class LearnDamage extends AbstractDamageModifier {

    public LearnDamage() {
        priority = 32767;
    }

    @Override
    public void onLastDamageTakenUpdate(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature target) {
        if (target instanceof AbstractMonster && DamageModifierManager.getInstigator(info) instanceof AbstractCard
                && target.currentHealth > 0
                && target.currentHealth - lastDamageTaken <= 0
                && !target.halfDead
                && !target.hasPower(MinionPower.POWER_ID)
                && !target.hasPower(UnawakenedPower.POWER_ID)) {
            ArrayList<AbstractCard> possibleCards = AbstractDungeon.player.masterDeck.group.stream().filter(AbstractCard::canUpgrade).collect(Collectors.toCollection(() -> new ArrayList<>(64)));

            AbstractCard theCard = null;
            if (!possibleCards.isEmpty()) {
                theCard = possibleCards.get(AbstractDungeon.miscRng.random(0, possibleCards.size() - 1));
                theCard.upgrade();
                AbstractDungeon.player.bottledCardUpgradeCheck(theCard);
            }

            if (theCard != null) {
                AbstractDungeon.effectsQueue.add(new UpgradeShineEffect(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                AbstractDungeon.topLevelEffectsQueue.add(new ShowCardBrieflyEffect(theCard.makeStatEquivalentCopy()));
                addToTop(new WaitAction(Settings.ACTION_DUR_MED));
            }
        }
    }

    @Override
    public boolean isInherent() {
        return true;
    }

    @Override
    public AbstractDamageModifier makeCopy() {
        return new LearnDamage();
    }
}
