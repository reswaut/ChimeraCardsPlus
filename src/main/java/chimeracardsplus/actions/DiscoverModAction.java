package chimeracardsplus.actions;

import CardAugments.cardmods.AbstractAugment;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;

import java.util.ArrayList;

import static CardAugments.CardAugmentsMod.getAllValidMods;
import static CardAugments.CardAugmentsMod.getTrulyRandomValidCardMod;
import static basemod.helpers.CardModifierManager.addModifier;

public class DiscoverModAction extends AbstractGameAction {
    private boolean retrieveCard = false;
    private final AbstractCard baseCard;

    public DiscoverModAction(AbstractCard card) {
        this.baseCard = card;
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FAST;
    }

    public void update() {
        ArrayList<AbstractCard> generatedCards = this.generateCardChoices();

        if (this.duration == Settings.ACTION_DUR_FAST) {
            AbstractDungeon.cardRewardScreen.customCombatOpen(generatedCards, CardRewardScreen.TEXT[1], false);
            this.tickDuration();
        } else {
            if (!this.retrieveCard) {
                if (AbstractDungeon.cardRewardScreen.discoveryCard != null) {
                    AbstractCard disCard = AbstractDungeon.cardRewardScreen.discoveryCard.makeStatEquivalentCopy();
                    if (AbstractDungeon.player.hasPower("MasterRealityPower")) {
                        disCard.upgrade();
                    }

                    disCard.current_x = -1000.0F * Settings.xScale;
                    if (AbstractDungeon.player.hand.size() < 10) {
                        AbstractDungeon.effectList.add(new ShowCardAndAddToHandEffect(disCard, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                    } else {
                        AbstractDungeon.effectList.add(new ShowCardAndAddToDiscardEffect(disCard, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                    }

                    AbstractDungeon.cardRewardScreen.discoveryCard = null;
                }

                this.retrieveCard = true;
            }

            this.tickDuration();
        }
    }

    private ArrayList<AbstractCard> generateCardChoices() {
        ArrayList<AbstractAugment> all = getAllValidMods(baseCard);
        ArrayList<AbstractCard> ret = new ArrayList<>();
        if (all.isEmpty()) {
            ret.add(baseCard);
            return ret;
        }
        ArrayList<AbstractAugment> derp = new ArrayList<>();
        if (all.size() <= 3) {
            derp = all;
        } else {
            while (derp.size() != 3) {
                boolean dupe = false;
                AbstractAugment tmp = getTrulyRandomValidCardMod(baseCard);
                AbstractCard card1 = baseCard.makeStatEquivalentCopy();
                addModifier(card1, tmp);

                for (AbstractAugment c : derp) {
                    AbstractAugment mod = (AbstractAugment) c.makeCopy();
                    AbstractCard card2 = baseCard.makeStatEquivalentCopy();
                    addModifier(card2, mod);
                    if (tmp.identifier(card1).equals(mod.identifier(card2))) {
                        dupe = true;
                        break;
                    }
                }

                if (!dupe) {
                    derp.add((AbstractAugment) tmp.makeCopy());
                }
            }
        }
        for (AbstractAugment augment : derp) {
            AbstractCard card = baseCard.makeStatEquivalentCopy();
            addModifier(card, augment);
            ret.add(card);
        }
        return ret;
    }
}