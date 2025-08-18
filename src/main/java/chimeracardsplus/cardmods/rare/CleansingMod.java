package chimeracardsplus.cardmods.rare;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.powers.AbstractPower.PowerType;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class CleansingMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(CleansingMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;
    private boolean addedExhaust = true;

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (!card.exhaust && card.type != CardType.POWER) {
            addedExhaust = true;
            card.exhaust = true;
        } else {
            addedExhaust = false;
        }
    }

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return cardCheck(abstractCard, c -> c.cost >= -1 && doesntUpgradeExhaust());
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        ArrayList<String> debuffs = AbstractDungeon.player.powers.stream().filter(p -> p.type == PowerType.DEBUFF).map(p -> p.ID).collect(Collectors.toCollection(() -> new ArrayList<>(4)));
        if (debuffs.isEmpty()) {
            return;
        }
        addToBot(new RemoveSpecificPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                debuffs.get(AbstractDungeon.miscRng.random(0, debuffs.size() - 1))));
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
        return insertAfterText(rawDescription, addedExhaust ? CARD_TEXT[0] : CARD_TEXT[1]);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new CleansingMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}