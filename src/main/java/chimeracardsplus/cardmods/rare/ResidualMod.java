package chimeracardsplus.cardmods.rare;

import CardAugments.cardmods.AbstractAugment;
import CardAugments.patches.InfiniteUpgradesPatches;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.ChimeraCardsPlus;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class ResidualMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(ResidualMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;
    private int originalCost;

    public ResidualMod() {
        this(0);
    }
    public ResidualMod(int originalCost) {
        this.originalCost = originalCost;
    }

    private int getEnergyLeft() {
        int energyLeft = 4 - originalCost;
        if (originalCost < 0 || energyLeft < 0) {
            energyLeft = 0;
        }
        return energyLeft;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        originalCost = card.cost;
        card.cost = 3;
        card.costForTurn = card.cost;
        card.initializeDescription();
    }

    @Override
    public void onUpgradeCheck(AbstractCard card) {
        originalCost = card.cost;
        card.cost = 3;
        card.costForTurn = card.cost;
        card.initializeDescription();
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost >= 0 && card.cost <= 4;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        this.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                EnergyPanel.setEnergy(getEnergyLeft());
                this.isDone = true;
            }
        });
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
        return insertAfterText(rawDescription, String.format(CARD_TEXT[0], getEnergyLeft()));
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new ResidualMod(originalCost);
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @SpirePatch(
            clz = InfiniteUpgradesPatches.class,
            method = "infCheck"
    )
    public static class RestoreCardCostBeforeUpgradePatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractCard card) {
            if (!CardModifierManager.hasModifier(card, ID)) {
                return;
            }
            ResidualMod modifier = (ResidualMod) CardModifierManager.getModifiers(card, ID).get(0);
            card.cost = modifier.originalCost;
            modifier.originalCost = 0;
        }
    }
}