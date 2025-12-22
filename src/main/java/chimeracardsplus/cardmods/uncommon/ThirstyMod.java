package chimeracardsplus.cardmods.uncommon;

import CardAugments.cardmods.DynvarCarrier;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

public class ThirstyMod extends AbstractAugmentPlus implements DynvarCarrier {
    public static final String ID = ChimeraCardsPlus.makeID(ThirstyMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;
    private static final String DESCRIPTION_KEY = '!' + ID + '!';
    private int uses;

    public ThirstyMod() {
        this(2);
    }

    public ThirstyMod(int uses) {
        this.uses = uses;
    }

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return abstractCard.type == CardType.CURSE && isCardRemovable(abstractCard, true);
    }

    @Override
    public boolean preDiscardPotion(AbstractCard card, CardGroup group, AbstractPotion potion) {
        if (group.type != CardGroupType.MASTER_DECK || !CardModifierManager.hasModifier(card, ID)) {
            return false;
        }

        ThirstyMod modifier = (ThirstyMod) CardModifierManager.getModifiers(card, ID).get(0);
        modifier.uses -= 1;
        card.initializeDescription();
        if (modifier.uses > 0 || !isCardRemovable(card, false)) {
            return false;
        }

        AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(card));
        AbstractDungeon.player.masterDeck.removeCard(card);
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
        return insertAfterText(rawDescription, String.format(uses <= 1 ? CARD_TEXT[1] : CARD_TEXT[0], DESCRIPTION_KEY));
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new ThirstyMod(uses);
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public AugmentBonusLevel getModBonusLevel() {
        return AugmentBonusLevel.HEALING;
    }

    @Override
    public String key() {
        return ID;
    }

    @Override
    public int val(AbstractCard abstractCard) {
        return uses;
    }

    @Override
    public int baseVal(AbstractCard abstractCard) {
        return 2;
    }

    @Override
    public boolean modified(AbstractCard abstractCard) {
        return val(abstractCard) != baseVal(abstractCard);
    }

    @Override
    public boolean upgraded(AbstractCard abstractCard) {
        return false;
    }
}