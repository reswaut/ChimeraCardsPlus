package chimeracardsplus.cardmods.rare;

import CardAugments.cardmods.AbstractAugment;
import CardAugments.patches.InterruptUseCardFieldPatches.InterceptUseField;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ChemicalMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(ChemicalMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return cardCheck(abstractCard, c -> c.cost == -1 && doesntUpgradeCost());
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        InterceptUseField.interceptUse.set(card, Boolean.TRUE);
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
        return insertBeforeText(rawDescription, CARD_TEXT[0]);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        card.energyOnUse += 1;
        card.use(AbstractDungeon.player, target instanceof AbstractMonster ? (AbstractMonster) target : null);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new ChemicalMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}