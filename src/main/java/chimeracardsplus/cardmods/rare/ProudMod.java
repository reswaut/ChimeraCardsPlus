package chimeracardsplus.cardmods.rare;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class ProudMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(ProudMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    public void onInitialApplication(AbstractCard card) {
        card.isInnate = true;
        card.exhaust = true;
        card.cost += 1;
        card.costForTurn = card.cost;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, (c) -> (notExhaust(c) && notInnate(c) && doesntUpgradeExhaust() && doesntUpgradeInnate()
                && c.cost >= 0 && doesntUpgradeCost()
                && (c.type == AbstractCard.CardType.ATTACK || c.type == AbstractCard.CardType.SKILL)));
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
        return CARD_TEXT[0] + insertAfterText(rawDescription, CARD_TEXT[1]);
    }

    @Override
    public void atEndOfTurn(AbstractCard card, CardGroup group) {
        if (group.type != CardGroup.CardGroupType.HAND) {
            return;
        }
        this.addToBot(new MakeTempCardInDrawPileAction(card.makeStatEquivalentCopy(), 1, false, true));
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new ProudMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}