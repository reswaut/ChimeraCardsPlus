package chimeracardsplus.cardmods.rare;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.GetAllInBattleInstances;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.NeowsLament;

import static basemod.helpers.CardModifierManager.modifiers;

public class BlessedMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(BlessedMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;
    private boolean used;

    public BlessedMod() {
        this.used = false;
    }
    public BlessedMod(boolean used) {
        this.used = used;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, (c) -> (c.cost >= -1
                && isNormalCard(c)))
                && characterCheck((p) -> {
            AbstractRelic relic = p.getRelic(NeowsLament.ID);
                    return (relic != null) && relic.counter > 1;
                });
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (!used) {
            AbstractRelic relic = AbstractDungeon.player.getRelic(NeowsLament.ID);
            if (relic != null && relic.counter > 0) {
                relic.flash();
                relic.counter += 1;
                this.used = true;
            }
        }
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.uuid.equals(card.uuid)) {
                for (AbstractCardModifier mod : modifiers(c)) {
                    if (mod instanceof BlessedMod) {
                        ((BlessedMod) mod).used = true;
                    }
                }
                c.applyPowers();
            }
        }
        for (AbstractCard c : GetAllInBattleInstances.get(card.uuid)) {
            for (AbstractCardModifier mod : modifiers(c)) {
                if (mod instanceof BlessedMod) {
                    ((BlessedMod) mod).used = true;
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
    public Color getGlow(AbstractCard card) {
        if (!CardCrawlGame.isInARun() || used) {
            return null;
        }
        AbstractRelic relic = AbstractDungeon.player.getRelic(NeowsLament.ID);
        if (relic == null || relic.counter <= 0) {
            return null;
        }
        return Color.GOLD;
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
        String text;
        if (used) {
            text = CARD_TEXT[1];
        } else if (!CardCrawlGame.isInARun()) {
            text = CARD_TEXT[0];
        } else {
            AbstractRelic relic = AbstractDungeon.player.getRelic(NeowsLament.ID);
            if (relic == null || relic.counter <= 0) {
                text = CARD_TEXT[1];
            } else {
                text = CARD_TEXT[0];
            }
        }
        return insertAfterText(rawDescription, text);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new BlessedMod(used);
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}