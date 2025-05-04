package chimeracardsplus.cardmods.rare;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;

public class MummifiedMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(MummifiedMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, (c) -> c.cost >= -1 && c.type == AbstractCard.CardType.POWER);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        ArrayList<AbstractCard> groupCopy = new ArrayList<>();
        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if (c.cost > 0 && c.costForTurn > 0 && !c.freeToPlayOnce) {
                groupCopy.add(c);
            }
        }
        for (CardQueueItem i : AbstractDungeon.actionManager.cardQueue) {
            if (i.card != null) {
                groupCopy.remove(i.card);
            }
        }
        if (!groupCopy.isEmpty()) {
            groupCopy.get(AbstractDungeon.cardRandomRng.random(0, groupCopy.size() - 1)).setCostForTurn(0);
        }
    }

    @Override
    public String getPrefix() {
        return TEXT[0];
    }

    @Override
    public String getSuffix() {
        return TEXT[1];
    }

    @Override
    public String getAugmentDescription() {
        return TEXT[2];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return insertAfterText(rawDescription, CARD_TEXT[0]);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new MummifiedMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}