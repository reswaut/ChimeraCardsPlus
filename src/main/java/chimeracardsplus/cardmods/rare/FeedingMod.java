package chimeracardsplus.cardmods.rare;

import CardAugments.cardmods.DynvarCarrier;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import chimeracardsplus.damagemods.FeedDamage;
import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.cards.red.Feed;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FeedingMod extends AbstractAugmentPlus implements DynvarCarrier {
    public static final String ID = ChimeraCardsPlus.makeID(FeedingMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;
    private static final String DESCRIPTION_KEY = '!' + ID + '!';
    private boolean addedExhaust = true;

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return abstractCard.cost >= -1 && abstractCard.baseDamage >= 2 && abstractCard.rarity != CardRarity.BASIC && abstractCard.type == CardType.ATTACK;
    }

    @Override
    public float modifyBaseDamage(float damage, DamageType type, AbstractCard card, AbstractMonster target) {
        return damage * 0.75F;
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        if (Feed.ID.equals(card.cardID)) {
            return magic + baseVal(card);
        }
        return magic;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (Feed.ID.equals(card.cardID)) {
            return;
        }
        DamageModifierManager.addModifier(card, new FeedDamage(baseVal(card)));
        addedExhaust = !card.exhaust;
        card.exhaust = true;
    }

    @Override
    public void onUpgradeCheck(AbstractCard card) {
        if (Feed.ID.equals(card.cardID)) {
            return;
        }
        List<AbstractDamageModifier> mods = DamageModifierManager.modifiers(card);
        Collection<AbstractDamageModifier> toRemove = mods.stream().filter(m -> m instanceof FeedDamage).collect(Collectors.toCollection(() -> new ArrayList<>(1)));
        for (AbstractDamageModifier m : toRemove) {
            DamageModifierManager.removeModifier(card, m);
        }
        DamageModifierManager.addModifier(card, new FeedDamage(baseVal(card)));

        if (!card.exhaust) {
            addedExhaust = true;
            card.exhaust = true;
            card.initializeDescription();
        }
    }

    @Override
    public String key() {
        return ID;
    }

    @Override
    public int val(AbstractCard abstractCard) {
        return baseVal(abstractCard);
    }

    @Override
    public int baseVal(AbstractCard abstractCard) {
        return 2 + getEffectiveUpgrades(abstractCard);
    }

    @Override
    public boolean modified(AbstractCard abstractCard) {
        return false;
    }

    @Override
    public boolean upgraded(AbstractCard abstractCard) {
        return abstractCard.timesUpgraded != 0 || abstractCard.upgraded;
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
        if (Feed.ID.equals(card.cardID)) {
            return rawDescription;
        }
        return insertAfterText(rawDescription, String.format(addedExhaust ? CARD_TEXT[0] : CARD_TEXT[1], DESCRIPTION_KEY));
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new FeedingMod();
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