package chimeracardsplus.cardmods.rare;

import CardAugments.patches.RolledModFieldPatches.RolledModField;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.MultiCardPreview;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import chimeracardsplus.cardmods.special.AwakenedMod;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

public class UnawakenedMod extends AbstractAugmentPlus {
    public static final String ID = ChimeraCardsPlus.makeID(UnawakenedMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return isCardRemovable(abstractCard, true);
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        AbstractCard preview = card.makeCopy();
        while (preview.canUpgrade() && preview.timesUpgraded < card.timesUpgraded) {
            preview.upgrade();
        }
        CardModifierManager.addModifier(preview, new AwakenedMod());
        MultiCardPreview.add(card, preview);
    }

    @Override
    public void onRemoveFromMasterDeck(AbstractCard card) {
        AbstractCard awakenedCard = MultiCardPreview.multiCardPreview.get(card).stream().filter(o -> CardModifierManager.hasModifier(o, AwakenedMod.ID)).findFirst().map(AbstractCard::makeStatEquivalentCopy).orElse(null);
        if (awakenedCard == null) {
            awakenedCard = card.makeCopy();
            for (int i = 0; i < card.timesUpgraded; ++i) {
                if (awakenedCard.canUpgrade()) {
                    awakenedCard.upgrade();
                }
            }
            CardModifierManager.addModifier(awakenedCard, new AwakenedMod());
        }
        RolledModField.rolled.set(awakenedCard, true);
        AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(awakenedCard, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
    }

    @Override
    public void onUpgradeCheck(AbstractCard card) {
        for (AbstractCard o : MultiCardPreview.multiCardPreview.get(card)) {
            if (CardModifierManager.hasModifier(o, AwakenedMod.ID) && o.canUpgrade()) {
                o.upgrade();
                o.initializeDescription();
            }
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

    @Override
    public AugmentBonusLevel getModBonusLevel() {
        return AugmentBonusLevel.HEALING;
    }
}