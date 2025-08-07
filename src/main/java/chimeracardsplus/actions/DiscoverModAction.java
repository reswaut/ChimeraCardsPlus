package chimeracardsplus.actions;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.patches.RolledModFieldPatches;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.interfaces.HealingMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.watcher.MasterRealityPower;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class DiscoverModAction extends AbstractGameAction {
    private boolean retrieveCard = false;
    private final AbstractCard baseCard;

    public DiscoverModAction(AbstractCard card) {
        this.baseCard = card;
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FAST;
    }

    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            ArrayList<AbstractCard> generatedCards = this.generateCardChoices();
            AbstractDungeon.cardRewardScreen.customCombatOpen(generatedCards, CardRewardScreen.TEXT[1], false);
            this.tickDuration();
        } else {
            if (!this.retrieveCard) {
                if (AbstractDungeon.cardRewardScreen.discoveryCard != null) {
                    AbstractCard disCard = AbstractDungeon.cardRewardScreen.discoveryCard.makeStatEquivalentCopy();
                    if (AbstractDungeon.player.hasPower(MasterRealityPower.POWER_ID)) {
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
        ArrayList<AbstractAugment> filter = CardAugmentsMod.getAllValidMods(baseCard).stream().filter((mod) -> !(mod instanceof HealingMod)).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<AbstractCard> ret = new ArrayList<>();
        if (filter.isEmpty()) {
            ret.add(baseCard);
            return ret;
        }
        ArrayList<Integer> derp = new ArrayList<>();
        while (derp.size() < Math.min(3, filter.size())) {
            int tmp = AbstractDungeon.miscRng.random(0, filter.size() - 1);
            if (!derp.contains(tmp)) {
                derp.add(tmp);
            }
        }
        for (int id : derp) {
            AbstractCard card = baseCard.makeStatEquivalentCopy();
            CardModifierManager.addModifier(card, filter.get(id).makeCopy());
            RolledModFieldPatches.RolledModField.rolled.set(card, true);
            ret.add(card);
        }
        return ret;
    }
}