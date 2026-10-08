package chimeracardsplus.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

public class UpgradeRandomCardInDiscardAction extends AbstractGameAction {
    @Override
    public void update() {
        CardGroup upgradeable = new CardGroup(CardGroupType.UNSPECIFIED);
        for (AbstractCard c : AbstractDungeon.player.discardPile.group) {
            if (c.canUpgrade()) {
                upgradeable.addToTop(c);
            }
        }
        if (!upgradeable.isEmpty()) {
            AbstractCard c = upgradeable.getRandomCard(true);
            c.upgrade();
            c.applyPowers();
            AbstractDungeon.topLevelEffects.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
        }
        isDone = true;
    }
}
