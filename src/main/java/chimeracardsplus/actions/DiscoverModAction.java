package chimeracardsplus.actions;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.patches.RolledModFieldPatches.RolledModField;
import basemod.BaseMod;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus.AugmentBonusLevel;
import chimeracardsplus.helpers.Constants;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.watcher.MasterRealityPower;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DiscoverModAction extends AbstractGameAction {
    private boolean first = true;
    private final AbstractCard baseCard;

    public DiscoverModAction(AbstractCard card) {
        baseCard = card;
        actionType = ActionType.CARD_MANIPULATION;
    }

    @Override
    public void update() {
        if (first) {
            first = false;
            AbstractDungeon.cardRewardScreen.customCombatOpen(generateCardChoices(), CardRewardScreen.TEXT[1], true);
            return;
        }
        if (AbstractDungeon.cardRewardScreen.discoveryCard != null) {
            AbstractCard disCard = AbstractDungeon.cardRewardScreen.discoveryCard.makeStatEquivalentCopy();
            if (AbstractDungeon.player.hasPower(MasterRealityPower.POWER_ID) && disCard.canUpgrade()) {
                disCard.upgrade();
            }

            disCard.current_x = -1000.0F * Settings.xScale;
            if (AbstractDungeon.player.hand.size() >= BaseMod.MAX_HAND_SIZE) {
                AbstractDungeon.effectList.add(new ShowCardAndAddToDiscardEffect(disCard, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                AbstractDungeon.player.createHandIsFullDialog();
            } else {
                AbstractDungeon.effectList.add(new ShowCardAndAddToHandEffect(disCard, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
            }

            AbstractDungeon.cardRewardScreen.discoveryCard = null;
        }
        isDone = true;
    }

    private ArrayList<AbstractCard> generateCardChoices() {
        List<AbstractAugment> filter = AbstractAugmentPlus.filterModsByBonusLevel(CardAugmentsMod.getAllValidMods(baseCard), AugmentBonusLevel.NORMAL);
        ArrayList<AbstractCard> ret = new ArrayList<>(Constants.DEFAULT_LIST_SIZE);
        if (filter.isEmpty()) {
            ret.add(baseCard);
            return ret;
        }
        Collection<Integer> derp = new ArrayList<>(Constants.DEFAULT_LIST_SIZE);
        for (int i = Math.min(3, filter.size()); i > 0; --i) {
            int tmp;
            do {
                tmp = AbstractDungeon.miscRng.random(0, filter.size() - 1);
            } while (derp.contains(tmp));
            derp.add(tmp);
        }
        for (int id : derp) {
            AbstractCard card = baseCard.makeStatEquivalentCopy();
            CardModifierManager.addModifier(card, filter.get(id).makeCopy());
            RolledModField.rolled.set(card, true);
            ret.add(card);
        }
        return ret;
    }
}