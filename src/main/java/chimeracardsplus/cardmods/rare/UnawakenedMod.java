package chimeracardsplus.cardmods.rare;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.special.AwakenedMod;
import chimeracardsplus.interfaces.TriggerOnPurgeMod;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import static basemod.helpers.CardModifierManager.addModifier;

public class UnawakenedMod extends AbstractAugment implements TriggerOnPurgeMod {
    public static final String ID = ChimeraCardsPlus.makeID(UnawakenedMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public boolean validCard(AbstractCard card) {
        return true;
    }

    @Override
    public boolean isRemovable(AbstractCard card) {
        return true;
    }

    @Override
    public void onRemoveFromMasterDeck(AbstractCard card) {
        AbstractCard AwakenedCard = card.makeCopy();
        for (int i = 0; i < card.timesUpgraded; ++i) {
            if (AwakenedCard.canUpgrade()) {
                AwakenedCard.upgrade();
            }
        }
        addModifier(AwakenedCard, new AwakenedMod());
        AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(AwakenedCard, (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
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
        return new UnawakenedMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}