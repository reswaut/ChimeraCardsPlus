package chimeracardsplus.cardmods.rare;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.interfaces.TriggerOnObtainMod;
import chimeracardsplus.interfaces.TriggerOnUpdateObjectsMod;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.shrines.Transmogrifier;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ChangingMod extends AbstractAugment implements TriggerOnObtainMod, TriggerOnUpdateObjectsMod {
    public static final String ID = ChimeraCardsPlus.makeID(ChangingMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;
    public static final String UI_TEXT = CardCrawlGame.languagePack.getEventString(Transmogrifier.ID).OPTIONS[2];
    private final Set<AbstractDungeon.CurrentScreen> VALID_SCREENS = new HashSet<>(Arrays.asList(
            AbstractDungeon.CurrentScreen.COMBAT_REWARD,
            AbstractDungeon.CurrentScreen.MAP,
            AbstractDungeon.CurrentScreen.NONE,
            AbstractDungeon.CurrentScreen.SHOP,
            AbstractDungeon.CurrentScreen.VICTORY
    ));
    private AbstractDungeon.CurrentScreen prevScreen;
    private boolean pickup, cardsSelected;

    public ChangingMod() {
        this.pickup = false;
        this.cardsSelected = true;
    }

    public ChangingMod(boolean pickup) {
        this.pickup = pickup;
        this.cardsSelected = true;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return isNormalCard(card) && card.rarity != AbstractCard.CardRarity.BASIC && AbstractDungeon.getCurrMapNode() != null && AbstractDungeon.getCurrRoom() instanceof MonsterRoom;
    }

    @Override
    public void onObtain(AbstractCard card) {
        pickup = true;
    }

    @Override
    public boolean onUpdateObjects(AbstractCard card) {
        if (AbstractDungeon.getCurrMapNode() == null) {
            return false;
        }
        AbstractRoom.RoomPhase phase = AbstractDungeon.getCurrRoom().phase;
        if (cardsSelected && pickup && phase != AbstractRoom.RoomPhase.INCOMPLETE && phase != AbstractRoom.RoomPhase.COMBAT && VALID_SCREENS.contains(AbstractDungeon.screen)) {
            prevScreen = AbstractDungeon.screen;
            pickup = false;
            CardGroup group = CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards());
            group.removeCard(card);
            AbstractDungeon.gridSelectScreen.open(group, 1, UI_TEXT, false, true, true, false);
            AbstractDungeon.dynamicBanner.hide();
            cardsSelected = false;
            AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.INCOMPLETE;
        }
        if (!cardsSelected && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            cardsSelected = true;
            AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            c.untip();
            c.unhover();

            AbstractDungeon.player.masterDeck.removeCard(c);
            AbstractDungeon.transformCard(c, false, AbstractDungeon.miscRng);
            AbstractCard transCard = AbstractDungeon.getTransformedCard();
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(transCard, c.current_x, c.current_y));

            AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            return true;
        }
        if (!cardsSelected && AbstractDungeon.screen == prevScreen) {
            cardsSelected = true;
            AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
        return false;
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
        return new ChangingMod(this.pickup);
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}