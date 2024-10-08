package chimeracardsplus.cardmods.uncommon;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.MultiCardPreview;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.tempCards.Miracle;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.CollectPower;

public class CollectedMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(CollectedMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;
    private int effect;
    private boolean addedExhaust = false;

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, (c) -> c.cost >= 0 && c.cost < 3 && doesntUpgradeCost());
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (!card.exhaust && card.type != AbstractCard.CardType.POWER) {
            addedExhaust = true;
            card.exhaust = true;
        }
        AbstractCard miracle = new Miracle();
        miracle.upgrade();
        MultiCardPreview.add(card, miracle);
        effect = 3 - card.cost;
        card.cost = 3;
        card.costForTurn = card.cost;
    }

    @Override
    public void onUpgradeCheck(AbstractCard card) {
        if (!card.exhaust && card.type != AbstractCard.CardType.POWER) {
            addedExhaust = true;
            card.exhaust = true;
        }
        card.initializeDescription();
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        this.addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new CollectPower(AbstractDungeon.player, effect), effect));
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
        if (effect <= 0 || effect > 3) {
            return rawDescription;
        }
        return insertAfterText(rawDescription, addedExhaust ? CARD_TEXT[effect - 1] : CARD_TEXT[effect - 1 + 3]);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new CollectedMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}