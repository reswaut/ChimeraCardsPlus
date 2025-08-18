package chimeracardsplus.cardmods.rare;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.interfaces.BonusMod;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.mod.stslib.StSLib;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.GetAllInBattleInstances;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.NeowsLament;

public class BlessedMod extends AbstractAugment implements BonusMod {
    public static final String ID = ChimeraCardsPlus.makeID(BlessedMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;
    private boolean used;

    public BlessedMod() {
        used = false;
    }
    public BlessedMod(boolean used) {
        this.used = used;
    }

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return abstractCard.cost >= -1 && isNormalCard(abstractCard) && characterCheck(p -> {
            AbstractRelic relic = p.getRelic(NeowsLament.ID);
            return relic != null && relic.counter > 1;
        });
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (!used) {
            AbstractRelic relic = AbstractDungeon.player.getRelic(NeowsLament.ID);
            if (relic != null && relic.counter > 0) {
                relic.flash();
                relic.counter += 1;
                used = true;
            }
        }
        do {
            AbstractCard c = StSLib.getMasterDeckEquivalent(card);
            if (c != null && CardModifierManager.hasModifier(c, ID)) {
                BlessedMod modifier = (BlessedMod) CardModifierManager.getModifiers(c, ID).get(0);
                modifier.used = true;
                c.applyPowers();
            }
        } while (false);
        for (AbstractCard c : GetAllInBattleInstances.get(card.uuid)) {
            if (CardModifierManager.hasModifier(c, ID)) {
                BlessedMod modifier = (BlessedMod) CardModifierManager.getModifiers(c, ID).get(0);
                modifier.used = true;
                c.applyPowers();
            }
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
        return Color.GOLD.cpy();
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