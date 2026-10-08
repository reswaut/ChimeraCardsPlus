package chimeracardsplus.cardmods.rare;

import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import com.megacrit.cardcrawl.actions.common.GainGoldAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.SpotlightPlayerEffect;

import java.util.stream.Stream;

public class RoyalMod extends AbstractAugmentPlus {
    public static final String ID = ChimeraCardsPlus.makeID(RoyalMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;
    private boolean addedExhaust = true;
    private int amount;

    public RoyalMod() {
        this(0);
    }

    public RoyalMod(int amount) {
        this.amount = amount;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (card.type == CardType.POWER) {
            addedExhaust = false;
        } else {
            addedExhaust = !card.exhaust;
            card.exhaust = true;
        }
        AbstractCard copy = makeNewInstance(card);
        if (copy != null) {
            amount = copy.cost;
        }
    }

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        AbstractCard copy = makeNewInstance(abstractCard);
        return copy != null && cardCheck(copy, c -> (c.cost >= 1 || c.cost == -1) && c.rarity != CardRarity.BASIC && doesntUpgradeExhaust() && Stream.of(CardType.ATTACK, CardType.SKILL, CardType.POWER).anyMatch(cardType -> c.type == cardType));
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        int goldAmount = amount > 0 ? amount : card.energyOnUse;
        if (goldAmount > 0) {
            AbstractDungeon.effectList.add(new RainingGoldEffect(goldAmount, true));
            AbstractDungeon.effectsQueue.add(new SpotlightPlayerEffect());
            addToBot(new GainGoldAction(goldAmount));
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
        String text = "";
        if (amount == -1) {
            text = addedExhaust ? CARD_TEXT[2] : CARD_TEXT[3];
        } else if (amount >= 1) {
            text = String.format(addedExhaust ? CARD_TEXT[0] : CARD_TEXT[1], amount);
        }
        return insertAfterText(rawDescription, text);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new RoyalMod(amount);
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public AugmentBonusLevel getModBonusLevel() {
        return AugmentBonusLevel.HEALING;
    }
}