package chimeracardsplus.cardmods.rare;

import CardAugments.patches.InfiniteUpgradesPatches.InfUpgradeField;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import com.evacipated.cardcrawl.mod.stslib.cards.interfaces.BranchingUpgradesCard;
import com.evacipated.cardcrawl.mod.stslib.cards.interfaces.MultiUpgradeCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

public class PeacefulMod extends AbstractAugmentPlus {
    public static final String ID = ChimeraCardsPlus.makeID(PeacefulMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return abstractCard.type == CardType.CURSE && doesntOverride(abstractCard, "canUpgrade") && !(abstractCard instanceof BranchingUpgradesCard) && !(abstractCard instanceof MultiUpgradeCard) && isCardRemovable(abstractCard, false);
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        InfUpgradeField.inf.set(card, true);
    }

    @Override
    public void onUpgradeCheck(AbstractCard card) {
        if (AbstractDungeon.player == null || AbstractDungeon.player.masterDeck == null || !AbstractDungeon.player.masterDeck.contains(card)) {
            return;
        }
        if (!isCardRemovable(card, false)) {
            return;
        }
        AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(card));
        AbstractDungeon.player.masterDeck.removeCard(card);
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
        return new PeacefulMod();
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