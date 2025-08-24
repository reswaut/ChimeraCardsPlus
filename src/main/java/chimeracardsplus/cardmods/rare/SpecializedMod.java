package chimeracardsplus.cardmods.rare;

import CardAugments.patches.RolledModFieldPatches.RolledModField;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

public class SpecializedMod extends AbstractAugmentPlus {
    public static final String ID = ChimeraCardsPlus.makeID(SpecializedMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;
    private boolean inherentHack = true;

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return abstractCard.rarity != CardRarity.BASIC && isNormalCard(abstractCard);
    }

    @Override
    public boolean onObtain(AbstractCard card) {
        inherentHack = true;
        AbstractCard cardToAdd = card.makeStatEquivalentCopy();
        inherentHack = false;
        if (cardToAdd != null) {
            AbstractCard card1 = cardToAdd.makeStatEquivalentCopy();
            AbstractCard card2 = cardToAdd.makeStatEquivalentCopy();
            RolledModField.rolled.set(card1, true);
            RolledModField.rolled.set(card2, true);
            AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(card1, Settings.WIDTH / 2.0F - 190.0F * Settings.scale, Settings.HEIGHT / 2.0F));
            AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(card2, Settings.WIDTH / 2.0F + 190.0F * Settings.scale, Settings.HEIGHT / 2.0F));
        }
        return true;
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
        return new SpecializedMod();
    }

    @Override
    public boolean isInherent(AbstractCard card) {
        return inherentHack;
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