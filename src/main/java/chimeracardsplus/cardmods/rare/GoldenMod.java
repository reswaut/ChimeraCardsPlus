package chimeracardsplus.cardmods.rare;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.interfaces.HealingMod;
import com.megacrit.cardcrawl.actions.common.GainGoldAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.SpotlightPlayerEffect;

public class GoldenMod extends AbstractAugment implements HealingMod {
    public static final String ID = ChimeraCardsPlus.makeID(GoldenMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.exhaust = true;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, (c) -> (c.cost >= -1 && c.cost != 0 &&
                c.rarity != AbstractCard.CardRarity.BASIC && doesntUpgradeCost() && notExhaust(c) && (c.type == AbstractCard.CardType.ATTACK || c.type == AbstractCard.CardType.SKILL)));
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        int gold = (card.cost > 0) ? card.cost : card.energyOnUse;
        AbstractDungeon.effectList.add(new RainingGoldEffect(gold, true));
        AbstractDungeon.effectsQueue.add(new SpotlightPlayerEffect());
        this.addToBot(new GainGoldAction(gold));
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
        if (card.cost == -1) {
            return insertAfterText(rawDescription, CARD_TEXT[1]);
        }
        return insertAfterText(rawDescription, String.format(CARD_TEXT[0], card.cost));
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new GoldenMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}