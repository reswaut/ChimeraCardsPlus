package chimeracardsplus.cardmods.rare;

import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.DynvarCarrier;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.GetAllInBattleInstances;

import static basemod.helpers.CardModifierManager.modifiers;

public class GeneticMod extends AbstractAugment implements DynvarCarrier {
    public static final String ID = ChimeraCardsPlus.makeID(GeneticMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;
    public static final String DESCRIPTION_KEY = "!" + ID + "!";
    public boolean modified;
    public boolean upgraded;
    private boolean addedExhaust;
    private int block;

    public GeneticMod() {
        this.block = 1;
    }
    public GeneticMod(int baseBlock) {
        this.block = baseBlock;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        addedExhaust = !card.exhaust;
        card.exhaust = true;
    }
    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, (c) -> (c.cost >= 0 && c.baseBlock >= 5 && c.rarity != AbstractCard.CardRarity.BASIC));
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return this.block;
    }

    public int getBaseVal(AbstractCard card) {
        return card.baseBlock / 5;
    }

    public String key() {
        return ID;
    }

    public int val(AbstractCard card) {
        return this.getBaseVal(card);
    }

    public int baseVal(AbstractCard card) {
        return this.getBaseVal(card);
    }

    public boolean modified(AbstractCard card) {
        return this.modified;
    }

    public boolean upgraded(AbstractCard card) {
        this.modified = card.timesUpgraded != 0 || card.upgraded;
        this.upgraded = card.timesUpgraded != 0 || card.upgraded;
        return this.upgraded;
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
        return insertAfterText(rawDescription, String.format(addedExhaust ? CARD_TEXT[0] : CARD_TEXT[1], DESCRIPTION_KEY));
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        int increment = getBaseVal(card);
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.uuid.equals(card.uuid)) {
                for (AbstractCardModifier mod : modifiers(c)) {
                    if (mod instanceof GeneticMod) {
                        ((GeneticMod) mod).block += increment;
                    }
                }
                c.applyPowers();
            }
        }
        for (AbstractCard c : GetAllInBattleInstances.get(card.uuid)) {
            for (AbstractCardModifier mod : modifiers(c)) {
                if (mod instanceof GeneticMod) {
                    ((GeneticMod) mod).block += increment;
                }
            }
            c.applyPowers();
        }
    }

    @Override
    public void onApplyPowers(AbstractCard card) {
        card.initializeDescription();
    }

    @Override
    public void onUpgradeCheck(AbstractCard card) {
        if (!card.exhaust) {
            addedExhaust = true;
            card.exhaust = true;
        }
        card.initializeDescription();
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new GeneticMod(block);
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}