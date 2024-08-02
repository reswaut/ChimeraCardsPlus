package chimeracardsplus.actions;

import CardAugments.cardmods.AbstractAugment;
import chimeracardsplus.interfaces.TriggerOnObtainMod;
import chimeracardsplus.interfaces.TriggerOnPurgeMod;
import chimeracardsplus.interfaces.TriggerPreDeathMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;

import java.util.ArrayList;

import static CardAugments.CardAugmentsMod.getAllValidMods;
import static basemod.helpers.CardModifierManager.addModifier;
import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.miscRng;

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
        ArrayList<AbstractAugment> filt = new ArrayList<>();
        for (AbstractAugment mod : all) {
            if (!(mod instanceof TriggerOnObtainMod || mod instanceof TriggerOnPurgeMod || mod instanceof TriggerPreDeathMod)) {
                filt.add(mod);
            }
        }
        ArrayList<AbstractCard> ret = new ArrayList<>();
        if (filt.isEmpty()) {
            ret.add(baseCard);
            return ret;
        }
        ArrayList<Integer> derp = new ArrayList<>();
        while (derp.size() < Math.min(3, filt.size())) {
            int tmp = miscRng.random(0, filt.size() - 1);
            if (!derp.contains(tmp)) {
                derp.add(tmp);
            }
        }
        for (int id : derp) {
            AbstractCard card = baseCard.makeStatEquivalentCopy();
            addModifier(card, filt.get(id).makeCopy());
            ret.add(card);
        }
        return ret;
    }
}